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

import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionLine;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumeFunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.resumeFunctions.FunctionRanking;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.CubeMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricaCalculadaFuncaoMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.util.CubeListener;
import com.msoft.mbi.cube.util.DefaultCubeListener;
import lombok.Getter;
import lombok.Setter;

public abstract class Cube extends Dimension {

    @Getter
	protected List<ColumnMetaData> columnsViewed = new ArrayList<>();
    @Getter
	private final List<DimensaoMetaData> hierarchyLine = new ArrayList<>();
    @Getter
	private DimensaoMetaData lastMetaDataLine = null;
    @Getter
	private final List<DimensaoMetaData> hierarchyColumn = new ArrayList<>();
    private DimensaoMetaData lastMetaDataColumn = null;
    @Getter
	private final List<MetricAdditiveMetaData> hierarchyMetricAdditive = new ArrayList<>();
    @Setter
	@Getter
	private List<MetricCalculatedMetaData> hierarchyMetricCalculated = new ArrayList<>();
    @Getter
	private final List<MetricMetaData> hierarchyMetric = new ArrayList<>();
    @Getter
	protected MetricsMap metricsMap = null;
    private transient List<Dimension> dimensionsLastLevelColumns = null;
    protected transient List<Dimension> dimensionsLastLevelLines = null;
    protected MetricFilters metricFilters;
    protected MetricFiltersAccumulatedValue metricFiltersAccumulatedValue;
    @Getter
	private final List<Dimension> dimensionsPool = new ArrayList<>();
    @Getter
    protected List<MetricMetaData> metricsTotalHorizontal = new ArrayList<>();
    @Setter
    @Getter
    private CubeListener cubeListener = new DefaultCubeListener();

    protected Cube(CubeMetaData metaData) {
        super(metaData);
        super.getMetaData().setCubo(this);
        super.cube = this;
        this.metricsMap = new MetricsMap(this);
        this.lastMetaDataLine = metaData;
    }

    public static Cube factoryMultiDimensionalCube(CubeMetaData metaData) {
        MultiDimensionalCube cube = new MultiDimensionalCube(metaData);
        cube.factory();
        cube.dimensionsLine = new Dimensions();
        cube.dimensionsColumn = new Dimensions();
        return cube;
    }

    public static Cube factoryDefaultCube(CubeMetaData metaData) {
        CubeDefaultFormat cube = new CubeDefaultFormat(metaData);
        cube.factory();
        cube.dimensionsLine = new Dimensions();
        cube.dimensionsColumn = new Dimensions();
        return cube;
    }

	public void addDimensionPool(Dimension dimension) {
        this.dimensionsPool.add(dimension);
        if (dimension.getMetaData().isUltima()) {
            Dimension.increaseTotalSize(dimension);
        }
    }

	@Override
    public Dimensions getDimensionsBelow() {
        return this.dimensionsLine;
    }

	@Override
    public CubeMetaData getMetaData() {
        return (CubeMetaData) this.metaData;
    }

    @Override
    public void setKeyValue() {
        this.keyValue = BRANCO;
    }

    public List<Dimension> getDimensionsLastLevelColumns() {
        if (this.dimensionsLastLevelColumns == null) {
            this.dimensionsLastLevelColumns = new ArrayList<>();
            this.getLastLevelList(this.dimensionsColumn.values(), this.dimensionsLastLevelColumns);
        }
        return dimensionsLastLevelColumns;
    }

    public List<Dimension> getDimensionsLastLevelLines() {
        if (this.dimensionsLastLevelLines == null) {
            this.dimensionsLastLevelLines = new ArrayList<>();
            this.getLastLevelList(this.dimensionsLine.values(), this.dimensionsLastLevelLines);
        }
        return dimensionsLastLevelLines;
    }

    protected boolean isMetricUsedInCalculation(CampoMetaData campoMetaData) {
        List<CampoMetaData> camposMetric = this.getMetaData().getCamposMetrica();
        for (CampoMetaData campoMetric : camposMetric) {
            if (isMetricPresent(campoMetric.getNomeCampo(), campoMetaData)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMetricUsedInMetricFilter(CampoMetaData campoMetaData) {
        String filterExpression = this.getMetaData().getExpressaoFiltrosMetrica();
        String filterExpressionAcc = this.getMetaData().getExpressaoFiltrosAcumulado();
        return this.isMetricPresent(filterExpression, campoMetaData) || this.isMetricPresent(filterExpressionAcc, campoMetaData);
    }

    private boolean isMetricPresent(String expression, CampoMetaData metric) {
        if (expression.contains("[")) {
            StringTokenizer tokenizer = new StringTokenizer(expression, "]");
            while (tokenizer.hasMoreElements()) {
                String parte = tokenizer.nextToken();
                parte = parte.substring(parte.indexOf("[") + 1);
                if (metric.getTituloCampo().equalsIgnoreCase(parte)) {
                    return true;
                }
            }
        } else {
            String prefixoChaveCampo = "#\\$";
            String prefixoChaveCampoAcum = "(acum\\()*";
            String posfixoChaveCampo = "\\$!";
            String posfixoChaveCampoAcum = "\\)*";
            Pattern p = Pattern.compile(prefixoChaveCampo + prefixoChaveCampoAcum + "\\d+" + posfixoChaveCampoAcum + posfixoChaveCampo);
            Matcher m = p.matcher(expression);
            while (m.find()) {
                String chaveCampo = m.group();
                chaveCampo = chaveCampo.substring(2, chaveCampo.length() - 2);
                if (metric.getCampo() == Integer.parseInt(chaveCampo)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getMetricVisualizationStatus(CampoMetaData campoMetaData) {
        String visualizacao = campoMetaData.getPadrao();
        if ("N".equals(visualizacao)) {
            if (this.isMetricUsedInCalculation(campoMetaData) || this.isMetricUsedInMetricFilter(campoMetaData)) {
                visualizacao = CampoMetaData.METRICA_VISUALIZACAO_RESTRITA;
            } else {
                visualizacao = CampoMetaData.METRICA_NAO_ADICIONADA;
            }
        }
        return visualizacao;
    }

    protected String convertConditionalExpression(String expressaoOriginal) {
        expressaoOriginal = convertConditionalExpressionToTitle(expressaoOriginal);
        expressaoOriginal = expressaoOriginal.replaceAll("[Ss][Ee][(]", "IF(");
        expressaoOriginal = expressaoOriginal.replaceAll(";", ",");
        return expressaoOriginal;
    }

    private String convertConditionalExpressionToTitle(String expressaoOriginal) {
        String prefixoChaveCampo = "#\\$";
        String prefixoChaveCampoAcum = "(acum\\()*";
        String posfixoChaveCampo = "\\$!";
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
            CampoMetaData campoAux;
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

    private String convertMetricFilterExpression(String filterExpression) {
        filterExpression = convertConditionalExpressionToTitle(filterExpression);
        Pattern p = Pattern.compile("\\[([\\s]?[\\w������������\\-_����\\\\����� �'!@#\\$%�&*=+�|,.;:?/��������()]+)+\\]+");
        Matcher m = p.matcher(filterExpression);
        if (m.find()) {
            String tituloCampo = m.group();
            String padraoTituloCampo = tituloCampo.replaceAll("\\[", "\\\\[");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\$", "\\\\\\$");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\*", "\\\\*");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\+", "\\\\+");
            padraoTituloCampo = padraoTituloCampo.replaceAll("=", "\\\\=");
            padraoTituloCampo = padraoTituloCampo.replaceAll("]", "\\\\]");
            padraoTituloCampo = padraoTituloCampo.replaceAll("/", "\\\\/");
            padraoTituloCampo = padraoTituloCampo.replaceAll("-", "\\\\-");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\(", "\\\\(");
            padraoTituloCampo = padraoTituloCampo.replaceAll("\\)", "\\\\)");
            String sPatternCampoConector = padraoTituloCampo + "\\s?(not)? in";
            Pattern pCampoIN = Pattern.compile(sPatternCampoConector);
            Matcher mCampoIn = pCampoIN.matcher(filterExpression);
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
                String sValoresExpressao = filterExpression.substring(sCampoIn.length());
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
                    filterExpression = novaExpressao;
                }
            }

        }
        return filterExpression;
    }

    protected String convertMetricFilterToFieldTitle(String expressaoOriginal) {
        String delimitador = "and |or ";
        expressaoOriginal = expressaoOriginal.toLowerCase();
        expressaoOriginal = expressaoOriginal.replaceAll("having", "");
        String[] filtros = expressaoOriginal.split(delimitador);
        StringBuilder novaExpressao = new StringBuilder();
        for (int x = 0; x < filtros.length; x++) {
            String expressao = filtros[x];
            String expressaoConvertida = convertMetricFilterExpression(expressao);
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

    public MetricMetaData getMetricByTitle(String title) {
        for (MetricMetaData metaData : this.hierarchyMetric) {
            if (title.equals(metaData.getTitle())) {
                return metaData;
            }
        }
        return null;
    }

    public MetricMetaData getMetricMetaDataRelativeField(String referenceTitle, String function) {
        for (MetricCalculatedMetaData metaData : this.hierarchyMetricCalculated) {
            if (metaData instanceof MetricaCalculadaFuncaoMetaData metaDataFunction) {
                if (function.equals(metaDataFunction.getFuncaoCampo()) && referenceTitle.equals(metaDataFunction.getTituloCampoReferencia())) {
                    return metaData;
                }
            }
        }
        return null;
    }

    public void addHierarchyLine(DimensaoMetaData metaData) {
        this.hierarchyLine.add(metaData);
        if (this.lastMetaDataLine != null) {
            metaData.setParent(this.lastMetaDataLine);
            metaData.setNivelAcimaTotalizado();
            this.lastMetaDataLine.setFilho(metaData);
        }
        metaData.setCubo(this);
        metaData.setEixoReferencia(DimensaoMetaData.LINHA);
        this.lastMetaDataLine = metaData;
    }

    public void addHierarchyColumn(DimensaoMetaData metaData) {
        this.hierarchyColumn.add(metaData);
        if (this.lastMetaDataColumn != null) {
            metaData.setParent(this.lastMetaDataColumn);
            this.lastMetaDataColumn.setFilho(metaData);
        }
        metaData.setCubo(this);
        metaData.setEixoReferencia(DimensaoMetaData.COLUNA);
        this.lastMetaDataColumn = metaData;
    }

    public void addHierarchyLineMetricAdditive(MetricAdditiveMetaData additiveMetaData) {
        this.hierarchyMetricAdditive.add(additiveMetaData);
        this.hierarchyMetric.add(additiveMetaData);
        additiveMetaData.setCube(this);
    }

    public void addHierarchyLineMetricCalculated(MetricCalculatedMetaData metricMetaData) {
        this.hierarchyMetricCalculated.add(metricMetaData);
        this.hierarchyMetric.add(metricMetaData);
        metricMetaData.setCube(this);
    }

	protected void applyRanking(Iterator<Dimension> iDimensions, FunctionRanking function, int amount) {
        Dimension dimensionAux;
        List<Dimension> dimensoesRemover = new ArrayList<>();
        while (iDimensions.hasNext()) {
            dimensionAux = iDimensions.next();
            int sequencia = dimensionAux.getRankingSequence() != null ? dimensionAux.getRankingSequence() : -1;
            if (!function.testCondicao(sequencia, amount)) {
                dimensoesRemover.add(dimensionAux);
            }
        }
        if (!dimensoesRemover.isEmpty()) {
            this.removeDimensionsFiltersFunction(dimensoesRemover.get(0).getParent(), dimensoesRemover);
        }
    }

    private List<Dimension> applyMetricFilters(MetricFilters filtrosMetrica) {
        List<Dimension> lDimensoesLinhaRemover = new ArrayList<>();
        if (filtrosMetrica != null) {
            List<Dimension> dimensoesColunaRemover;
            Map<Dimension, Integer> remocoesColuna = null;
            Map<Dimension, List<Dimension>> mapDimensoesColunaLinhaRemover = new HashMap<>();
            List<Dimension> lDimensoesLinhaUltimoNivel = this.getDimensionsLastLevelLines();
            List<Dimension> lDimensoesColunaUltimoNivel = filtrosMetrica.getColumnDimensionsUse(this);

            for (Dimension dimensionLinha : lDimensoesLinhaUltimoNivel) {
                remocoesColuna = new HashMap<>();
                dimensoesColunaRemover = new ArrayList<>();

                if (!lDimensoesColunaUltimoNivel.isEmpty()) {
                    for (Dimension dimensionColuna : lDimensoesColunaUltimoNivel) {
                        remocoesColuna.put(dimensionColuna, 0);
                        MetricLine metricLine = this.metricsMap.getMetricLine(dimensionLinha, dimensionColuna);
                        if (!filtrosMetrica.testCondition(this.metricsMap, metricLine)) {
                            dimensoesColunaRemover.add(dimensionColuna);
                        }
                    }
                    mapDimensoesColunaLinhaRemover.put(dimensionLinha, dimensoesColunaRemover);
                } else {
                    MetricLine metricLine = this.metricsMap.getMetricLine(dimensionLinha);
                    if (!filtrosMetrica.testCondition(this.metricsMap, metricLine)) {
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
                        this.metricsMap.removeMetricLine(dimensionLinha, dimensionColunaRemover);
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

    protected List<MetricOrdering> buildMetricOrdering() {
        List<MetricOrdering> result = new ArrayList<MetricOrdering>();
        for (MetricMetaData metricMetaData : this.hierarchyMetric) {
            if (metricMetaData.isViewed()) {
                this.addMetricOrdering(metricMetaData.getMetricOrderings(), result);
            }
        }
        Collections.sort(result);
        return result;
    }

    protected void getLastLevelList(Collection<Dimension> dimensions, List<Dimension> dimensionList) {
        for (Dimension dimension : dimensions) {
            if (!dimension.getDimensionsBelow().isEmpty()) {
                this.getLastLevelList(dimension.getDimensionsBelow().values(), dimensionList);
            } else {
                dimensionList.add(dimension);
            }
        }
    }

    private void resumeData() {
        List<Dimension> lastLevelDimensions = this.getDimensionsLastLevelLines();
        List<Dimension> dimensoesLinhaRemover = this.applyMetricFilters(this.metricFilters);
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
            this.metricsMap.removeMetricLine(dimensionLinhaRemover);
            lastLevelDimensions.remove(dimensionLinhaRemover);
        }
        dimensoesLinhaRemover = this.applyMetricFilters(this.metricFiltersAccumulatedValue);
        if (!dimensoesLinhaRemover.isEmpty()) {
            this.removeDimensionsFiltersFunction(this, dimensoesLinhaRemover);
        }
        List<MetricOrdering> metricOrderings = this.buildMetricOrdering();
        this.reorderData(metricOrderings);
        if (!this.dimensionsLine.isEmpty()) {
            this.UpdateSequenceRanking(this.dimensionsLine.values().iterator());
            this.verifyRanking();
        }
    }

    public void process(ResultSet set) throws SQLException {
        this.cubeListener.start();
        DimensaoMetaData metadataLine = this.hierarchyLine.get(0);
        Dimension dimensionLine = new DimensionLine(this, metadataLine);
        int batchSize = 50;
        int count = 0;

        this.cubeListener.setHasData(false);
        while (set.next()) {
            this.cubeListener.setHasData(true);
            dimensionLine.process(set);
            this.metricsMap.accumulateMetricLine(this, set);

            count++;
            if (count % batchSize == 0) {
                if (this.cubeListener.stopProcess()) {
                    break;
                }
            }
        }

        this.resumeData();
        this.cubeListener.finish();
    }

    protected abstract void factory();

    protected abstract void removeDimensionsFiltersFunction(Dimension dimensionPai, List<Dimension> dimensions);

    protected abstract void UpdateSequenceRanking(Iterator<Dimension> dimensionIterator);

    protected abstract void verifyRanking();

    protected abstract void reorderData(List<MetricOrdering> metricOrderings);

    protected abstract void addMetricOrdering(List<MetricOrdering> metricOrderings, List<MetricOrdering> orderingList);

}
