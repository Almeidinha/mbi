package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.indicator.Expression;
import com.msoft.mbi.data.api.data.indicator.ExpressionCondition;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConditionalExpression {

    private ExpressionCondition expConditionalPart;
    private Expression expPartTrue;
    private Expression expPartFalse;


    public ConditionalExpression() {
    }

    public ConditionalExpression(ExpressionCondition expConditionalPart, Expression expPartTrue, Expression expPartFalse) {
        this.expConditionalPart = expConditionalPart;
        this.expPartTrue = expPartTrue;
        this.expPartFalse = expPartFalse;
    }


    public static int getConditionalOperatorInitialIndex(String condition) {
        if (condition.indexOf("=") > 0) {
            if (condition.charAt(condition.indexOf("=") - 1) == '!' && condition.charAt(condition.indexOf("=") - 2) != '$') {
                return condition.indexOf("!=");
            } else if (condition.charAt(condition.indexOf("=") - 1) == '>') {
                return condition.indexOf(">=");
            } else if (condition.charAt(condition.indexOf("=") - 1) == '<') {
                return condition.indexOf("<=");
            } else if (condition.charAt(condition.indexOf("=") - 1) == '=') {
                return condition.indexOf("==");
            } else if (condition.charAt(condition.indexOf("=") + 1) == '=') {
                return condition.indexOf("==");
            } else {
                return condition.indexOf("=");
            }
        } else if (condition.indexOf("<") > 0) {
            if (condition.charAt(condition.indexOf("<") + 1) == '>') {
                return condition.indexOf("<>");
            } else {
                return condition.indexOf("<");
            }
        } else if (condition.indexOf(">") > 0) {
            if (condition.charAt(condition.indexOf(">") - 1) == '<') {
                return condition.indexOf("<>");
            } else {
                return condition.indexOf(">");
            }
        }
        return -1;
    }

    public static int getConditionalOperatorFinalIndex(String condicao) {
        if (condicao.indexOf("=") > 0) {
            if (condicao.charAt(condicao.indexOf("=") - 1) == '!' && condicao.charAt(condicao.indexOf("=") - 2) == '$') {
                return condicao.indexOf("!=") + 2;
            } else if (condicao.charAt(condicao.indexOf("=") - 1) == '>') {
                return condicao.indexOf(">=") + 2;
            } else if (condicao.charAt(condicao.indexOf("=") - 1) == '<') {
                return condicao.indexOf("<=") + 2;
            } else if (condicao.charAt(condicao.indexOf("=") - 1) == '=') {
                return condicao.indexOf("==") + 2;
            } else if (condicao.charAt(condicao.indexOf("=") + 1) == '=') {
                return condicao.indexOf("==") + 2;
            } else {
                return condicao.indexOf("=") + 1;
            }
        } else if (condicao.indexOf("<") > 0) {
            if (condicao.charAt(condicao.indexOf("<") + 1) == '>') {
                return condicao.indexOf("<>") + 2;
            } else {
                return condicao.indexOf("<") + 1;
            }
        } else if (condicao.indexOf(">") > 0) {
            if (condicao.charAt(condicao.indexOf(">") - 1) == '<') {
                return condicao.indexOf("<>") + 2;
            } else {
                return condicao.indexOf(">") + 1;
            }
        }

        return -1;
    }

    public int getQuantidadeCamposExpressao() {
        int retorno = 0;
        if (this.expPartFalse != null) {
            retorno += this.expPartFalse.getFieldsExpressionAmount();
        }
        if (this.expPartTrue != null) {
            retorno += this.expPartTrue.getFieldsExpressionAmount();
        }
        return retorno;
    }
}
