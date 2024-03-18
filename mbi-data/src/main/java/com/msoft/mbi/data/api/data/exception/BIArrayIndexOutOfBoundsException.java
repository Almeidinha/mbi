package com.msoft.mbi.data.api.data.exception;

public class BIArrayIndexOutOfBoundsException extends BIException {

    private static final String defaultError = "10010 - Erro na posição do índice de um array";
    @SuppressWarnings("unused")
    private String exceptionMessage = "";
    @SuppressWarnings("unused")
    private String sql = "";

    public BIArrayIndexOutOfBoundsException() {
    }

    public BIArrayIndexOutOfBoundsException(Throwable t) {
        super(defaultError, t);
    }

    public String getErroPadrao() {
        return defaultError;
    }

    public String getMessage() {
        return "Ocorreu um erro ao tentar acessar um índice num array." + "\nO índice está fora dos limites do array.";
    }

    public String getHTMLMessage() {
        return defaultError + "<br>Ocorreu um erro ao tentar acessar um �ndice num array." + "<br>O índice está fora dos limites do array.";
    }
}
