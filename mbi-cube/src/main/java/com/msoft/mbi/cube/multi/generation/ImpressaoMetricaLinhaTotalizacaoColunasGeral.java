package com.msoft.mbi.cube.multi.generation;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;

public class ImpressaoMetricaLinhaTotalizacaoColunasGeral extends ImpressaoMetricaLinha {

    private static boolean aplicarAlertasCelulaMetrica = true;

    public ImpressaoMetricaLinhaTotalizacaoColunasGeral(List<MetricaMetaData> metricas, List<String> funcoesAplicar) {
        super(metricas, CalculoSumarizacaoTipoSomaTodasMetricas.getInstance(), getFuncaoAplicar(funcoesAplicar));
    }

    private static String getFuncaoAplicar(List<String> funcoesAplicar) {
        if (funcoesAplicar.contains(MetricaMetaData.TOTALIZACAO_GERAL)) {
            aplicarAlertasCelulaMetrica = true;
            return MetricaMetaData.TOTALIZACAO_GERAL;
        } else {
            if (!funcoesAplicar.contains(MetricaMetaData.TOTALIZACAO_AH)) {
                aplicarAlertasCelulaMetrica = true;
            } else {
                aplicarAlertasCelulaMetrica = false;
            }
            return MetricaMetaData.TOTALIZACAO_AH;
        }
    }


    @Override
    public void imprimeValoresMetrica(Dimension dimensionLinha, Dimension dimensionLinhaAnterior, Dimension dimensionColuna, String propriedadeCelula, Impressor impressor, Cubo cubo, String tipoLinha) {
        this.imprimeValorMetrica(null, propriedadeCelula, dimensionLinha, dimensionLinhaAnterior, dimensionColuna, impressor, cubo, tipoLinha);
    }

    @Override
    protected List<ColorAlertConditionsMetrica> getAlertasCoresMetrica(MetricaMetaData metaData) {
        List<ColorAlertConditionsMetrica> alertas = new ArrayList<ColorAlertConditionsMetrica>();
        if (aplicarAlertasCelulaMetrica) {
            for (MetricaMetaData metaDataAux : metricas) {
                List<ColorAlertConditionsMetrica> alertasMetrica = metaDataAux.getAlertasCoresCelula(this.getFuncaoAlertaCelulaMetrica());
                if (alertasMetrica != null) {
                    alertas.addAll(metaDataAux.getAlertasCoresCelula(this.getFuncaoAlertaCelulaMetrica()));
                }
            }
        }
        return alertas;
    }

    @Override
    protected void imprimeValorMetrica(MetricaMetaData metaData, String propriedadeAplicar, Double valor, Impressor impressor) {
        int nCasasDecimais = this.getNCasasDecimais();
        impressor.imprimeValorNumero(propriedadeAplicar, valor, nCasasDecimais);
    }

    private int getNCasasDecimais() {
        int nCasasDecimais = 0;
        for (MetricaMetaData metrica : this.metricas) {
            if (metrica.getNCasasDecimais() > nCasasDecimais) {
                nCasasDecimais = metrica.getNCasasDecimais();
            }
        }
        return nCasasDecimais;
    }
}
