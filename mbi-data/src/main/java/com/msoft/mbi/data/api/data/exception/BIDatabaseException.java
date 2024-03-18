package com.msoft.mbi.data.api.data.exception;

import java.io.Serial;

public class BIDatabaseException extends BIException {

    @Serial
    private static final long serialVersionUID = 348216247881209632L;

    private static final String defaultError = "10008 - Erro durante a transação no banco";
    private String exceptionMessage = "";

    public BIDatabaseException() {
        super(defaultError);
    }

    public BIDatabaseException(Throwable t) {
        super(defaultError, t);
        this.exceptionMessage = t.getMessage();
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public String getDefaultError() {
        return defaultError;
    }

    public String getMessage() {
        return "Erro gerado pelo banco: " + exceptionMessage;
    }

    public String getHTMLMessage() {
        return defaultError + "<br>Erro gerado pelo banco: " + exceptionMessage;
    }
}
