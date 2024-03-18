package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;

@Getter
public class DateException extends Throwable {

    private String message;

    public DateException() {
        super();
    }

    public DateException(String msg) {
        super(msg);
        this.message = msg;
    }

    public DateException(String msg, Throwable t) {
        super(msg, t);
        this.message = msg;
    }

    public String getDefaultError() {
        return "Formato de data Inválido";
    }

    public String getHTMLMessage() {
        return "<br>" + this.getMessage();
    }

}
