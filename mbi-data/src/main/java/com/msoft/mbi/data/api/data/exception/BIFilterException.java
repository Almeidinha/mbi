package com.msoft.mbi.data.api.data.exception;

public class BIFilterException extends BIException {

    private static final String defaultError = "10030 - Error setting filters.";
    private String message;

    public static final int FILTER_NOT_SET = 1;

    public BIFilterException() {
        this(null);
    }

    public BIFilterException(String msg) {
        this(msg, null);
    }


    public BIFilterException(String message, Throwable t) {
        super(message, t);
        this.message = message;
    }

    public String getErroPadrao() {
        return BIFilterException.defaultError;
    }

    public String getMessage() {
        return this.message;
    }

    public String getHTMLMessage() {
        return "<br>" + this.message + "</br>";
    }

}
