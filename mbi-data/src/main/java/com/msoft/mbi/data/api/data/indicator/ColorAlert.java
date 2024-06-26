package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.util.BIMacro;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
public class ColorAlert {

    @Getter
    private Indicator indicator;
    @Getter
    private int sequence;
    @Getter
    private Field firstField;
    private String firstFieldFunction;
    @Getter
    private Operator operator;
    @Getter
    private String firstValue;
    private String secondValue;
    @Getter
    private String valueType;
    @Getter
    private Field secondField;
    private String secondFieldFunction;
    @Getter
    private String action;
    private AlertProperty alertProperty;
    @Getter
    private boolean compareToAnotherField;

    public static final String TOTALIZACAO_HORIZONTAL            = "totalizacaoHorizontal";
    public static final String MEDIA_HORIZONTAL                  = "mediaHorizontal";
    public static final String PARTICIPACAO_HORIZONTAL           = "participacaoHorizontal";
    public static final String PARTICIPACAO_ACUMULADA_HORIZONTAL = "participacaoAcumuladaHorizontal";
    public static final String ACUMULADO_HORIZONTAL              = "acumuladoHorizontal";
    public static final String ANALISE_HORIZONTAL                = "analiseHorizontal";
    public static final String TOTALIZACAO_VERTICAL              = "totalizacaoVertical";
    public static final String ANALISE_VERTICAL                  = "analiseVertical";
    public static final String PARTICIPACAO_ACUMULADA            = "participacaoAcumulada";
    public static final String ACUMULADO_VERTICAL                = "acumuladoVertical";
    public static final String TOTALIZACAO_PARCIAL               = "totalizacaoParcial";
    public static final String TOTALIZACAO                       = "totalizacao";

    public static final String SEM_FUNCAO                        = "semFuncao";

    public static final String LINHA                             = "linha";
    public static final String CELULA                            = "celula";

    public String getFirstFieldFunction() {
        if (firstFieldFunction != null) {
            firstFieldFunction = firstFieldFunction.trim();
        }
        return firstFieldFunction;
    }

    public String getSecondFieldFunction() {
        if (secondFieldFunction != null) {
            secondFieldFunction = secondFieldFunction.trim();
        }
        return secondFieldFunction;
    }

    public String getFirstDoubleValue() {
        String result = getFirstValue();
        result = result.replace('.', 'X');
        result = result.replace("X", "");
        result = result.replace(",", ".");
        return result;
    }

    public AlertProperty getAlertProperty() {
        if (alertProperty == null) {
            alertProperty = new AlertProperty();
        }
        return alertProperty;
    }

    public String toString() {
        String result;
        if (this.isCompareToAnotherField()) {
            result = this.toStringOutroCampo();
        } else {
            result = this.toStringValor();
        }
        return result;
    }

    public String toStringValor() {
        String result = "";
        if (isFinished()) {
            if (!SEM_FUNCAO.equals(this.getFirstFieldFunction())) {
                result += this.translateFunction(this.getFirstFieldFunction()) + " ";
            }
            result += this.getFirstField().getTitle() + " ";
            result += this.operator.getDescription() + " ";
            if (Operators.BETWEEN.equals(this.operator.getSymbol())) {
                result += this.firstValue;
                result += " e ";
                result += this.secondValue;
            } else {
                if (this.firstValue.indexOf('@') == 1 || firstValue.indexOf('@') == 0) {
                    String valor = this.firstValue;
                    if (this.firstValue.charAt(0) == '\'') {
                        valor = this.firstValue.substring(1, valor.length() - 1);
                    }
                    BIMacros m = new BIMacros();
                    BIMacro macro = m.getMacroById(valor.substring(0, valor.lastIndexOf('|') + 1));
                    result += macro.getDescription();
                } else {
                    result += this.firstValue;
                }
            }
        }
        return result;
    }

    public String toStringOutroCampo() {
        String result = "";
        if (isFinished()) {
            String funcaoUtilizada = "";
            if (!SEM_FUNCAO.equals(this.getFirstFieldFunction())) {
                funcaoUtilizada = this.translateFunction(this.getFirstFieldFunction()) + " ";
                result += funcaoUtilizada;
            }
            result += this.getFirstField().getTitle() + " ";
            result += this.operator.getDescription() + " ";
            result += this.firstValue;
            if ("P".equals(this.valueType)) {
                result += "% ";
            } else {
                result += " (unidade) ";
            }
            if (this.getFirstField().getFieldId() == this.getSecondField().getFieldId()) {
                result += this.translateFunction(this.getSecondFieldFunction()) + " ";
            } else {
                result += funcaoUtilizada + " ";
            }
            result += this.getSecondField().getTitle() + " ";
        }
        return result;
    }

    public boolean isFinished() {
        return this.getFirstField().getTitle() != null;
    }

    public String translateFunction(String func) {
        return switch (func.trim()) {
            case ColorAlert.TOTALIZACAO_HORIZONTAL -> "Totalização horizontal";
            case ColorAlert.MEDIA_HORIZONTAL -> "Média horizontal";
            case ColorAlert.PARTICIPACAO_HORIZONTAL -> "Participação horizontal";
            case ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL -> "Participação acumulada horizontal";
            case ColorAlert.ACUMULADO_HORIZONTAL -> "Acumulado horizontal";
            case ColorAlert.ANALISE_HORIZONTAL -> "Análise Horizontal";
            case ColorAlert.TOTALIZACAO_VERTICAL -> "Totalização vertical";
            case ColorAlert.ANALISE_VERTICAL -> "Análise vertical";
            case ColorAlert.PARTICIPACAO_ACUMULADA -> "Participação acumulada vertical";
            case ColorAlert.ACUMULADO_VERTICAL -> "Acumulado vertical";
            case ColorAlert.TOTALIZACAO_PARCIAL -> "Totalização parcial";
            case ColorAlert.TOTALIZACAO -> "Totalização";
            default -> "";
        };
    }

    public String getSecondValue() {
        return secondValue ==null ? "0" : secondValue.trim();
    }

    public String getSegundoValorDouble() {
        String result = getSecondValue();
        result = result.replace('.', 'X');
        result = result.replace("X", "");
        result = result.replace(",", ".");
        return result;
    }

    public ColorAlert copy(Indicator indicator) {
        ColorAlert result = new ColorAlert();

        List<Field> fields = indicator.getFields();
        for (Field field : fields) {
            if (field != null) {
                if (this.firstField != null && this.firstField.getFieldId() == field.getFieldId()) {
                    result.setFirstField(field);
                }
                if (this.secondField != null && this.secondField.getFieldId() == field.getFieldId()) {
                    result.setSecondField(field);
                }
            }
        }

        result.setIndicator(indicator);
        result.setSequence(this.sequence);
        result.setFirstFieldFunction(this.firstFieldFunction);
        result.setOperator(this.operator);
        result.setFirstValue(this.firstValue);
        result.setSecondValue(this.secondValue);
        result.setValueType(this.valueType);
        result.setSecondFieldFunction(this.secondFieldFunction);
        result.setAction(this.action);
        result.setCompareToAnotherField(this.compareToAnotherField);
        result.setAlertProperty(this.alertProperty);
        return result;
    }
}
