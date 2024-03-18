package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.filters.FilterFunction;
import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.*;

public class ValuesRepository {

    @Getter
    @Setter
    private int type;
    private final ArrayList<String> values;
    @Setter
    @Getter
    private int fieldCode;

    public static final int TIPO_SEQUENCIA = 1;
    public static final int TIPO_ACUMULADO = 2;

    public ValuesRepository(int tipo, int codigoCampo) {
        this.type = tipo;
        this.fieldCode = codigoCampo;
        this.values = new ArrayList<>();
    }

    public void addValor(double valor) {
        this.values.add(String.valueOf(valor));
    }

    public void addValor(int valor) {
        this.values.add(String.valueOf(valor));
    }

    public ArrayList<String> getValues(int tipoOrdenacao) {
        ArrayList<String> retorno;
        ordena(tipoOrdenacao);
        if (this.type == FilterFunction.ACCUMULATED_FILTER) {
            retorno = formataValores();
            return retorno;
        }
        return values;
    }

    public void ordena(int tipoOrdenacao) {
        Comparator<String> comp;
        comp = stringCmp;
        if (tipoOrdenacao == 2) {
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

    public ArrayList<String> formataValores() {
        ArrayList<String> retorno = new ArrayList<>();
        for (String valore : this.values) {
            NumberFormat numberFormat = BIUtil.getFormatter(2);
            double valorFormatar = Double.parseDouble(valore);
            retorno.add(numberFormat.format(valorFormatar));
        }
        return retorno;
    }
}
