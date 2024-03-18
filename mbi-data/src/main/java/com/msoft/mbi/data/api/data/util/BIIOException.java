package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.BIException;
import lombok.Getter;

@Getter
public class BIIOException extends BIException {

    private static final String defaultError = "10021 - Erro de leitura ou escrita em dispositivo de Entrada/Saida";
    private String message;

    public static final int ERRO_LEITURA_ARQUIVO = 1;
    public static final int ERRO_COMUNICACAO_SOCKET = 2;
    public static final int ERRO_ENVIO_DE_ARQUIVO = 3;
    public static final int ERRO_GRAVANDO_LOG = 4;
    public static final int ERRO_LEITURA_BUFFER = 5;
    public static final int ERRO_SUBIR_SERVIDOR = 6;
    public static final int ERRO_CONECTAR_CLIENTE = 7;
    public static final int ERRO_CONECTAR_SERVIDOR = 8;
    public static final int ERRO_AO_ESCREVER_NO_BUFFER = 9;

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
