package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.filters.FilterFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class ValuesRepository {

    @Getter
    @Setter
    private int type;
    private final ArrayList<String> values;
    @Setter
    @Getter
    private int fieldCode;

    public static final int SEQUENCE_TYPE = 1;
    public static final int ACCUMULATED_TYPE = 2;

    public ValuesRepository(int type, int fieldCode) {
        this.type = type;
        this.fieldCode = fieldCode;
        this.values = new ArrayList<>();
    }

    public void addValor(double valor) {
        this.values.add(String.valueOf(valor));
    }

    public void addValor(int valor) {
        this.values.add(String.valueOf(valor));
    }

    public ArrayList<String> getValues(int orderingType) {
        ArrayList<String> result;
        order(orderingType);
        if (this.type == FilterFunction.ACCUMULATED_FILTER) {
            result = formatValues();
            return result;
        }
        return values;
    }

    public void order(int orderingType) {
        Comparator<String> comp;
        comp = stringCmp;
        if (orderingType == 2) {
            comp = stringCmpReverse;
        }
        values.sort(comp);
    }

    static final Comparator<String> stringCmp = (o1, o2) -> {
        double s1 = Double.parseDouble(o1);
        double s2 = Double.parseDouble(o2);
        return Double.compare(s1, s2);
    };

    static final Comparator<String> stringCmpReverse = (o1, o2) -> {
        double s1 = Double.parseDouble(o1);
        double s2 = Double.parseDouble(o2);
        return Double.compare(s2, s1);
    };

    public ArrayList<String> formatValues() {
        ArrayList<String> result = new ArrayList<>();
        for (String value : this.values) {
            result.add(BIUtil.formatDoubleToText(value, 2));
        }
        return result;
    }
}
