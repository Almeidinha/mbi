package com.msoft.mbi.data.api.data.oldindicator;

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
                if (dimAux.getCode() == codigoDimensao) {
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
            if (dimensao.getCode() == codigoUltimoNivel || codigoUltimoNivel == -1) {
                Field campoIndicador = indicador.getFieldPorCodigo(String.valueOf(dimensao.getCode()));
                if (campoIndicador != null && "S".equals(campoIndicador.getDefaultField())) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }
}
