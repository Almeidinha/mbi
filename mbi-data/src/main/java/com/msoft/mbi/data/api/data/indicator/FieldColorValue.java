package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FieldColorValue {

    private Indicator indicator;
    private Field field;
    private String value;
    private String color;
    private String application;

    public void incluir(FieldColorValue corValorField, ConnectionBean conexao) throws BIException {
        // TODO Add color in database
    }

    public void excluir(FieldColorValue corValorField, ConnectionBean conexao) throws BIException {
        // TODO Delete from Database
    }

    public Object clone(Indicator indicator, Field filed) {
        FieldColorValue result = new FieldColorValue();
        result.setIndicator(indicator);
        result.setField(filed);
        result.setApplication(this.application);
        result.setColor(this.color);
        result.setValue(this.value);
        return result;
    }
}
