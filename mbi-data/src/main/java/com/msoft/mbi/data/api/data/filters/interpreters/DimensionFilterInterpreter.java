package com.msoft.mbi.data.api.data.filters.interpreters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.DimensionFilterJDBC;
import com.msoft.mbi.data.api.data.filters.FilterFactory;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

public class DimensionFilterInterpreter {

    private final String dml;
    private DimensionFilter dimensionFilter;
    private final Indicator indicator;

    public DimensionFilterInterpreter(String dml, Indicator indicator) throws BIException {
        this.dml = dml;
        this.indicator = indicator;
        decode();
    }

    private void decode() throws BIException {
        dimensionFilter = searchForFilter(dml);
    }


   private DimensionFilter searchForFilter(String query) throws BIException {
        query = this.removeTrailingParentheses(query);
        int tempIndex = -1;
        int andIndex;
        int orIndex;
        do {
            andIndex = query.indexOf(" AND ", tempIndex + 1);
            orIndex = query.indexOf(" OR ", tempIndex + 1);
            if (andIndex == -1 || (orIndex != -1 && orIndex < andIndex))
                andIndex = orIndex;
            tempIndex = andIndex;
            if (andIndex == -1) {
                return createFilter(query);
            }
        } while (!validateConnectors(query.substring(0, andIndex)));

        int pos;
        int posAnt = 0;
        int posAnt2 = 0;
        DimensionFilter filterJDBC = new DimensionFilterJDBC();
        String connector = query.substring(andIndex + 1, query.indexOf(' ', andIndex + 1));
        filterJDBC.setConnector(connector);

        while ((pos = query.indexOf(" " + connector + " ", posAnt)) != -1) {
            pos += connector.length() + 2;
            if (validateConnectors(query.substring(0, pos))) {
                filterJDBC.addDimensionFilter(searchForFilter(query.substring(posAnt2, pos - (connector.length() + 2))));
                posAnt2 = pos;
            }
            posAnt = pos;
        }
        filterJDBC.addDimensionFilter(searchForFilter(query.substring(posAnt2)));
        return filterJDBC;
    }

    private DimensionFilter createFilter(String query) throws BIException {
        StringTokenizer strTok = new StringTokenizer(query, " ");
        String name = strTok.nextToken();
        String nickname = "";

        int dotIndex = name.indexOf('.');
        if (dotIndex != -1) {
            nickname = name.substring(0, dotIndex);
            name = name.substring(dotIndex + 1);
        }

        Field fieldByName = this.indicator.getFieldByName(nickname, name);

        String operator = strTok.nextToken();
        if (operator.equalsIgnoreCase("IS") || operator.equalsIgnoreCase("NOT")) {
            if (strTok.countTokens() > 1) {
                operator += " " + strTok.nextToken();
            }
        }

        int operatorIndex = query.indexOf(operator);
        query = query.substring(operatorIndex);

        String value = "";
        int quoteIndex = query.indexOf("'");
        if (quoteIndex != -1) {
            value += query.substring(quoteIndex, query.lastIndexOf("'") + 1);
        } else {
            value += strTok.nextToken();
            if (operator.contains("IN(") && value.endsWith(")")) {
                value = value.substring(0, value.length() - 1);
            }
        }

        if (value.contains("%") && !value.startsWith("'%") && operator.equalsIgnoreCase("LIKE")) {
            operator += "%";
        }

        if (operator.equalsIgnoreCase("NOT LIKE")) {
            operator = "notlike";
        }

        return FilterFactory.createDimensionFilter(fieldByName, operator, value);
    }

    private String removeTrailingParentheses(String query) {
        if (query == null || query.isEmpty()) {
            return query;
        }

        query = query.trim();

        if (query.startsWith("(") && query.endsWith(")")) {
            return query.substring(1, query.length() - 1).trim();
        }

        return query;
    }

    public DimensionFilter getFilter() {
        return this.dimensionFilter;
    }

    private boolean validateConnectors(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        int openCount = StringUtils.countMatches(query, "(");
        int closeCount = StringUtils.countMatches(query, ")");

        return openCount == closeCount;
    }
}
