package com.msoft.mbi.data.api.data.oldindicator;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExpressionCondition {

    private Expression firstAttribute;
    private Operator operator;
    private Expression secondAttribute;

    public ExpressionCondition() {}

    public ExpressionCondition(Expression firstAttribute, Operator operator, Expression secondAttribute) {
        this.firstAttribute = firstAttribute;
        this.operator = operator;
        this.secondAttribute = secondAttribute;
    }

}
