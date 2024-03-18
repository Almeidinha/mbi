package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;

public class BIInvalidParameterException extends BIException {

    private static final String defaultError = "10011 - Par�metro inv�lido";
    @Getter
    private String parameter = "";
    private String value = "";

    public BIInvalidParameterException() {
        super(defaultError);
    }

    public BIInvalidParameterException(String parametro, String valor) {
        super(defaultError);
        this.parameter = parametro;
        this.value = valor;
    }

    public String getDefaultError() {
        return defaultError;
    }

    public String getMessage() {
        return "'" + value + "' nao e um valor valido para o parâmetro " + parameter + " dessa funcao.";
    }

    public String getHTMLMessage() {
        return defaultError + "<br>'" + value + "' nao e um valor valido para o parâmetro " + parameter + " dessa funcao.";
    }
}
