package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.DateException;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class BIData implements Comparable<Object> {

    private GregorianCalendar calendar = new GregorianCalendar();
    public static final String FORMATO_DIA_MES_ANO_TELA = "dd/MM/yyyy";
    public static final String FORMATO_MES_ANO_TELA = "MM/yyyy";
    public static final String FORMATO_MES_ANO_BANCO = "yyyyMM";
    @Getter
    @Setter
    private String formatoEntrada = FORMATO_DIA_MES_ANO_TELA;
    @Getter
    @Setter
    private String formatoSaida = FORMATO_DIA_MES_ANO_TELA;
    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = 60 * ONE_SECOND;
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_WEEK = 7 * ONE_DAY;

    public BIData() {
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        zerarHora();
    }

    public BIData(long date) {
        super();
        this.calendar.setTimeInMillis(date);
        zerarHora();
    }

    public BIData(Date date) {
        super();

        if (date == null)
            throw new NullPointerException();

        this.calendar.setTimeInMillis(date.getTime());
        zerarHora();
    }
    
    public BIData(Calendar date) {
        super();

        if (date == null)
            throw new NullPointerException();

        this.calendar.setTimeInMillis(date.getTimeInMillis());
        zerarHora();
    }

    public BIData(String date) throws DateException {
        super();
        Date data = validate(date);
        this.calendar.setTimeInMillis(data.getTime());
        zerarHora();
    }

    public BIData(String date, String formatoEntrada) throws DateException {
        super();
        this.formatoEntrada = formatoEntrada;
        Date data = validate(date);
        this.calendar.setTimeInMillis(data.getTime());
        zerarHora();
    }

    private void zerarHora() {

        this.calendar.set(Calendar.HOUR, 0);
        this.calendar.set(Calendar.AM_PM, Calendar.AM);
        this.calendar.set(Calendar.MINUTE, 0);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);
    }

    public String format() {
        return format(calendar.getTime());
    }

    private String format(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat(this.formatoSaida);
        formatador.setTimeZone(this.calendar.getTimeZone());
        return formatador.format(data);
    }

    public Date validate(String dataEntrada) throws DateException {

        Date d = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.formatoEntrada);
            d = simpleDateFormat.parse(dataEntrada);

            if (!dataEntrada.equals(simpleDateFormat.format(d))) {
                throw new DateException("Data inválida: " + dataEntrada);
            }

            return d;

        } catch (ParseException _e) {
            throw new DateException("Não foi possível converter a data de String '" + dataEntrada + "' para o tipo Data", _e);
        }
    }

    public java.sql.Date getSqlDate() {
        this.zerarHora();
        return new java.sql.Date(this.calendar.getTimeInMillis());
    }

    public BIData set(int field, int value) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.set(field, value);
        return new BIData(gc);
    }

    public TimeZone getTimeZone() {
        return this.calendar.getTimeZone();
    }

    public BIData setFirstDayOfWeek(int value) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.setFirstDayOfWeek(value);
        return new BIData(gc);
    }

    public BIData setGregorianChange(Date date) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.setGregorianChange(date);
        return new BIData(gc);
    }

    public BIData setLenient(boolean lenient) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.setLenient(lenient);
        return new BIData(gc);
    }

    public BIData setMinimalDaysInFirstWeek(int value) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.setMinimalDaysInFirstWeek(value);
        return new BIData(gc);
    }

    public BIData setTimeInMillis(long millis) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.setTimeInMillis(millis);
        return new BIData(gc);
    }

    public BIData setTimeZone(TimeZone value) {
        GregorianCalendar gc = (GregorianCalendar) calendar.clone();
        gc.setTimeZone(value);
        return new BIData(gc);
    }

    public BIData adiciona(int field, int qtd) {

        switch (field) {
            case Calendar.DAY_OF_YEAR:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.DAY_OF_WEEK:
            case Calendar.DAY_OF_MONTH:
            case Calendar.WEEK_OF_YEAR:
            case Calendar.MONTH:
            case Calendar.YEAR:
                GregorianCalendar gc = (GregorianCalendar) calendar.clone();
                gc.add(field, qtd);
                return new BIData(gc);
            default:
                throw new RuntimeException("Opção inválida para incremento de Data");
        }

    }

    public int getMaxDiasAno() {
        return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    public int getMaxDiasMes() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int diferencaDias(BIData data) {

        long diferenca = data.getSqlDate().getTime() - this.getSqlDate().getTime();
        int qtdDias = (int) (diferenca / (BIData.ONE_DAY));
        qtdDias = Math.abs(qtdDias);

        return qtdDias;
    }

    public int diferencaMeses(BIData data, boolean considerarDia) {
        int qtdMeses1 = this.get(Calendar.MONTH) + 12 * this.get(Calendar.YEAR);
        int qtdMeses2 = data.get(Calendar.MONTH) + 12 * data.get(Calendar.YEAR);
        int diferenca = qtdMeses2 - qtdMeses1;
        diferenca = Math.abs(diferenca);

        if (considerarDia) {
            BIData dataMenor = getMenor(this, data);
            BIData dataMaior = getMaior(data, this);
            if (dataMenor.get(Calendar.DAY_OF_MONTH) >= dataMaior.get(Calendar.DAY_OF_MONTH))
                diferenca--;
        }

        return diferenca;
    }

    public static BIData getMenor(BIData data1, BIData data2) {
        return (data1.isAntes(data2)) ? data1 : data2;
    }

    public static BIData getMaior(BIData data1, BIData data2) {
        return (data1.isDepois(data2)) ? data1 : data2;
    }

    public boolean isDepois(BIData data) {
        return (this.compareTo(data) > 0);
    }
    public boolean isAntes(BIData data) {
        return (this.compareTo(data) < 0);
    }

    public int get(int field) {
        return calendar.get(field);
    }

    public boolean isAnoBisexto() {
        return calendar.isLeapYear(Calendar.YEAR);
    }

    public int compareTo(Object o) {
        BIData data = (BIData) o;
        return (int) ((this.getSqlDate().getTime() - data.getSqlDate().getTime()) % Integer.MAX_VALUE);
    }

    public String toString() {
        return this.format();
    }

    public boolean equals(Object obj) {
        if (obj instanceof BIData parametro) {
            return this.getSqlDate().getTime() == parametro.getSqlDate().getTime();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (int) this.getSqlDate().getTime();
    }

    public static void main(String[] args) throws DateException {

        BIData hoje = new BIData("15/02/2006");
        System.out.println(hoje.getMaxDiasMes());

        SimpleDateFormat format = new SimpleDateFormat("MMyyyy");
        try {
            Date date = format.parse("021982");
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        BIData clonado = (BIData) super.clone();
        clonado.calendar = (GregorianCalendar) this.calendar.clone();
        return clonado;
    }
}
