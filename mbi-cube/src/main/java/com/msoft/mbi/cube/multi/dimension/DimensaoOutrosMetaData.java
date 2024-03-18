package com.msoft.mbi.cube.multi.dimension;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsDimensao;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoPadraoComparator;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public class DimensaoOutrosMetaData extends DimensaoMetaData {

    private DimensaoMetaData metaDataDimensaoRanking;

    public DimensaoMetaData getMetaDataDimensaoRanking() {
        return metaDataDimensaoRanking;
    }

    @Override
    public List<ColorAlertConditionsDimensao> getColorAlertCells() {
        return new ArrayList<>();
    }

    @Override
    public List<ColorAlertConditionsDimensao> getAlertasCoresLinha() {
        return new ArrayList<>();
    }

    public DimensaoOutrosMetaData(DimensaoMetaData metaDataDimensaoRanking) {
        super(metaDataDimensaoRanking.getTitle(), metaDataDimensaoRanking.getColuna(), metaDataDimensaoRanking.getTipo());
        this.metaDataDimensaoRanking = metaDataDimensaoRanking;
        this.setCubo(metaDataDimensaoRanking.getCubo());
        this.setEixoReferencia(DimensaoMetaData.LINHA);
        this.setParent(metaDataDimensaoRanking.getParent());
        this.setNivelAcimaTotalizado(metaDataDimensaoRanking.getNivelAcimaTotalizado());
        this.setAscendente(false);
        this.setHasSequenceFields(metaDataDimensaoRanking.hasSequenceFields());
        this.setComparator(DimensaoPadraoComparator.getInstance());
    }

    @Override
    public String getEstiloPadrao() {
        return CellProperty.PROPRIEDADE_CELULA_OUTROS;
    }

}
