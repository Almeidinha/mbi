package com.msoft.mbi.cube.multi.generation;

import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import com.msoft.mbi.cube.multi.metrics.MetricaMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;

public interface Impressor {

    public void iniciaImpressao();

    public void finalizaImpressao();

    public String getValorNulo();

    public String getValorVazio();

    public void setCorBordasPadrao(String corBorda);

    public void abreLinha();

    public void fechaLinha();

    public void adicionaEstilo(CellProperty cellProperty, String nomeEstilo);

    public void adicionaEstiloCabecalhoColuna(CellProperty cellProperty, String nomeEstilo);

    public void adicionaEstiloPropriedadeEspecificaColuna(CellProperty propriedadeMetrica, String nomeEstilo);

    public void adicionaEstiloLink(CellProperty cellProperty, String nomeEstilo);

    public void imprimeColuna(String propriedadeCelula, String valorFormatado);

    public void imprimeColuna(String propriedadeCelula, String valorFormatado, int colspan, int rowspan);

    public void imprimeCabecalhoColuna(String propriedadeCelula, ColunaMetaData metaData);

    public void imprimeCabecalhoColuna(String propriedadeCelula, ColunaMetaData metaData, int colspan, int rowspan);

    public void imprimeCabecalhoColuna(String propriedadeCelula, String tituloColuna);

    public void imprimeCabecalhoDimensaoLinha(DimensaoMetaData dimensaoMetaData);

    public void imprimeCabecalhoTotalParcial(String propriedadeCelula, String valor, int colspan, int rowspan,
                                             DimensaoMetaData dimensaoTotalizada);

    public void imprimeValorColuna(String propriedadeCelula, int colspan, int rowspan, Object valor,
                                   ColunaMetaData metaData);

    public void imprimeValorColuna(String propriedadeCelula, Object valor, ColunaMetaData metaData);

    public void imprimeValorDimensaoLinha(String propriedadeCelula, int colspan, int rowspan, Object valor,
                                          DimensaoMetaData metaData);

    public void imprimeValorMetrica(String propriedadeCelula, Double valor, MetricaMetaData metaData);

    public void imprimeValorNumero(String propriedadeCelula, Double valor, int nCasasDecimais);

    public void imprimeCampoSequencia(String sequencia);

    public void imprimeCampoSequencia(DimensaoMetaData dimensaoMetaData, String sequencia, int colspan, int rowspan);

    public void imprimeValorNumeroPercentual(String nomeEstiloTotal, Double valor, int nCasasDecimais);

    void abreLinhaHead();

    void fechaLinhaHead();

    void abreLinhaBody();

    void fechaLinhaBody();

}
