package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.consult.ConsultResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class DimensionTotalized extends Dimension {

    @Getter
    @Setter
    private Object[][] results;
    @Setter
    @Getter
    private List<DimensionTotalized> childDimensions;
    private boolean isParent;
    @Getter
    private int resultNumber = 0;

    public PartialTotalization getTotalizacaoParcial(Field campo, Dimension dimensaoConsulta) {
        Object[][] valores = dimensaoConsulta.consulta(this.results);
        return this.getPartialTotalizations().getTotalPartial(valores, campo);

    }

    public boolean isDimensaoRaiz() {
        return this.getParentDimension() == null || this.getParentDimension().getValue() == null;
    }

    public DimensionTotalized(ConsultResult valor, int tamanho) {
        super(valor, tamanho, null, null);
    }

    public void setTotalizacaoParcial(Field campo, double valor, Dimension dimensaoConsulta) {
        Object[][] valores = dimensaoConsulta.consulta(this.results);
        PartialTotalization totalizacaoParcial = new PartialTotalization();
        totalizacaoParcial.setField(campo);
        totalizacaoParcial.setPartialTotalization(valor);
        totalizacaoParcial.setValues(valores);
        this.getPartialTotalizations().addToTotalPartial(totalizacaoParcial);
    }

    public double geraTotalizacaoPai(Field campo, Dimension dimensaoConsulta) {
        Object[][] valores = dimensaoConsulta.consulta(this.results);
        double valorTotal = 0;
        for (DimensionTotalized childDimension : childDimensions) {
            PartialTotalization totalizacaoFilha = childDimension.getTotalizacaoParcial(campo, dimensaoConsulta);
            valorTotal += totalizacaoFilha.getPartialTotalization();
        }
        PartialTotalization totalizacaoParcial = new PartialTotalization();
        totalizacaoParcial.setField(campo);
        totalizacaoParcial.setPartialTotalization(valorTotal);
        totalizacaoParcial.setValues(valores);
        this.getPartialTotalizations().addToTotalPartial(totalizacaoParcial);
        return valorTotal;
    }


    public void adicionaFilha(DimensionTotalized newFilha) {
        if (this.childDimensions == null)
            this.childDimensions = new ArrayList<>();
        this.childDimensions.add(newFilha);
        this.resultNumber += newFilha.getResults().length - 1;
    }


    @Override
    public void redimensionaResultados() {
        if (this.results != null)
            super.redimensionaResultados(this.results, 0, this.results.length);
    }


    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean isDimensaoPai) {
        this.isParent = isDimensaoPai;
    }

}
