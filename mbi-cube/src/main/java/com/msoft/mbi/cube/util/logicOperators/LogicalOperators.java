package com.msoft.mbi.cube.util.logicOperators;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class LogicalOperators {

    public static final String GREATER_THAN = ">";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS_THAN = "<";
    public static final String LESS_EQUAL = "<=";
    public static final String EQUAL = "=";
    public static final String NOT_EQUAL = "<>";
    public static final String CONTAINS = "like";
    public static final String NOT_CONTAINS = "notlike";
    public static final String STARTS_WITH = "like%";
    public static final String FIRST_N = "primeiros(n)";
    public static final String LAST_N = "ultimos(n)";
    public static final String BETWEEN_EXCLUSIVE = "between";
    public static final String BETWEEN_INCLUSIVE = "periodo";
    public static final OperaTorContainsText OPERA_TOR_CONTAINS_TEXT = new OperaTorContainsText();
    public static final OperaTorNotContainsText OPERA_TOR_NOT_CONTAINS_TEXT = new OperaTorNotContainsText();
    public static final OperaTorNotEqualsDimension OPERA_TOR_NOT_EQUALS_DIMENSION = new OperaTorNotEqualsDimension();
    public static final OperaTorNotEqualsNumber OPERA_TOR_NOT_EQUALS_NUMBER = new OperaTorNotEqualsNumber();
    public static final OperaTorBetweenExclusiveData OPERA_TOR_BETWEEN_EXCLUSIVE_DATA = new OperaTorBetweenExclusiveData();
    public static final OperaTorBetweenInclusiveData OPERA_TOR_BETWEEN_INCLUSIVE_DATA = new OperaTorBetweenInclusiveData();
    public static final OperatorBetweenNumbers OPERATOR_BETWEEN_NUMBERS = new OperatorBetweenNumbers();
    public static final OperatorEqualDimension OPERATOR_EQUAL_DIMENSION = new OperatorEqualDimension();
    public static final OperaTorIgualNumber OPERA_TOR_IGUAL_NUMBER = new OperaTorIgualNumber();
    public static final OperatorStatsWithText OPERATOR_STATS_WITH_TEXT = new OperatorStatsWithText();
    public static final OperatorGreaterDate OPERATOR_GREATER_DATE = new OperatorGreaterDate();
    public static final OperatorGreaterEqualDate OPERATOR_GREATER_EQUAL_DATE = new OperatorGreaterEqualDate();
    public static final OperatorGreaterEqualNumber OPERATOR_GREATER_EQUAL_NUMBER = new OperatorGreaterEqualNumber();
    public static final OperatorGreaterNumber OPERATOR_GREATER_NUMBER = new OperatorGreaterNumber();
    public static final OperatorLessThanDate OPERATOR_LESS_THAN_DATE = new OperatorLessThanDate();
    public static final OperatorLessEqualDate OPERATOR_LESS_EQUAL_DATE = new OperatorLessEqualDate();
    public static final OperatorLessEqualNumber OPERATOR_LESS_EQUAL_NUMBER = new OperatorLessEqualNumber();
    public static final OperatorLessThanNumber OPERATOR_LESS_THAN_NUMBER = new OperatorLessThanNumber();

    public static class OperaTorContainsText extends OperaTorSingleValue<String> {

        @Override
        public boolean compare(String firstValue, String secondValue) {
            return firstValue.contains(secondValue);
        }
    }

    public static class OperaTorNotContainsText extends OperaTorSingleValue<String> {
        @Override
        public boolean compare(String firstValue, String secondValue) {
            return !firstValue.contains(secondValue);
        }
    }

    public static class OperaTorNotEqualsDimension extends OperaTorSingleValue {

        @Override
        public boolean compare(Object firstValue, Object secondValue) {
            return !firstValue.equals(secondValue);
        }
    }

    public static class OperaTorNotEqualsNumber extends OperaTorSingleValue<Number> {

        @Override
        public boolean compare(Number firstValue, Number secondValue) {
            return firstValue.doubleValue() != secondValue.doubleValue();
        }
    }

    public static class OperaTorBetweenExclusiveData implements OperaTor<Date> {

        @Override
        public boolean compare(Date firstValue, List<Date> valuesToCompare) {
            Date firstValueIni = valuesToCompare.get(0);
            Date firstValueFim = valuesToCompare.get(1);
            return firstValue.compareTo(firstValueIni) >= 0 && firstValue.compareTo(firstValueFim) < 0;
        }
    }

    public static class OperaTorBetweenInclusiveData implements OperaTor<Date> {

        @Override
        public boolean compare(Date firstValue, List<Date> valuesToCompare) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dt = sdf.format(firstValue);

            Date data = Date.valueOf(dt);

            Date firstValueIni = valuesToCompare.get(0);
            Date firstValueFim = valuesToCompare.get(1);
            return data.compareTo(firstValueIni) >= 0 && data.compareTo(firstValueFim) <= 0;
        }
    }

    public static class OperatorBetweenNumbers implements OperaTor<Number> {

        @Override
        public boolean compare(Number firstValue, List<Number> valuesToCompare) {
            double firstValueIni = valuesToCompare.get(0).doubleValue();
            double firstValueFim = valuesToCompare.get(1).doubleValue();
            return firstValue.doubleValue() >= firstValueIni && firstValue.doubleValue() <= firstValueFim;
        }
    }

    public static class OperatorEqualDimension extends OperaTorSingleValue {

        @Override
        public boolean compare(Object firstValue, Object secondValue) {
            return firstValue.equals(secondValue);
        }
    }

    public static class OperaTorIgualNumber extends OperaTorSingleValue<Number> {

        @Override
        public boolean compare(Number firstValue, Number secondValue) {
            return firstValue.doubleValue() == secondValue.doubleValue();
        }
    }

    public static class OperatorStatsWithText extends OperaTorSingleValue<String> {

        @Override
        public boolean compare(String firstValue, String secondValue) {
            return firstValue.startsWith(secondValue);
        }
    }

    public static class OperatorGreaterDate extends OperaTorSingleValue<Date> {

        @Override
        public boolean compare(Date firstValue, Date secondValue) {
            return firstValue.compareTo(secondValue) > 0;
        }
    }

    public static class OperatorGreaterEqualDate extends OperaTorSingleValue<Date> {

        @Override
        public boolean compare(Date firstValue, Date secondValue) {
            return firstValue.compareTo(secondValue) >= 0;
        }
    }

    public static class OperatorGreaterEqualNumber extends OperaTorSingleValue<Number> {

        @Override
        public boolean compare(Number firstValue, Number secondValue) {
            return firstValue.doubleValue() >= secondValue.doubleValue();
        }
    }

    public static class OperatorGreaterNumber extends OperaTorSingleValue<Number> {

        @Override
        public boolean compare(Number firstValue, Number secondValue) {
            return firstValue.doubleValue() > secondValue.doubleValue();
        }
    }

    public static class OperatorLessThanDate extends OperaTorSingleValue<Date> {

        @Override
        public boolean compare(Date firstValue, Date secondValue) {
            return firstValue.compareTo(secondValue) < 0;
        }
    }

    public static class OperatorLessEqualDate extends OperaTorSingleValue<Date> {

        @Override
        public boolean compare(Date firstValue, Date secondValue) {
            return firstValue.compareTo(secondValue) <= 0;
        }
    }

    public static class OperatorLessEqualNumber extends OperaTorSingleValue<Number> {

        @Override
        public boolean compare(Number firstValue, Number secondValue) {
            return firstValue.doubleValue() <= secondValue.doubleValue();
        }
    }

    public static class OperatorLessThanNumber extends OperaTorSingleValue<Number> {

        @Override
        public boolean compare(Number firstValue, Number secondValue) {
            return firstValue.doubleValue() < secondValue.doubleValue();
        }
    }
}
