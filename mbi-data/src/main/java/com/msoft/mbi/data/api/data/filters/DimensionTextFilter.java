package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIMacro;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.NotImplementedException;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DimensionTextFilter extends DimensionFilter {

    private boolean startParentheses;
    private boolean endParentheses;
    private int startParentCount;
    private int endParentCount;

    public DimensionTextFilter() {
        super();
    }

    public DimensionTextFilter(DimensionFilter fd) throws BIException {
        if (fd != null) {
            this.setConnector(fd.getConnector());
            if (fd.getCondition() != null) {
                this.setCondition(new TextCondition(fd.getCondition()));
            }
            this.setMacro(fd.getMacro(), fd.getMacroField());
            ArrayList<DimensionFilter> list = new ArrayList<>();
            if (fd.getFilters() != null) {
                for (DimensionFilter filter : fd.getFilters()) {
                    list.add(new DimensionTextFilter(filter));
                }
            }
            this.setFilters(list);
        }
    }

    public DimensionTextFilter(Condition condition) {
        super.setCondition(condition);
    }

    @Override
    public DimensionFilter copy() throws BIException {
        DimensionFilter copyFilter = new DimensionTextFilter();
        if (this.getCondition() == null || this.getMacro() != null) {
            if (this.getFilters() != null) {
                List<DimensionFilter> dimensionFilters = new ArrayList<>();
                for (DimensionFilter filter : this.getFilters()) {
                    dimensionFilters.add(filter.copy());
                }
                copyFilter.setFilters(dimensionFilters);
                copyFilter.setConnector(this.getConnector());
            }
            if (this.getMacro() != null) {
                copyFilter.setMacro(BIMacro.copy(this.getMacro()), Field.copy(this.getMacroField()));
                if (this.getCondition() != null)
                    copyFilter.setCondition(this.getCondition().copy());
            }
        } else {
            copyFilter.setConnector(this.getConnector());
            copyFilter.setCondition(this.getCondition().copy());
        }
        copyFilter.setDrillDown(this.isDrillDown());
        return copyFilter;
    }

    @Override
    public int applyValues(PreparedStatement stmt, Integer position) throws BIException {
        throw new NotImplementedException("DimensionTextFilter.applyValuesapplyValues(PreparedStatement stmt, Integer position) not available");
    }

    @Override
    public String applyValues(String query, Integer position) throws BISQLException {
        Condition condition = this.getCondition();
        try {
            if (condition != null) {
                query = condition.applyValues(query, position);
            }
            if (this.getFilters() != null) {
                for (DimensionFilter filter : this.getFilters()) {
                    query = filter.applyValues(query, position);
                }
            }
            return query;
        } catch (Exception e) {
            throw new BISQLException(e, "Erro ao setar o valor na SQL.");
        }
    }

    public boolean haveStartParentheses() {
        return this.startParentheses;
    }

    public boolean haveEndParentheses() {
        return this.endParentheses;
    }

    public void setCondition(Field campo, Operator operator, String valor) throws BIException {
        TextCondition condition = new TextCondition(campo, operator, valor);
        super.setCondition(condition);

    }

    public void setCondition(Field campo, String operator, String valor) throws BIException {
        TextCondition condition = new TextCondition(campo, operator, valor);
        super.setCondition(condition);
    }


    public String getFormattedValue() {
        return this.getCondition().getFormattedValue();
    }

}
