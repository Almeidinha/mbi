package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.DateException;
import com.msoft.mbi.data.api.data.filters.Condition;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.FilterFactory;
import com.msoft.mbi.data.api.data.htmlbuilder.Celula;
import com.msoft.mbi.data.api.data.htmlbuilder.EstiloHTML;
import com.msoft.mbi.data.api.data.htmlbuilder.Linha;
import com.msoft.mbi.data.api.data.htmlbuilder.LinkHTML;
import com.msoft.mbi.data.api.data.util.BIData;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


@Setter
public class ColorsAlert {

    private ArrayList<ColorAlert> colorAlertList;

    public ArrayList<ColorAlert> getColorAlertList() {
        if (colorAlertList == null) {
            colorAlertList = new ArrayList<>();
        }
        return colorAlertList;
    }

    public void addToColorAlertList(ColorAlert colorAlert) {
        this.getColorAlertList().add(colorAlert);
    }

    public ArrayList<ColorAlert> getColorAlertListValues() {
        ArrayList<ColorAlert> list = new ArrayList<>();
        for (ColorAlert colorsAlert : this.getColorAlertList()) {
            if (colorsAlert != null && !colorsAlert.isCompareToAnotherField()) {
                list.add(colorsAlert);
            }
        }
        return list;
    }

    public ArrayList<ColorAlert> getListaAlertasOutroField() {
        ArrayList<ColorAlert> list = new ArrayList<>();
        for (ColorAlert colorsAlert : this.getColorAlertList()) {
            if (colorsAlert != null && colorsAlert.isCompareToAnotherField()) {
                list.add(colorsAlert);
            }
        }
        return list;
    }

    public void remove(ColorAlert alertaCores) {
        this.getColorAlertList().remove(alertaCores);
        Iterator<ColorAlert> i = this.getColorAlertList().iterator();
        int index = 0;
        while (i.hasNext()) {
            ColorAlert aux = i.next();
            aux.setSequence(index++);
        }
    }

    public void setIndicator(Indicator indicator) {
        for (ColorAlert alertaCores : this.getColorAlertList()) {
            if (alertaCores != null) {
                alertaCores.setIndicator(indicator);
            }
        }
    }

    public boolean buscaAplicaAlertaValor(Object valor, Celula celula, Linha linha, Field campo, String funcao, int numeroPosicaoDecimais, Dimension dimensao, boolean ehHTML) throws BIException, DateException {
        boolean retorno = false;
        ColorAlert alertaCores;
        ArrayList<ColorAlert> listaAlertasField = this.getListaAlertasValorFuncaoField(funcao, campo);
        for (ColorAlert colorAlert : listaAlertasField) {
            alertaCores = colorAlert;
            if (alertaCores != null) {
                if (!ehRestritaPorLinha(alertaCores.getAction(), dimensao)) {
                    Operator operador = alertaCores.getOperator();

                    if (campo == null || campo.getFieldType().equals(Constants.METRIC)) {
                        valor = BIUtil.formatValue(Double.parseDouble(String.valueOf(valor)), numeroPosicaoDecimais);
                        double valComp = Double.parseDouble(alertaCores.getFirstDoubleValue());
                        valComp = BIUtil.formatValue(valComp, numeroPosicaoDecimais);
                        double segundoValor = 0;
                        if (alertaCores.getSecondValue() != null) {
                            segundoValor = BIUtil.formatValue(Double.parseDouble(alertaCores.getSegundoValorDouble()), numeroPosicaoDecimais);
                        }
                        if (this.comparaValorDouble((Double) valor, operador, valComp, segundoValor)) {
                            Object estilo = this.aplicaPropriedade(alertaCores, celula, linha, ehHTML, Constants.DIMENSION.equals(alertaCores.getFirstField().getFieldType()));
                            if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
                                dimensao.setLineAppliedStyle(estilo);
                                retorno = true;
                            }
                            break;
                        }
                    } else {
                        if (!campo.getDataType().equals(Constants.DATE)) {
                            String valorComp = alertaCores.getFirstValue();
                            if (valorComp.trim().startsWith("@|") && valorComp.trim().endsWith("|")) {
                                DimensionFilter filtroDimensao = FilterFactory.createDimensionFilter(campo, alertaCores.getOperator().getSymbol(), valorComp);
                                Condition condicao = filtroDimensao.getCondition();
                                valorComp = condicao.getValue();
                            }
                            if (this.comparaValorString((String) valor, operador, valorComp)) {
                                Object estilo = this.aplicaPropriedade(alertaCores, celula, linha, ehHTML, Constants.DIMENSION.equals(alertaCores.getFirstField().getFieldType()));
                                if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
                                    dimensao.setLineAppliedStyle(estilo);
                                    retorno = true;
                                }
                                break;
                            }
                        } else {
                            String dataString = alertaCores.getFirstValue();
                            String dataString2 = alertaCores.getSecondValue();
                            if (valor != null && !valor.toString().trim().isEmpty()) {
                                BIData data = new BIData(valor.toString(), BIData.FORMATO_DIA_MES_ANO_TELA);
                                if (dataString.trim().startsWith("@|") && dataString.trim().endsWith("|")) {
                                    DimensionFilter filtroDimensao = FilterFactory.createDimensionFilter(campo, alertaCores.getOperator().getSymbol(), dataString);
                                    if (filtroDimensao.getFilters() != null && !filtroDimensao.getFilters().isEmpty()) {
                                        Iterator<DimensionFilter> iT = filtroDimensao.getFilters().iterator();
                                        boolean todosVerdadeiro = true;
                                        while (iT.hasNext()) {
                                            DimensionFilter filtro = iT.next();
                                            if (filtro != null && filtro.getCondition() != null) {
                                                Condition condicao = filtro.getCondition();
                                                BIData dataComparacao = new BIData(condicao.getFormattedValue(), BIData.FORMATO_DIA_MES_ANO_TELA);
                                                if (!comparaValorDate(data, condicao.getOperator(), dataComparacao, null)) {
                                                    todosVerdadeiro = false;
                                                }
                                            }
                                        }
                                        if (todosVerdadeiro) {
                                            Object estilo = this.aplicaPropriedade(alertaCores, celula, linha, ehHTML, Constants.DIMENSION.equals(alertaCores.getFirstField().getFieldType()));
                                            if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
                                                dimensao.setLineAppliedStyle(estilo);
                                                retorno = true;
                                            }
                                            break;
                                        }
                                    } else {
                                        Condition condicao = filtroDimensao.getCondition();
                                        BIData dataComparacao = new BIData(condicao.getFormattedValue(), BIData.FORMATO_DIA_MES_ANO_TELA);
                                        if (comparaValorDate(data, condicao.getOperator(), dataComparacao, null)) {
                                            Object estilo = this.aplicaPropriedade(alertaCores, celula, linha, ehHTML, Constants.DIMENSION.equals(alertaCores.getFirstField().getFieldType()));
                                            if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
                                                dimensao.setLineAppliedStyle(estilo);
                                                retorno = true;
                                            }
                                        }
                                    }
                                } else {
                                    BIData dataComparacao = new BIData(dataString.trim(), BIData.FORMATO_DIA_MES_ANO_TELA);
                                    BIData dataComparacao2 = null;
                                    if (alertaCores.getOperator().getSymbol().equals(Operators.BETWEEN)) {
                                        dataComparacao2 = new BIData(dataString2.trim(), BIData.FORMATO_DIA_MES_ANO_TELA);
                                    }

                                    if (comparaValorDate(data, alertaCores.getOperator(), dataComparacao, dataComparacao2)) {
                                        Object estilo = this.aplicaPropriedade(alertaCores, celula, linha, ehHTML, Constants.DIMENSION.equals(alertaCores.getFirstField().getFieldType()));
                                        if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
                                            dimensao.setLineAppliedStyle(estilo);
                                            retorno = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return retorno;
    }

    private ArrayList<ColorAlert> getListaAlertasValorFuncaoField(String funcao, Field campo) {
        ArrayList<ColorAlert> retorno = new ArrayList<>();
        for (ColorAlert alertaCores : this.getColorAlertListValues()) {
            if (alertaCores != null) {
                if (funcao.trim().equals(alertaCores.getFirstFieldFunction().trim())) {
                    if (ColorAlert.TOTALIZACAO_HORIZONTAL.equals(funcao.trim()) || ColorAlert.TOTALIZACAO.equals(funcao.trim())) {
                        retorno.add(alertaCores);
                    } else {
                        if (campo.equals(alertaCores.getFirstField())) {
                            retorno.add(alertaCores);
                        }
                    }
                }
            }
        }
        return retorno;
    }

    public boolean buscaAplicaAlertaOutroCampo(double valor, Field primeiroField, String funcaoPrimeiroField, Object[][] valores, Celula celula, Linha linha,
                                               int numeroPosicaoDecimais, Dimension dimColuna, Dimension dimLinha, CachedResults registroTotalizado,
                                               int tipoComparacao, boolean ehHTML) throws BIException {
        boolean retorno = false;
        ColorAlert alertaCores;
        ArrayList<ColorAlert> listaAlertasField = this.getListaAlertasOutroFieldFuncaoField(funcaoPrimeiroField, primeiroField);
        Iterator<ColorAlert> i = listaAlertasField.iterator();
        while (i.hasNext()) {
            alertaCores = i.next();
            if (alertaCores != null) {

                if (primeiroField == null || primeiroField.getFieldType().equals(Constants.METRIC)) {
                    if (alertaCores.getSecondField() != null && "S".equals(alertaCores.getSecondField().getDefaultField())) {
                        if (!ehRestritaPorLinha(alertaCores.getAction(), dimColuna)) {
                            valor = BIUtil.formatValue(valor, numeroPosicaoDecimais);

                            double valComp = 0;
                            String funcaoField = alertaCores.getFirstFieldFunction();
                            boolean mesmoField = false;
                            if (alertaCores.getFirstField().equals(alertaCores.getSecondField())) {
                                funcaoField = alertaCores.getSecondFieldFunction();
                                mesmoField = true;
                            }
                            valComp = this.getValorComparacao(alertaCores.getSecondField(), alertaCores.getFirstField(), valores, funcaoField, dimColuna, dimLinha, alertaCores.getIndicator(), registroTotalizado, tipoComparacao, mesmoField);
                            valComp = BIUtil.formatValue(valComp, numeroPosicaoDecimais);
                            if (this.aplicaAlertaOutroField(alertaCores, valor, valComp, numeroPosicaoDecimais, celula, linha, ehHTML, dimColuna)) {
                                if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
                                    retorno = true;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return retorno;
    }

    private double getValorComparacao(Field campo, Field campoPrincipal, Object[][] valores, String funcaoField, Dimension dimColuna,
                                      Dimension dimLinha, Indicator indicador, CachedResults registroTotalizado, int tipoComparacao,
                                      boolean mesmoField) throws BIException {
        double[] acumuladoLinha = dimColuna.getAccumulatedLine();
        HashMap<String, Double> totaisLinha = dimColuna.getTotalLines();
        HashMap<Field, Double> participacaoHorizontal = dimColuna.getHorizontalParticipation();

        double retorno = -1;
        int indiceField = getIndiceField(valores, campo);
        int indiceFieldPrincipal = getIndiceField(valores, campoPrincipal);

        switch (funcaoField.trim()) {
            case ColorAlert.SEM_FUNCAO -> retorno = (Double) valores[1][indiceField];
            case ColorAlert.ANALISE_HORIZONTAL -> {
                if (campo.isHorizontalAnalysis()) {
                    for (int i = indiceField + 1; valores[0][i] != null; i++) {
                        if (((Field) valores[0][i]).isHorizontalAnalysis()) {
                            if (valores[1][i] != null) {
                                double ini, fin, res;
                                fin = (Double) valores[1][indiceField];
                                ini = (Double) valores[1][i];
                                if (ini != 0) {
                                    res = (ini - fin) / ini;
                                    retorno = (res * -1) * 100;
                                } else {
                                    retorno = 0;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            case ColorAlert.PARTICIPACAO_HORIZONTAL -> {
                if (campo.isHorizontalParticipation()) {
                    double ini = (Double) valores[1][indiceField];
                    double total = totaisLinha.get(String.valueOf(campo.getFieldId()));
                    if (total != 0) {
                        retorno = (ini / total) * 100;
                    }
                }
            }
            case ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL -> {
                if (campo.isHorizontalParticipationAccumulated()) {
                    double valAux;
                    double ini = (Double) valores[1][indiceField];
                    double total = totaisLinha.get(String.valueOf(campo.getFieldId()));

                    if (ini != 0) {
                        if (participacaoHorizontal.get(campo) != null) {
                            valAux = participacaoHorizontal.get(campo);
                            if (total != 0) {
                                retorno = (ini / total + valAux) * 100;
                            }
                        } else {
                            if (total != 0) {
                                retorno = (ini / total) * 100;
                            }
                        }
                    } else {
                        retorno = 0;
                    }
                }
            }
            case ColorAlert.ACUMULADO_HORIZONTAL -> {
                if (!"N".equals(campo.getAccumulatedLine()) && acumuladoLinha.length > indiceField) {
                    double valorComp = acumuladoLinha[indiceField];
                    if (campo.getAccumulatedLine().equals("E")) {
                        valorComp = dimColuna.calculaExpressaoAcumulado(campo, indiceField);
                    }
                    retorno = valorComp;
                }
            }
            case ColorAlert.MEDIA_HORIZONTAL -> {
                if (campo.isMediaLine()) {
                    double valorComp = acumuladoLinha[indiceField];
                    if (campo.getAccumulatedLine().equals("E")) {
                        valorComp = dimColuna.calculaExpressaoAcumulado(campo, indiceField);
                    }
                    retorno = valorComp / dimColuna.getColumnLineAmount();
                }
            }
            case ColorAlert.ANALISE_VERTICAL -> {
                if (campo.isVerticalAnalysis()) {
                    double perc = (Double) valores[1][indiceField] / (Double) valores[1][indiceField + 1];
                    retorno = perc * 100;
                }
            }
            case ColorAlert.PARTICIPACAO_ACUMULADA -> {
                if (campo.isAccumulatedParticipation()) {
                    Double soma = (Double) valores[1][indiceField];
                    if (indiceFieldPrincipal > indiceField) {
                        soma = 0d;
                    }
                    soma = dimColuna.atualizaTeste(campo, valores[1], soma, indiceField);
                    Double valor = (Double) valores[1][indiceField + 1];
                    if (valor != 0) {
                        retorno = (soma / valor) * 100;
                    } else {
                        retorno = 0;
                    }
                }
            }
            case ColorAlert.ACUMULADO_VERTICAL -> {
                if (campo.isAccumulatedValue()) {
                    Double soma = (Double) valores[1][indiceField];
                    if (indiceFieldPrincipal > indiceField) {
                        soma = 0d;
                    }
                    soma = dimColuna.atualizaTeste(campo, valores[1], soma, indiceField);
                    retorno = soma;
                }
            }
            case ColorAlert.TOTALIZACAO_VERTICAL -> {
                if (campo.isTotalizingField()) {
                    for (int k = 0; k < valores[0].length; k++) {
                        if (valores[0][k] != null && valores[0][k].equals(campo)) {
                            retorno = (Double) valores[1][k + 1];
                            if (campo.isExpression() && campo.isApplyTotalizationExpression()) {
                                if (registroTotalizado != null) {
                                    Expression.aplicaExpressaoNoRegistroTotalizado(indicador, campo, registroTotalizado);
                                    retorno = Double.parseDouble(registroTotalizado.getValor(campo.getFieldId()));
                                }
                            }
                            break;
                        }
                    }
                }
            }
            case ColorAlert.TOTALIZACAO_PARCIAL -> {
                if (campo.isPartialTotalization()) {
                    double soma;
                    if (mesmoField) {
                        valores = dimColuna.consulta(valores);
                    }
                    PartialTotalization totalizacaoParcial = indicador.getPartialTotalizations().getTotalizacaoParcial(valores, campo);
                    if (totalizacaoParcial != null) {
                        soma = totalizacaoParcial.getPartialTotalization();
                        if (campo.isApplyTotalizationExpression()) {
                            Expression.aplicaExpressaoNoRegistroTotalizado(indicador, campo, registroTotalizado);
                            soma = registroTotalizado.getDouble(campo.getFieldId());
                        }
                        retorno = soma;
                    }
                }
            }
            case ColorAlert.TOTALIZACAO_HORIZONTAL -> retorno = dimColuna.getTotalLine();
            case ColorAlert.TOTALIZACAO -> retorno = dimColuna.getLineSum();
        }
        return retorno;
    }

    private boolean aplicaAlertaOutroField(ColorAlert alertaCores, double valor, double valComp, int numeroPosicaoDecimais,
                                           Celula celula, Linha linha, boolean ehHTML, Dimension dimensao) {
        boolean retorno = false;
        Operator operador = alertaCores.getOperator();
        double valorAuxiliar = BIUtil.formatValue(Double.parseDouble(alertaCores.getSecondFieldFunction()), numeroPosicaoDecimais);

        boolean aplica;
        if ("V".equals(alertaCores.getValueType())) {
            aplica = this.comparaOutroFieldDouble(valor, operador, valComp, valorAuxiliar);
        } else {
            aplica = this.comparaOutroFieldPercentualDouble(valor, operador, valComp, valorAuxiliar);
        }
        if (aplica) {
            Object estilo = this.aplicaPropriedade(alertaCores, celula, linha, ehHTML, Constants.DIMENSION.equals(alertaCores.getFirstField().getFieldType()));
            if (ColorAlert.LINHA.equals(alertaCores.getAction())) {
                dimensao.setLineAppliedStyle(estilo);
                retorno = true;
            }
        }
        return retorno;
    }

    private ArrayList<ColorAlert> getListaAlertasOutroFieldFuncaoField(String funcao, Field campo) {
        ArrayList<ColorAlert> retorno = new ArrayList<>();
        for (ColorAlert alertaCores : this.getListaAlertasOutroField()) {
            if (alertaCores != null) {
                if (funcao.trim().equals(alertaCores.getFirstFieldFunction().trim())) {
                    if (ColorAlert.TOTALIZACAO_HORIZONTAL.equals(funcao.trim()) || ColorAlert.TOTALIZACAO.equals(funcao.trim())) {
                        retorno.add(alertaCores);
                    } else {
                        if (campo.equals(alertaCores.getFirstField())) {
                            retorno.add(alertaCores);
                        }
                    }
                }
            }
        }
        return retorno;
    }

    private Object aplicaPropriedade(ColorAlert alertaCores, Celula celula, Linha linha, boolean ehHTML, boolean isDimensao) {
        Object estilo = this.criaEstilo(alertaCores, ehHTML, celula);

        if (alertaCores.getAction().equals(ColorAlert.LINHA)) {
            linha.setEstilo(estilo, isDimensao);
            linha.setAlertaAplicado(true);
            linha.setEstiloAplicado(estilo);

            List<Celula> celulas = linha.getCelulas();
            if (celulas != null) {
                for (Celula celulaAux : celulas) {
                    if (celulaAux != null) {
                        if (!celulaAux.isAlertaAplicado() && !celulaAux.isDimensaoColuna()) {
                            celulaAux.setEstilo(null);
                            celulaAux.setClasse("");
                            celulaAux.setAlertaAplicado(true);
                            if (celulaAux.getConteudo().getClass().getName().endsWith("HTML")) {
                                LinkHTML link = (LinkHTML) celulaAux.getConteudo();
                                link.setClasse("");
                                link.setEstilo((EstiloHTML) estilo);
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        } else {
            celula.setEstilo(estilo);
            if (celula.getConteudo().getClass().getName().endsWith("HTML")) {
                LinkHTML link = (LinkHTML) celula.getConteudo();
                link.setClasse("");
                link.setEstilo((EstiloHTML) estilo);
            }
            celula.setAlertaAplicado(true);
        }
        return estilo;
    }


    public Object criaEstilo(ColorAlert alertaCores, boolean ehHTML, Celula celula) {
        return criaEstiloHTML(alertaCores);
    }

        public Object criaEstiloHTML(ColorAlert alertaCores) {
            EstiloHTML estilo = new EstiloHTML();
            estilo.setFontFamily(alertaCores.getAlertProperty().getFontName());
            estilo.setFontSize(alertaCores.getAlertProperty().getFontSize());
            estilo.setFontColor(alertaCores.getAlertProperty().getFontColor());
            estilo.setBackgroundColor(alertaCores.getAlertProperty().getCellBackgroundColor());

            if (alertaCores.getAlertProperty().hasBold()) {
                estilo.setFontWeight("bold");
            }
            if (alertaCores.getAlertProperty().hasItalic()) {
                estilo.setFontStyle("italic");
            }

            return estilo;
        }

        private boolean comparaValorDouble(double valor, Operator operador, double valorComp, double segundoValor) {
            boolean retorno = false;
            switch (operador.getSymbol().trim()) {
                case Operators.GREATER_THAN -> {
                    if (valor > valorComp) {
                        retorno = true;
                    }
                }
                case Operators.GREATER_TAN_OR_EQUAL -> {
                    if (valor >= valorComp) {
                        retorno = true;
                    }
                }
                case Operators.EQUAL_TO -> {
                    if (valor == valorComp) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN -> {
                    if (valor < valorComp) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN_OR_EQUAL -> {
                    if (valor <= valorComp) {
                        retorno = true;
                    }
                }
                case Operators.BETWEEN -> {
                    if (valor >= valorComp && valor <= segundoValor) {
                        retorno = true;
                    }
                }
                case Operators.NOT_EQUAL_TO -> {
                    if (valor != valorComp) {
                        retorno = true;
                    }
                }
            }
            return retorno;
        }

        private boolean comparaOutroFieldDouble(double valor, Operator operador, double valorComp, double valorAuxiliar) {
            boolean retorno = false;
            switch (operador.getSymbol().trim()) {
                case Operators.GREATER_THAN -> {
                    if (valor - valorComp > valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.GREATER_TAN_OR_EQUAL -> {
                    if (valor - valorComp >= valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.EQUAL_TO -> {
                    if (valor == (valorComp + valorAuxiliar)) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN -> {
                    if (valorComp - valor > valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN_OR_EQUAL -> {
                    if (valorComp - valor >= valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.NOT_EQUAL_TO -> {
                    if (valor != (valorComp + valorAuxiliar)) {
                        retorno = true;
                    }
                }
            }
            return retorno;
        }

        private boolean comparaValorDate(BIData data, Operator operador, BIData dataComparacao, BIData dataComparacao2) {
            boolean retorno = false;
            int comparacao = data.compareTo(dataComparacao);
            switch (operador.getSymbol().trim()) {
                case Operators.GREATER_THAN -> {
                    if (comparacao > 0) {
                        retorno = true;
                    }
                }
                case Operators.GREATER_TAN_OR_EQUAL -> {
                    if (comparacao == 0 || comparacao > 0) {
                        retorno = true;
                    }
                }
                case Operators.EQUAL_TO -> {
                    if (comparacao == 0) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN -> {
                    if (comparacao < 0) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN_OR_EQUAL -> {
                    if (comparacao == 0 || comparacao < 0) {
                        retorno = true;
                    }
                }
                case Operators.BETWEEN -> {
                    if (comparacao == 0 || comparacao > 0) {
                        if (dataComparacao2 != null) {
                            int comparacao2 = data.compareTo(dataComparacao2);
                            if (comparacao2 == 0 || comparacao2 < 0) {
                                retorno = true;
                            }
                        }
                    }
                }
                case Operators.NOT_EQUAL_TO -> {
                    if (comparacao != 0) {
                        retorno = true;
                    }
                }
            }
            return retorno;
        }

        private boolean comparaOutroFieldPercentualDouble(double valor, Operator operador, double valorComp, double valorAuxiliar) {
            boolean retorno = false;

            valorAuxiliar = (valorAuxiliar / 100) * valorComp;

            switch (operador.getSymbol().trim()) {
                case Operators.GREATER_THAN -> {
                    if (valor > valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.GREATER_TAN_OR_EQUAL -> {
                    if (valor >= valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.EQUAL_TO -> {
                    if (valor == valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN -> {
                    if (valor < valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.LESS_THAN_OR_EQUAL -> {
                    if (valor <= valorAuxiliar) {
                        retorno = true;
                    }
                }
                case Operators.NOT_EQUAL_TO -> {
                    if (valor != valorAuxiliar) {
                        retorno = true;
                    }
                }
            }
            return retorno;
        }

        private boolean comparaValorString(String valor, Operator operador, String valorComp) {
            boolean retorno = false;
            if (Operators.EQUAL_TO.equals(operador.getSymbol().trim())) {
                if (valor.trim().equals(valorComp.trim())) {
                    retorno = true;
                }
            } else if (Operators.STARTS_WITH.equals(operador.getSymbol())) {
                if (valor.startsWith(valorComp.trim())) {
                    retorno = true;
                }
            } else if (Operators.CONTAINS.equals(operador.getSymbol())) {
                if (valor.contains(valorComp.trim())) {
                    retorno = true;
                }
            } else if (Operators.NOT_CONTAINS.equals(operador.getSymbol())) {
                if (!valor.contains(valorComp.trim())) {
                    retorno = true;
                }
            } else if (Operators.NOT_EQUAL_TO.equals(operador.getSymbol().trim())) {
                if (!valor.trim().equals(valorComp.trim())) {
                    retorno = true;
                }
            }
            return retorno;
        }

        private int getIndiceField(Object[][] valores, Field campo) {
            int retorno = -1;
            for (int i = 0; i < valores[0].length; i++) {
                if (valores[0][i] != null && campo.equals(valores[0][i])) {
                    retorno = i;
                    break;
                }
            }
            return retorno;
        }

        public boolean ehRestritaPorLinha(String acao, Dimension dimensao) {
            return ColorAlert.LINHA.equals(acao) && dimensao.isAlertLineStyle();
        }

        public ColorsAlert clone(Indicator indicador) {
            ColorsAlert retorno = new ColorsAlert();
            retorno.setColorAlertList(new ArrayList<>());
            if (this.colorAlertList != null) {
                for (ColorAlert alertaCores : this.colorAlertList) {
                    ColorAlert novoAlerta = alertaCores.clone(indicador);

                    retorno.addToColorAlertList(novoAlerta);
                }
            }
            return retorno;
        }
    }
