package com.msoft.mbi.data.api.data.indicator;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MetricDimensionRestriction {

    private Field metric;
    private List<Field> dimensions;

    public MetricDimensionRestriction(Field metrica) {
        this.metric = metrica;
    }

    public void addDimensao(Field dimensao) {
        this.getDimensions().add(dimensao);
    }

    public boolean isDimensaoRestrita(int codigoDimensao) {
        boolean retorno = false;
        for (Field dimAux : this.getDimensions()) {
            if (dimAux != null) {
                if (dimAux.getFieldId() == codigoDimensao) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }

    public boolean isMetricaRestrita(Indicator indicador, int codigoUltimoNivel) {
        boolean retorno = false;
        for (Field dimensao : this.getDimensions()) {
            if (dimensao.getFieldId() == codigoUltimoNivel || codigoUltimoNivel == -1) {
                Field campoIndicador = indicador.getFieldByCode(String.valueOf(dimensao.getFieldId()));
                if (campoIndicador != null && "S".equals(campoIndicador.getDefaultField())) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }
}
