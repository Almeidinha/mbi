package com.msoft.mbi.cube.multi.dimension;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.MetricLine;
import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TipoData;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.column.TipoHora;
import com.msoft.mbi.cube.multi.column.TipoNumero;
import com.msoft.mbi.cube.multi.column.TextType;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsDimensao;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsMetrica;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoComparator;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Dimension implements Comparable<Dimension> {

    public static final String CLOSE = "]";
    public static final String OPEN = "[";
    public static final String EMPTY = "";
    @Setter(AccessLevel.NONE)
    protected Dimension parent = null;
    @Setter(AccessLevel.NONE)
    protected DimensionMetaData metaData;
    @Setter(AccessLevel.NONE)
    protected Cube cube = null;
    private Comparable<String> value = null;
    private Comparable<String> visualizationValue = null;
    @Setter(AccessLevel.NONE)
    protected Dimensions dimensionsLine = null;
    @Setter(AccessLevel.NONE)
    protected Dimensions dimensionsColumn = null;
    @Setter(AccessLevel.NONE)
    private int totalSize = 0;
    @Setter(AccessLevel.NONE)
    private transient Dimension dimensionTotalLevelUp = null;
    protected transient DataType<Object> type;
    private Integer rankingSequence = -1;
    @Setter(AccessLevel.NONE)
    protected transient String keyValue;

    public Dimension(DimensionMetaData metaData) {
        this.metaData = metaData;
        this.type = this.metaData.getDataType();
    }

    public Dimension(Dimension parent, DimensionMetaData metaData) {
        this(metaData);
        this.parent = parent;
        this.cube = this.parent.getCube();
    }

    public int getLevel() {
        if (this.parent != null) {
            return 1 + this.parent.getLevel();
        } else {
            return -1;
        }
    }

    public int getSize() {
        return this.getDimensionsBelow().size();
    }

    public Comparable<Object> getOrderValue() {
        Object obj;
        if (this.value == null) {
            if (this.metaData.getDataType() instanceof TipoNumero) {
                obj = TipoDecimal.BRANCO;
            } else if (this.metaData.getDataType() instanceof TipoData) {
                obj = TipoData.BRANCO;
            } else if (this.metaData.getDataType() instanceof TipoHora) {
                obj = TipoHora.BRANCO;
            } else {
                obj = TextType.EMPTY;
            }
        } else {
            DimensionMetaData metaData = this.metaData;
            if (this.metaData.getOrderingDimension() != null) {
                metaData = this.metaData.getOrderingDimension();
            }
            if (metaData.getDataType() instanceof TextType) {
                obj = this.getValue().toString().trim();
                int posIni = obj.toString().indexOf("(*");
                if (posIni > -1) {
                    obj = obj.toString().replace(obj.toString().substring(posIni), "");
                }
            } else {
                obj = this.getValue();

                if ((this.metaData.getDataType() instanceof TipoData) || (this.metaData.getDataType() instanceof TipoNumero)) {
                    int posIni = obj.toString().indexOf("(*");

                    if (posIni > -1) {
                        obj = obj.toString().replace(obj.toString().substring(posIni), "");

                        if (this.metaData.getDataType() instanceof TipoData)
                            obj = Date.valueOf(obj.toString());
                    }
                }

                if (this.metaData.getDataType() instanceof TipoNumero)
                    obj = Double.valueOf(obj.toString());

            }
        }

        return (Comparable<Object>) obj;
    }

    public void setValue(Comparable<String> value) {
        this.value = value;
        this.setKeyValue();
    }

    public String getFormattedValue() {
        return this.metaData.getFormattedValue(this.value);
    }

    protected void setDimensionTotalLevelUp() {
        this.dimensionTotalLevelUp = this.searchDimensionTotalLevelUp();
    }

    public boolean isSameAxis(Dimension dimension) {
        return this.metaData.getReferenceAxis() == dimension.getMetaData().getReferenceAxis();
    }

    public boolean isSameAxis(String title) {
        if ((this.metaData.getTitle()).equals(title)) {
            return true;
        } else if (this.parent != null && this.parent.isSameAxis(this)) {
            return this.parent.isSameAxis(title);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.buildCompleteKeyValue().toString();
    }

    public int compareTo(Dimension o) {
        if (this.value == o.getValue()) {
            return 0;
        }

        if (DimensionLineOutros.VALOR_OUTROS.equals(o.getValue())) {
            return -1;
        }

        if (DimensionLineOutros.VALOR_OUTROS.equals(this.value)) {
            return 1;
        }

        return this.metaData.getComparator().compare(this, o);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != obj.getClass()) return false;

        if (Objects.equals(this.getKeyValue(), ((Dimension) obj).getKeyValue())) {
            return true;
        }
        Dimension outraDimension = (Dimension) obj;
        return this.isSameAxis(outraDimension) && this.getKeyValue().equals(outraDimension.getKeyValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent);
    }

    public boolean equals(Dimension dimension) {
        if (Objects.equals(this.getKeyValue(), dimension.getKeyValue())) {
            return true;
        }
        return this.isSameAxis(dimension) && this.getKeyValue().equals(dimension.getKeyValue());
    }

    protected StringBuilder buildCompleteKeyValue() {
        StringBuilder key = new StringBuilder(EMPTY);
        if (this.parent != null && this.parent.isSameAxis(this)) {
            key = this.parent.buildCompleteKeyValue();
        }
        key.append(OPEN);
        key.append(this.value);
        key.append(CLOSE);
        return key;
    }

    protected StringBuilder buildKeyValue() {
        StringBuilder key = new StringBuilder(EMPTY);
        if (this.isSameAxis(this.parent)) {
            if (this.parent != null) {
                key = this.parent.buildKeyValue();
            }
        }
        key.append(OPEN);
        key.append(this.value);
        key.append(CLOSE);
        return key;
    }

    public String getMetricDefaultStyles(int currentLine) {
        return CellProperty.CELL_PROPERTY_METRIC_VALUES[currentLine % 2];
    }

    public Dimensions getSameLevelDimensions() {
        if (this.metaData.isLine()) {
            return this.parent.getDimensionsLine();
        } else {
            return this.parent.getDimensionsColumn();
        }
    }

    public Dimension getPreviousDimension(Dimension parentDimensionLimit) {
        Dimension dimensionPai = this.getParent();
        if (dimensionPai != null && !this.getMetaData().equals(parentDimensionLimit.getMetaData())) {
            Comparable<Dimension> previous = this.getSameLevelDimensions().lowerKey(this);
            if (previous != null) {
                return (Dimension) previous;
            }
            if (!Objects.equals(dimensionPai.getMetaData(), this.getDimensionTotalLevelUp())) {
                Dimension dimensionAnteriorPai = dimensionPai.getPreviousDimension(parentDimensionLimit);
                if (dimensionAnteriorPai != null) {
                    return (Dimension) dimensionAnteriorPai.getDimensionsBelow().lastKey();
                }
            }
        }
        return null;
    }

    private Dimension getFirstDimensionColumn() {
        return (Dimension) this.getDimensionsColumn().firstKey();
    }

    public Dimension getSameLevelFirstDimensionColumn() { // TODO is this refactor correct???
        Dimension currentColumn = this.cube;

        while (!currentColumn.getMetaData().equals(this.getMetaData())) {
            currentColumn = currentColumn.getFirstDimensionColumn();
        }

        return currentColumn;
    }

    public boolean isFirstDimensionColumnSameLevel() {
        Dimension parent = this.getParent();
        if (parent != null) {
            Dimension primeiraDimension = parent.getFirstDimensionColumn();
            if (this.equals(primeiraDimension)) {
                return parent.isFirstDimensionColumnSameLevel();
            } else {
                return false;
            }
        }
        return true;
    }

    protected static void increaseTotalSize(Dimension dimension) {
        dimension.totalSize++;
        if (dimension.parent != null && dimension.parent.isSameAxis(dimension)) {
            Dimension.increaseTotalSize(dimension.parent);
        }
    }

    protected static void decreaseTotalSize(Dimension dimension) {
        decreaseTotalSize(dimension, 1);
    }

    protected static void decreaseTotalSize(Dimension dimension, int size) {
        dimension.totalSize -= size;
        if (dimension.parent != null && dimension.parent.isSameAxis(dimension)) {
            Dimension.decreaseTotalSize(dimension.parent, size);
        }
    }

    private Dimension searchDimensionTotalLevelUp() {
        if (this.getMetaData().getParent() == null) {
            return this;
        }
        if (!this.getMetaData().getParent().isTotalPartial()) {
            return this.getParent().searchDimensionTotalLevelUp();
        }
        return this.getParent();
    }

    public void addDimensionLine(Dimension dimensionLine) {
        this.dimensionsLine.addDimension(dimensionLine);
    }

    public void removeDimensionLines() {
        this.dimensionsLine.removeDimensions();
    }

    public void removeDimensionLine(Dimension dimensionLine) {
        if (this.parent != null && this.totalSize == 1 && this.isSameAxis(dimensionLine)) {
            this.parent.removeDimensionLine(this);
        } else {
            this.dimensionsLine.removeDimension(dimensionLine);
        }
    }

    public void resetDimensionsLines(DimensaoComparator comparator) {
        this.dimensionsLine = new Dimensions();
        decreaseTotalSize(this, this.totalSize);
    }

    public void addDimensionsColumn(Dimension dimensionsColumn) {
        this.dimensionsColumn.addDimension(dimensionsColumn);
    }

    public void removeDimensionsColumn(Dimension dimensionsColumn) {
        if (this.parent != null && this.totalSize == 1 && this.isSameAxis(dimensionsColumn)) {
            this.parent.removeDimensionsColumn(this);
        } else {
            this.dimensionsColumn.removeDimension(dimensionsColumn);
            this.removeLastLevelDimensionColumnLine(dimensionsColumn);
        }
    }

    private void removeLastLevelDimensionColumnLine(Dimension dimension) {
        List<Dimension> lastLevelColumnLine = this.cube.getDimensionsLastLevelLines();
        Dimensions dimensionColumnLine = lastLevelColumnLine.get(0).getDimensionsColumn();
        if (!dimensionColumnLine.isEmpty()) {
            Dimension tempDimensionColumn = (Dimension) dimensionColumnLine.firstKey();
            if (tempDimensionColumn.getMetaData().equals(dimension.getMetaData())) {
                for (Dimension dimensionLine : lastLevelColumnLine) {
                    dimensionLine.dimensionsColumn.removeDimension(dimension);
                }
            }
        }
    }

    public int countPartialAggregatesInHierarchy() {
        if (this.metaData.isLast()) {
            return 0;
        }

        int result = 0;

        result += (this.metaData.isTotalPartial() || this.metaData.isPartialTotalExpression()) ? 1 : 0;
        result += this.metaData.isMediaPartial() ? 1 : 0;
        result += this.metaData.isExpressionPartial() ? 1 : 0;

        result += this.getDimensionsBelow().values().stream()
                .mapToInt(Dimension::countPartialAggregatesInHierarchy)
                .sum();

        return result;
    }

    private String searchDimensionAlertProperty(List<ColorAlertConditionsDimensao> alerts) {
        String retorno = null;
        for (ColorAlertConditionsDimensao colorAerts : alerts) {
            if (colorAerts.testCondition(this.getValue())) {
                retorno = CellProperty.CELL_PROPERTY_ALERTS_PREFIX + colorAerts.getSequence();
            }
        }
        return retorno;
    }

    public String searchDimensionAlertLineProperty() {
        return searchDimensionAlertProperty(this.getMetaData().getColorAlertLines());
    }

    public String searchDimensionAlertCellProperty() {
        return searchDimensionAlertProperty(this.getMetaData().getColorAlertCells());
    }

    public String searchMetricAlertLineProperty(List<MetricMetaData> metricasMetaData, String function, Dimension dimensionColumn) {
        
        MetricLine metricLine = this.cube.getMetricsMap().getMetricLine(this);
        if (metricLine == null) {
            return null;
        }

        return metricasMetaData.stream()
                .map(metaData -> metricLine.getMetrics().get(metaData.getTitle()))
                .filter(Objects::nonNull)
                .map(metricExpression -> metricExpression.searchMetricLineAlert(function, this, dimensionColumn))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public String searchMetricsPropertyAlertsRowFunctionsTotalColumns(List<MetricMetaData> metricMetaData, List<String> functions) {
        String propriedadeAlerta = null;
        DimensionNullColumn dimensionNullColumn = new DimensionNullColumn(this.cube);
        for (String function : functions) {
            String property = this.searchMetricAlertLineProperty(metricMetaData, function, dimensionNullColumn);
            if (property != null) {
                propriedadeAlerta = property;
            }
        }
        return propriedadeAlerta;
    }

    public String searchMetricAlertLineProperty(List<MetricMetaData> metricsMetaData, String function, List<Dimension> dimensionColumns) {
        String propriedadeAlerta = null;
        for (Dimension dimensionColumn : dimensionColumns) {
            String metricAlertLineProperty = searchMetricAlertLineProperty(metricsMetaData, function, dimensionColumn);
            if (metricAlertLineProperty != null) {
                propriedadeAlerta = metricAlertLineProperty;
            }
        }
        return propriedadeAlerta;
    }

    public String searchAlertMetricCell(List<ColorAlertConditionsMetrica> alertConditions, Double value, Dimension dimensionColumn) {
        if (alertConditions == null) {
            return null;
        }

        for (ColorAlertConditionsMetrica colorAlert : alertConditions) {
            if (colorAlert.testaCondicao(value, this, dimensionColumn, this.cube)) {
                return CellProperty.CELL_PROPERTY_ALERTS_PREFIX + colorAlert.getSequence();
            }
        }

        return null;
    }

    public abstract void process(ResultSet set) throws SQLException;

    public abstract void setKeyValue();

    public abstract Dimensions getDimensionsBelow();
}
