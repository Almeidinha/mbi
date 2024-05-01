package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.DateException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

@Log4j2
public final class BIData implements Comparable<Object> {

    private GregorianCalendar calendar = new GregorianCalendar();
    public static final String DAY_MONTH_YEAR_4DF = "dd/MM/yyyy";
    public static final String DAY_MONTH_YEAR_2DF = "dd/MM/yy";
    public static final String MONTH_DAY_YEAR_4DF = "MM/dd/yyyy";
    public static final String MONTH_DAY_YEAR_2DF = "MM/dd/yy";
    public static final String YEAR_MONTH_DAY_4DF = "yyyy/MM/dd";
    public static final String YEAR_MONTH_DAY_DASH_4DF = "yyyy-MM-dd";
    public static final String DAY_MONTH_YEAR_DASH_4DF = "dd-MM-yyyy";
    public static final String YEAR_MONTH_DAY_2D_NS = "yyyyMMdd";
    public static final String MONTH_YEAR_FORMAT = "MM/yyyy";
    public static final String MONTH_YEAR_EMPTY_FORMAT = "yyyyMM";
    @Getter
    @Setter
    private String entryFormat = DAY_MONTH_YEAR_4DF;
    @Getter
    @Setter
    private String outputFormat = DAY_MONTH_YEAR_4DF;
    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = 60 * ONE_SECOND;
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_WEEK = 7 * ONE_DAY;

    public BIData() {
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        resetHour();
    }

    public BIData(long date) {
        super();
        this.calendar.setTimeInMillis(date);
        resetHour();
    }

    public BIData(Date date) {
        super();

        if (date == null)
            throw new NullPointerException();

        this.calendar.setTimeInMillis(date.getTime());
        resetHour();
    }
    
    public BIData(Calendar date) {
        super();

        if (date == null)
            throw new NullPointerException();

        this.calendar.setTimeInMillis(date.getTimeInMillis());
        resetHour();
    }

    public BIData(String date) throws DateException {
        super();
        Date data = validate(date);
        this.calendar.setTimeInMillis(data.getTime());
        resetHour();
    }

    public BIData(String date, String entryFormat)  {
        super();
        this.entryFormat = entryFormat;
        Date data;
        try {
            data = validate(date);
        } catch (DateException e) {
            throw new RuntimeException(e);
        }
        this.calendar.setTimeInMillis(data.getTime());
        resetHour();
    }

    private void resetHour() {

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
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.outputFormat);
        dateFormat.setTimeZone(this.calendar.getTimeZone());
        return dateFormat.format(data);
    }

    public Date validate(String entryDate) throws DateException {

        Date d = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.entryFormat);
            d = simpleDateFormat.parse(entryDate);

            if (!entryDate.equals(simpleDateFormat.format(d))) {
                throw new DateException("Data inválida: " + entryDate);
            }

            return d;

        } catch (ParseException _e) {
            throw new DateException("Não foi possível converter a data de String '" + entryDate + "' para o tipo Data", _e);
        }
    }

    public java.sql.Date getSqlDate() {
        this.resetHour();
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

    public BIData add(int field, int qtd) {

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

    public int daysDifference(BIData data) {

        long difference = data.getSqlDate().getTime() - this.getSqlDate().getTime();
        int amount = (int) (difference / (BIData.ONE_DAY));
        amount = Math.abs(amount);

        return amount;
    }

    public int monthDifference(BIData data, boolean includeDay) {
        int amountOne = this.get(Calendar.MONTH) + 12 * this.get(Calendar.YEAR);
        int amountTwo = data.get(Calendar.MONTH) + 12 * data.get(Calendar.YEAR);
        int difference = amountTwo - amountOne;
        difference = Math.abs(difference);

        if (includeDay) {
            BIData smallerDate = getSmaller(this, data);
            BIData largerDate = getLarger(data, this);
            if (smallerDate.get(Calendar.DAY_OF_MONTH) >= largerDate.get(Calendar.DAY_OF_MONTH))
                difference--;
        }

        return difference;
    }

    public static BIData getSmaller(BIData data1, BIData data2) {
        return (data1.isBefore(data2)) ? data1 : data2;
    }

    public static BIData getLarger(BIData data1, BIData data2) {
        return (data1.isAfter(data2)) ? data1 : data2;
    }

    public boolean isAfter(BIData data) {
        return (this.compareTo(data) > 0);
    }
    public boolean isBefore(BIData data) {
        return (this.compareTo(data) < 0);
    }

    public int get(int field) {
        return calendar.get(field);
    }

    public boolean isLeapYear() {
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
        if (obj instanceof BIData parameter) {
            return this.getSqlDate().getTime() == parameter.getSqlDate().getTime();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (int) this.getSqlDate().getTime();
    }

    public static void main(String[] args) throws DateException {

        BIData today = new BIData("15/02/2006");
        System.out.println(today.getMaxDiasMes());

        SimpleDateFormat format = new SimpleDateFormat("MMyyyy");
        try {
            Date date = format.parse("021982");
            System.out.println(date);
        } catch (ParseException e) {
            log.error("Error in BIData.main", e);
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        BIData cloned = (BIData) super.clone();
        cloned.calendar = (GregorianCalendar) this.calendar.clone();
        return cloned;
    }
}
