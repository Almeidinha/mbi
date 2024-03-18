package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.DimensionFilterJDBC;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class MacroFilterFactory {

    @Getter
    @Setter
    private Field field;
    protected ArrayList<Operator> operators;
    protected ArrayList<String> values;

    public MacroFilterFactory() {
    }

    public abstract void unravelMacro() throws BIException;

    public DimensionFilter getDimensionFilter() throws BIException {
        DimensionFilter dimensionFilter = new DimensionFilterJDBC();
        if (values.size() == 1) {
            dimensionFilter = new DimensionFilterJDBC();
            dimensionFilter.setCondition(field, operators.get(0), values.get(0));
        } else {
            if (values.size() > 1) {
                Iterator<String> v = values.iterator();
                Iterator<Operator> o = operators.iterator();
                dimensionFilter = new DimensionFilterJDBC();
                dimensionFilter.setConnector("and");
                while (v.hasNext()) {
                    DimensionFilter filter = new DimensionFilterJDBC();
                    filter.setCondition(field, o.next(), v.next());
                    dimensionFilter.addDimensionFilter(filter);
                }
            }
        }
        return dimensionFilter;
    }
}
