package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;

@Getter
public class BIFieldNotFoundException extends BIException {

    private static final String defaultError = "10004 - Erro ao obter campo do banco";
    private String table = "";
    private String field = "";

    public BIFieldNotFoundException() {
        super(defaultError);
    }

    public BIFieldNotFoundException(Throwable t, String tabela, String campo) {
        super(defaultError, t);
        this.table = tabela;
        this.field = campo;
    }

    public String getDefaultError() {
        return defaultError;
    }

    public String getMessage() {
        return "Nao foi possivel encontrar o campo " + field + " na tabela " + table + ".";
    }

    public String getHTMLMessage() {
        return defaultError + "<br>Nao foi possível encontrar o campo " + field + " na tabela " + table + ".";
    }
}
