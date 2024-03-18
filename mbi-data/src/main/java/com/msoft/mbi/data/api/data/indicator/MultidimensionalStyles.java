package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.htmlbuilder.EstiloHTML;

import java.util.HashMap;

public class MultidimensionalStyles {

    private static MultidimensionalStyles multidimensionalEstilos;

    public static final String             ESTILO_DESC_DIMENSAO_LINHA  = "estilo_desc_dim_linha";
    public static final String             ESTILO_VAL_DIMENSAO_LINHA   = "estilo_val_dim_linha";
    public static final String             ESTILO_DESC_METRICA_LINHA   = "estilo_desc_metr_linha";
    public static final String             ESTILO_VAL_METRICA_LINHA    = "estilo_val_metr_linha";
    public static final String             ESTILO_DESC_DIMENSAO_COLUNA = "estilo_desc_dim_coluna";
    public static final String             ESTILO_VAL_DIMENSAO_COLUNA  = "estilo_val_dim_coluna";
    public static final String             ESTILO_VAL_METRICA_COLUNA  = "estilo_val_metr_coluna";
    public static final String             ESTILO_DIMENSAO_TOTALIZACAO = "estilo_dim_totalizacao";
    public static final String             ESTILO_TOTALIZACAO_TABELA = "estilo_totalizacao_tab";

    public static MultidimensionalStyles getInstancia() {
        if (multidimensionalEstilos == null) {
            multidimensionalEstilos = new MultidimensionalStyles();
        }
        return multidimensionalEstilos;
    }

    private MultidimensionalStyles() {
        this.instanciaEstilos();
    }

    public HashMap<String, EstiloHTML> estilos;

    public HashMap<String, EstiloHTML> getEstilos() {
        if (estilos == null) {
            this.estilos = new HashMap<>();
        }
        return estilos;
    }

    public void setEstilos(HashMap<String, EstiloHTML> estilos) {
        this.estilos = estilos;
    }

    private void instanciaEstilos() {

        EstiloHTML estilo = new EstiloHTML();

        estilo.setFontColor("#FFFFFF");
        estilo.setBorder(1, "solid", "#FFFFFF");
        estilo.setBackgroundColor("#3377CC");
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontWeight("bold");
        estilo.setFontStyle("normal");

        this.getEstilos().put(ESTILO_DESC_DIMENSAO_LINHA, estilo);

        estilo = new EstiloHTML();
        estilo.setFontColor("#000080");
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontWeight("normal");
        estilo.setFontStyle("normal");
        estilo.setBackgroundColor("#A2C8E8");

        this.getEstilos().put(ESTILO_VAL_DIMENSAO_LINHA, estilo);

        estilo = new EstiloHTML();
        estilo.setFontColor("#000080");
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontWeight("bold");
        estilo.setFontStyle("normal");

        this.getEstilos().put(ESTILO_DESC_METRICA_LINHA, estilo);

        estilo = new EstiloHTML();
        estilo.setFontColor("#000080");
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontWeight("bold");
        estilo.setFontStyle("bold");
        estilo.setFontStyle("boldl");

        this.getEstilos().put(ESTILO_VAL_METRICA_LINHA, estilo);

        estilo = new EstiloHTML();
        estilo.setFontColor("#FFFFFF");
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontWeight("normal");
        estilo.setFontStyle("normal");

        this.getEstilos().put(ESTILO_DESC_DIMENSAO_COLUNA, estilo);

        estilo = new EstiloHTML();
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontColor("#000080");
        estilo.setFontStyle("normal");
        estilo.setTextDecoration("none");

        this.getEstilos().put(ESTILO_DIMENSAO_TOTALIZACAO, estilo);

        estilo = new EstiloHTML();
        estilo.setFontColor("#000080");
        estilo.setFontSize(10);
        estilo.setFontFamily("verdana");
        estilo.setFontWeight("normal");
        estilo.setFontStyle("normal");

        this.getEstilos().put(ESTILO_VAL_METRICA_COLUNA, estilo);

        estilo = new EstiloHTML();
        estilo.setBackgroundColor("#A2C8E8");
        estilo.setFontSize(10);
        estilo.setFontWeight("bold");
        estilo.setFontFamily("verdana");
        estilo.setFontColor("#000080");
        estilo.setFontStyle("normal");
        estilo.setTextDecoration("none");

        this.getEstilos().put(ESTILO_TOTALIZACAO_TABELA, estilo);
    }
}
