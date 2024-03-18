package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;

public class BISQLException extends BIException {

    private static final String defaultError = "10001 - Erro durante um comando SQL";
    private String exceptionMessage = "";
    @Getter
    private String sql = "";

    public static final int ERRO_CONSULTA = 10;
    public static final int ERRO_INSERT = 11;
    public static final int ERRO_DELETE = 12;
    public static final int ERRO_UPDATE = 13;

    /**
     * Construtor
     */
    public BISQLException() {
        super(defaultError);
    }

    public BISQLException(Throwable t, String sql) {
        super(defaultError, t);
        this.exceptionMessage = t.getMessage();
        this.sql = sql;
    }


    public String getMessage() {
        return "Erro gerado pelo banco: " + exceptionMessage + "\nComando SQL: " + sql;
    }

    public String getHTMLMessage() {
        return defaultError + "<br>Erro gerado pelo banco: " + exceptionMessage + "<br>Comando SQL: " + sql;
    }
}
