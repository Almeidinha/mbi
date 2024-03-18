package com.msoft.mbi.data.api.data.oldindicator;

import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;

@Setter
public class MetricDimensionRestrictions {

    private ArrayList<MetricDimensionRestriction> restrictions;

    public MetricDimensionRestrictions() {
    }

    public ArrayList<MetricDimensionRestriction> getRestrictions() {
        if (restrictions == null) {
            restrictions = new ArrayList<>();
        }
        return restrictions;
    }

    public void addRestricao(MetricDimensionRestriction restricao) {
        boolean existeRestricao = false;
        Iterator<MetricDimensionRestriction> i = this.getRestrictions().iterator();
        int indice = -1;
        while (i.hasNext()) {
            indice++;
            MetricDimensionRestriction restrAux = i.next();
            if (restrAux != null) {
                if (restricao.getMetric().getCode() == restrAux.getMetric().getCode()) {
                    existeRestricao = true;
                    break;
                }
            }
        }
        if (existeRestricao) {
            if (!restricao.getDimensions().isEmpty()) {
                this.getRestrictions().set(indice, restricao);
            } else {
                this.getRestrictions().remove(indice);
            }
        } else {
            if (!restricao.getDimensions().isEmpty()) {
                this.getRestrictions().add(restricao);
            }
        }
    }

    public MetricDimensionRestriction getRestMetricaDimensao(int codigoMetrica) {
        MetricDimensionRestriction retorno = null;
        for (MetricDimensionRestriction restAux : this.getRestrictions()) {
            if (restAux.getMetric().getCode() == codigoMetrica) {
                retorno = restAux;
                break;
            }
        }
        return retorno;
    }

    public void confirma(int indiceIndicador) {
        // TODO save ind restrictions
        //RestricaoAuxiliar.getIndicador(indiceIndicador).setRestricoesMetricaDimensao(this);
    }
}
