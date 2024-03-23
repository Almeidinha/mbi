package com.msoft.mbi.cube.multi.renderers;

public class MaskMonthYear implements MaskRenderer, MaskEisDimensionDate {

    public static final String MONTH_ABBREVIATED = "MMM";
    public static final String MONTH_NOT_ABBREVIATED = "MMMM";
    public static final String YEAR_ABBREVIATED = "yy";
    public static final String YEAR_NOT_ABBREVIATED = "yyyy";
    private static final String[] MONTH_NAME_ABBREVIATED = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
    private static final String[] MONT_NAME_NOT_ABBREVIATED = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private String[] maskMonth;
    private String maskYear;
    private String separator = "";
    private int model;
    private final String mask;

    public MaskMonthYear(String mask) {
        this.mask = mask;
        this.resolveMask();
    }

    public void resolveMask() {
        String mascaraMes = null;
        if (!mask.isEmpty()) {
            String aux = mask.substring(0, mask.indexOf("'"));

            if (aux.equals(MONTH_ABBREVIATED) || aux.equals(MONTH_NOT_ABBREVIATED)) {
                mascaraMes = aux;
                this.maskYear = mask.substring(mask.lastIndexOf("'") + 1);
                this.model = 0;
            } else if (aux.equals("yy") || aux.equals("yyyy")) {
                this.maskYear = aux;
                mascaraMes = mask.substring(mask.lastIndexOf("'") + 1);
                this.model = 1;
            }
            this.separator = mask.substring(mask.indexOf("'") + 1, mask.lastIndexOf("'"));
            if (MaskMonthYear.MONTH_ABBREVIATED.equals(mascaraMes)) {
                this.maskMonth = MaskMonthYear.MONTH_NAME_ABBREVIATED;
            } else {
                this.maskMonth = MaskMonthYear.MONT_NAME_NOT_ABBREVIATED;
            }
        } else {
            this.maskYear = null;
            this.maskMonth = null;
            this.separator = "";
        }

    }

    private String concatMonthYear(String month, String year) {

        if (this.maskMonth != null) {
            month = this.maskMonth[((int) Double.parseDouble(month)) - 1];
        }
        if (this.maskYear != null && this.maskYear.equals(MaskMonthYear.YEAR_ABBREVIATED))
            year = year.substring(2);
        if (this.model == 0)
            return (month + this.separator + year);
        else
            return (year + this.separator + month);
    }

    @Override
    public Object apply(Object valor) {
        String anoMes = String.valueOf(valor);
        anoMes = anoMes.trim();
        if (!anoMes.equalsIgnoreCase("")) {
            String mes = anoMes.substring(5, 7);
            String ano = anoMes.substring(0, 4);
            return this.concatMonthYear(mes, ano);
        }
        return "";
    }

    public static boolean validaMascara(String mascara) {
        if (mascara != null) {
            return (mascara.contains(MONTH_ABBREVIATED)) && (mascara.contains("yy"));
        }
        return false;
    }

}
