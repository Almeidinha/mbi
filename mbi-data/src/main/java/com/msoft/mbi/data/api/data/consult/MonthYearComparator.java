package com.msoft.mbi.data.api.data.consult;

import java.util.HashMap;
import java.util.Map;

public class MonthYearComparator {

    private static final Map<String, Integer> MONTHS = getMONTHS();

    private static Map<String, Integer> getMONTHS() {
        Map<String, Integer> meses = new HashMap<>();
        meses.put("JAN", 1);
        meses.put("FEV", 2);
        meses.put("MAR", 3);
        meses.put("ABR", 4);
        meses.put("MAI", 5);
        meses.put("JUN", 6);
        meses.put("JUL", 7);
        meses.put("AGO", 8);
        meses.put("SET", 9);
        meses.put("OUT", 10);
        meses.put("NOV", 11);
        meses.put("DEZ", 12);
        meses.put("JANEIRO", 1);
        meses.put("FEVEREIRO", 2);
        meses.put("MARÇO", 3);
        meses.put("ABRIL", 4);
        meses.put("MAIO", 5);
        meses.put("JUNHO", 6);
        meses.put("JULHO", 7);
        meses.put("AGOSTO", 8);
        meses.put("SETEMBRO", 9);
        meses.put("OUTUBRO", 10);
        meses.put("NOVEMBRO", 11);
        meses.put("DEZEMBRO", 12);
        return meses;
    }

    public static boolean contains(String mes) {
        if (mes != null && !mes.isEmpty()) {
            return MONTHS.containsKey(mes.trim().toUpperCase());
        }
        return false;
    }

    public static Integer getNumMes(String mes) {
        if (MonthYearComparator.contains(mes)) {
            mes = mes.trim().toUpperCase();
            return MONTHS.get(mes);
        }
        return -1;
    }

    public static int compare(String o1, String o2) {
        String mes1 = o1.trim().toUpperCase();
        String mes2 = o2.trim().toUpperCase();
        return MONTHS.get(mes1).compareTo(MONTHS.get(mes2));
    }
}
