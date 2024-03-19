package com.msoft.mbi.cube.multi.metaData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoParticipacaoAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedAcumuladoValorAVMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedEvolucaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAHMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedParticipacaoAVMetaData;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ColorAlertMetadata implements Serializable {

    public static final int PAINT_LINE_ACTION = 1;
    public static final int PAINT_CELL_ACTION = 2;
    @Setter
    private int sequence;
    @Setter
    private int action;
    @Setter
    private String operator;
    @Setter
    private String fontColor;
    @Setter
    private String backGroundColor;
    @Setter
    private String fontStyle;
    @Setter
    private boolean bold;
    @Setter
    private boolean italic;
    @Setter
    private int fontSize;
    @Setter
    private String function;
    @Setter
    private List<Object> values;
    private String typeComparison;
    private Double referenceValue;
    private String secondFieldTitle;
    private String destineFieldFunction;
    public static final String NO_FUNCTION = "semFuncao";
    public static final String VALUE_ALERT_TYPE = "tipoAlertaValor";
    public static final String SECOND_FIELD_ALERT_TYPE = "tipoAlertaOutroCampo";
    public static final String COMPARISON_ALERT_TYPE = "V";
    public static final String PERCENT_COMPARISON_ALERT_TYPE = "P";
    private static final List<String> AH_FUNCTIONS = new ArrayList<>();

    public ColorAlertMetadata(int sequence, String operator, Object valor, int action, String function, String fontColor, String backGroundColor,
                              String fontStyle, boolean bold, boolean italic, int fontSize) {
        this(sequence, operator, new ArrayList<>(), action, fontColor, backGroundColor, fontStyle, bold, italic, fontSize, function);
        values.add(valor);
    }

    public ColorAlertMetadata(int sequence, String operator, Object valor1, Object valor2, int action, String function, String fontColor, String backGroundColor,
                              String fontStyle, boolean bold, boolean italic, int fontSize) {
        this(sequence, operator, new ArrayList<>(), action, fontColor, backGroundColor, fontStyle, bold, italic, fontSize, function);
        if (valor1 != null)
            values.add(valor1);
        if (valor2 != null)
            values.add(valor2);
    }

    public ColorAlertMetadata(int sequence, String operator, Double referenceValue, int action, String function, String fontColor, String backGroundColor,
                              String fontStyle, boolean bold, boolean italic, int fontSize, String typeComparison, String secondFieldTitle,
                              String destineFieldFunction) {
        this(sequence, operator, new ArrayList<>(), action, fontColor, backGroundColor, fontStyle, bold, italic, fontSize, function);
        this.typeComparison = typeComparison;
        this.referenceValue = referenceValue;
        this.secondFieldTitle = secondFieldTitle;
        this.destineFieldFunction = destineFieldFunction;
    }

    public ColorAlertMetadata(int sequence, String operator, Object valor1, Object valor2, String fontColor, String backGroundColor, String fontStyle,
                              boolean bold, boolean italic, int fontSize) {
        this(sequence, operator, new ArrayList<>(), PAINT_CELL_ACTION, fontColor, backGroundColor, fontStyle, bold, italic, fontSize,
                NO_FUNCTION);
        if (valor1 != null)
            values.add(valor1);
        if (valor2 != null)
            values.add(valor2);
    }

    private ColorAlertMetadata(int sequence, String operator, List<Object> values, int action, String fontColor, String backGroundColor, String fontStyle,
                               boolean bold, boolean italic, int fontSize, String function) {
        this.sequence = sequence;
        this.action = action;
        this.operator = operator;
        this.values = values;
        this.fontColor = fontColor;
        this.backGroundColor = backGroundColor;
        this.fontStyle = fontStyle;
        this.bold = bold;
        this.italic = italic;
        this.fontSize = fontSize;
        this.function = function;
        AH_FUNCTIONS.add(MetricMetaData.TOTAL_AH);
        AH_FUNCTIONS.add(MetricMetaData.MEDIA_AH);
        AH_FUNCTIONS.add(MetricMetaData.ACCUMULATED_VALUE_AH);
    }

    public boolean isRelativeFieldFunction() {
        return this.function.equals(MetricCalculatedParticipacaoAVMetaData.AV)
                || this.function.equals(MetricCalculatedAcumuladoParticipacaoAVMetaData.PARTICIPACAO_ACUMULADA_AV)
                || this.function.equals(MetricCalculatedAcumuladoValorAVMetaData.VALOR_ACUMULADO_AV)
                || this.function.equals(MetricCalculatedAcumuladoParticipacaoAHMetaData.PARTICIPACAO_ACUMULADA_AH)
                || this.function.equals(MetricCalculatedParticipacaoAHMetaData.PARTICIPACAO_AH)
                || this.function.equals(MetricCalculatedEvolucaoAHMetaData.AH);
    }

    public static List<String> getHorizaontalToalFunctionList() {

        return new ArrayList<>(AH_FUNCTIONS);
    }

}
