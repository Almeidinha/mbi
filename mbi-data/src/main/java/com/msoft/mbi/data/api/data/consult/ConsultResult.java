package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIFilterException;
import com.msoft.mbi.data.api.data.indicator.Field;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public abstract class ConsultResult {

    @Getter
    @Setter
    protected Field field;
    @Setter
    @Getter
    protected ArrayList<Object> values;
    private boolean isSequenceFiltered = false;

    ConsultResult() {
        this.values = new ArrayList<>();
    }

    ConsultResult(Field field) {
        this();
        this.field = field;
    }

    ConsultResult(Field field, Object valor) {
        this(field);
        this.values.add(valor);
    }

    ConsultResult(Field field, Collection<Object> values) {
        this(field);
        this.values.addAll(values);
    }

    ConsultResult(Field field, ArrayList<Object> values) {
        this(field);
        this.values.addAll(values);
    }

    public void addToValues(Collection<Object> values) {
        this.values.addAll(values);
    }

    public void addToValues(ArrayList<Object> values) {
        this.values.addAll(values);
    }

    public void addValor(Object value) {
        if (value == null || value.equals("")) {
            value = " ";
        }
        values.add(values.size(), value);
    }

    public void removeValor(int index) {
        this.values.remove(index);
    }

    public void setValues(Object valor, int index) {
        this.values.set(index, valor);
    }

    public void addToValues(Object value) {
        this.values.add(value);
    }

    public Object getValor(int index) {
        if (index < this.values.size() && index != -1) {
            return this.values.get(index);
        } else {
            return null;
        }
    }

    public abstract Object getFormattedValue(int index) throws BIFilterException;

    public String toString() {
        return this.field + "\n" + this.values + "\n";
    }

    public boolean isSequenceFiltered() {
        return isSequenceFiltered;
    }

    public void setSequenceFiltered(boolean isSequenceFiltered) {
        this.isSequenceFiltered = isSequenceFiltered;
    }

    public List<Object> getFormattedValues() {
        return IntStream.range(0, this.values.size())
                .mapToObj(i -> {
                    try {
                        return this.getFormattedValue(i);
                    } catch (BIFilterException e) {
                        log.error("Error formatting value at index " + i + ": " + e.getMessage());
                        return null; // or some default value
                    }
                })
                .collect(Collectors.toList());
    }

}
