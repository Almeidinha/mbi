package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIFilterException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.util.BIMacro;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

@Data
@Getter
@Setter
@SuppressWarnings("unused")
public abstract class DimensionFilter implements Filter, Cloneable {

    private ArrayList<DimensionFilter> filters;
    private String connector;
    private Condition condition;
    private BIMacro macro;
    private Field macroField;
    private boolean drillDown = false;

    public DimensionFilter() {
    }

    public DimensionFilter(Field macroField, BIMacro macro, String connector) {
        this.macroField = macroField;
        this.macro = macro;
        this.connector = connector;
    }

    public DimensionFilter(ArrayList<DimensionFilter> filters, String connector, Condition condition, BIMacro macro, Field macroField) {
        this.filters = filters;
        this.connector = connector;
        this.condition = condition;
        this.macro = macro;
        this.macroField = macroField;
    }

    public DimensionFilter getDimensionFilter(int index) throws BIException {
        if (this.filters != null) {
            return filters.get(index);
        } else {
            if (index == 0) {
                return this;
            } else {
                return null;
            }
        }
    }

    public void setMacro(BIMacro macro, Field field) {
        this.macro = macro;
        this.macroField = field;
    }

    public boolean isMacro() {
        return Optional.ofNullable(this.macro).isPresent();
    }

    public int hasFilters() {
        if (this.isMacro() || Optional.ofNullable(this.filters).isEmpty())
            return 0;
        else
            return this.filters.size();
    }

    public int hasAllFilters() {
        if (this.isMacro()) {
            return 0;
        } else if (Optional.ofNullable(this.filters).isEmpty()) {
            return 1;
        } else {
            int ret = 0;
            for (DimensionFilter filter : this.filters) {
                ret += filter.hasAllFilters();
            }
            return ret;
        }
    }

    public Iterator<DimensionFilter> getIterator() {
        if (Optional.ofNullable(this.filters).isPresent()) {
            return this.filters.iterator();
        }
        return null;
    }

    public void setDimensionFilter(DimensionFilter dimensionFilter, int index) throws BIFilterException {
        if (Optional.ofNullable(this.filters).isPresent()) {
            filters.set(index, dimensionFilter);
        } else {
            if (index == 0) {
                this.setCondition(dimensionFilter.getCondition());
                this.setConnector(dimensionFilter.getConnector());
                this.setDrillDown(dimensionFilter.drillDown);
                this.setFilters(dimensionFilter.getFilters());
                this.setMacro(dimensionFilter.macro, dimensionFilter.macroField);
            } else {
                BIFilterException biFilterException = new BIFilterException();
                biFilterException.setAction("Erro ao setar os filtros.");
                biFilterException.setLocal(this.getClass(), "setDimensionFilter(DimensionFilter dimensionFilter, int index)");
                throw biFilterException;
            }
        }
    }

    public void remove(int index) {
        this.filters.remove(index);
    }

    public String getConnector() {
        return Optional.ofNullable(this.connector).orElse("");
    }

    public String getConnectorDescription() {
        if (Optional.ofNullable(this.connector).isPresent()) {
            if (this.connector.equals("AND"))
                return "E";
            else if (this.connector.equals("OR"))
                return "OU";
        }
        return this.connector;
    }

    public void setConnector(String newConnector) {
        connector = Optional.ofNullable(newConnector).orElse("").toUpperCase();
    }

    public void removeAll() {
        this.filters = null;
        this.condition = null;
        this.macro = null;
    }

    public boolean removePerField(Field field) {
        return this.removePerField(field.getFieldId());
    }

    public boolean removePerField(int code) {
        if (isMacro() && code == this.macroField.getFieldId()) {
            return true;
        } else if (this.condition != null && code == this.condition.getField().getFieldId()) {
            return true;
        } else if (this.filters != null) {
            this.filters.removeIf(filter -> filter.removePerField(code));
        }
        return false;
    }

    public Field getMacroField() {
        if (this.isMacro()) {
            return this.macroField;
        }
        return null;
    }

    public Field getField() {
        if (this.isMacro()) {
            return this.macroField;
        } else {
            return this.condition.getField();
        }
    }

    public Operator getOperator() {
        if (!this.isMacro()) {
            return this.condition.getOperator();
        }

        if (Optional.ofNullable(this.condition).isPresent()) {
            return this.condition.getOperator();
        }
        return new Operator("=");

    }

    public String getSQLValue() throws BIException {
        if (this.isMacro()) {
            return this.macro.getId();
        } else {
            return this.condition.getSQLValue();
        }
    }

    public String getValue() throws BIException {
        if (this.isMacro()) {
            return this.macro.getId();
        } else {
            return this.condition.getValue();
        }
    }

    public boolean addDimensionFilter(DimensionFilter filter) {
        if (Optional.ofNullable(this.filters).isEmpty()) {
            this.filters = new ArrayList<>();
        }
        return filters.add(filter);
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean save) {
        StringBuilder result = new StringBuilder();

        if (Optional.ofNullable(this.condition).isEmpty()) {
            if (this.isMacro() && save) {
                if (Optional.ofNullable(this.macroField.getTableNickname()).isPresent() && !this.macroField.getTableNickname().trim().isEmpty()) {
                    result.append(this.macroField.getTableNickname()).append(".");
                }
                result.append(this.macroField.getName()).append(" = '").append(this.macro.getId()).append("'");
            } else if (Optional.ofNullable(this.filters).isPresent() && !this.filters.isEmpty()) {
                result.append("(");
                for (int i = 0; i < filters.size(); i++) {
                    result.append(filters.get(i).toString(save));
                    if (i < filters.size() - 1) {
                        result.append(" ").append(this.connector).append(" ");
                    }
                }
                result.append(")");
            } else {
                result.append("1 = 1");
            }
        } else {
            if (save || isMacro()) {
                if (Optional.ofNullable(this.macroField.getTableNickname()).isPresent() && !this.macroField.getTableNickname().trim().isEmpty()) {
                    result.append(this.macroField.getTableNickname()).append(".");
                }
                result.append(this.macroField.getName()).append(" ").append(this.condition.getOperator().getSymbol()).append(" '").append(this.macro.getId()).append("'");
            } else {
                result.append(this.condition.toString());
            }
        }
        return result.toString();
    }

    @Override
    public abstract Object clone();

}
