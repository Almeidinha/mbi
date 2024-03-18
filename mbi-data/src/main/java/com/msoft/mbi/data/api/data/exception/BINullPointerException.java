package com.msoft.mbi.data.api.data.exception;

public class BINullPointerException extends BIException {

    private static final String	defaultError			= "10003 - Objeto com valor null";

    public BINullPointerException() {
        super(defaultError);
    }

    public BINullPointerException(Throwable t) {
        super(defaultError, t);
    }

    public String getDefaultError() {
        return defaultError;
    }

    public String getMessage() {
        return "Ocorreu um erro ao tentar acessar um metodo de uma variavel ainda nao inicializada(com valor 'null').";
    }

    public String getHTMLMessage() {
        return defaultError + "<br>Ocorreu um erro ao tentar acessar um metodo de uma variavel ainda nao inicializada(com valor 'null').";
    }
}
