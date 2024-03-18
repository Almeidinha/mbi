package com.msoft.mbi.cube.multi.renderers;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CellProperty implements Serializable {

    private int fontSize;
    private String fontName;
    private String fontColor;
    private String backGroundColor;
    private String alignment;
    private String borderColor;
    private boolean bold;
    private boolean italic;
    private boolean specificBorder = false;
    private int width;
    private String sFloat = null;
    private String dateMask = "";
    private Map<String, String> extraAttributes = new HashMap<>();
    public static String ALINHAMENTO_ESQUERDA = "left";
    public static String ALINHAMENTO_DIREITA = "right";
    public static String ALINHAMENTO_CENTRO = "center";
    public final static String PROPRIEDADE_CELULA_VALOR_TOTALPARCIALLINHAS = "vlrTPcl";
    public final static String PROPRIEDADE_CELULA_CABECALHO_TOTALPARCIAL = "cbcTPcl";
    public final static String PROPRIEDADE_CELULA_CABECALHO_TOTAISCOLUNAS = "cbcTCls";
    public final static String PROPRIEDADE_CELULA_TOTALGERAL = "totGrl";
    public final static String PROPRIEDADE_CELULA_CABECALHO_DIMENSAO = "cbcDim";
    public final static String PROPRIEDADE_TEXTO = "Texto";
    public final static String PROPRIEDADE_CELULA_VALOR_DIMENSAO = "vlrDim";
    public final static String PROPRIEDADE_CELULA_CABECALHO_METRICA = "cbcMet";
    public final static String PROPRIEDADE_CELULA_OUTROS = "linOtrs";
    public final static String PROPRIEDADE_CELULA_SEQUENCIA = "celSeq";
    public final static String PROPRIEDADE_CELULA_PREFIXO = "propCel";
    public final static String PROPRIEDADE_CELULA_VALOR_METRICA1 = "vlrMet1";
    public final static String PROPRIEDADE_CELULA_VALOR_METRICA2 = "vlrMet2";
    public final static String PROPRIEDADE_CELULA_DATA_METRICA1 = "datMet1";
    public final static String PROPRIEDADE_CELULA_DATA_METRICA2 = "datMet2";
    public final static String PROPRIEDADE_CELULA_ALERTAS_PREFIXO = "alrt";
    public final static String[] PROPRIEDADE_CELULA_VALOR_METRICAS = new String[]{PROPRIEDADE_CELULA_VALOR_METRICA1, PROPRIEDADE_CELULA_VALOR_METRICA2};
    public final static String PROPRIEDADE_CELULA_CABECALHO_PADRAO = "cbcPad";
    public final static String PROPRIEDADE_CELULA_CABECALHO_SEQUENCIA = "cbcPad";

    public String getSFloat() {
        return sFloat;
    }

    public void setSFloat(String float1) {
        sFloat = float1;
    }

    public void addExtraAttributes(String attribute, String value) {
        this.extraAttributes.put(attribute, value);
    }

}
