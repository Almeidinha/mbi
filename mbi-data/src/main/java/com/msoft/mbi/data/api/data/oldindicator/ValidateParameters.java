package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;
import com.msoft.mbi.data.api.data.exception.BIInvalidParameterException;

import java.util.ArrayList;
import java.util.List;

public class ValidateParameters {

    private String invalidParameter = "";
    private String invalidValue = "";
    private final List<List<Object>> parameterList = new ArrayList<>();

    public ValidateParameters() {
    }

    public String getParametroInvalido() {
        return this.invalidParameter;
    }

    public String getValorInvalido() {
        return this.invalidValue;
    }

    public void add(String nome, String valor, boolean contem, String valores) throws BIException {
        try {
            List<String> list_valores = new ArrayList<String>();
            list_valores.add(valores);

            List<Object> listAux = new ArrayList<Object>();
            listAux.add(nome);
            listAux.add(valor);
            listAux.add(String.valueOf(contem));
            listAux.add(list_valores);

            this.parameterList.add(listAux);
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("validar parametro.");
            biex.setLocal(this.getClass(), "verifica_parametros()");
            throw biex;
        }
    }

    public void add(String nome, int valor, boolean contem, int valores) throws BIException {
        try {
            List<String> list_valores = new ArrayList<>();
            list_valores.add(String.valueOf(valores));

            List<Object> listAux = new ArrayList<>();
            listAux.add(nome);
            listAux.add(String.valueOf(valor));
            listAux.add(String.valueOf(contem));
            listAux.add(list_valores);

            this.parameterList.add(listAux);
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("validar parametro.");
            biex.setLocal(this.getClass(), "verifica_parametros()");
            throw biex;
        }
    }

    public void add(String nome, Object obj_valor, boolean contem, String valores) throws BIException {
        try {
            List<String> list_valores = new ArrayList<>();
            list_valores.add(valores);

            List<Object> listAux = new ArrayList<>();
            listAux.add(nome);
            listAux.add(obj_valor);
            listAux.add(String.valueOf(contem));
            listAux.add(list_valores);

            this.parameterList.add(listAux);
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("validar parametro.");
            biex.setLocal(this.getClass(), "verifica_parametros()");
            throw biex;
        }
    }

    public void add(String nome, Object obj_valor, boolean contem, List<String> list_valores) throws BIException {
        try {
            List<Object> listAux = new ArrayList<Object>();
            listAux.add(nome);
            listAux.add(obj_valor);
            listAux.add(String.valueOf(contem));
            listAux.add(list_valores);

            this.parameterList.add(listAux);
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("validar parametro.");
            biex.setLocal(this.getClass(), "verifica_parametros()");
            throw biex;
        }
    }

    public void add(String nome, String valor, boolean contem, List<String> list_valores) throws BIException {
        try {
            List<Object> listAux = new ArrayList<>();
            listAux.add(nome);
            listAux.add(valor);
            listAux.add(String.valueOf(contem));
            listAux.add(list_valores);

            this.parameterList.add(listAux);
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("validar parametro.");
            biex.setLocal(this.getClass(), "verifica_parametros()");
            throw biex;
        }
    }

    public void verifica_parametros() throws BIException {
        List<Object> listAux;
        String nome;
        String valor;
        String contem;
        List<Object> list_valores;
        for (int i = 0; i < this.parameterList.size(); i++) {
            listAux = this.parameterList.get(i);
            nome = (String) listAux.get(0);
            valor = (String) listAux.get(1);
            contem = (String) listAux.get(2);
            list_valores = (List<Object>) listAux.get(3);
            if (contem.equals("true")) {
                if (!list_valores.contains(valor)) {
                    this.invalidParameter = nome;
                    this.invalidValue = valor;
                    BIInvalidParameterException biex = new BIInvalidParameterException(this.invalidParameter,
                            this.invalidValue);
                    biex.setAction("validar parametro.");
                    biex.setLocal(this.getClass(), "verifica_parametros()");
                    throw biex;
                }
            } else {
                if (list_valores.contains(valor)) {
                    this.invalidParameter = nome;
                    this.invalidValue = valor;
                    BIInvalidParameterException biex = new BIInvalidParameterException(this.invalidParameter,
                            this.invalidValue);
                    biex.setAction("validar parametro.");
                    biex.setLocal(this.getClass(), "verifica_parametros()");
                    throw biex;
                }
            }
        }
    }
}
