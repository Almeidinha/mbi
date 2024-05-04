package com.msoft.mbi.cube.multi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimension;
import com.msoft.mbi.cube.multi.dimension.DimensionLine;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import com.msoft.mbi.cube.multi.dimension.Dimensions;
import com.msoft.mbi.cube.multi.resumefunctions.MetricFilters;
import com.msoft.mbi.cube.multi.resumefunctions.MetricFiltersAccumulatedValue;
import com.msoft.mbi.cube.multi.resumefunctions.FunctionRanking;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metadata.CubeMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricOrdering;
import com.msoft.mbi.cube.multi.metrics.additive.MetricAdditiveMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedFunctionMetaData;
import com.msoft.mbi.cube.multi.metrics.calculated.MetricCalculatedMetaData;
import com.msoft.mbi.cube.util.CubeListener;
import com.msoft.mbi.cube.util.DefaultCubeListener;
import lombok.Getter;
import lombok.Setter;

public abstract class Cube extends Dimension {

    @Getter
	protected List<ColumnMetaData> columnsViewed = new ArrayList<>();
    @Getter
	private final List<DimensionMetaData> hierarchyLine = new ArrayList<>();
    @Getter
	private DimensionMetaData lastMetaDataLine = null;
    @Getter
	private final List<DimensionMetaData> hierarchyColumn = new ArrayList<>();
    private DimensionMetaData lastMetaDataColumn = null;
    @Getter
	private final List<MetricAdditiveMetaData> hierarchyMetricAdditive = new ArrayList<>();
    @Setter
	@Getter
	private List<MetricCalculatedMetaData> hierarchyMetricCalculated = new ArrayList<>();
    @Getter
	private final List<MetricMetaData> hierarchyMetric = new ArrayList<>();
    @Getter
	protected MetricsMap metricsMap = null;
    private List<Dimension> dimensionsLastLevelColumns = null;
    protected List<Dimension> dimensionsLastLevelLines = null;
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
        super.getMetaData().setCube(this);
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
        DefaultCube cube = new DefaultCube(metaData);
        cube.factory();
        cube.dimensionsLine = new Dimensions();
        cube.dimensionsColumn = new Dimensions();
        return cube;
    }

	public void addDimensionPool(Dimension dimension) {
        this.dimensionsPool.add(dimension);
        if (dimension.getMetaData().isLast()) {
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
        this.keyValue = EMPTY;
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

    protected boolean isMetricUsedInCalculation(MetaDataField metaDataField) {
        List<MetaDataField> camposMetric = this.getMetaData().getMetricFields();
        for (MetaDataField campoMetric : camposMetric) {
            if (isMetricPresent(campoMetric.getName(), metaDataField)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isMetricUsedInMetricFilter(MetaDataField metaDataField) {
        String filterExpression = this.getMetaData().getMetricFieldsExpression();
        String filterExpressionAcc = this.getMetaData().getAccumulatedFieldExpression();
        return this.isMetricPresent(filterExpression, metaDataField) || this.isMetricPresent(filterExpressionAcc, metaDataField);
    }

    private boolean isMetricPresent(String expression, MetaDataField metric) {
        if (expression.contains("[")) {
            StringTokenizer tokenizer = new StringTokenizer(expression, "]");
            while (tokenizer.hasMoreElements()) {
                String parte = tokenizer.nextToken();
                parte = parte.substring(parte.indexOf("[") + 1);
                if (metric.getTitle().equalsIgnoreCase(parte)) {
                    return true;
                }
            }
        } else {
            String fieldKeyPrefix = "#\\$";
            String accFieldPrefixKey = "(acum\\()*";
            String fieldKeySuffixKey = "\\$!";
            String accFieldSuffixKey = "\\)*";
            Pattern p = Pattern.compile(fieldKeyPrefix + accFieldPrefixKey + "\\d+" + accFieldSuffixKey + fieldKeySuffixKey);
            Matcher m = p.matcher(expression);
            while (m.find()) {
                String chaveCampo = m.group();
                chaveCampo = chaveCampo.substring(2, chaveCampo.length() - 2);
                if (metric.getId() == Integer.parseInt(chaveCampo)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getMetricVisualizationStatus(MetaDataField metaDataField) {
        String visualizacao = metaDataField.getDefaultField();
        if ("N".equals(visualizacao)) {
            if (this.isMetricUsedInCalculation(metaDataField) || this.isMetricUsedInMetricFilter(metaDataField)) {
                visualizacao = MetaDataField.METRIC_RESTRICTED_VIEW;
            } else {
                visualizacao = MetaDataField.NOT_ADDED_METRIC;
            }
        }
        return visualizacao;
    }

    protected String convertConditionalExpression(String expressaoOriginal) {
        expressaoOriginal = convertConditionalExpressionToTitle(expressaoOriginal);
        expressaoOriginal = expressaoOriginal.replaceAll("[Ss][Ee][(]", "IF(");
        expressaoOriginal = expressaoOriginal.replace(";", ",");
        return expressaoOriginal;
    }

    private String convertConditionalExpressionToTitle(String expressaoOriginal) {
        String fieldKeyPrefix = "#\\$";
        String accFieldKeyPrefix = "(acum\\()*";
        String fieldKeySuffix = "\\$!";
        String accFieldKeySuffix = "\\)*";

        String regex = fieldKeyPrefix + accFieldKeyPrefix + "\\d+" + accFieldKeySuffix + fieldKeySuffix;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expressaoOriginal);

        while (matcher.find()) {
            String fieldkey = matcher.group();
            fieldkey = fieldkey.substring(2, fieldkey.length() - 2);

            String fieldkeyExp = fieldKeyPrefix + fieldkey + fieldKeySuffix;
            fieldkeyExp = fieldkeyExp.replace("\\(", "\\\\(");
            fieldkeyExp = fieldkeyExp.replace("\\)", "\\\\)");

            Pattern fieldKeyPattern = Pattern.compile("\\d+");
            Matcher fieldKeyMatcher = fieldKeyPattern.matcher(fieldkey);
            MetaDataField tempField;

            if (fieldKeyMatcher.find()) {
                String codCampo = fieldKeyMatcher.group();
                tempField = this.getMetaData().getMetricFieldByCode(Integer.parseInt(codCampo));

                if (tempField != null) {
                    String tituloCampo = tempField.getTitle().replace("\\$", "###");
                    tituloCampo = fieldkey.replaceAll(codCampo, tituloCampo);

                    String expRegFieldTitle = Pattern.quote(fieldkeyExp);
                    expressaoOriginal = expressaoOriginal.replaceAll(expRegFieldTitle, "[" + tituloCampo + "]");
                    expressaoOriginal = expressaoOriginal.replace("###", "\\$");
                }
            }
        }

        return expressaoOriginal;
    }

    public String convertMetricFilterExpression(String filterExpression) {
        filterExpression = convertConditionalExpressionToTitle(filterExpression);

        Pattern pattern = Pattern.compile("\\[(\\s?[\\w\u0080-ÿ\\-_ñÑáéíóúÁÉÍÓÚüÜçÇâêîôÂÊÎÔãõÃÕ'!@#$%&*=+|,.;:?/()]+)+]+");
        Matcher matcher = pattern.matcher(filterExpression);

        if (matcher.find()) {
            String fieldTitle = matcher.group();
            String defaultFieldTitle = Pattern.quote(fieldTitle);

            String connectorPattern = defaultFieldTitle + "\\s?(not)? in";
            Pattern fieldINPattern = Pattern.compile(connectorPattern);
            Matcher fieldINMatcher = fieldINPattern.matcher(filterExpression);

            if (fieldINMatcher.find()) {
                String sCampoIn = fieldINMatcher.group();
                String operator = (sCampoIn.contains("not")) ? " <> " : " = ";
                String connector = (sCampoIn.contains("not")) ? " & " : " | ";

                String expressionValue = filterExpression.substring(sCampoIn.length());
                String parenthesisReg = "\\([\\s)]?";
                Pattern parenthesisPattern = Pattern.compile(parenthesisReg);
                Matcher parenthesisMatcher = parenthesisPattern.matcher(expressionValue);

                if (parenthesisMatcher.find()) {
                    String newExpression = "(";
                    String parenthesisValue = parenthesisMatcher.group();
                    String filterValue = expressionValue.substring(parenthesisValue.length());
                    StringTokenizer st = new StringTokenizer(filterValue, ",");
                    StringBuilder notInFilter = new StringBuilder();

                    while (st.hasMoreTokens()) {
                        String valor = st.nextToken();
                        notInFilter.append(fieldTitle).append(operator).append(valor).append(connector);
                    }

                    notInFilter.delete((notInFilter.length() - connector.length()), notInFilter.length());
                    newExpression += notInFilter.toString();
                    filterExpression = newExpression;
                }
            }
        }

        return filterExpression;
    }

    protected String convertMetricFilterToFieldTitle(String originalExpression) {
        String delimiter = "\\s+and\\s+|\\s+or\\s+";
        originalExpression = originalExpression.toLowerCase().replaceAll("\\bhaving\\b", "");
        String[] filters = originalExpression.split(delimiter);
        StringBuilder newExpression = new StringBuilder();

        for (int x = 0; x < filters.length; x++) {
            String expression = filters[x];
            String convertedExpression = convertMetricFilterExpression(expression);
            newExpression.append(convertedExpression);
            if (x < filters.length - 1) {
                int nextExpressionIndex = originalExpression.indexOf(filters[x + 1], expression.length());
                newExpression.append(originalExpression, expression.length(), nextExpressionIndex);
                originalExpression = originalExpression.substring(nextExpressionIndex);
            }
        }

        return newExpression.toString()
                .replaceAll("\\s+and\\s+", " & ")
                .replaceAll("\\s+or\\s+", " | ");
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
            if (metaData instanceof MetricCalculatedFunctionMetaData metaDataFunction) {
                if (function.equals(metaDataFunction.getFieldFunction()) && referenceTitle.equals(metaDataFunction.getReferenceFieldTitle())) {
                    return metaData;
                }
            }
        }
        return null;
    }

    public void addHierarchyLine(DimensionMetaData metaData) {
        this.hierarchyLine.add(metaData);
        if (this.lastMetaDataLine != null) {
            metaData.setParent(this.lastMetaDataLine);
            metaData.setUpperLevelTotal();
            this.lastMetaDataLine.setChild(metaData);
        }
        metaData.setCube(this);
        metaData.setReferenceAxis(DimensionMetaData.LINE);
        this.lastMetaDataLine = metaData;
    }

    public void addHierarchyColumn(DimensionMetaData metaData) {
        this.hierarchyColumn.add(metaData);
        if (this.lastMetaDataColumn != null) {
            metaData.setParent(this.lastMetaDataColumn);
            this.lastMetaDataColumn.setChild(metaData);
        }
        metaData.setCube(this);
        metaData.setReferenceAxis(DimensionMetaData.COLUMN);
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

    protected void applyRanking(List<Dimension> dimensions, FunctionRanking function, int amount) {
        List<Dimension> dimensionsToRemove = new ArrayList<>();
        for (Dimension dimension : dimensions) {
            int sequence = dimension.getRankingSequence() != null ? dimension.getRankingSequence() : -1;
            if (!function.testCondicao(sequence, amount)) {
                dimensionsToRemove.add(dimension);
            }
        }
        if (!dimensionsToRemove.isEmpty()) {
            Dimension parentDimension = dimensionsToRemove.get(0).getParent();
            removeDimensionsFiltersFunction(parentDimension, dimensionsToRemove);
        }
    }

    private List<Dimension> applyMetricFilters(MetricFilters metricFilters) {
        List<Dimension> dimensions = new ArrayList<>();
        if (metricFilters != null) {
            List<Dimension> dimensoesColunaRemover;
            Map<Dimension, Integer> remocoesColuna = null;
            Map<Dimension, List<Dimension>> dimensionListHashMap = new HashMap<>();
            List<Dimension> lastLevelLines = this.getDimensionsLastLevelLines();
            List<Dimension> lastLevelColumns = metricFilters.getColumnDimensionsUse(this);

            for (Dimension dimensionLine : lastLevelLines) {
                remocoesColuna = new HashMap<>();
                dimensoesColunaRemover = new ArrayList<>();

                if (!lastLevelColumns.isEmpty()) {
                    for (Dimension dimensionColumn : lastLevelColumns) {
                        remocoesColuna.put(dimensionColumn, 0);
                        MetricLine metricLine = this.metricsMap.getMetricLine(dimensionLine, dimensionColumn);
                        if (!metricFilters.testCondition(this.metricsMap, metricLine)) {
                            dimensoesColunaRemover.add(dimensionColumn);
                        }
                    }
                    dimensionListHashMap.put(dimensionLine, dimensoesColunaRemover);
                } else {
                    MetricLine metricLine = this.metricsMap.getMetricLine(dimensionLine);
                    if (!metricFilters.testCondition(this.metricsMap, metricLine)) {
                        dimensions.add(dimensionLine);
                    }
                }
            }

            if (!dimensionListHashMap.isEmpty()) {
                List<Dimension> lDimensoesColunaRemover;

                for (Map.Entry<Dimension, List<Dimension>> entry : dimensionListHashMap.entrySet()) {
                    lDimensoesColunaRemover = entry.getValue();

                    if (lDimensoesColunaRemover.size() == lastLevelColumns.size()) {
                        dimensions.add(entry.getKey());
                    }

                    for (Dimension dimensionToRemove : lDimensoesColunaRemover) {
                        remocoesColuna.put(dimensionToRemove, (remocoesColuna.get(dimensionToRemove)) + 1);
                        this.metricsMap.removeMetricLine(entry.getKey(), dimensionToRemove);
                    }
                }
                for (Map.Entry<Dimension, Integer> entry : remocoesColuna.entrySet()) {
                    if (entry.getValue() == lastLevelLines.size()) {
                        entry.getKey().getParent().removeDimensionsColumn(entry.getKey());
                        lastLevelColumns.remove(entry.getKey());
                    }
                }
            }
        }
        return dimensions;
    }

    protected List<MetricOrdering> buildMetricOrdering() {
        List<MetricOrdering> result = new ArrayList<>();
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
        int checkProcessing = 0;
        for (Dimension dimensionLinesToRemove : dimensoesLinhaRemover) {
            checkProcessing++;
            if (checkProcessing % 100 == 0 && this.cubeListener.stopProcess()) {
                return;
            }
            Dimension dimensionPai = dimensionLinesToRemove.getParent();
            dimensionPai.removeDimensionLine(dimensionLinesToRemove);
            this.metricsMap.removeMetricLine(dimensionLinesToRemove);
            lastLevelDimensions.remove(dimensionLinesToRemove);
        }
        dimensoesLinhaRemover = this.applyMetricFilters(this.metricFiltersAccumulatedValue);
        if (!dimensoesLinhaRemover.isEmpty()) {
            this.removeDimensionsFiltersFunction(this, dimensoesLinhaRemover);
        }
        List<MetricOrdering> metricOrderings = this.buildMetricOrdering();
        this.reorderData(metricOrderings);
        if (!this.dimensionsLine.isEmpty()) {
            this.updateSequenceRanking(this.dimensionsLine.values());
            this.verifyRanking();
        }
    }

    public void process(ResultSet set) throws SQLException {
        this.cubeListener.start();
        DimensionMetaData metadataLine = this.hierarchyLine.get(0);
        Dimension dimensionLine;
        int batchSize = 50;
        int count = 0;

        this.cubeListener.setHasData(false);

        while (set.next()) {
            this.cubeListener.setHasData(true);
            dimensionLine = new DimensionLine(this, metadataLine);
            dimensionLine.process(set);
            this.metricsMap.accumulateMetricLine(this, set);

            count++;
            if (count % batchSize == 0 && (this.cubeListener.stopProcess())) {
                break;
            }
        }

        this.resumeData();
        this.cubeListener.finish();
    }

    protected abstract void factory();

    protected abstract void removeDimensionsFiltersFunction(Dimension dimensionPai, List<Dimension> dimensions);

    protected abstract void updateSequenceRanking(Collection<Dimension> dimensions);

    protected abstract void verifyRanking();

    protected abstract void reorderData(List<MetricOrdering> metricOrderings);

    protected abstract void addMetricOrdering(List<MetricOrdering> metricOrderings, List<MetricOrdering> orderingList);

}
