package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BIParametrosConexaoException extends BIException {

    private static String defaultError = "Erro nos paramentros da conexão";
    private String message = "";
    public static final int ERRO_DRIVER = 1;
    public static final int ERRO_URL = 2;

    public static final int ERRO_PADRAO = 3;

    public BIParametrosConexaoException() {
        super(defaultError);
    }

    public BIParametrosConexaoException(Throwable t) {
        super(defaultError, t);
    }

    public BIParametrosConexaoException(String defaultError, String message) {
        super(defaultError);
        this.message = message;
    }

    public BIParametrosConexaoException(Throwable t, String defaultError, String message) {
        super(defaultError, t);
        this.message = message;
    }

    public BIParametrosConexaoException(String message) {
        super(defaultError);
        this.message = message;
    }

    public BIParametrosConexaoException(Throwable t, String message) {
        super(defaultError, t);
        this.message = message;
    }

    public static void setDefaultError(String defaultError) {
        BIParametrosConexaoException.defaultError = defaultError;
    }

    public String getDefaultError() {
        return defaultError;
    }

    public String getHTMLMessage() {
        String messageHtml = message;
        int i = 0;
        while ((i = messageHtml.indexOf('\n')) != -1)
            messageHtml = messageHtml.substring(0, i) + "<br>" + messageHtml.substring(i);
        return defaultError + "<br>" + message;

    }
}
