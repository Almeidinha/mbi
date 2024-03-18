package com.msoft.mbi.cube.multi.renderers;

public class MascaraMesAno implements MascaraRenderer, MascaraEisDimensaoDat {

    public static final String MES_ABREVIADO = "MMM";
    public static final String MES_NAO_ABREVIADO = "MMMM";
    public static final String ANO_ABREVIADO = "yy";
    public static final String ANO_NAO_ABREVIADO = "yyyy";
    private static final String[] MES_ABREV = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
    private static final String[] MES_NAO_ABREV = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    private String[] mascaraMes;
    private String mascaraAno;
    private String separador = "";
    private int modelo;
    private String mascara;

    public MascaraMesAno(String mascara) {
        this.mascara = mascara;
        this.resolveMascara();
    }

    public void resolveMascara() {
        String mascaraMes = null;
        if (!mascara.isEmpty()) {
            String aux = mascara.substring(0, mascara.indexOf("'"));

            if (aux.equals(MES_ABREVIADO) || aux.equals(MES_NAO_ABREVIADO)) {
                mascaraMes = aux;
                this.mascaraAno = mascara.substring(mascara.lastIndexOf("'") + 1);
                this.modelo = 0;
            } else if (aux.equals("yy") || aux.equals("yyyy")) {
                this.mascaraAno = aux;
                mascaraMes = mascara.substring(mascara.lastIndexOf("'") + 1);
                this.modelo = 1;
            }
            this.separador = mascara.substring(mascara.indexOf("'") + 1, mascara.lastIndexOf("'"));
            if (MascaraMesAno.MES_ABREVIADO.equals(mascaraMes)) {
                this.mascaraMes = MascaraMesAno.MES_ABREV;
            } else {
                this.mascaraMes = MascaraMesAno.MES_NAO_ABREV;
            }
        } else {
            this.mascaraAno = null;
            this.mascaraMes = null;
            this.separador = "";
        }

    }

    private String concatena(String mes, String ano) {

        if (this.mascaraMes != null) {
            mes = this.mascaraMes[((int) Double.parseDouble(mes)) - 1];
        }
        if (this.mascaraAno != null && this.mascaraAno.equals(MascaraMesAno.ANO_ABREVIADO))
            ano = ano.substring(2);
        if (this.modelo == 0)
            return (mes + this.separador + ano);
        else
            return (ano + this.separador + mes);
    }

    @Override
    public Object aplica(Object valor) {
        String anoMes = String.valueOf(valor);
        anoMes = anoMes.trim();
        if (!anoMes.equalsIgnoreCase("")) {
            String mes = anoMes.substring(5, 7);
            String ano = anoMes.substring(0, 4);
            return this.concatena(mes, ano);
        }
        return "";
    }

    public static boolean validaMascara(String mascara) {
        if (mascara != null) {
            if ((mascara.contains(MES_ABREVIADO)) && (mascara.contains("yy")))
                return true;
        }
        return false;
    }

}
