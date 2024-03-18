package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.Filters;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import com.msoft.mbi.data.api.data.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class BIDriller {

    public BIDriller() {
    }
    
    public void addDrillDown(String valor, String codField, Indicator indicator) throws BIException {
        List<Field> cmp = indicator.getFields();
        Field campo = cmp.get(Indicator.getFieldIndex(cmp, codField));
        Field proxField = this.buscaProximaSequenciaDrillDown(campo, indicator.getFields());
        while (indicator.isMultidimensional() && proxField != null && proxField.getDisplayLocation() == Constants.LINE) {
            proxField = this.buscaProximaSequenciaDrillDown(proxField, indicator.getFields());
        }

        if (this.existeDimensao(indicator.getFields(), 1, campo.getDisplayLocation())) {
            campo.setDefaultField("N");
            campo.setDisplayLocation(0);
        } else if (proxField != null) {
            campo.setDefaultField("N");
            campo.setDisplayLocation(0);
            proxField.setDefaultField("S");
            if (indicator.isMultidimensional()) {
                proxField.setDisplayLocation(Constants.COLUMN);
            }
        }

        this.apagaFilters(campo, indicator.getDimensionFilter(), null, indicator.getFilters());
        indicator.addFiltro(campo, "=", this.formataValor(campo, valor));
    }

    private String formataValor(Field campo, String valor) throws BIException {
        try {
            if (Constants.DATE.equals(campo.getDataType()) && !"".equals(valor) && valor != null) {
                String connectionId = campo.getIndicator().getConnectionId();
                ConnectionBean conexao = new ConnectionBean(connectionId);
                SimpleDateFormat sdf = null;
                try {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = sdf.parse(valor.trim());
                    // TODO GEt from connection in DB
                    // sdf.applyPattern(conexao.getFormatoData().trim());
                    return sdf.format(d);
                } catch (ParseException e) {
                    try {
                        sdf.applyLocalizedPattern("dd/MM/yyyy");
                        Date d = sdf.parse(valor.trim());
                        // TODO GEt from connection in DB
                        // sdf.applyPattern(conexao.getFormatoData().trim());
                        return sdf.format(d);
                    } catch (ParseException e1) {
                        try {
                            sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date d = sdf.parse(valor.trim());
                            // TODO GEt from connection in DB
                            // sdf.applyPattern(conexao.getFormatoData().trim());
                            return sdf.format(d);
                        } catch (ParseException e2) {
                            sdf = new SimpleDateFormat("dd-MM-yyyy");
                            Date d = sdf.parse(valor.trim());
                            // TODO GEt from connection in DB
                            // sdf.applyPattern(conexao.getFormatoData().trim());
                            return sdf.format(d);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return valor.trim();
        }
        return valor;
    }

    public void navegaEsconde(String codField, Indicator indicator, boolean esconde) throws BIException {
        List<Field> cmp = indicator.getFields();
        Field campo = cmp.get(Indicator.getFieldIndex(cmp, codField));
        Field proxField = this.buscaProximaSequenciaDrillDown(campo, indicator.getFields());
        while (proxField != null && proxField.getDisplayLocation() == Constants.LINE) {
            proxField = this.buscaProximaSequenciaDrillDown(proxField, indicator.getFields());
        }
        if (this.existeDimensao(indicator.getFields(), 1, campo.getDisplayLocation())) {
            campo.setDefaultField("N");
            campo.setDisplayLocation(0);
        } else if (proxField != null) {
            campo.setDefaultField("N");
            campo.setDisplayLocation(0);
            proxField.setDefaultField("S");
        }
    }

    public void navega(String codField, Indicator indicator, boolean esconde) throws BIException {
        Field campo = indicator.getFields().get(Indicator.getFieldIndex(indicator.getFields(), codField));
        Field proxField = this.buscaProximaSequenciaDrillDown(campo, indicator.getFields());
        while (proxField != null && proxField.getDisplayLocation() == Constants.LINE) {
            proxField = this.buscaProximaSequenciaDrillDown(proxField, indicator.getFields());
        }
        if (proxField != null) {
            if (esconde) {
                proxField.setDefaultField("N");
                proxField.setDisplayLocation(0);
            } else {
                proxField.setDefaultField("S");
                proxField.setDisplayLocation(Constants.COLUMN);
            }
        }
    }

    public Field buscaProximaSequenciaDrillDown(Field campo, List<Field> campos) {
        int seq = campo.getDrillDownSequence();
        int indiceEscolhido = -1;
        int proximaSequencia = 9999999;
        Field campoEscolhido = null;
        for (int i = 0; i < campos.size(); i++) {
            Field campoAux = campos.get(i);
            if (campoAux != null && campoAux.getFieldType().equals("D")) {
                if (campoAux.isDrillDown() && campoAux.getDrillDownSequence() > seq && campoAux.getDrillDownSequence() < proximaSequencia) {
                    indiceEscolhido = i;
                    campoEscolhido = campos.get(indiceEscolhido);
                    proximaSequencia = campoEscolhido.getDrillDownSequence();
                }
            }
        }
        if (indiceEscolhido != -1)
            return campoEscolhido;
        else
            return null;
    }
    
    private boolean existeDimensao(List<Field> campos, int qtde, int local) {
        int cont = 0;
        for (Field campo : campos) {
            if (campo != null && campo.getFieldType().equals("D") && campo.getDefaultField().equals("S")
                    && campo.getDisplayLocation() == local) {
                cont++;
            }
        }
        return cont > qtde;
    }
    
    private void apagaFilters(Field campo, DimensionFilter filtroDimensao, DimensionFilter filtroPai, Filters filtros) throws BIException {
        if (filtroDimensao == null)
            return;
        if ((filtroDimensao.getCondition() != null) && (filtroDimensao.getField().getCode() == campo.getCode())
                && filtroDimensao.getOperator().getSymbol().equals("=")) {
            filtros.removeDimensionFilter(filtroPai, filtroDimensao, false);
        }
        if (filtroDimensao.getFilters() != null && filtroDimensao.getFilters().size() <= 1) {
            if ((filtroDimensao.getCondition() != null) && filtroDimensao.getField().getCode() == campo.getCode()) {
                filtroDimensao.getOperator().getSymbol().equals("=");
            }
        }
    }

    public void addDrillUp(Indicator indicator) throws BIException {
        Field cmpEsconder = this.buscaFieldAtual(indicator);
        if (cmpEsconder != null) {
            Field cmpMostrar = this.buscaFieldAnterior(cmpEsconder.getDrillDownSequence(), indicator.getFields());
            while (indicator.isMultidimensional() && cmpMostrar != null && cmpMostrar.getDisplayLocation() == Constants.LINE) {
                cmpMostrar = this.buscaFieldAnterior(cmpMostrar.getDrillDownSequence(), indicator.getFields());
            }
            if (cmpMostrar != null) {
                this.apagaFilters(cmpEsconder, indicator.getDimensionFilter(), null, indicator.getFilters());
                if (indicator.getDimensionFilter() != null) {
                    if (indicator.getDimensionFilter() != null) {
                        int ind = 0;
                        if (indicator.getDimensionFilter().getFilters() != null) {
                            ind = indicator.getDimensionFilter().getFilters().size();
                            while (ind > 0 && (!indicator.getDimensionFilter().getDimensionFilter(ind - 1).isDrillDown())) {
                                ind--;
                            }
                            if (ind >= 1) {
                                this.apagaFilters(cmpMostrar, indicator.getDimensionFilter().getDimensionFilter(ind - 1), indicator.getDimensionFilter(),
                                        indicator.getFilters());
                            }
                        } else if (indicator.getDimensionFilter().isDrillDown()) {
                            this.apagaFilters(cmpMostrar, indicator.getDimensionFilter(), null, indicator.getFilters());
                        }
                    }
                }
                cmpEsconder.setDefaultField("N");
                cmpEsconder.setDisplayLocation(0);
                cmpMostrar.setDefaultField("S");
                if (indicator.isMultidimensional()) {
                    cmpMostrar.setDisplayLocation(Constants.COLUMN);
                }
            }
        }
    }

    public Field buscaFieldAtual(Indicator indicator) {
        List<Field> campos = indicator.getFields();
        for (Field campo : campos) {
            if (campo != null && campo.getFieldType().equals("D") && (campo.getDefaultField().equals("S"))
                    && (!indicator.isMultidimensional() || campo.getDisplayLocation() == Constants.COLUMN)) {
                return campo;
            }
        }
        return null;
    }

    public Field buscaFieldAnterior(int seq, List<Field> campos) {
        int indiceEscolhido = -1;
        int proximaSequencia = 0;
        Field campoEscolhido = null;
        for (int i = 0; i < campos.size(); i++) {
            Field campo = campos.get(i);
            if (campo != null && campo.getFieldType().equals("D")) {
                if (campo.isDrillDown() && campo.getDrillDownSequence() < seq && campo.getDrillDownSequence() > proximaSequencia) {
                    indiceEscolhido = i;
                    campoEscolhido = campos.get(indiceEscolhido);
                    proximaSequencia = campoEscolhido.getDrillDownSequence();
                }
            }
        }
        if (indiceEscolhido != -1)
            return campoEscolhido;
        else
            return null;
    }

}
