package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BIGeneralException extends BIException {

    private static final String defaultError = "10013 - Erro generico";
    private String message = "";

    public BIGeneralException() {
        super(defaultError);
    }

    public BIGeneralException(Throwable t) {
        super(defaultError, t);
    }

    public BIGeneralException(String defaultError, String msg) {
        super(defaultError);
        this.message = msg;
    }

    public BIGeneralException(Throwable t, String defaultError, String message) {
        super(defaultError, t);
        this.message = message;
    }

    public BIGeneralException(String message) {
        super(defaultError);
        this.message = message;
    }

    public BIGeneralException(Throwable t, String message) {
        super(defaultError, t);
        this.message = message;
    }

    public String getDefaultError() {
        return defaultError;
    }

    public String getHTMLMessage() {
        String msgHTML = message;
        int i = 0;
        while ((i = msgHTML.indexOf('\n')) != -1)
            msgHTML = msgHTML.substring(0, i) + "<br>" + msgHTML.substring(i);
        return defaultError + "<br>" + message;
    }
}
