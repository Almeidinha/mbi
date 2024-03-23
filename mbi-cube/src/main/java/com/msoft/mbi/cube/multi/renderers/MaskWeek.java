package com.msoft.mbi.cube.multi.renderers;

public class MaskWeek implements MaskRenderer, MaskEisDimensionDate {


    public static final String ABBREVIATED = "sss";
    public static final String NOT_ABBREVIATED = "ssss";
    private static final String[] WEEKS_DAYS_ABBREVIATED = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};
    private static final String[] WEEKS_DAYS = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
    private String[] masks;
    private final String mask;

    public MaskWeek(String mask) {
        this.mask = mask;
        this.resolveMask();
    }

    @Override
    public void resolveMask() {
        if (mask != null) {
            if (mask.equals(MaskWeek.ABBREVIATED)) {
                this.masks = MaskWeek.WEEKS_DAYS_ABBREVIATED;
            } else if (mask.equals(MaskWeek.NOT_ABBREVIATED)) {
                this.masks = MaskWeek.WEEKS_DAYS;
            }
        }
    }

    @Override
    public Object apply(Object valor) {
        if (this.masks != null) {
            return this.masks[((int) Double.parseDouble(valor.toString())) - 1];
        }
        return valor;
    }

}
