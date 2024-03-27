package com.msoft.mbi.data.api.data.exception;

import lombok.Getter;

@Getter
public class BIIOException extends BIException {

    private static final String defaultError = "10021 - Erro de leitura ou escrita em dispositivo de Entrada/Saida";
    private String message;

    public static final int FILE_READING_ERROR = 1;
    public static final int COMMUNICATION_SOCKET_ERROR = 2;
    public static final int FILE_SENDING_ERROR = 3;
    public static final int LOG_RECORD_ERROR = 4;
    public static final int BUFFER_READ_ERROR = 5;
    public static final int SERVER_START_ERROR = 6;
    public static final int CLIENT_CONNECT_ERROR = 7;
    public static final int SERVER_CONNECT_ERROR = 8;
    public static final int BUFFER_WRITE_ERROR = 9;

    public BIIOException() {
        super();
    }

    public BIIOException(String msg) {
        this(msg, null);
    }

    public BIIOException(Exception e) {
        super(e);
    }

    public BIIOException(String msg, Throwable t) {
        super(msg, t);
        this.message = msg;
    }

    public String getDefaultError() {
        return BIIOException.defaultError;
    }

    public String getHTMLMessage() {
        return "<br>" + this.message;
    }

}
