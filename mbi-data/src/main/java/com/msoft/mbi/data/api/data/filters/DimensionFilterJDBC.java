package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIMacro;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DimensionFilterJDBC extends DimensionFilter implements Filter {

    private boolean startParentheses = false;
    private boolean endParentheses = false;
    private int startParentCount = 0;
    private int endParentCount = 0;

    public DimensionFilterJDBC() {
        super();
    }

    public void setCondition(Field field, Operator operator, String value) throws BIException {
        ConditionJDBC conditionJDBC = new ConditionJDBC(field, operator, value);
        super.setCondition(conditionJDBC);
    }

    public void setCondition(Field field, String operator, String value) throws BIException {
        ConditionJDBC conditionJDBC = new ConditionJDBC(field, operator, value);
        super.setCondition(conditionJDBC);
    }


    @Override
    public DimensionFilter copy() throws BIException {
        DimensionFilter dimensionFilter = new DimensionFilterJDBC();
        if (this.getCondition() == null || this.getMacro() != null) {
            if (this.getFilters() != null) {
                List<DimensionFilter> dimensionFilters = new ArrayList<>();
                for (DimensionFilter filter : this.getFilters()) {
                    dimensionFilters.add(filter.copy());
                }
                dimensionFilter.setFilters(dimensionFilters);
                dimensionFilter.setConnector(this.getConnector());
            }
            if (this.getMacro() != null) {
                dimensionFilter.setMacro(BIMacro.copy(this.getMacro()), Field.copy(this.getMacroField()));
                if (this.getCondition() != null)
                    dimensionFilter.setCondition(this.getCondition().copy());
            }
        } else {
            dimensionFilter.setConnector(this.getConnector());
            dimensionFilter.setCondition(this.getCondition().copy());
        }
        dimensionFilter.setDrillDown(this.isDrillDown());
        return dimensionFilter;
    }

    @Override
    public String applyValues(String query, Integer position) throws BIException {
        throw new NotImplementedException("DimensionFilterJDBC.applyValues(String query, Integer position) not available");
    }

    @Override
    public int applyValues(PreparedStatement stmt, Integer position) throws BISQLException {
        Condition condition = this.getCondition();
        try {
            if (condition != null) {
                position = condition.applyValues(stmt, position);
            }
            if (this.getFilters() != null) {
                for (DimensionFilter filter : this.getFilters()) {
                    position = filter.applyValues(stmt, position);
                }
            }
            return position;
        } catch (Exception e) {
            throw new BISQLException(e, "Error applying value at position: " + position);
        }
    }

    public String getFormattedValue() {
        return this.getCondition().getFormattedValue();
    }

    @Override
    public boolean haveStartParentheses() {
        return this.startParentheses;
    }

    @Override
    public boolean haveEndParentheses() {
        return this.endParentheses;
    }
}
