package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.indicator.ConditionJDBC;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIMacro;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;

@Getter
@Setter
public class DimensionFilterJDBC extends DimensionFilter implements Filter, Cloneable {

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

    @SneakyThrows
    @Override
    public Object clone() {
        DimensionFilter dimensionFilter = new DimensionFilterJDBC();
        if (this.getCondition() == null || this.getMacro() != null) {
            if (this.getFilters() != null) {
                ArrayList<DimensionFilter> novo = new ArrayList<>();
                for (DimensionFilter filter : this.getFilters()) {
                    novo.add((DimensionFilterJDBC) filter.clone());
                }
                dimensionFilter.setFilters(novo);
                dimensionFilter.setConnector(this.getConnector());
            }
            if (this.getMacro() != null) {
                dimensionFilter.setMacro((BIMacro) this.getMacro().clone(), (Field) this.getMacroField().clone());
                if (this.getCondition() != null)
                    dimensionFilter.setCondition((Condition) this.getCondition().clone());
            }
        } else {
            dimensionFilter.setConnector(this.getConnector());
            dimensionFilter.setCondition((Condition) this.getCondition().clone());
        }
        dimensionFilter.setDrillDown(this.isDrillDown());
        return dimensionFilter;
    }

    public Object applyValues(Object stmt, Integer posicao) throws BISQLException {
        Condition condition = this.getCondition();
        try {
            if (condition != null) {
                posicao = (Integer) condition.applyValues(stmt, posicao);
            }
            if (this.getFilters() != null) {
                for (DimensionFilter fil : this.getFilters()) {
                    posicao = (Integer) fil.applyValues(stmt, posicao);
                }
            }
            return posicao;
        } catch (Exception e) {
            throw new BISQLException(e, "Erro ao setar o valor na SQL.");
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
