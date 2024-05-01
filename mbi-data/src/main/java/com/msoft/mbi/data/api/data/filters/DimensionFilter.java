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
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Data
@Getter
@Setter
@SuppressWarnings("unused")
public abstract class DimensionFilter implements Filter {

    private List<DimensionFilter> filters;
    private String connector;
    private Condition condition;
    private BIMacro macro;
    private Field macroField;
    private boolean drillDown = false;

    protected DimensionFilter() {
    }

    protected DimensionFilter(Field macroField, BIMacro macro, String connector) {
        this.macroField = macroField;
        this.macro = macro;
        this.connector = connector;
    }

    protected DimensionFilter(List<DimensionFilter> filters, String connector, Condition condition, BIMacro macro, Field macroField) {
        this.filters = filters;
        this.connector = connector;
        this.condition = condition;
        this.macro = macro;
        this.macroField = macroField;
    }

    public DimensionFilter getDimensionFilter(int index) {
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

    public String getSqlValue() {
        if (this.isMacro()) {
            return this.macro.getId();
        } else {
            return this.condition.getSqlValue();
        }
    }

    public String getValue() {
        if (this.isMacro()) {
            return this.macro.getId();
        } else {
            return this.condition.getValue();
        }
    }

    public void addDimensionFilter(DimensionFilter filter) {
        if (Optional.ofNullable(this.filters).isEmpty()) {
            this.filters = new ArrayList<>();
        }
        filters.add(filter);
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean save) {
        StringBuilder result = new StringBuilder();

        if (condition == null) {
            if (isMacro() && save) {
                appendMacroField(result);
                result.append(" = `").append(macro.getId()).append("`");
            } else if (filters != null && !filters.isEmpty()) {
                result.append("(");
                appendFilters(result, save);
                result.append(")");
            } else {
                result.append("1 = 1");
            }
        } else {
            if (save || isMacro()) {
                appendMacroField(result);
                result.append(" ").append(condition.getOperator().getSymbol()).append(" `").append(macro.getId()).append("`");
            } else {
                result.append(condition.toString());
            }
        }
        return result.toString();
    }

    private void appendMacroField(StringBuilder result) {
        String tableNickname = Optional.ofNullable(macroField.getTableNickname()).orElse("");
        if (!tableNickname.trim().isEmpty()) {
            result.append(tableNickname).append(".");
        }
        result.append(macroField.getName());
    }

    private void appendFilters(StringBuilder result, boolean save) {
        IntStream.range(0, filters.size())
                .forEach(i -> {
                    result.append(filters.get(i).toString(save));
                    if (i < filters.size() - 1) {
                        result.append(" ").append(connector).append(" ");
                    }
                });
    }

    protected abstract DimensionFilter copy() throws BIException;

}
