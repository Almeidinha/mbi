package com.msoft.mbi.data.api.data.indicator;

import java.util.Comparator;

public class FieldComparator {

    public static final Comparator<Field> ORDENACAO_TITULO = new OrdenacaoPeloTituloCampo();

    private static class OrdenacaoPeloTituloCampo implements Comparator<Field> {
        public int compare(Field campo1, Field campo2) {
            String title1 = campo1.getTitle().toUpperCase().trim();
            String title2 = campo2.getTitle().toUpperCase().trim();
            return title1.compareTo(title2);
        }
    }

}
