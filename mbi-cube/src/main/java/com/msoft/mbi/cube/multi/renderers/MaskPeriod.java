package com.msoft.mbi.cube.multi.renderers;

public class MaskPeriod implements MaskRenderer, MaskEisDimensionDate {

    public static final String MASK_NUMBER = "n";
    public static final String MASK_PERIOD_ABBREVIATED = "nnn";
    public static final String MASK_PERIOD_NOT_ABBREVIATED = "nnnn";
    private static final String[] PERIOD_ABBREVIATED = {"Pri", "Seg", "Ter", "Qua", "Qui", "Sex"};
    private static final String[] PERIODS = {"Primeiro", "Segundo", "Terceiro", "Quarto", "Quinto", "Sexto"};
    private String maskComplement = null;
    private String[] maskNumber;
    private String mask;

    public MaskPeriod() {
    }

    public MaskPeriod(String mask) {
        this.mask = mask;
        this.resolveMask();

    }

    @Override
    public void resolveMask() {
        String mascaraNumero;
        if (mask.contains("'")) {
            mascaraNumero = mask.substring(0, mask.indexOf("'")).trim();
            this.maskComplement = mask.substring(mask.indexOf("'") + 1, mask.lastIndexOf("'"));
        } else {
            mascaraNumero = mask.trim();
        }
        if (MaskPeriod.MASK_PERIOD_ABBREVIATED.equals(mascaraNumero)) {
            this.maskNumber = MaskPeriod.PERIOD_ABBREVIATED;
        } else if (MaskPeriod.MASK_PERIOD_NOT_ABBREVIATED.equals(mascaraNumero)) {
            this.maskNumber = MaskPeriod.PERIODS;
        }

    }

    @Override
    public Object apply(Object value) {
        if (this.maskNumber == null) {
            if (this.maskComplement == null) {
                return value;
            } else {
                return (((int) Double.parseDouble(value.toString())) + this.maskComplement);
            }
        } else {
            if (this.maskComplement == null) {
                return this.maskNumber[((int) Double.parseDouble(value.toString()) - 1)];
            } else {
                return (this.maskNumber[((int) Double.parseDouble(value.toString()) - 1)] + this.maskComplement);
            }
        }
    }

    public static boolean validaMascara(String mascara) {
        if (mascara != null) {
            return mascara.contains("n");
        }
        return false;

    }

}
