package com.msoft.mbi.cube.multi.dimension;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.coloralertcondition.ColorAlertConditionsDimensao;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoPadraoComparator;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.Getter;

@Getter
public class DimensionMetaDataOthers extends DimensionMetaData {

    private final DimensionMetaData dimensionMetaDataRanking;

    @Override
    public List<ColorAlertConditionsDimensao> getColorAlertCells() {
        return new ArrayList<>();
    }

    @Override
    public List<ColorAlertConditionsDimensao> getColorAlertLines() {
        return new ArrayList<>();
    }

    public DimensionMetaDataOthers(DimensionMetaData dimensionMetaDataRanking) {
        super(dimensionMetaDataRanking.getTitle(), dimensionMetaDataRanking.getColumn(), dimensionMetaDataRanking.getDataType());
        this.dimensionMetaDataRanking = dimensionMetaDataRanking;
        this.setCube(dimensionMetaDataRanking.getCube());
        this.setReferenceAxis(DimensionMetaData.LINE);
        this.setParent(dimensionMetaDataRanking.getParent());
        this.setUpperLevelTotal(dimensionMetaDataRanking.getUpperLevelTotal());
        this.setAscending(false);
        this.setHasSequenceFields(dimensionMetaDataRanking.hasSequenceFields());
        this.setComparator(DimensaoPadraoComparator.getInstance());
    }

    @Override
    public String getDefaultStyle() {
        return CellProperty.CELL_PROPERTY_OTHERS;
    }

}
