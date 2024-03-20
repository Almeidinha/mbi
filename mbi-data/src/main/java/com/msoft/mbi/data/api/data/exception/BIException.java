package com.msoft.mbi.data.api.data.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintWriter;

@Getter
@Setter

public class BIException extends Exception {

    @Builder
    public BIException(String message, String local, String action) {
        super(message);
        this.local = local;
        this.action = action;
    }

    private Throwable throwable;
    private String local = "";
    private String action = "";
    private String type = "ERORR";
    private int errorCode;

    public BIException() {
    }

    public BIException(String msg) {
        super(msg);
    }

    public BIException(String msg, Throwable t) {
        super(msg, t);
        this.throwable = t;
    }

    public BIException(Throwable t) {
        super(t);
        this.throwable = t;
    }

    public void printStackTrace() {
        super.printStackTrace();
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
    }

    public void setLocal(Class<?> classe, String metodo) {
        String pack = "";
        if (classe.getPackage() != null)
            pack = classe.getPackage().getName();
        if (!pack.isEmpty())
            setLocal(pack + "." + classe.getName(), metodo);
        else
            setLocal(classe.getName(), metodo);
    }

    public void setLocal(String classe, String metodo) {
        this.local = classe + " -> " + metodo;
    }

}
