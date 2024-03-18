package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.DateException;

import java.util.*;

public class BIDate extends GregorianCalendar {

    public BIDate() {
        super();
    }

    public BIDate(String dataString) throws DateException {
        this();
        try {
            StringTokenizer s = new StringTokenizer(dataString);
            String dia = s.nextToken("/");
            String mes = s.nextToken("/");
            String ano = s.nextToken("/");
            BIDate dataAux = new BIDate(Integer.parseInt(dia), Integer.parseInt(mes), Integer.parseInt(ano));
            setTimeInMillis(dataAux.getTimeInMillis());
        } catch (NoSuchElementException n) {
            throw new DateException("Formato de data Inválida", n);
        }
    }

    public BIDate(TimeZone arg0) {
        super(arg0);
    }

    public BIDate(Locale arg0) {
        super(arg0);
    }

    public BIDate(TimeZone arg0, Locale arg1) {
        super(arg0, arg1);
    }

    public BIDate(int dia, int mes, int ano) {
        super(ano, mes - 1, dia);
    }

    public BIDate(int arg0, int arg1, int arg2, int arg3, int arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    public BIDate(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        super(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public int getMes() {
        return this.get(MONTH) + 1;
    }

    public String toString() {
        String retorno = "";
        if (get(DAY_OF_MONTH) < 10) {
            retorno += "0";
        }
        retorno += get(DAY_OF_MONTH) + "/";
        if (getMes() < 10) {
            retorno += "0";
        }
        return retorno + getMes() + "/" + get(YEAR);
    }

    public static void main(String[] args) {
        BIDate data = new BIDate(1, 1, 2003);
        System.out.println(data);
    }
}
