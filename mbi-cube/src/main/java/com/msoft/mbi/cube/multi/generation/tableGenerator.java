package com.msoft.mbi.cube.multi.generation;

import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditions;
import com.msoft.mbi.cube.multi.metrics.MetricMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public abstract class tableGenerator {

    protected Impressor impressor = null;
    protected Cube cube = null;
    protected int metricsAmount = 0;
    protected List<MetricMetaData> visibleMetrics;
    protected int currentLine = 0;
    private boolean scheduled = false;

    public abstract void processar(Impressor iImpressor);

    protected void createsSpecificStylesColumns() {
        List<ColumnMetaData> columns = this.cube.getColumnsViewed();
        int index = 0;
        for (ColumnMetaData metaData : columns) {
            String classname = CellProperty.PROPRIEDADE_CELULA_PREFIXO + index++;
            this.impressor.adicionaEstiloPropriedadeEspecificaColuna(metaData.getCellProperty(), classname);
        }
    }

    protected void createColorAlertStyles(List<?> alertColors) {
        for (Object colors : alertColors) {
            ColorAlertConditions colorAlertConditions = (ColorAlertConditions) colors;
            this.impressor.adicionaEstilo(colorAlertConditions.getAlertProperty(), CellProperty.PROPRIEDADE_CELULA_ALERTAS_PREFIXO + colorAlertConditions.getSequence());
        }
    }

    protected void createDefaultStyles() {
        CellProperty cellPropertyOne = createCellProperty(CellProperty.ALINHAMENTO_DIREITA, "000080", "CCCCCC", "Verdana", 10, "3377CC", true);
        this.impressor.adicionaEstilo(cellPropertyOne, CellProperty.PROPRIEDADE_CELULA_TOTALGERAL);

        CellProperty metricOneProperty = createCellProperty(CellProperty.ALINHAMENTO_DIREITA, "000080", "D7E3F7", "Verdana", 10, "3377CC", true);
        this.impressor.adicionaEstilo(metricOneProperty, CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA1);

        CellProperty metricTwoProperty = createCellProperty(CellProperty.ALINHAMENTO_DIREITA, "000080", "FFFFFF", "Verdana", 10, "3377CC", true);
        this.impressor.adicionaEstilo(metricTwoProperty, CellProperty.PROPRIEDADE_CELULA_VALOR_METRICA2);

        CellProperty dataMetricOneProperty = createCellProperty(CellProperty.ALINHAMENTO_DIREITA, "000080", "D7E3F7", "Verdana", 10, "3377CC", true);
        this.impressor.adicionaEstilo(dataMetricOneProperty, CellProperty.PROPRIEDADE_CELULA_DATA_METRICA1);

        CellProperty dataMetricTwoProperty = createCellProperty(CellProperty.ALINHAMENTO_DIREITA, "000080", "FFFFFF", "Verdana", 10, "3377CC", true);
        this.impressor.adicionaEstilo(dataMetricTwoProperty, CellProperty.PROPRIEDADE_CELULA_DATA_METRICA2);

        CellProperty cellPropertyTwo = createCellProperty(CellProperty.ALINHAMENTO_CENTRO, "FFFFFF", "3377CC", "Verdana", 10, "000000", true);
        cellPropertyTwo.setWidth(10);
        this.impressor.adicionaEstilo(cellPropertyTwo, CellProperty.PROPRIEDADE_CELULA_SEQUENCIA);
    }

    protected CellProperty createCellProperty(String alignment, String fontColor, String backgroundColor,
                                              String fontName, int fontSize, String borderColor, boolean specificBorder) {
        return CellProperty.builder()
                .alignment(alignment)
                .fontColor(fontColor)
                .backGroundColor(backgroundColor)
                .fontName(fontName)
                .fontSize(fontSize)
                .borderColor(borderColor)
                .specificBorder(specificBorder)
                .build();
    }

    protected void openLine() {
        this.impressor.abreLinha();
        this.currentLine++;
    }

    public void setEmail(boolean email) {
        this.scheduled = email;
    }

    public boolean isEmail() {
        return this.scheduled;
    }

}
