package com.msoft.mbi.data.api.data.indicator;

import java.util.Comparator;

public class FieldComparator {

    public static final Comparator<Field> ORDER_BY_TITLE = new OrderByTitle();

    private static class OrderByTitle implements Comparator<Field> {
        public int compare(Field fieldOne, Field fieldTwo) {
            String titleOne = fieldOne.getTitle().toUpperCase().trim();
            String titleTwo = fieldTwo.getTitle().toUpperCase().trim();
            return titleOne.compareTo(titleTwo);
        }
    }

}
