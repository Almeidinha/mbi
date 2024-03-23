package com.msoft.mbi.cube.multi.renderers;


public class MaskMonth implements MaskRenderer, MaskEisDimensionDate {

    public static final String ABBREVIATED = "MMM";
    public static final String NOT_ABBREVIATED = "MMMM";
    private static final String[] MONTH_ABBREVIATED = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
    private static final String[] MONTHS = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private String[] format;
    private final String mask;

    public MaskMonth(String mask) {
        this.mask = mask;
        this.resolveMask();
    }

    public void resolveMask() {
        if (mask != null) {
            if (mask.equals(MaskMonth.ABBREVIATED)) {
                this.format = MaskMonth.MONTH_ABBREVIATED;
            } else if (mask.equals(MaskMonth.NOT_ABBREVIATED)) {
                this.format = MaskMonth.MONTHS;
            }
        }
    }

    @Override
    public Object apply(Object valor) {
        if (valor != null && this.format != null) {
            if (valor.toString().contains(";")) {
                return valor;
            }
            return this.format[((int) Double.parseDouble(valor.toString())) - 1];
        }
        return valor;
    }

}
