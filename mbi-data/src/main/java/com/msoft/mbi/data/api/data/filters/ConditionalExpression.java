package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.indicator.Expression;
import com.msoft.mbi.data.api.data.indicator.ExpressionCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class ConditionalExpression {

    private ExpressionCondition expConditionalPart;
    private Expression expPartTrue;
    private Expression expPartFalse;
    private static final int NOT_FOUND = -1;

    public ConditionalExpression() {
    }

    public ConditionalExpression(ExpressionCondition expConditionalPart, Expression expPartTrue, Expression expPartFalse) {
        this.expConditionalPart = expConditionalPart;
        this.expPartTrue = expPartTrue;
        this.expPartFalse = expPartFalse;
    }

    public static int getConditionalOperatorInitialIndex(String condition) {
        int equalIndex = condition.indexOf("=");
        if (equalIndex > 0) {
            return getOperatorIndexBasedOnEqual(condition, equalIndex);
        }
        int lessThanIndex = condition.indexOf("<");
        if (lessThanIndex > 0) {
            return condition.charAt(lessThanIndex + 1) == '>' ? condition.indexOf("<>") : lessThanIndex;
        }
        int greaterThanIndex = condition.indexOf(">");
        if (greaterThanIndex > 0) {
            return condition.charAt(greaterThanIndex - 1) == '<' ? condition.indexOf("<>") : greaterThanIndex;
        }
        return NOT_FOUND;
    }

    private static int getOperatorIndexBasedOnEqual(String condition, int equalIndex) {
        if (condition.charAt(equalIndex - 1) == '!' && condition.charAt(equalIndex - 2) != '$') {
            return condition.indexOf("!=");
        } else if (condition.charAt(equalIndex - 1) == '>') {
            return condition.indexOf(">=");
        } else if (condition.charAt(equalIndex - 1) == '<') {
            return condition.indexOf("<=");
        } else if (condition.charAt(equalIndex - 1) == '=' || condition.charAt(equalIndex + 1) == '=') {
            return condition.indexOf("==");
        } else {
            return equalIndex;
        }
    }

    public static int getConditionalOperatorFinalIndex(String condition) {
        Map<String, Integer> operatorLengths = new HashMap<>();
        operatorLengths.put("!=", 2);
        operatorLengths.put(">=", 2);
        operatorLengths.put("<=", 2);
        operatorLengths.put("==", 2);
        operatorLengths.put("<>", 2);
        operatorLengths.put("=", 1);
        operatorLengths.put("<", 1);
        operatorLengths.put(">", 1);

        for (Map.Entry<String, Integer> entry : operatorLengths.entrySet()) {
            String operator = entry.getKey();
            int index = condition.indexOf(operator);
            if (index > 0) {
                return index + entry.getValue();
            }
        }

        return -1;
    }

    public int getExpressionFieldsCount() {
        int count = 0;
        if (this.expPartFalse != null) {
            count += this.expPartFalse.getFieldsExpressionAmount();
        }
        if (this.expPartTrue != null) {
            count += this.expPartTrue.getFieldsExpressionAmount();
        }
        return count;
    }
}
