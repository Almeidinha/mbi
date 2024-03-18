package com.msoft.mbi.data.api.data.filters.interpreters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.*;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.data.indicator.Operator;
import lombok.Getter;

import java.util.StringTokenizer;

public class MetricFilterInterpreter {

    private MetricFilters metricFilters;
    private final Indicator indicator;
    private String dml;
    private boolean hasSequenceFilters = false;
    private boolean hasAccumulatedFilters = false;

    @Getter
    private FilterSequence filterSequence;
    @Getter
    private FilterAccumulated filterAccumulated;

    public MetricFilterInterpreter(String dml, Indicator indicator) throws BIException {
        this.dml = dml;
        this.indicator = indicator;
        decodifica();
    }

    private void decodifica() throws BIException {
        if (dml.contains("HAVING")) {
            dml = dml.substring("HAVING".length() + 1);
            int pos = 0;
            int posAnt = 0;
            metricFilters = new MetricFilters();
            while ((pos = dml.indexOf("AND", pos)) != -1) {
                if (dml.substring(posAnt, pos).trim().startsWith("#$sequencia$!") || dml.substring(posAnt, pos).trim().startsWith("#$Acum")) {
                    break;
                }
                metricFilters.add(buscaFiltro(dml.substring(posAnt, pos)));
                pos = pos + 3;
                posAnt = pos;
            }
            String strCodigo = dml.substring(posAnt);

            String condicao1 = "";
            String condicao2 = "";
            int index = strCodigo.indexOf("AND");
            if (index != -1) {
                condicao1 = strCodigo.substring(0, index);
                condicao2 = strCodigo.substring(index + 3);
            } else {
                condicao1 = strCodigo;
            }
            condicao1 = condicao1.trim();
            condicao2 = condicao2.trim();

            if (condicao1.contains("#$sequencia$!") || condicao1.contains("#$Acum")) {
                this.buscaFiltroFuncao(condicao1);
                if (!condicao2.trim().isEmpty()) {
                    this.buscaFiltroFuncao(condicao2);
                }
            } else {
                metricFilters.add(buscaFiltro(strCodigo));
            }
        } else {
            String condicao1;
            String condicao2 = "";
            String strCodigo = dml;
            int index = strCodigo.indexOf("AND");
            if (index != -1) {
                condicao1 = strCodigo.substring(0, index);
                condicao2 = strCodigo.substring(index + 3);
            } else {
                condicao1 = strCodigo;
            }
            condicao1 = condicao1.trim();
            condicao2 = condicao2.trim();

            if (condicao1.contains("#$sequencia$!") || condicao1.contains("#$Acum")) {
                this.buscaFiltroFuncao(condicao1);
                if (!condicao2.trim().isEmpty()) {
                    this.buscaFiltroFuncao(condicao2);
                }
            }
        }
    }

    private MetricFilter buscaFiltro(String str) throws BIException {
        StringTokenizer strTok = new StringTokenizer(str);
        StringBuilder campo = new StringBuilder();
        String operador = "";
        while (strTok.countTokens() > 2) {
            String aux = String.valueOf(strTok.nextElement());
            if ("IN(".equalsIgnoreCase(aux) || "NOT".equalsIgnoreCase(aux)) {
                operador += aux;
                break;
            } else {
                campo.append(aux);
            }
        }
        if ("NOT".equalsIgnoreCase(operador)) {
            operador += " " + strTok.nextElement();
        } else if (operador.isEmpty()) {
            operador += String.valueOf(strTok.nextElement());
        }
        StringBuilder valor = new StringBuilder();
        while (strTok.countTokens() > 0) {
            valor.append((String) strTok.nextElement());
        }
        String str_codigoField;
        Field campo_aux;

        if (campo.indexOf("#$") != -1) {
            str_codigoField = campo.substring(campo.indexOf("#$") + 2, campo.lastIndexOf("$!"));
            campo_aux = indicator.getFieldByCode(str_codigoField);
        } else {
            String nome = campo.toString();
            String apelido = "";

            if (campo.toString().indexOf('(') != -1) {
                nome = campo.substring(campo.toString().indexOf('(') + 1, campo.toString().lastIndexOf(')'));
            }

            if (nome.indexOf('.') != -1) {
                apelido = nome.substring(0, nome.indexOf('.'));
                nome = nome.substring(nome.indexOf('.') + 1);
            }
            campo_aux = indicator.getFieldByName(apelido, nome);
        }

        while (valor.charAt(0) == '\'') {
            valor = new StringBuilder(valor.substring(1));
        }
        while (valor.charAt(valor.length() - 1) == '\'') {
            valor = new StringBuilder(valor.substring(0, valor.length() - 1));
            if (valor.length() < 2)
                break;
        }
        if (operador.contains("IN(") && valor.lastIndexOf(")") == valor.length() - 1) {
            valor = new StringBuilder(valor.substring(0, valor.length() - 1));
        }
        return FilterFactory.createMetricFilter(campo_aux, operador, valor.toString());
    }

    public FilterSequence buscaFilterSequence(String condicao) {
        condicao = condicao.substring(14);
        StringTokenizer strToken = new StringTokenizer(condicao);
        String operador = strToken.nextToken();
        StringBuilder valor = new StringBuilder();
        while (strToken.countTokens() > 0) {
            valor.append((String) strToken.nextElement());
        }
        FilterSequence retorno = new FilterSequence(new Operator(operador), valor.toString());
        this.hasSequenceFilters = true;
        return retorno;
    }

    public FilterAccumulated buscaFilterAccumulated(String condicao) {
        FilterAccumulated retorno;
        condicao = condicao.trim();
        int indiceParenteses = condicao.indexOf(")");
        String codigoField = condicao.substring(7, indiceParenteses);
        condicao = condicao.substring(indiceParenteses + 3);
        StringTokenizer st = new StringTokenizer(condicao);
        String oper = String.valueOf(st.nextElement());
        StringBuilder valor = new StringBuilder();
        while (st.countTokens() > 0) {
            valor.append((String) st.nextElement());
        }
        Operator operador = new Operator(oper);
        Field campo = indicator.getFieldByCode(codigoField);
        retorno = new FilterAccumulated(operador, valor.toString(), campo);

        this.hasAccumulatedFilters = true;
        return retorno;
    }

    public MetricFilters getFilters() {
        return this.metricFilters;
    }

    public boolean hasSequenceFilters() {
        return this.hasSequenceFilters;
    }

    public boolean hasAccumulatedFilters() {
        return this.hasAccumulatedFilters;
    }

    public void buscaFiltroFuncao(String condicao) {
        if (condicao.contains("#$sequencia$!")) {
            this.filterSequence = this.buscaFilterSequence(condicao);
        } else if (condicao.contains("#$Acum")) {
            this.filterAccumulated = this.buscaFilterAccumulated(condicao);
        }
    }
}
