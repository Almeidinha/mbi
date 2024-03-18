package com.msoft.mbi.cube.multi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionLinha;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.resumeFunctions.FunctionRanking;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.CuboMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.metrics.OrdenacaoMetrica;
import com.msoft.mbi.cube.multi.metrics.additive.MetricaAditivaMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaFuncaoMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaMetaData;
import com.msoft.mbi.cube.util.CubeListener;
import com.msoft.mbi.cube.util.DefaultCubeListener;
import lombok.Getter;
import lombok.Setter;

public abstract class Cubo extends Dimension {

    @Getter
	protected List<ColunaMetaData> colunasVisualizadas = new ArrayList<>();
    @Getter
	private final List<DimensaoMetaData> hierarquiaLinha = new ArrayList<>();
    @Getter
	private DimensaoMetaData lastMetaDataLinha = null;
    @Getter
	private final List<DimensaoMetaData> hierarquiaColuna = new ArrayList<>();
    private DimensaoMetaData lastMetaDataColuna = null;
    @Getter
	private final List<MetricaAditivaMetaData> hierarquiaMetricaAditiva = new ArrayList<>();
    @Setter
	@Getter
	private List<MetricaCalculadaMetaData> hierarquiaMetricaCalculada = new ArrayList<>();
    @Getter
	private final List<MetricaMetaData> hierarquiaMetrica = new ArrayList<>();
    @Getter
	protected MapaMetricas mapaMetricas = null;
    private transient List<Dimension> dimensoesUltimoNivelColuna = null;
    protected transient List<Dimension> dimensoesUltimoNivelLinha = null;
    protected MetricFilters filtrosMetrica;
    protected MetricFiltersAccumulatedValue filtrosMetricaAcumulado;
    @Getter
	private final List<Dimension> poolDimensoes = new ArrayList<>();
    protected List<MetricaMetaData> metricasTotalizaHorizontal = new ArrayList<>();
    @Setter
    @Getter
    private CubeListener cubeListener = new DefaultCubeListener();

    protected Cubo(CuboMetaData metaData) {
        super(metaData);
        super.getMetaData().setCubo(this);
        super.cube = this;
        this.mapaMetricas = new MapaMetricas(this);
        this.lastMetaDataLinha = metaData;
    }

    public static Cubo factoryCuboFormatoMultiDimensional(CuboMetaData metaData) {
        CuboFormatoMultiDimensional cubo = new CuboFormatoMultiDimensional(metaData);
        cubo.factory();
        cubo.dimensionsLine = new Dimensions();
        cubo.dimensionsColumn = new Dimensions();
        return cubo;
    }

    public static Cubo factoryCuboFormatoPadrao(CuboMetaData metaData) {
        CubeDefaultFormat cubo = new CubeDefaultFormat(metaData);
        cubo.factory();
        cubo.dimensionsLine = new Dimensions();
        cubo.dimensionsColumn = new Dimensions();
        return cubo;
    }

	public void addDimensaoPool(Dimension dimension) {
        this.poolDimensoes.add(dimension);
        if (dimension.getMetaData().isUltima()) {
            Dimension.increaseTotalSize(dimension);
        }
    }

	@Override
    public Dimensions getDimensoesAbaixo() {
        return this.dimensionsLine;
    }

	@Override
    public CuboMetaData getMetaData() {
        return (CuboMetaData) this.metaData;
    }

    @Override
    public void setKeyValue() {
        this.keyValue = BRANCO;
    }

    public List<MetricaMetaData> getMetricasTotalizacaoHorizontal() {
        return this.metricasTotalizaHorizontal;
    }

	public List<Dimension> getDimensoesUltimoNivelColuna() {
        if (this.dimensoesUltimoNivelColuna == null) {
            this.dimensoesUltimoNivelColuna = new ArrayList<Dimension>();
            this.geraListaUltimoNivel(this.dimensionsColumn.values(), this.dimensoesUltimoNivelColuna);
        }
        return dimensoesUltimoNivelColuna;
    }

    public List<Dimension> getDimensoesUltimoNivelLinha() {
        if (this.dimensoesUltimoNivelLinha == null) {
            this.dimensoesUltimoNivelLinha = new ArrayList<Dimension>();
            this.geraListaUltimoNivel(this.dimensionsLine.values(), this.dimensoesUltimoNivelLinha);
        }
        return dimensoesUltimoNivelLinha;
    }

    protected boolean isMetricaUtilizadaEmCalculo(CampoMetaData campoMetaData) {
        List<CampoMetaData> camposMetrica = this.getMetaData().getCamposMetrica();
        for (CampoMetaData campoMetrica : camposMetrica) {
            if (isMetricaPresente(campoMetrica.getNomeCampo(), campoMetaData)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMetricaUtilizadaEmFiltrosMetrica(CampoMetaData campoMetaData) {
        String expressaoFiltro = this.getMetaData().getExpressaoFiltrosMetrica();
        String expressaoFiltroAcumulado = this.getMetaData().getExpressaoFiltrosAcumulado();
        return this.isMetricaPresente(expressaoFiltro, campoMetaData) || this.isMetricaPresente(expressaoFiltroAcumulado, campoMetaData);
    }

    private boolean isMetricaPresente(String expressao, CampoMetaData metrica) {
        if (expressao.contains("[")) {
            StringTokenizer tokenizer = new StringTokenizer(expressao, "]");
            while (tokenizer.hasMoreElements()) {
                String parte = tokenizer.nextToken();
                parte = parte.substring(parte.indexOf("[") + 1);
                if (metrica.getTituloCampo().equalsIgnoreCase(parte)) {
                    return true;
                }
            }
        } else {
            String prefixoChaveCampo = "\\#\\$";
            String prefixoChaveCampoAcum = "(acum\\()*";
            String posfixoChaveCampo = "\\$\\!";
            String posfixoChaveCampoAcum = "\\)*";
            Pattern p = Pattern.compile(prefixoChaveCampo + prefixoChaveCampoAcum + "\\d+" + posfixoChaveCampoAcum + posfixoChaveCampo);
            Matcher m = p.matcher(expressao);
            while (m.find()) {
                String chaveCampo = m.group();
                chaveCampo = chaveCampo.substring(2, chaveCampo.length() - 2);
                if (metrica.getCampo() == Integer.parseInt(chaveCampo)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getStatusVisualizacaoMetrica(CampoMetaData campoMetrica) {
        String visualizacao = campoMetrica.getPadrao();
        if ("N".equals(visualizacao)) {
            if (this.isMetricaUtilizadaEmCalculo(campoMetrica) || this.isMetricaUtilizadaEmFiltrosMetrica(campoMetrica)) {
                visualizacao = CampoMetaData.METRICA_VISUALIZACAO_RESTRITA;
            } else {
                visualizacao = CampoMetaData.METRICA_NAO_ADICIONADA;
            }
        }
        return visualizacao;
    }

    protected String converteExpressaoCondicional(String expressaoOriginal) {
        expressaoOriginal = converteExpressaoCodigoCamposParaTitulos(expressaoOriginal);
        expressaoOriginal = expressaoOriginal.replaceAll("[Ss][Ee][(]", "IF(");
        expressaoOriginal = expressaoOriginal.replaceAll(";", ",");
        return expressaoOriginal;
    }

    private String converteExpressaoCodigoCamposParaTitulos(String expressaoOriginal) {
        String prefixoChaveCampo = "\\#\\$";
        String prefixoChaveCampoAcum = "(acum\\()*";
        String posfixoChaveCampo = "\\$\\!";
        String posfixoChaveCampoAcum = "\\)*";
        Pattern p = Pattern.compile(prefixoChaveCampo + prefixoChaveCampoAcum + "\\d+" + posfixoChaveCampoAcum + posfixoChaveCampo);
        Matcher m = p.matcher(expressaoOriginal);
        while (m.find()) {
            String chaveCampo = m.group();
            chaveCampo = chaveCampo.substring(2, chaveCampo.length() - 2);

            String expRegChaveCampo = prefixoChaveCampo + chaveCampo + posfixoChaveCampo;
            expRegChaveCampo = expRegChaveCampo.replaceAll("\\(", "\\\\(");
            expRegChaveCampo = expRegChaveCampo.replaceAll("\\)", "\\\\)");

            Pattern pChaveCampo = Pattern.compile("\\d+");
            Matcher mChaveCampo = pChaveCampo.matcher(chaveCampo);
            CampoMetaData campoAux = null;
            if (mChaveCampo.find()) {
                String codCampo = mChaveCampo.group();
                campoAux = this.getMetaData().getCampoMetricaByCodigo(Integer.parseInt(codCampo));
                String tituloCampo = campoAux.getTituloCampo().replaceAll("\\$", "###");
                tituloCampo = chaveCampo.replaceAll(codCampo, tituloCampo);
                expressaoOriginal = expressaoOriginal.replaceAll(expRegChaveCampo, "[" + tituloCampo + "]");
                expressaoOriginal = expressaoOriginal.replaceAll("###", "\\$");
            }
        }
        return expressaoOriginal;
    }

    private String converteExpressaoFiltroMetrica(String expressaoFiltro) {
        expressaoFiltro = converteExpressaoCodigoCamposParaTitulos(expressaoFiltro);
        Pattern p = Pattern.compile("\\[([\\s]?[\\w������������\\-_����\\\\����� �'!@#\\$%�&*=+�|,.;:?/��������()]+)+\\]+");
        Matcher m = p.matcher(expressaoFiltro);
        if (m.find()) {
            String tituloCampo = m.group();
            String padraoTituloCampo = tituloCampo.replaceAll("\\[", "\\\\[");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\$", "\\\\\\$");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\*", "\\\\*");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\+", "\\\\+");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\=", "\\\\=");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\]", "\\\\]");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\/", "\\\\/");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\-", "\\\\-");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\(", "\\\\(");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\)", "\\\\)");
            String sPatternCampoConector = padraoTituloCampo + "\\s?(not)? in";
            Pattern pCampoIN = Pattern.compile(sPatternCampoConector);
            Matcher mCampoIn = pCampoIN.matcher(expressaoFiltro);
            if (mCampoIn.find()) {
                String sCampoIn = mCampoIn.group();
                String operador;
                String conector;
                if (sCampoIn.contains("not")) {
                    operador = " <> ";
                    conector = " & ";
                } else {
                    operador = " = ";
                    conector = " | ";
                }
                String sValoresExpressao = expressaoFiltro.substring(sCampoIn.length());
                String sPatternParentese = "\\([\\s)]?";
                Pattern pParentese = Pattern.compile(sPatternParentese);
                Matcher mParentese = pParentese.matcher(sValoresExpressao);
                if (mParentese.find()) {
                    String novaExpressao = "(";
                    String valorParentese = mParentese.group();
                    String valoresFiltro = sValoresExpressao.substring(valorParentese.length());
                    StringTokenizer st = new StringTokenizer(valoresFiltro, ",");
                    StringBuilder filtroNotIn = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        String valor = st.nextToken();
                        filtroNotIn.append(tituloCampo).append(operador).append(valor).append(conector);
                    }
                    filtroNotIn.delete((filtroNotIn.length() - conector.length()), (filtroNotIn.length()));
                    novaExpressao += filtroNotIn.toString();
                    expressaoFiltro = novaExpressao;
                }
            }

        }
        return expressaoFiltro;
    }

    protected String converteFiltroMetricaParaTitulosCampo(String expressaoOriginal) {
        String delimitador = "and |or ";
        expressaoOriginal = expressaoOriginal.toLowerCase();
        expressaoOriginal = expressaoOriginal.replaceAll("having", "");
        String[] filtros = expressaoOriginal.split(delimitador);
        StringBuilder novaExpressao = new StringBuilder();
        for (int x = 0; x < filtros.length; x++) {
            String expressao = filtros[x];
            String expressaoConvertida = converteExpressaoFiltroMetrica(expressao);
            novaExpressao.append(expressaoConvertida);
            if (x < filtros.length - 1) {
                int indexProximaExpressao = expressaoOriginal.indexOf(filtros[x + 1]);
                novaExpressao.append(expressaoOriginal, expressao.length(), indexProximaExpressao);
                expressaoOriginal = expressaoOriginal.substring(indexProximaExpressao);
            }
        }
        novaExpressao = new StringBuilder(novaExpressao.toString().replaceAll(" and ", " & "));
        novaExpressao = new StringBuilder(novaExpressao.toString().replaceAll(" or ", " | "));
        return novaExpressao.toString();
    }

    public MetricaMetaData getMetricaMetaDataByTitulo(String tituloCampo) {
        for (MetricaMetaData metaData : this.hierarquiaMetrica) {
            if (tituloCampo.equals(metaData.getTitulo())) {
                return metaData;
            }
        }
        return null;
    }

    public MetricaMetaData getMetricaMetaDataCampoRelativo(String tituloCampoReferencia, String funcao) {
        for (MetricaCalculadaMetaData metaData : this.hierarquiaMetricaCalculada) {
            if (metaData instanceof MetricaCalculadaFuncaoMetaData metaDataFuncao) {
                if (funcao.equals(metaDataFuncao.getFuncaoCampo()) && tituloCampoReferencia.equals(metaDataFuncao.getTituloCampoReferencia())) {
                    return metaData;
                }
            }
        }
        return null;
    }

    public void addHierarquiaLinha(DimensaoMetaData metaData) {
        this.hierarquiaLinha.add(metaData);
        if (this.lastMetaDataLinha != null) {
            metaData.setParent(this.lastMetaDataLinha);
            metaData.setNivelAcimaTotalizado();
            this.lastMetaDataLinha.setFilho(metaData);
        }
        metaData.setCubo(this);
        metaData.setEixoReferencia(DimensaoMetaData.LINHA);
        this.lastMetaDataLinha = metaData;
    }

    public void addHierarquiaColuna(DimensaoMetaData metaData) {
        this.hierarquiaColuna.add(metaData);
        if (this.lastMetaDataColuna != null) {
            metaData.setParent(this.lastMetaDataColuna);
            this.lastMetaDataColuna.setFilho(metaData);
        }
        metaData.setCubo(this);
        metaData.setEixoReferencia(DimensaoMetaData.COLUNA);
        this.lastMetaDataColuna = metaData;
    }

    public void addHierarquiaMetricaAditiva(MetricaAditivaMetaData metricaMetaData) {
        this.hierarquiaMetricaAditiva.add(metricaMetaData);
        this.hierarquiaMetrica.add(metricaMetaData);
        metricaMetaData.setCubo(this);
    }

    public void addHierarquiaMetricaCalculada(MetricaCalculadaMetaData metricaMetaData) {
        this.hierarquiaMetricaCalculada.add(metricaMetaData);
        this.hierarquiaMetrica.add(metricaMetaData);
        metricaMetaData.setCubo(this);
    }

	protected void aplicaRanking(Iterator<Dimension> iDimensoes, FunctionRanking funcao, int qtdDimensoes) {
        Dimension dimensionAux = null;
        List<Dimension> dimensoesRemover = new ArrayList<Dimension>();
        while (iDimensoes.hasNext()) {
            dimensionAux = iDimensoes.next();
            int sequencia = dimensionAux.getRankingSequence() != null ? dimensionAux.getRankingSequence() : -1;
            if (!funcao.testCondicao(sequencia, qtdDimensoes)) {
                dimensoesRemover.add(dimensionAux);
            }
        }
        if (!dimensoesRemover.isEmpty()) {
            this.removeDimensionsFiltersFunction(dimensoesRemover.get(0).getParent(), dimensoesRemover);
        }
    }

    private List<Dimension> aplicaFiltrosMetrica(MetricFilters filtrosMetrica) {
        List<Dimension> lDimensoesLinhaRemover = new ArrayList<Dimension>();
        if (filtrosMetrica != null) {
            List<Dimension> dimensoesColunaRemover = null;
            Map<Dimension, Integer> remocoesColuna = null;
            Map<Dimension, List<Dimension>> mapDimensoesColunaLinhaRemover = new HashMap<>();
            List<Dimension> lDimensoesLinhaUltimoNivel = this.getDimensoesUltimoNivelLinha();
            List<Dimension> lDimensoesColunaUltimoNivel = filtrosMetrica.getColumnDimensionsUse(this);

            for (Dimension dimensionLinha : lDimensoesLinhaUltimoNivel) {
                remocoesColuna = new HashMap<>();
                dimensoesColunaRemover = new ArrayList<>();

                if (!lDimensoesColunaUltimoNivel.isEmpty()) {
                    for (Dimension dimensionColuna : lDimensoesColunaUltimoNivel) {
                        remocoesColuna.put(dimensionColuna, 0);
                        MetricLine metricLine = this.mapaMetricas.getMetricLine(dimensionLinha, dimensionColuna);
                        if (!filtrosMetrica.testCondition(this.mapaMetricas, metricLine)) {
                            dimensoesColunaRemover.add(dimensionColuna);
                        }
                    }
                    mapDimensoesColunaLinhaRemover.put(dimensionLinha, dimensoesColunaRemover);
                } else {
                    MetricLine metricLine = this.mapaMetricas.getMetricLine(dimensionLinha);
                    if (!filtrosMetrica.testCondition(this.mapaMetricas, metricLine)) {
                        lDimensoesLinhaRemover.add(dimensionLinha);
                    }
                }
            }

            if (!mapDimensoesColunaLinhaRemover.isEmpty()) {
                List<Dimension> lDimensoesColunaRemover;

                for (Dimension dimensionLinha : mapDimensoesColunaLinhaRemover.keySet()) {
                    lDimensoesColunaRemover = mapDimensoesColunaLinhaRemover.get(dimensionLinha);

                    if (lDimensoesColunaRemover.size() == lDimensoesColunaUltimoNivel.size()) {
                        lDimensoesLinhaRemover.add(dimensionLinha);
                    }

                    for (Dimension dimensionColunaRemover : lDimensoesColunaRemover) {
                        remocoesColuna.put(dimensionColunaRemover, (remocoesColuna.get(dimensionColunaRemover)) + 1);
                        this.mapaMetricas.removeMetricLine(dimensionLinha, dimensionColunaRemover);
                    }
                }

                for (Dimension dimensionColuna : remocoesColuna.keySet()) {
                    if (remocoesColuna.get(dimensionColuna) == lDimensoesLinhaUltimoNivel.size()) {
                        dimensionColuna.getParent().removeDimensionsColumn(dimensionColuna);
                        lDimensoesColunaUltimoNivel.remove(dimensionColuna);
                    }
                }
            }
        }
        return lDimensoesLinhaRemover;
    }

    protected List<OrdenacaoMetrica> geraOrdenadacoesMetrica() {
        List<OrdenacaoMetrica> retorno = new ArrayList<OrdenacaoMetrica>();
        for (MetricaMetaData metricaMetaData : this.hierarquiaMetrica) {
            if (metricaMetaData.isVisualizada()) {
                this.adidionaOrdenacoesMetrica(metricaMetaData.getOrdenacoesMetrica(), retorno);
            }
        }
        Collections.sort(retorno);
        return retorno;
    }

    protected void geraListaUltimoNivel(Collection<Dimension> cDimensoes, List<Dimension> listaUltimoNivel) {
        for (Dimension dimension : cDimensoes) {
            if (!dimension.getDimensoesAbaixo().isEmpty()) {
                this.geraListaUltimoNivel(dimension.getDimensoesAbaixo().values(), listaUltimoNivel);
            } else {
                listaUltimoNivel.add(dimension);
            }
        }
    }

    private void resumeDados() {
        List<Dimension> dimensoesLinhaUltimoNivel = this.getDimensoesUltimoNivelLinha();
        List<Dimension> dimensoesLinhaRemover = this.aplicaFiltrosMetrica(this.filtrosMetrica);
        int verificaProcessamento = 0;
        for (Dimension dimensionLinhaRemover : dimensoesLinhaRemover) {
            verificaProcessamento++;
            if (verificaProcessamento % 100 == 0) {
                if (this.cubeListener.stopProcess()) {
                    return;
                }
            }
            Dimension dimensionPai = dimensionLinhaRemover.getParent();
            dimensionPai.removeDimensionLine(dimensionLinhaRemover);
            this.mapaMetricas.removeMetricLine(dimensionLinhaRemover);
            dimensoesLinhaUltimoNivel.remove(dimensionLinhaRemover);
        }
        dimensoesLinhaRemover = this.aplicaFiltrosMetrica(this.filtrosMetricaAcumulado);
        if (!dimensoesLinhaRemover.isEmpty()) {
            this.removeDimensionsFiltersFunction(this, dimensoesLinhaRemover);
        }
        List<OrdenacaoMetrica> metricasOrdenadas = this.geraOrdenadacoesMetrica();
        this.reorderData(metricasOrdenadas);
        if (!this.dimensionsLine.isEmpty()) {
            this.UpdateSequenceRanking(this.dimensionsLine.values().iterator());
            this.verifyRanking();
        }
        metricasOrdenadas = null;
        System.gc();
    }

    public void processar(ResultSet set) throws SQLException {
        this.cubeListener.start();
        DimensaoMetaData linhaMetaData = this.hierarquiaLinha.get(0);
        Dimension dimensionLinha = new DimensionLinha(this, linhaMetaData);
        int batchSize = 50; // Adjust the batch size as needed
        int count = 0;

        this.cubeListener.setHasData(false);
        while (set.next()) {
            this.cubeListener.setHasData(true);
            dimensionLinha.processar(set);
            this.mapaMetricas.accumulateMetricLine(this, set);

            count++;
            if (count % batchSize == 0) {
                if (this.cubeListener.stopProcess()) {
                    break;
                }
            }
        }

        this.resumeDados();
        this.cubeListener.finish();
    }

    protected abstract void factory();

    protected abstract void removeDimensionsFiltersFunction(Dimension dimensionPai, List<Dimension> dimensoesRemover);

    protected abstract void UpdateSequenceRanking(Iterator<Dimension> iDimensoesLinha);

    protected abstract void verifyRanking();

    protected abstract void reorderData(List<OrdenacaoMetrica> metricasOrdenadas);

    protected abstract void adidionaOrdenacoesMetrica(List<OrdenacaoMetrica> ordenacoesMetrica,
                                                      List<OrdenacaoMetrica> listaOrdenacoes);

}
