package com.msoft.mbi.cube.multi.dimension.comparator;

import java.io.Serial;
import java.util.Iterator;
import java.util.List;

import com.msoft.mbi.cube.multi.LinhaMetrica;
import com.msoft.mbi.cube.multi.MapaMetricas;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.metrics.OrdenacaoMetrica;

public class DimensaoMetricaComparator extends DimensaoComparator {

    @Serial
    private static final long serialVersionUID = -8895106282059813571L;

    private MapaMetricas mapaMetricasCubo;
    private List<OrdenacaoMetrica> ordenacoesMetrica;

    public DimensaoMetricaComparator(MapaMetricas mapaMetricas, List<Dimension> dimensoesColuna, List<OrdenacaoMetrica> metricasOrdenadas) {
        this.mapaMetricasCubo = mapaMetricas;
        this.ordenacoesMetrica = metricasOrdenadas;
    }

    private int comparaMetricas(int retorno, LinhaMetrica linhaMetricaDimensao1, LinhaMetrica linhaMetricaDimensao2, OrdenacaoMetrica ordenacaoMetrica) {
        Double valorDimensao1 = ordenacaoMetrica.calculaValorOrdenacao(this.mapaMetricasCubo, linhaMetricaDimensao1);
        Double valorDimensao2 = ordenacaoMetrica.calculaValorOrdenacao(this.mapaMetricasCubo, linhaMetricaDimensao2);
        int ordem = ordenacaoMetrica.getTipoOrdenacao();
        if (valorDimensao1 == null) {
            if (valorDimensao2 == null) {
                retorno = 0;
            } else {
                retorno = -1 * ordem;
            }
        } else if (valorDimensao2 == null) {
            retorno = ordem;
        } else {
            retorno = valorDimensao1.compareTo(valorDimensao2) * ordem;
        }
        return retorno;
    }

    @Override
    public int compare(Comparable<Dimension> o1, Comparable<Dimension> o2) {
        Dimension dimensionLinha1 = (Dimension) o1;
        Dimension dimensionLinha2 = (Dimension) o2;
        int retorno = 0;
        Iterator<OrdenacaoMetrica> iMetricasOrdenadas = this.ordenacoesMetrica.iterator();
        while (retorno == 0 && iMetricasOrdenadas.hasNext()) {
            OrdenacaoMetrica ordenacaoMetrica = iMetricasOrdenadas.next();
            LinhaMetrica linhaMetricaDimensao1 = null;
            LinhaMetrica linhaMetricaDimensao2 = null;
            List<Dimension> lDimensoesColuna = ordenacaoMetrica.getDimensoesColunaUtilizar(dimensionLinha1.getCube());
            Iterator<Dimension> iDimensoesColuna = lDimensoesColuna.iterator();
            if (lDimensoesColuna.size() > 0) {
                while (retorno == 0 && iDimensoesColuna.hasNext()) {
                    Dimension dimensionColuna = iDimensoesColuna.next();
                    linhaMetricaDimensao1 = this.mapaMetricasCubo.getLinhaMetrica(dimensionLinha1, dimensionColuna);
                    linhaMetricaDimensao2 = this.mapaMetricasCubo.getLinhaMetrica(dimensionLinha2, dimensionColuna);
                    retorno = this.comparaMetricas(retorno, linhaMetricaDimensao1, linhaMetricaDimensao2, ordenacaoMetrica);
                }
            } else {
                linhaMetricaDimensao1 = this.mapaMetricasCubo.getLinhaMetrica(dimensionLinha1);
                linhaMetricaDimensao2 = this.mapaMetricasCubo.getLinhaMetrica(dimensionLinha2);
                retorno = this.comparaMetricas(retorno, linhaMetricaDimensao1, linhaMetricaDimensao2, ordenacaoMetrica);
            }
        }
        if (retorno == 0) {
            retorno = dimensionLinha1.getOrderValue().compareTo(dimensionLinha2.getOrderValue());
        }
        return retorno;
    }
}
