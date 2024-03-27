package com.msoft.mbi.data.api.data.htmlbuilder;

import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.ConditionalExpression;
import com.msoft.mbi.data.api.data.indicator.*;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StringHelper {

    public static String convertCodeExpressionToTitle(String expression, Indicator indicator) throws BIException {
        boolean hasMoreField = expression.contains(Constants.START_EXPRESSION) && expression.contains(Constants.END_EXPRESSION);

        while (hasMoreField) {
            String fieldCode = extractFieldCode(expression);
            Field field = indicator.getFieldByCode(fieldCode);

            if (field != null) {
                expression = replaceFieldCodeWithFieldTitle(expression, fieldCode, field);
            } else {
                throw new BIException("Field with code " + fieldCode + " does not exist.");
            }

            hasMoreField = expression.contains(Constants.START_EXPRESSION) && expression.contains(Constants.END_EXPRESSION);
        }
        return expression;
    }

    private static String extractFieldCode(String expression) {
        return expression.substring(expression.indexOf(Constants.START_EXPRESSION) + 2, expression.indexOf(Constants.END_EXPRESSION));
    }

    private static String replaceFieldCodeWithFieldTitle(String expression, String fieldCode, Field field) {
        return expression.replace(Constants.START_EXPRESSION + fieldCode + Constants.END_EXPRESSION, "{" + field.getTitle() + "}");
    }

    public static String convertExpressionCodeToNickName(String expression, boolean noAggregationFields, Indicator indicator) throws BIException {
        while (hasMoreFields(expression)) {
            String fieldCode = extractFieldCode(expression);
            Field field = indicator.getFieldByCode(fieldCode);

            if (field != null) {
                expression = processField(expression, noAggregationFields, indicator, fieldCode, field);
            } else {
                throw new BIException("Field with code " + fieldCode + " does not exist.");
            }
        }
        return expression;
    }

    private static boolean hasMoreFields(String expression) {
        return expression.contains(Constants.START_EXPRESSION) && expression.contains(Constants.END_EXPRESSION);
    }

    private static String processField(String expression, boolean noAggregationFields, Indicator indicator, String fieldCode, Field field) throws BIException {
        if (field.isExpression()) {
            String recExpression = convertExpressionCodeToNickName(field.getName(), noAggregationFields, indicator);
            String replacement = "(" + recExpression + ")";
            expression = BIUtil.replaceString(expression, fieldCode, replacement);
        } else {
            String sqlExpression = noAggregationFields || field.getAggregationType().equals(Constants.EMPTY) ?
                    field.getSqlExpressionWithoutNickName() :
                    getSqlExpressionWithAggregation(field);

            expression = BIUtil.replaceString(expression, fieldCode, sqlExpression);
        }
        return expression;
    }

    private static String getSqlExpressionWithAggregation(Field campoAux) {
        if (campoAux.getAggregationType().equalsIgnoreCase("COUNT_DIST")) {
            return "COUNT(DISTINCT " + campoAux.getSqlExpressionWithoutNickName() + ")";
        } else {
            return campoAux.getAggregationType() + "(" + campoAux.getSqlExpressionWithoutNickName() + ")";
        }
    }

    public static List<Object> convertExpressionFromStringToArray(String expression, Indicator indicator) {
        List<Object> expressionSlice = new ArrayList<>();
        StringBuilder constantString = new StringBuilder();
        int openCount = 0;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            switch (ch) {
                case '(':
                    processOpeningParenthesis(expressionSlice, openCount++);
                    break;
                case ')':
                    processClosingParenthesis(expressionSlice, constantString, openCount--);
                    break;
                case '#':
                    i = processFieldCode(expression, i, expressionSlice, indicator);
                    break;
                case ' ':
                    processSpace(expressionSlice, constantString);
                    break;
                default:
                    if (isArithmeticOperator(ch)) {
                        processArithmeticOperator(expressionSlice, constantString, ch);
                    } else if (Character.isDigit(ch) || ch == ',' || ch == '.') {
                        processDigitOrDecimal(expressionSlice, constantString, ch, i, expression.length());
                    }
                    break;
            }
        }
        return expressionSlice;
    }

    private static void processOpeningParenthesis(List<Object> expressionSlice, int openCount) {
        expressionSlice.add(new Parenthesis(Parenthesis.OPEN, getLevelAux(openCount)));
    }

    private static void processClosingParenthesis(List<Object> expressionSlice, StringBuilder constantString, int openCount) {
        if (!constantString.isEmpty()) {
            expressionSlice.add(constantString.toString());
            constantString.setLength(0);
        }
        expressionSlice.add(new Parenthesis(Parenthesis.CLOSE, getLevelAux(openCount)));
    }

    private static boolean isArithmeticOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private static String getLevelAux(int level) {
        StringBuilder levelBuilder = new StringBuilder();
        levelBuilder.append(level);
        for (int i = 1; i < level; i++) {
            levelBuilder.append('.').append(i);
        }
        return levelBuilder.toString();
    }

    public static boolean hasAllExpressionFields(List<Field> fields, Field field, Indicator indicator) {
        if (field != null && field.isExpression()
                && (field.getName().toUpperCase().trim().startsWith("SE(") || field.getName().toUpperCase().trim().startsWith("IF("))) {
            ConditionalExpression conditionalExpression = getExpressionConditional(field.getName(), indicator);

            if (conditionalExpression != null) {
                ExpressionCondition conditionalPart = conditionalExpression.getExpConditionalPart();
                Expression truePart = conditionalExpression.getExpPartTrue();
                Expression falsePart = conditionalExpression.getExpPartFalse();

                boolean conditionalFields = conditionalPart.getFirstAttribute().temTodosFieldsExpressao(fields, field)
                        && conditionalPart.getSecondAttribute().temTodosFieldsExpressao(fields, field);
                boolean trueFields = truePart.temTodosFieldsExpressao(fields, field);
                boolean falseFields = falsePart.temTodosFieldsExpressao(fields, field);

                return conditionalFields && trueFields && falseFields;
            }
        } else {
            return indicator.getFieldExpression(Objects.requireNonNull(field).getName()).temTodosFieldsExpressao(fields, field);
        }
        return false;
    }

    private static ExpressionCondition getConditionExpression(String condition, Indicator indicator) {
        condition = condition.trim();
        int conditionalOperatorInitialIndex = ConditionalExpression.getConditionalOperatorInitialIndex(condition);
        int conditionalOperatorFinalIndex = ConditionalExpression.getConditionalOperatorFinalIndex(condition);

        String firstPart = condition.substring(0, conditionalOperatorInitialIndex);
        String conditionalOperator = condition.substring(conditionalOperatorInitialIndex, conditionalOperatorFinalIndex);
        String secondPart = condition.substring(conditionalOperatorFinalIndex);

        Expression firstPartExp = new Expression(StringHelper.convertExpressionFromStringToArray(firstPart, indicator));
        Expression secondPartExp = new Expression(StringHelper.convertExpressionFromStringToArray(secondPart, indicator));
        Operator operator = new Operator(conditionalOperator);
        return new ExpressionCondition(firstPartExp, operator, secondPartExp);
    }

    public static ConditionalExpression getExpressionConditional(String expression, Indicator indicator) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        expression = trimExpression(expression);
        String[] parts = splitExpression(expression);

        return createConditionalExpression(parts, indicator);
    }

    private static String trimExpression(String expression) {
        expression = expression.trim();
        expression = expression.replaceAll("^(SE|IF)\\(", "");
        expression = expression.substring(0, expression.lastIndexOf(')'));
        return expression;
    }

    private static String[] splitExpression(String expression) {
        String[] parts = expression.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expression format is incorrect. Expected format: `part1;part2;part3`. Received: " + expression);
        }
        return parts;
    }

    private static ConditionalExpression createConditionalExpression(String[] parts, Indicator indicator) {
        ExpressionCondition expParteConditional = getConditionExpression(parts[0], indicator);
        Expression expParteTrue = new Expression(StringHelper.convertExpressionFromStringToArray(parts[1], indicator));
        Expression expParteFalse = new Expression(StringHelper.convertExpressionFromStringToArray(parts[2], indicator));

        return new ConditionalExpression(expParteConditional, expParteTrue, expParteFalse);
    }

    public static double calculateConditionalExpression(ConditionalExpression expression, CachedResults cachedResults) throws BIException {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null");
        }

        Expression expAux = new Expression();
        ExpressionCondition conditionalPart = expression.getExpConditionalPart();
        double firstAttribute = expAux.calculaExpressao(conditionalPart.getFirstAttribute(), cachedResults);
        double secondAttribute = expAux.calculaExpressao(conditionalPart.getSecondAttribute(), cachedResults);
        Operator conditionalOperator = conditionalPart.getOperator();

        boolean validCondition = checkCondition(conditionalOperator, firstAttribute, secondAttribute);

        Expression relevantExpression = validCondition ? expression.getExpPartTrue() : expression.getExpPartFalse();
        return expAux.calculaExpressao(relevantExpression, cachedResults);
    }

    private static boolean checkCondition(Operator operator, double firstAttribute, double secondAttribute) {
        return switch (operator.getSymbol()) {
            case ">" -> firstAttribute > secondAttribute;
            case ">=" -> firstAttribute >= secondAttribute;
            case "<" -> firstAttribute < secondAttribute;
            case "<=" -> firstAttribute <= secondAttribute;
            case "=", "==" -> firstAttribute == secondAttribute;
            case "<>", "!=" -> firstAttribute != secondAttribute;
            default -> false; // Or throw an exception for unknown operators
        };
    }

    private static void processArithmeticOperator(List<Object> expressionSlice, StringBuilder constantString, char ch) {
        if (!constantString.isEmpty()) {
            expressionSlice.add(constantString.toString());
            constantString.setLength(0);
        }
        expressionSlice.add(new ArithmeticOperator(String.valueOf(ch)));
    }

    private static int processFieldCode(String expression, int i, List<Object> expressionSlice, Indicator indicator) {
        if (i + 1 < expression.length() && expression.charAt(i + 1) == '$') {
            int endIndex = expression.indexOf(Constants.END_EXPRESSION, i);
            if (endIndex != -1) {
                String fieldCode = expression.substring(i + 2, endIndex);
                expressionSlice.add(indicator.getFieldByCode(fieldCode));
                i = endIndex + 2;
            }
        }
        return i;
    }

    private static void processDigitOrDecimal(List<Object> expressionSlice, StringBuilder constantString, char ch, int i, int length) {
        constantString.append(ch);
        if (i == length - 1) {
            expressionSlice.add(constantString.toString());
        }
    }

    private static void processSpace(List<Object> expressionSlice, StringBuilder constantString) {
        if (!constantString.isEmpty()) {
            expressionSlice.add(constantString.toString());
            constantString.setLength(0);
        }
    }

}
