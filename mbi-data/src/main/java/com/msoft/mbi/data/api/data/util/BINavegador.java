package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.BIArrayIndexOutOfBoundsException;
import com.msoft.mbi.data.api.data.exception.BINullPointerException;

public class BINavegador {

    private Object[] objects;
    private int navegacao = -1;

    public BINavegador(Object[] objects) throws BINullPointerException {
        this.objects = objects;
        if (this.objects == null) {
            BINullPointerException biex = new BINullPointerException();
            biex.setAction("inicializar navegador com um array vazio.");
            biex.setLocal(this.getClass(), "temProximo()");
            throw biex;
        }
    }

    public boolean temProximo() {
        boolean retorno = false;
        for (int i = this.navegacao + 1; i < this.objects.length; i++) {
            if (this.objects[i] != null) {
                retorno = true;
                break;
            }
        }
        return retorno;
    }

    public Object proximo() throws BIArrayIndexOutOfBoundsException {
        try {
            Object object = null;
            int navegacaoAux = this.navegacao + 1;
            for (int i = 0; i < this.objects.length; i++) {
                if (this.objects[navegacaoAux] != null) {
                    break;
                }
                navegacaoAux++;
            }
            this.navegacao = navegacaoAux;
            object = this.objects[navegacaoAux];
            return object;
        } catch (ArrayIndexOutOfBoundsException aex) {
            BIArrayIndexOutOfBoundsException biex = new BIArrayIndexOutOfBoundsException(aex);
            biex.setLocal(this.getClass(), "proximo()");
            biex.setAction("buscar proximo item no array.");
            throw biex;
        }
    }

    public Object caminha(int passos) throws BIArrayIndexOutOfBoundsException {
        try {
            Object object = this.objects[this.navegacao + passos];
            this.navegacao += passos;
            return object;
        } catch (ArrayIndexOutOfBoundsException aex) {
            BIArrayIndexOutOfBoundsException biex = new BIArrayIndexOutOfBoundsException(aex);
            biex.setLocal(this.getClass(), "proximo()");
            biex.setAction("buscar item no array.");
            throw biex;
        }
    }

    public void reset() {
        this.navegacao = -1;
    }

    public int tamanho() {
        int cont = 0;
        for (int i = 0; i < this.objects.length; i++) {
            if (this.objects[i] != null) {
                cont++;
            }
        }
        return cont;
    }
}
