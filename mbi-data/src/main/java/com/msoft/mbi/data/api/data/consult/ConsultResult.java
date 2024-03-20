package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIFilterException;
import com.msoft.mbi.data.api.data.indicator.Field;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

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

    public void addToValues(ArrayList<Object> valores) {
        this.values.addAll(valores);
    }

    public void addValor(Object valor) {
        if (valor == null || valor.equals("")) {
            valor = " ";
        }
        values.add(values.size(), valor);
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

    public ArrayList<Object> getFormattedValues() throws BIFilterException {
        ArrayList<Object> formattedValues = new ArrayList<>();
        for (int x = 0; x < this.values.size(); x++) {
            Object valor = this.getFormattedValue(x);
            formattedValues.add(valor);
        }
        return formattedValues;
    }

}
