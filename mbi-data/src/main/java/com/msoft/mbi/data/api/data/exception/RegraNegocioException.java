package com.msoft.mbi.data.api.data.exception;

import java.rmi.RemoteException;

public class RegraNegocioException extends RemoteException {

    private String message;

    public RegraNegocioException() {
        super();
    }

    public RegraNegocioException(String message) {
        super(message);
        this.message = message;
    }

    public RegraNegocioException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public RegraNegocioException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.message = cause.getMessage();
    }

    /** @see java.lang.Throwable#getMessage() */
    public String getMessage() {
        return this.message;
    }
}
