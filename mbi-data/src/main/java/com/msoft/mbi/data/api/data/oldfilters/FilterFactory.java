package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.*;
import com.msoft.mbi.data.api.data.util.BIMacro;
import com.msoft.mbi.data.api.data.util.Constants;

public class FilterFactory {

    public FilterFactory() {
    }

    public static DimensionFilter createDimensionFilter(Field field, String operator, String value) throws BIException {
        DimensionFilter dimensionFilter;
        Operator newOperator = new Operator(operator);
        if (value.indexOf("@|") == 1 || value.indexOf("@|") == 0) {
            if (value.charAt(0) == '\'') {
                value = value.substring(1, value.length() - 1);
            }
            value = value.substring(value.indexOf('@'));
            BIMacros m = new BIMacros();
            BIMacro macro = m.getMacroById(value.substring(0, value.lastIndexOf('|') + 1));
            MacroFilterFactory macroFilterFactory = null;
            String concatenacao = "";
            if (value.lastIndexOf('|') + 1 < value.length())
                concatenacao = value.substring(value.lastIndexOf('|') + 1);

            if (macro.getFieldType().equals(Constants.DATE)) {
                macroFilterFactory = new MacroFilterDateFactory(field, macro, concatenacao, newOperator);
            } else if (macro.getFieldType().equals(Constants.STRING)) {
                macroFilterFactory = new MacroFilterTextFactory(field, macro);
            }
            dimensionFilter = macroFilterFactory.getDimensionFilter();
            dimensionFilter.setMacro(macro, field);
        } else {
            dimensionFilter = new DimensionFilterJDBC();
            dimensionFilter.setCondition(field, newOperator, value);
        }
        return dimensionFilter;
    }

    public static MetricFilter createMetricFilter(Field field, String operator, String value) throws BIException {
        MetricFilter metricFilter = new MetricFilterJDBC();
        metricFilter.setCondition(field, operator, value);
        return metricFilter;
    }
}
