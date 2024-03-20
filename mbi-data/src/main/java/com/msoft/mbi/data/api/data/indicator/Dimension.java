package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.consult.ConsultResult;
import com.msoft.mbi.data.api.data.consult.ConsultResultFactory;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.DateException;
import com.msoft.mbi.data.api.data.filters.ConditionalExpression;
import com.msoft.mbi.data.api.data.filters.FilterAccumulated;
import com.msoft.mbi.data.api.data.filters.FilterSequence;
import com.msoft.mbi.data.api.data.htmlbuilder.*;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@SuppressWarnings("unused")
public class Dimension {


    private Dimension[] bottomDimensions;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Dimension[] columnDimension;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Dimension[] lineDimension;
    @Setter(AccessLevel.NONE)
    private ConsultResult value;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int index = 1;

    private Dimension parentDimension;
    @Setter(AccessLevel.NONE)
    private Object[][] results;
    @Setter(AccessLevel.NONE)
    private double totalLine;
    private double[] accumulatedLine;
    @Getter(AccessLevel.NONE)
    private Indicator indicator;
    private int counter;
    private boolean isCounterEnabled = false;
    private int columnLineAmount;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public double[] lineExpression;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private PartialTotalizations partialTotalizations;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public HashMap<String, Double> totalLines = null;
    @Setter(AccessLevel.NONE)
    private HashMap<Field, Double> horizontalParticipation = null;
    private boolean mountTableWithoutLink = false;
    private boolean filterBySequence = false;
    private boolean filterByAccumulated = false;
    private double lineSum;
    @Setter(AccessLevel.NONE)
    private boolean alertLineStyle = false;
    @Setter(AccessLevel.NONE)
    private Object lineAppliedStyle = null;
    private double parentValue = 0;
    private boolean parentValueFound = false;

    public Dimension(ConsultResult valor, int tamanho, Dimension[] linha, Dimension[] coluna) {
        this.value = valor;
        this.bottomDimensions = new Dimension[tamanho + 1];
        this.results = new Object[tamanho + 1][];
        this.lineDimension = linha;
        this.columnDimension = coluna;
    }

    public int getAlturaDimensoesAbaixo() {
        int retorno = 0;
        if (this.bottomDimensions[0] != null) {
            if (this.bottomDimensions[0].getValue().getField().getDefaultField().equals("S")) {
                retorno = 1;
            }
            retorno += this.bottomDimensions[0].getAlturaDimensoesAbaixo();
        }
        return retorno;
    }

    public int getQtdeDimensoesAbaixo(boolean adicionaTotal) {
        int cont = 0;
        int aux;
        int qtdeFiltrAcum = 0;
        int qtdeDimensoes = 0;
        for (Dimension biDimension : this.bottomDimensions) {
            if (biDimension != null) {
                qtdeDimensoes++;
                if (!biDimension.filterByAccumulated) {
                    aux = biDimension.getQtdeDimensoesAbaixo(adicionaTotal);
                    if (aux > 1) {
                        cont += aux;
                    } else {
                        cont++;
                    }
                } else {
                    qtdeFiltrAcum++;
                }
            } else {
                break;
            }
        }
        if (qtdeFiltrAcum > 0 && qtdeFiltrAcum == qtdeDimensoes) {
            this.filterByAccumulated = true;
        }
        if (adicionaTotal && this.getValue().getField().isPartialTotalization() && this.getValue().getField().getDefaultField().equals("S") && this.temDimensoesAbaixo())
            cont++;
        return cont;
    }

    public void atualizaFiltroAcumulado() {
        int qtdeFiltrAcum = 0;
        int qtdeDimensoes = 0;
        if (this.temDimensoesAbaixo()) {
            for (Dimension biDimension : this.bottomDimensions) {
                if (biDimension != null) {
                    biDimension.atualizaFiltroAcumulado();
                } else {
                    break;
                }
            }
        }
        for (Dimension biDimension : this.bottomDimensions) {
            if (biDimension != null) {
                qtdeDimensoes++;
                if (biDimension.isFilterByAccumulated()) {
                    qtdeFiltrAcum++;
                }
            } else {
                break;
            }
        }
        if (qtdeFiltrAcum == qtdeDimensoes && qtdeFiltrAcum != 0) {
            this.filterByAccumulated = true;
        }
    }

    public boolean temMaisDimensoesVisualizadasAbaixo() {
        boolean retorno = false;
        for (Dimension bottonDimension : this.bottomDimensions) {
            if (bottonDimension != null) {
                if (bottonDimension.getValue().getField().getDefaultField().equals("S")) {
                    retorno = true;
                    break;
                } else {
                    retorno = bottonDimension.temMaisDimensoesVisualizadasAbaixo();
                    if (retorno) {
                        break;
                    }
                }
            }
        }
        return retorno;
    }

    public boolean temMaisDimensoesAbaixo() {
        boolean retorno = false;
        for (Dimension biDimension : this.bottomDimensions) {
            if (biDimension != null) {
                if (!biDimension.getValue().getField().getDefaultField().equals("N")) {
                    retorno = true;
                    break;
                } else {
                    retorno = biDimension.temMaisDimensoesVisualizadasAbaixo();
                    if (retorno) {
                        break;
                    }
                }
            }
        }
        return retorno;
    }

    public boolean temMaisDimensoesVisualizadasAcima() {
        boolean retorno = false;
        if (this.parentDimension != null) {
            if (this.parentDimension.getValue().getField().getDefaultField().equals("S")) {
                retorno = true;
            } else {
                retorno = this.parentDimension.temMaisDimensoesVisualizadasAcima();
            }
        }
        return retorno;
    }

    public boolean temDimensoesAbaixo() {
        for (Dimension biDimension : bottomDimensions) {
            if (biDimension != null) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String retorno;
        retorno = " " + this.value.getValor(0).toString().trim();
        for (Dimension biDimension : this.bottomDimensions) {
            if (biDimension != null) {
                retorno += " " + biDimension;
            } else {
                break;
            }
        }
        retorno += "\n";
        return retorno;
    }

    public double calculoLinha(Field metrica, int posicao) throws BIException {
        for (int i = 2; i < this.results[0].length; i++) {
            if (this.results[0][i] == metrica) {
                return this.calculoLinha(i, posicao);
            }
        }
        return 0d;
    }


    private double calculoLinha(int indice, int posicao) throws BIException {
        Field campo = (Field) this.results[0][indice];

        if (this.accumulatedLine == null)
            this.accumulatedLine = new double[this.results[0].length];
        if (this.lineExpression == null) {
            this.lineExpression = new double[this.accumulatedLine.length];
        }

        double valAux = 0;

        if (this.temDimensoesAbaixo()) {
            double total = 0d;
            double valor;
            for (Dimension biDimension : this.bottomDimensions) {
                if (biDimension != null) {
                    biDimension.setIndicator(this.indicator);
                    valor = biDimension.calculoLinha(indice, indice);
                    this.accumulatedLine[indice] += valor;
                    total += valor;

                    if (campo.isSumLine()) {
                        this.totalLine += valor;
                    }
                    if (campo.isHorizontalParticipation() || campo.isHorizontalParticipationAccumulated() || campo.getAccumulatedOrder() != 0) {
                        valAux += valor;
                    }
                } else {
                    break;
                }
            }

            if (campo.isHorizontalParticipation() || campo.isHorizontalParticipationAccumulated() || campo.getAccumulatedOrder() != 0) {
                double finalValAux = valAux;
                this.getTotalLines().computeIfAbsent(String.valueOf(campo.getFieldId()), k -> finalValAux);
            }

            return total;
        } else {
            for (int i = 1; i < this.results.length; i++) {
                if (this.results[i][indice] != null) {
                    this.accumulatedLine[indice] += (Double) this.results[i][indice];
                }
            }
            if (campo.isSumLine()) {
                this.totalLine += this.accumulatedLine[indice];
            }

            if (campo.isHorizontalParticipation() || campo.isHorizontalParticipationAccumulated() || campo.getAccumulatedOrder() != 0) {
                valAux = this.accumulatedLine[indice];
                double finalValAux1 = valAux;
                this.getTotalLines().computeIfAbsent(String.valueOf(campo.getFieldId()), k -> finalValAux1);
            }
        }

        return this.accumulatedLine[indice];
    }

    private boolean hasTotalizacaoParcial() {
        if (this.value.getField().isPartialTotalization()) {
            return this.temDimensoesAbaixo();
        }
        return false;
    }


    private void caminha(Dimension dimensao, HTMLTable tabela, Field[] metricas, boolean coluna, Object[][] resultadosTodos, int indiceDimensaoLinha, int indiceDimensaoRaizLinha) throws BIException, DateException {
        Object[][] resultado = dimensao.consulta(resultadosTodos);
        if (resultado.length > 1 && dimensao.temDimensoesAbaixo()) {
            Dimension[] dimensoesAbaixo = dimensao.getBottomDimensions();
            for (int a = 0; a < dimensoesAbaixo.length; a++) {
                if (dimensoesAbaixo[a] != null) {
                    if (indiceDimensaoLinha != 0 && indiceDimensaoRaizLinha == 0) {
                        indiceDimensaoRaizLinha = -1;
                    }
                    caminha(dimensoesAbaixo[a], tabela, metricas, coluna, resultado, a, indiceDimensaoRaizLinha);
                } else {
                    break;
                }
            }
        } else if (!this.filterByAccumulated) {
            HTMLLine linha;

            linha = tabela.getCurrentLine();
            if (tabela.getLineIndex(linha) % 2 == 1) {
                linha.setBackGroundColor("#FFFFFF");
            } else {
                linha.setBackGroundColor("#D7E3F7");
            }
            for (int i = 0; i < metricas.length; i++) {
                if (metricas[i] != null && !metricas[i].getName().isEmpty()) {
                    if (coluna) {
                        for (int x = 0; x < metricas.length - (i + 1); x++) {
                            linha = tabela.getPreviousLine(linha);
                        }
                    }
                    if (resultado.length > 1) {
                        for (int j = 0; j < resultado[0].length; j++) {
                            if (resultado[0][j] != null && resultado[0][j].equals(metricas[i])) {
                                if (!((Field) resultado[0][j]).getDefaultField().equals("T")) {
                                    linha.addCell(new HTMLCell());
                                    linha.getCurrentCell().setNowrap(true);
                                    if (resultado[0][j] == null) {
                                        linha.getCurrentCell().setAlignment("left");
                                    } else {
                                        linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());
                                    }
                                    HTMLStyle estilo = MultidimensionalStyles.getInstancia().getEstilos().get(MultidimensionalStyles.ESTILO_VAL_METRICA_COLUNA);

                                    int decimalPositions = ((Field) resultado[0][j]).getNumDecimalPositions();
                                    linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(resultado[1][j], decimalPositions));

                                    boolean aplicouEstilo;
                                    aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor(resultado[1][j], linha.getCurrentCell(), linha, (Field) resultado[0][j], ColorAlert.SEM_FUNCAO, ((Field) resultado[0][j]).getNumDecimalPositions(), this, true);
                                    if (aplicouEstilo) {
                                        this.setAlertLineStyle(aplicouEstilo);
                                    }
                                    aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo((Double) resultado[1][j], (Field) resultado[0][j], ColorAlert.SEM_FUNCAO, resultado, linha.getCurrentCell(), linha, ((Field) resultado[0][j]).getNumDecimalPositions(), this, dimensao, null, 0, true);
                                    if (aplicouEstilo) {
                                        this.setAlertLineStyle(aplicouEstilo);
                                    }

                                    if (this.alertLineStyle) {
                                        linha.setStyle(lineAppliedStyle);
                                    } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                        linha.getCurrentCell().setStyle(estilo);
                                    }
                                    Double soma = null;
                                    if (((Field) resultado[0][j]).isAccumulatedParticipation() || ((Field) resultado[0][j]).isAccumulatedValue()) {
                                        soma = this.resgataSomaAcumulada(resultado, j);
                                    }
                                    if (((Field) resultado[0][j]).isTotalizingField()) {
                                        if (((Field) resultado[0][j]).isVerticalAnalysis()) {
                                            linha.addCell(new HTMLCell());
                                            linha.getCurrentCell().setNowrap(true);
                                            if (resultado[0][j] == null || (Double) resultado[1][j + 1] == 0) {
                                                linha.getCurrentCell().setContent("-");
                                            } else {
                                                linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());
                                                double perc = (Double) resultado[1][j] / (Double) resultado[1][j + 1];
                                                linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(perc, 2));

                                                aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor(perc * 100, linha.getCurrentCell(), linha, (Field) resultado[0][j], ColorAlert.ANALISE_VERTICAL, ((Field) resultado[0][j]).getNumDecimalPositions(), this, true);
                                                if (aplicouEstilo) {
                                                    this.setAlertLineStyle(aplicouEstilo);
                                                }

                                                aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo(perc * 100, (Field) resultado[0][j], ColorAlert.ANALISE_VERTICAL, resultado, linha.getCurrentCell(), linha, 2, this, null, null, 0, true);
                                                if (aplicouEstilo) {
                                                    this.setAlertLineStyle(aplicouEstilo);
                                                }

                                                if (this.alertLineStyle) {
                                                    linha.setStyle(lineAppliedStyle);
                                                } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                                    linha.getCurrentCell().setStyle(estilo);
                                                }

                                            }
                                        }
                                        if (((Field) resultado[0][j]).isAccumulatedParticipation()) {
                                            linha.addCell(new HTMLCell());
                                            linha.getCurrentCell().setNowrap(true);
                                            if (resultado[0][j] == null || (Double) resultado[1][j + 1] == 0) {
                                                linha.getCurrentCell().setContent("-");
                                            } else {
                                                linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());

                                                linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(soma / (Double) resultado[1][j + 1], 2));

                                                aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor(soma / (Double) resultado[1][j + 1] * 100, linha.getCurrentCell(), linha, (Field) resultado[0][j], ColorAlert.PARTICIPACAO_ACUMULADA, 2, this, true);
                                                if (aplicouEstilo) {
                                                    this.setAlertLineStyle(aplicouEstilo);
                                                }
                                                aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo((soma / (Double) resultado[1][j + 1]) * 100, (Field) resultado[0][j], ColorAlert.PARTICIPACAO_ACUMULADA, resultado, linha.getCurrentCell(), linha, 2, this, null, null, 0, true);
                                                if (aplicouEstilo) {
                                                    this.setAlertLineStyle(aplicouEstilo);
                                                }

                                                if (this.alertLineStyle) {
                                                    linha.setStyle(lineAppliedStyle);
                                                } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                                    linha.getCurrentCell().setStyle(estilo);
                                                }
                                            }
                                        }
                                    }
                                    if (((Field) resultado[0][j]).isAccumulatedValue()) {
                                        linha.addCell(new HTMLCell());
                                        linha.getCurrentCell().setNowrap(true);
                                        if (resultado[0][j] == null) {
                                            linha.getCurrentCell().setAlignment("left");
                                        } else {
                                            linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());
                                        }

                                        linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(soma, decimalPositions));
                                        aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor(soma, linha.getCurrentCell(), linha, (Field) resultado[0][j], ColorAlert.ACUMULADO_VERTICAL, ((Field) resultado[0][j]).getNumDecimalPositions(), this, true);
                                        if (aplicouEstilo) {
                                            this.setAlertLineStyle(aplicouEstilo);
                                        }

                                        aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo(soma, (Field) resultado[0][j], ColorAlert.ACUMULADO_VERTICAL, resultado, linha.getCurrentCell(), linha, ((Field) resultado[0][j]).getNumDecimalPositions(), this, null, null, 0, true);
                                        if (aplicouEstilo) {
                                            this.setAlertLineStyle(aplicouEstilo);
                                        }

                                        if (this.alertLineStyle) {
                                            linha.setStyle(lineAppliedStyle);
                                        } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                            linha.getCurrentCell().setStyle(estilo);
                                        }
                                    }

                                    if (((Field) resultado[0][j]).isHorizontalAnalysis() && !(indiceDimensaoLinha == 0 && indiceDimensaoRaizLinha == 0)) {
                                        linha.addCell(new HTMLCell());
                                        linha.getCurrentCell().setNowrap(true);
                                        if (resultado[0][j] == null) {
                                            linha.getCurrentCell().setAlignment("left");
                                        } else {
                                            linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());
                                        }

                                        for (int ii = j + 1; resultado[0][ii] != null; ii++) {
                                            if (((Field) resultado[0][ii]).isHorizontalAnalysis()) {
                                                if (resultado[1][ii] != null) {
                                                    double ini, fin, res;
                                                    fin = (Double) resultado[1][j];
                                                    ini = (Double) resultado[1][ii];
                                                    if (ini != fin) {
                                                        if (ini != 0) {
                                                            res = (ini - fin) / ini;
                                                            linha.getCurrentCell().setContent(BIUtil.formatDoubleToText((-1 * res), 2));
                                                            aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor(-1 * res * 100, linha.getCurrentCell(), linha, (Field) resultado[0][j], ColorAlert.ANALISE_HORIZONTAL, ((Field) resultado[0][j]).getNumDecimalPositions(), this, true);
                                                            if (aplicouEstilo) {
                                                                this.setAlertLineStyle(aplicouEstilo);
                                                            }

                                                            aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo(-1 * res * 100, (Field) resultado[0][j], ColorAlert.ANALISE_HORIZONTAL, resultado, linha.getCurrentCell(), linha, 2, this, null, null, 0, true);
                                                            if (aplicouEstilo) {
                                                                this.setAlertLineStyle(aplicouEstilo);
                                                            }
                                                        } else {
                                                            if (fin > 0) {
                                                                linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(1, 2));
                                                            } else if (fin < 0) {
                                                                linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(-1, 2));
                                                            }
                                                        }
                                                    } else {
                                                        linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(0, 2));
                                                    }
                                                } else {
                                                    linha.getCurrentCell().setContent("-");
                                                }
                                                break;
                                            }
                                        }
                                        if (this.alertLineStyle) {
                                            linha.setStyle(lineAppliedStyle);
                                        } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                            linha.getCurrentCell().setStyle(estilo);
                                        }
                                    }

                                    if (((Field) resultado[0][j]).isHorizontalParticipation()) {
                                        linha.addCell(new HTMLCell());
                                        linha.getCurrentCell().setNowrap(true);
                                        if (resultado[0][j] == null) {
                                            linha.getCurrentCell().setAlignment("left");
                                        } else {
                                            linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());
                                        }

                                        Field campo = (Field) resultado[0][j];
                                        if (campo.isHorizontalParticipation()) {
                                            if (resultado[1][j] != null && campo.isMetric()) {
                                                double ini = (Double) resultado[1][j];
                                                double total = this.getTotalLines().get(String.valueOf(campo.getFieldId()));

                                                if (total != 0) {
                                                    aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor((ini / total) * 100, linha.getCurrentCell(), linha, campo, ColorAlert.PARTICIPACAO_HORIZONTAL, 2, this, true);
                                                    if (aplicouEstilo) {
                                                        this.setAlertLineStyle(aplicouEstilo);
                                                    }

                                                    aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo((ini / total) * 100, campo, ColorAlert.PARTICIPACAO_HORIZONTAL, resultado, linha.getCurrentCell(), linha, 2, this, null, null, 0, true);
                                                    if (aplicouEstilo) {
                                                        this.setAlertLineStyle(aplicouEstilo);
                                                    }
                                                }

                                                if (ini != 0) {
                                                    linha.getCurrentCell().setContent(BIUtil.formatDoubleToText((ini / total), 2));
                                                } else {
                                                    linha.getCurrentCell().setContent("0");
                                                }

                                                if (this.alertLineStyle) {
                                                    linha.setStyle(lineAppliedStyle);
                                                } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                                    linha.getCurrentCell().setStyle(estilo);
                                                }
                                            }
                                        }
                                    }

                                    if (((Field) resultado[0][j]).isHorizontalParticipationAccumulated()) {
                                        linha.addCell(new HTMLCell());
                                        linha.getCurrentCell().setNowrap(true);
                                        if (resultado[0][j] == null) {
                                            linha.getCurrentCell().setAlignment("left");
                                        } else {
                                            linha.getCurrentCell().setAlignment(((Field) resultado[0][j]).getColumnAlignment());
                                        }

                                        Field campo = (Field) resultado[0][j];
                                        if (horizontalParticipation == null) {
                                            horizontalParticipation = new HashMap<>();
                                        }
                                        if (resultado[1][j] != null && campo.isMetric()) {
                                            double valAux;
                                            double ini = (Double) resultado[1][j];
                                            double total = this.getTotalLines().get(String.valueOf(campo.getFieldId()));

                                            if (ini != 0) {
                                                if (horizontalParticipation.get(campo) != null) {
                                                    valAux = horizontalParticipation.get(campo);
                                                    ini = ini / total + valAux;
                                                    horizontalParticipation.remove(campo);
                                                } else {
                                                    ini = ini / total;
                                                }
                                                horizontalParticipation.put(campo, ini);
                                                linha.getCurrentCell().setContent(BIUtil.formatDoubleToText(ini, 2));

                                                aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaValor(ini * 100, linha.getCurrentCell(), linha, campo, ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL, 2, this, true);
                                                if (aplicouEstilo) {
                                                    this.setAlertLineStyle(aplicouEstilo);
                                                }

                                                aplicouEstilo = this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo(ini * 100, campo, ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL, resultado, linha.getCurrentCell(), linha, 2, this, null, null, 0, true);
                                                if (aplicouEstilo) {
                                                    this.setAlertLineStyle(aplicouEstilo);
                                                }
                                            } else {
                                                linha.getCurrentCell().setContent("0");
                                            }
                                        }
                                        if (this.alertLineStyle) {
                                            linha.setStyle(lineAppliedStyle);
                                        } else if (!linha.getCurrentCell().isAppliedAlert()) {
                                            linha.getCurrentCell().setStyle(estilo);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        int folhas;
                        folhas = this.contFolhas(dimensao);
                        if (folhas == 0)
                            folhas = 1;

                        HTMLStyle estilo = MultidimensionalStyles.getInstancia().getEstilos().get(MultidimensionalStyles.ESTILO_VAL_METRICA_COLUNA);

                        for (int w = 0; w < folhas; w++) {
                            if (!metricas[i].getDefaultField().equals("T")) {
                                linha.addCell(new HTMLCell());
                                linha.getCurrentCell().setNowrap(true);
                                linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                linha.getCurrentCell().setContent("-");
                                if (metricas[i].isTotalizingField()) {
                                    if (metricas[i].isVerticalAnalysis()) {
                                        linha.addCell(new HTMLCell());
                                        linha.getCurrentCell().setNowrap(true);
                                        linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                        linha.getCurrentCell().setStyle(estilo);
                                        linha.getCurrentCell().setContent("-");
                                    }
                                    if (metricas[i].isAccumulatedParticipation()) {
                                        linha.addCell(new HTMLCell());
                                        linha.getCurrentCell().setNowrap(true);
                                        linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                        linha.getCurrentCell().setStyle(estilo);
                                        linha.getCurrentCell().setContent("-");
                                    }
                                }
                                if (metricas[i].isAccumulatedValue()) {
                                    linha.addCell(new HTMLCell());
                                    linha.getCurrentCell().setNowrap(true);
                                    linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                    linha.getCurrentCell().setStyle(estilo);
                                    linha.getCurrentCell().setContent("-");
                                }
                                if (metricas[i].isHorizontalAnalysis() && !(w == 0 && indiceDimensaoLinha == 0 && indiceDimensaoRaizLinha == 0)) {
                                    linha.addCell(new HTMLCell());
                                    linha.getCurrentCell().setNowrap(true);
                                    linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                    linha.getCurrentCell().setStyle(estilo);
                                    linha.getCurrentCell().setContent("-");
                                }
                                if (metricas[i].isHorizontalParticipation()) {
                                    linha.addCell(new HTMLCell());
                                    linha.getCurrentCell().setNowrap(true);
                                    linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                    linha.getCurrentCell().setStyle(estilo);
                                    linha.getCurrentCell().setContent("-");
                                }
                                if (metricas[i].isHorizontalParticipationAccumulated()) {
                                    linha.addCell(new HTMLCell());
                                    linha.getCurrentCell().setNowrap(true);
                                    linha.getCurrentCell().setAlignment(metricas[i].getColumnAlignment());
                                    linha.getCurrentCell().setStyle(estilo);
                                    linha.getCurrentCell().setContent("-");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Double resgataSomaAcumulada(Object[][] resultado, int posicaoFieldAtual) {
        for (int i = posicaoFieldAtual + 1; i < resultado[1].length; i++) {
            if (resultado[0][i] != null && (((Field) resultado[0][i]).isAccumulatedValue() || ((Field) resultado[0][i]).isAccumulatedParticipation()) && ((Field) resultado[0][i]).getName().equalsIgnoreCase("")) {
                return (Double) resultado[1][i];
            }
        }
        return 0D;
    }

    public Double atualizaTeste(Field metrica, Object[] resultado, Double valor, int indice) {
        Field campo = null;
        int i = 0;
        int posicaoLinha = 0;
        for (i = 0; i < this.results[0].length; i++) {
            campo = (Field) this.results[0][i];
            if (Constants.DIMENSION.equals(campo.getFieldType())) {
                if (Constants.LINE == campo.getDisplayLocation()) {
                    posicaoLinha = i;
                    break;
                }
            }
        }
        Dimension[] dimensoes = this.lineDimension;
        Dimension dimensao = null;
        for (int ii = 0; ii < this.lineDimension.length; ii++) {
            if (dimensoes[ii] != null) {
                if (dimensoes[ii].getValue().getValor(0).equals(resultado[posicaoLinha])) {
                    dimensao = dimensoes[ii];
                    dimensoes = dimensao.bottomDimensions;
                    ii = -1;
                    posicaoLinha++;
                }
            } else {
                break;
            }
        }
        for (i = indice + 1; i < dimensao.results[0].length; i++) {
            campo = (Field) this.results[0][i];
            if (campo.getFieldType() != null) {
                break;
            } else if (campo.isAccumulatedValue() || campo.isAccumulatedParticipation()) {
                Double atual = (Double) dimensao.results[1][i];
                if (atual == null) {
                    atual = 0d;
                }
                atual = atual + valor;
                return atual;
            }
        }
        return 0d;
    }

    private int contFolhas(Dimension raiz) {
        int cont = 0;
        if (raiz.temDimensoesAbaixo()) {
            Dimension[] dimensoesAbaixo = raiz.getBottomDimensions();
            if (dimensoesAbaixo[0].temDimensoesAbaixo()) {
                for (Dimension biDimension : dimensoesAbaixo) {
                    if (biDimension != null) {
                        cont += contFolhas(biDimension);
                    }
                }
            } else {
                cont = raiz.getQtdeDimensoesAbaixo(false);
            }
        }
        return cont;
    }

    public Object[][] consulta(Object[][] resultados) {
        int coluna = -1;
        Object[][] objFields = new Object[resultados.length][resultados[0].length];
        objFields[0] = resultados[0];
        for (int i = 0; i < resultados[0].length; i++) {
            if (resultados[0][i] != null) {
                if (((Field) resultados[0][i]).getFieldId() == this.value.getField().getFieldId()) {
                    coluna = i;
                    break;
                }
            }
        }
        int cont = 1;
        if (coluna != -1) {
            for (int i = 1; i < resultados.length; i++) {
                try {
                    if (this.value.getValor(0) != null && this.value.getValor(0).equals(resultados[i][coluna])) {

                        Dimension dimenAux = this;
                        int contDimensao = 1;
                        boolean sair = false;
                        while (dimenAux.getParentDimension() != null) {
                            dimenAux = dimenAux.getParentDimension();

                            if (!dimenAux.value.getValor(0).equals(resultados[i][coluna - contDimensao])) {
                                sair = true;
                                break;
                            }
                            contDimensao++;
                        }
                        if (sair)
                            continue;

                        objFields[cont] = resultados[i];
                        cont++;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        Object[][] objAux2 = new Object[cont][objFields[0].length];
        cont = 0;
        boolean temValor;
        for (Object[] objField : objFields) {
            if (objField != null) {
                temValor = false;

                for (Object o : objField) {
                    if (o != null) {
                        temValor = true;
                        break;
                    }
                }

                if (temValor) {
                    objAux2[cont] = objField;
                    cont++;
                }
            }
        }
        int posicao = 0;
        for (int i = 2; i < objAux2[0].length; i++) {
            if (objAux2[0][i] != null && ((Field) objAux2[0][i]).isHorizontalAnalysis()) {
                if (((Field) objAux2[0][i]).getTitle().contains(" AH%")) {
                    for (int ii = 2; ii < objAux2.length; ii++) {
                        if ("D".equals((objAux2[0][i]))) {
                            objAux2[ii][i] = objAux2[ii - 1][posicao];
                        } else {
                            objAux2[ii][i] = objAux2[1][posicao];
                        }
                    }
                } else {
                    posicao = i;
                }
            }
        }
        return objAux2;
    }

    public void toHTMLLinha(HTMLTable tabela, HTMLLine linha, HTMLLine linhaInferior, Field[] metricasLinha, Field[] camposColuna, int colspanHeader, int indiceDimensaoLinha, int indiceDimensaoRaizLinha) throws BIException, DateException, CloneNotSupportedException {
        boolean todosNull = true;
        int contador_temp;

        MultidimensionalStyles multidimensionalEstilos = MultidimensionalStyles.getInstancia();

        HTMLStyle estilo1 = multidimensionalEstilos.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_DIMENSAO_LINHA);

        HTMLStyle estilo2 = multidimensionalEstilos.getEstilos().get(MultidimensionalStyles.ESTILO_VAL_DIMENSAO_LINHA);

        for (int i = 0; i < this.bottomDimensions.length; i++) {
            if (this.bottomDimensions[i] != null) {
                if (!this.bottomDimensions[i].getValue().getField().getDefaultField().equals("T")) {
                    this.bottomDimensions[i].setMountTableWithoutLink(this.mountTableWithoutLink);
                    if (linha.getCells().isEmpty()) {
                        linha.addCell(new HTMLCell());
                        linha.getCurrentCell().setAlignment("right");

                        linha.getCurrentCell().setTHCell(true);

                        linha.getCurrentCell().setStyle(estilo1);
                        linha.getCurrentCell().setColspan(colspanHeader);
                        LinkHTML linkField = new LinkHTML("javascript:alteraField('" + this.bottomDimensions[i].getValue().getField().getFieldId() + "');", this.bottomDimensions[i].getValue().getField().getTitle(), "textWhiteOff");
                        if (!this.isMountTableWithoutLink()) {
                            linha.getCurrentCell().setContent("<b>" + linkField + "</b>");
                        } else {
                            linha.getCurrentCell().setContent("<b>" + this.bottomDimensions[i].getValue().getField().getTitle() + "</b>");
                        }
                        linha.getCurrentCell().setAppliedAlert(true);
                    }

                    todosNull = false;
                    linha.addCell(new HTMLCell());
                    if (this.bottomDimensions[i].temDimensoesAbaixo()) {
                        int colspan = this.bottomDimensions[i].getQtdeDimensoesAbaixo(false);
                        if (metricasLinha != null && metricasLinha.length > 0) {

                            contador_temp = 0;
                            for (Field field : metricasLinha) {
                                if (!(field != null && field.getName().isEmpty() && field.getTitle().equals("total"))) {
                                    if (!field.getDefaultField().equals("T")) {
                                        contador_temp++;
                                    }
                                }
                            }

                            colspan *= contador_temp;
                        }
                        linha.getCurrentCell().setColspan(colspan);
                    } else {
                        if (metricasLinha != null) {
                            contador_temp = 0;
                            for (Field field : metricasLinha) {
                                if (!(field != null && field.getName().isEmpty() && field.getTitle().equals("total"))) {
                                    if (!field.getDefaultField().equals("T")) {
                                        contador_temp++;
                                    }
                                }
                            }
                            linha.getCurrentCell().setColspan(contador_temp);
                        }
                    }

                    int quantidadeMetricasAH = this.getQuantidadeMetricasComAnaliseHorizontal(metricasLinha);
                    if (indiceDimensaoRaizLinha == 0 && i == 0 && quantidadeMetricasAH > 0) {
                        linha.getCurrentCell().setColspan(linha.getCurrentCell().getColspan() - quantidadeMetricasAH);
                    }

                    linha.getCurrentCell().setAlignment("center");
                    linha.getCurrentCell().setContent(this.bottomDimensions[i].getValue().getFormattedValue(0));
                    boolean aplicouAlertaLinha = this.indicator.getColorAlerts().buscaAplicaAlertaValor(this.bottomDimensions[i].getValue().getFormattedValue(0), linha.getCurrentCell(), linha, this.bottomDimensions[i].getValue().getField(), ColorAlert.SEM_FUNCAO, this.bottomDimensions[i].getValue().getField().getNumDecimalPositions(), this, true);
                    if (aplicouAlertaLinha) {
                        linha.setAppliedAlert(true);
                    }

                    if (!linha.isAppliedAlert() && !linha.getCurrentCell().isAppliedAlert()) {
                        linha.getCurrentCell().setStyle(estilo2);
                    }

                    linha.getCurrentCell().setAlignment(this.bottomDimensions[i].getValue().getField().getColumnAlignment());
                    linha.getCurrentCell().setWidth(String.valueOf(this.bottomDimensions[i].getValue().getField().getColumnWidth()));

                    if (linhaInferior == null) {
                        linhaInferior = new HTMLLine();
                        tabela.addLine(linhaInferior);
                    }
                    if (i != 0 && indiceDimensaoRaizLinha == 0) {
                        indiceDimensaoRaizLinha = -1;
                    }

                    this.bottomDimensions[i].setIndicator(this.indicator);
                    this.bottomDimensions[i].setMountTableWithoutLink(this.mountTableWithoutLink);
                    this.bottomDimensions[i].toHTMLLinha(tabela, linhaInferior, tabela.getNextLine(linhaInferior), metricasLinha, camposColuna, colspanHeader, i, indiceDimensaoRaizLinha);
                } else {

                    if (i != 0 && indiceDimensaoRaizLinha == 0) {
                        indiceDimensaoRaizLinha = -1;
                    }

                    todosNull = false;
                    this.bottomDimensions[i].setIndicator(this.indicator);
                    this.bottomDimensions[i].setMountTableWithoutLink(this.mountTableWithoutLink);
                    this.bottomDimensions[i].toHTMLLinha(tabela, linha, tabela.getNextLine(linha), metricasLinha, camposColuna, colspanHeader, i, indiceDimensaoRaizLinha);
                }
            }
        }
        if (todosNull) {
            if (linha.getCells().isEmpty()) {
                if (camposColuna != null) {
                    HTMLStyle estilo = multidimensionalEstilos.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_DIMENSAO_LINHA);

                    if (this.indicator.isUsesSequence()) {
                        linha.addCell(new HTMLCell());
                        linha.getCurrentCell().setBackGroundColor("#3377CC");
                        linha.getCurrentCell().setBorderColor("#FFFFFF");
                        linha.getCurrentCell().setStyle(estilo);
                        linha.getCurrentCell().setAlignment("center");
                        linha.getCurrentCell().setContent("Seq");
                    }

                    for (Field field : camposColuna) {
                        if (field != null && !field.getDefaultField().equals("T")) {
                            linha.addCell(new HTMLCell());
                            linha.getCurrentCell().setBackGroundColor("#3377CC");
                            linha.getCurrentCell().setBorderColor("#FFFFFF");
                            linha.getCurrentCell().setStyle(estilo);
                            linha.getCurrentCell().setAlignment("center");
                            linha.getCurrentCell().setTHCell(true);
                            HTMLTable tabelaAux = new HTMLTable();
                            tabelaAux.setWidth("100%");
                            tabelaAux.addLine(new HTMLLine());
                            tabelaAux.getCurrentLine().addCell(new HTMLCell());
                            tabelaAux.getCurrentLine().getCurrentCell().setWidth("100%");
                            HTMLStyle estiloAux = (HTMLStyle) estilo.clone();
                            tabelaAux.getCurrentLine().getCurrentCell().setStyle(estiloAux);
                            LinkHTML linkField = new LinkHTML("javascript:alteraField('" + field.getFieldId() + "');", field.getTitle(), "textWhiteOff");
                            if (!this.isMountTableWithoutLink()) {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent(linkField);
                            } else {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent(field.getTitle());
                            }

                            tabelaAux.getCurrentLine().addCell(new HTMLCell());

                            boolean temFiltro = false;
                            if (field.indicator.getFilters().getDimensionFilter() != null) {
                                temFiltro = field.indicator.checkFilters(field.indicator.getFilters().getDimensionFilter(), field);
                            }

                            LinkHTML link;
                            HTMLImage imagem;

                            if (temFiltro) {
                                imagem = new HTMLImage("imagens/_filter.gif", "12px", "12px");
                            } else {
                                imagem = new HTMLImage("imagens/_not_filter.png", "16px", "16px");
                            }

                            imagem.setId(field.getNickname() + field.getFieldId());

                            link = new LinkHTML("javascript:filtroHeader(" + field.getFieldId() + ",'" + imagem.getId() + "');", imagem);
                            tabelaAux.getCurrentLine().getCurrentCell().setContent("&nbsp;" + link);

                            tabelaAux.getCurrentLine().addCell(new HTMLCell());

                            link = null;
                            if (field.isNavigable()) {
                                link = new LinkHTML("javascript:browseDown(" + field.getFieldId() + ");", new HTMLImage("imagens/_mais.gif"));
                            } else if (field.isNavigableUpwards()) {
                                link = new LinkHTML("javascript:browseUp(" + field.getFieldId() + ");", new HTMLImage("imagens/_menos.gif"));
                            }
                            if (link != null && !this.isMountTableWithoutLink()) {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent(link);
                            } else {
                                tabelaAux.getCurrentLine().getCurrentCell().setContent("&nbsp;");
                            }
                            linha.getCurrentCell().setWidth(String.valueOf(field.getColumnWidth()));
                            linha.getCurrentCell().setContent(tabelaAux);
                        }
                    }
                }
            }
            if (metricasLinha != null) {

                HTMLStyle estilo = multidimensionalEstilos.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_METRICA_LINHA);

                for (Field field : metricasLinha) {
                    if (field != null) {
                        if (!field.getDefaultField().equals("T") && !(field.getName().isEmpty() && field.getTitle().equals("total"))) {
                            if (!(indiceDimensaoRaizLinha == 0 && indiceDimensaoLinha == 0 && field.isHorizontalAnalysis() && field.getTitle().contains("AH%"))) {
                                linha.addCell(new HTMLCell());
                                linha.getCurrentCell().setBackGroundColor("#A2C8E8");
                                linha.getCurrentCell().setTHCell(true);
                                linha.getCurrentCell().setStyle(estilo);
                                linha.getCurrentCell().setWidth(String.valueOf(field.getColumnWidth()));
                                linha.getCurrentCell().setAlignment(field.getColumnAlignment());
                                linha.getCurrentCell().setNowrap(true);
                                LinkHTML linkField = new LinkHTML("javascript:alteraField('" + field.getFieldId() + "');", field.getTitle(), "blueLinkDrillDown");
                                if (!this.isMountTableWithoutLink() && !field.isChildField()) {
                                    linha.getCurrentCell().setContent(linkField);
                                } else {
                                    linha.getCurrentCell().setContent(field.getTitle());
                                }
                            }
                        }
                    }
                }
            } else {
                linha.addCell(new HTMLCell());
                linha.getCurrentCell().setAlignment("center");
                linha.getCurrentCell().setBackGroundColor("#A2C8E8");
                linha.getCurrentCell().setContent("&nbsp;");
            }
        }
    }

    public void setResults(Object[][] results) {
        this.results[0] = results[0];
        this.results[this.index] = results[1];
        this.index++;
        if (this.parentDimension != null) {
            this.parentDimension.setResults(results);
        }
    }

    public Dimension getUltimaDimesao() {
        Dimension ret = null;
        Dimension aux = this;
        do {
            if (aux.bottomDimensions != null && aux.bottomDimensions.length > 0) {
                ret = aux;
                aux = aux.bottomDimensions[0];
            }
        } while (aux != null);
        return ret;
    }

    public void redimensionaResultados() {
        for (Dimension bottonDimension : this.bottomDimensions) {
            if (bottonDimension != null) {
                bottonDimension.redimensionaResultados();
            } else {
                this.redimensionaResultados(this.results, 0, this.results.length);
                break;
            }
        }
    }

    protected void redimensionaResultados(Object[][] vetor, int inicial, int fim) {
        if (vetor[vetor.length - 1] != null)
            return;
        if (vetor[0] == null) {
            inicial = 0;
            fim = 0;
        }
        if (inicial > (fim - 20)) {
            int indice;
            for (indice = fim - 1; indice >= 0; indice--) {
                if (vetor[indice] != null) {
                    break;
                }
            }
            this.results = new Object[indice + 1][];
            System.arraycopy(vetor, 0, this.results, 0, indice + 1);
        } else {
            int posicao = (inicial + fim) / 2;
            if (this.results[posicao] == null) {
                this.redimensionaResultados(vetor, inicial, posicao);
            } else {
                this.redimensionaResultados(vetor, posicao, fim);
            }
        }
    }

    public void caminhaDimensoesLinha(Dimension[] dimensoes, HTMLTable tabela) {
        if (dimensoes[0] != null) {
            for (Dimension dimensoe : dimensoes) {
                if (dimensoe != null) {
                    dimensoe.caminhaDimensoesLinha(dimensoe.bottomDimensions, tabela);
                } else {
                    break;
                }
            }
        }
    }

    private boolean escreveTotalParcial(HTMLLine linhaHTML, int indiceLinha, int sequencia, List<DimensionTotalized> dimensoesTotalizadas) throws BIException, DateException {
        boolean total = false;
        HashMap<Field, Object> participParcialHorizontal = new HashMap<>();
        HashMap<Field, List<Object>> participHorizontais = new HashMap<>();

        HTMLCell celula;
        MultidimensionalStyles multidimensionalEstilos = MultidimensionalStyles.getInstancia();

        HTMLStyle estilo = multidimensionalEstilos.getEstilos().get(MultidimensionalStyles.ESTILO_DESC_METRICA_LINHA);

        if (this.temDimensoesAbaixo()) {
            celula = new HTMLCell();
            linhaHTML.addCell(celula);
            celula.setStyle(estilo);
            celula.setNowrap(true);
            celula.setContent("Total");
            celula.setDimensionColumn(true);
            celula.setAlignment("left");
            celula.setBackGroundColor("#CCCCCC");
            celula.setTHCell(true);
            celula.setColspan(this.getAlturaDimensoesAbaixo());
            for (int i = 0; i < this.columnDimension.length; i++) {
                if (this.lineDimension[i] != null) {
                    total = total || this.escreveTotalParcial(this.lineDimension[i], this.results, linhaHTML, sequencia, i, i, participParcialHorizontal, dimensoesTotalizadas, participHorizontais);
                } else {
                    break;
                }
            }
            Field campo;
            for (int ii = 3; ii < this.results[0].length; ii++) {
                campo = (Field) this.results[0][ii];
                double aux = 0d;
                if (this.accumulatedLine != null)
                    aux = this.accumulatedLine[ii];

                if (campo != null && !campo.getDefaultField().equals("T")) {

                    
                    if (campo.isApplyTotalizationExpression()) {
                        this.partialTotalizations = this.indicator.getPartialTotalizations();
                        if (this.accumulatedLine != null && this.partialTotalizations != null) {
                            this.accumulatedLine[ii] = this.partialTotalizations.getAcumuladoTotalizParcial(campo, sequencia);
                        }
                    }
                    if (!campo.getAccumulatedLine().equals("N")) {
                        celula = new HTMLCell();
                        linhaHTML.addCell(celula);
                        celula.setNowrap(true);
                        celula.setStyle(estilo);
                        celula.setAlignment(campo.getColumnAlignment());
                        int decimalPositions = campo.getNumDecimalPositions();

                        if (campo.isPartialTotalization()) {
                            this.accumulatedLine[ii] = this.getAcumuladoLinhaAtualizado(ii);
                            if ("E".equals(campo.getAccumulatedLine())) {
                                this.accumulatedLine[ii] = this.calculaExpressaoAcumulado(campo, ii);
                            }
                            celula.setContent(BIUtil.formatDoubleToText(this.accumulatedLine[ii], decimalPositions));
                            this.indicator.getColorAlerts().buscaAplicaAlertaValor(this.accumulatedLine[ii], celula, linhaHTML, campo, ColorAlert.TOTALIZACAO_PARCIAL, campo.getNumDecimalPositions(), this, true);
                        } else {
                            celula.setContent("-");
                        }
                    }
                    if (campo.isSumLine() && this.accumulatedLine != null) {
                        this.totalLine -= aux;
                        this.totalLine += this.accumulatedLine[ii];
                        total = true;
                    }
                }
            }

            for (int ii = 3; ii < this.results[0].length; ii++) {
                campo = (Field) this.results[0][ii];
                if (campo != null && !campo.getDefaultField().equals("T")) {
                    if (campo.isMediaLine()) {
                        celula = new HTMLCell();
                        linhaHTML.addCell(celula);
                        celula.setNowrap(true);
                        celula.setStyle(estilo);
                        celula.setAlignment(campo.getColumnAlignment());
                        int decimalPositions = campo.getNumDecimalPositions();
                        if (campo.isPartialTotalization()) {
                            if ("E".equals(campo.getAccumulatedLine())) {
                                this.accumulatedLine[ii] = this.calculaExpressaoAcumulado(campo, ii);
                            }
                            if (getColumnLineAmount() > 0) {
                                celula.setContent(BIUtil.formatDoubleToText(this.accumulatedLine[ii] / getColumnLineAmount(), decimalPositions));
                                this.indicator.getColorAlerts().buscaAplicaAlertaValor(this.accumulatedLine[ii] / getColumnLineAmount(), celula, linhaHTML, campo, ColorAlert.TOTALIZACAO_PARCIAL, campo.getNumDecimalPositions(), this, true);
                            } else {
                                celula.setContent(BIUtil.formatDoubleToText(this.accumulatedLine[ii], decimalPositions));
                                this.indicator.getColorAlerts().buscaAplicaAlertaValor(this.accumulatedLine[ii], celula, linhaHTML, campo, ColorAlert.TOTALIZACAO_PARCIAL, campo.getNumDecimalPositions(), this, true);
                            }
                        } else {
                            celula.setContent("-");
                        }
                    }
                }
            }
            if (total) {
                celula = new HTMLCell();
                linhaHTML.addCell(celula);
                celula.setStyle(estilo);
                celula.setNowrap(true);
                celula.setAlignment("right");
                celula.setContent(BIUtil.formatDoubleToText(this.totalLine, 2));
            }
        }
        return total;
    }

    private boolean escreveTotalParcial(Dimension dimensao,
                                        Object[][] resultados, HTMLLine linha, int sequencia, int indiceDimensaoLinha, int indiceDimensaoRaizLinha,
                                        HashMap<Field, Object> participParcialHorizontal, List<DimensionTotalized> dimensoesTotalizadas,
                                        HashMap<Field, List<Object>> participHorizontais) throws BIException, DateException {
        Object[][] valores = dimensao.consulta(resultados);
        boolean total = false;
        if (dimensao.temDimensoesAbaixo()) {
            for (int i = 0; i < dimensao.bottomDimensions.length; i++) {
                if (dimensao.bottomDimensions[i] != null) {
                    dimensao.setIndicator(this.indicator);
                    dimensao.setAccumulatedLine(this.accumulatedLine);

                    if (indiceDimensaoLinha != 0 && indiceDimensaoRaizLinha == 0) {
                        indiceDimensaoRaizLinha = -1;
                    }

                    dimensao.escreveTotalParcial(dimensao.bottomDimensions[i], valores, linha, sequencia, i, indiceDimensaoRaizLinha, participParcialHorizontal, dimensoesTotalizadas, participHorizontais);
                } else {
                    break;
                }
            }
        } else {

            CachedResults registroTotalizado = this.indicator.getTableRecords().getRegistroTotalizado();
            registroTotalizado.next();
            registroTotalizado = this.geraRegistroTotalizado(valores, registroTotalizado, sequencia, dimensoesTotalizadas, dimensao);

            Field campo;
            double soma;
            new HTMLCell();
            HTMLCell celula;

            HTMLStyle estilo = MultidimensionalStyles.getInstancia().getEstilos().get(MultidimensionalStyles.ESTILO_VAL_METRICA_LINHA);

            for (int ii = 3; ii < valores[0].length; ii++) {
                campo = (Field) valores[0][ii];

                if (!campo.getDefaultField().equals("T")) {
                    if (campo.isHorizontalAnalysis() && campo.getTitle().contains("AH%") && indiceDimensaoLinha == 0 && indiceDimensaoRaizLinha == 0) {
                        Field pai = this.getFieldPai(campo, valores);
                        if (pai.isPartialTotalization()) {
                            PartialTotalization totalizParcial = this.partialTotalizations.getTotalizacaoParcial(valores, pai);
                            if (totalizParcial != null) {
                                double fin = totalizParcial.getPartialTotalization();

                                if (pai.isApplyTotalizationExpression()) {
                                    Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, pai, registroTotalizado);
                                    fin = registroTotalizado.getDouble(pai.getFieldId());
                                }
                                totalizParcial.setPartialTotalization(fin);
                                this.indicator.setPartialTotalizations(this.partialTotalizations);
                                List<Object> valoresField = new ArrayList<>();
                                participHorizontais.put(pai, valoresField);
                                valoresField.add(fin);
                            }
                        }
                    } else {
                        if ((campo.getFieldType() == null && !campo.isTotalizingField()) || Constants.METRIC.equals(campo.getFieldType())) {
                            celula = new HTMLCell();
                            linha.addCell(celula);
                            celula.setNowrap(true);
                            celula.setStyle(estilo);
                            celula.setAlignment(campo.getColumnAlignment());
                            if (campo.isPartialTotalization()) {
                                if (!("".equals(campo.getName()))) {
                                    PartialTotalization totalizParcial = this.partialTotalizations.getTotalizacaoParcial(valores, campo);
                                    soma = totalizParcial.getPartialTotalization();

                                    if (campo.isApplyTotalizationExpression()) {
                                        Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, campo, registroTotalizado);
                                        soma = registroTotalizado.getDouble(campo.getFieldId());
                                    }
                                    totalizParcial.setPartialTotalization(soma);
                                    this.indicator.setPartialTotalizations(this.partialTotalizations);

                                    celula.setContent(BIUtil.formatDoubleToText(soma, campo.getNumDecimalPositions()));
                                    this.indicator.getColorAlerts().buscaAplicaAlertaValor(soma, celula, linha, campo, ColorAlert.TOTALIZACAO_PARCIAL, 2, this, true);
                                    this.indicator.getColorAlerts().buscaAplicaAlertaOutroCampo(soma, campo, ColorAlert.TOTALIZACAO_PARCIAL, valores, linha.getCurrentCell(), linha, campo.getNumDecimalPositions(), this, dimensao, registroTotalizado, 0, true);
                                }
                            } else {
                                String conteudo = "-";
                                if (campo.isChildField() && campo.getTitle().startsWith("AH Participa��o")) {
                                    Field pai = this.getFieldPai(campo, valores);
                                    if (pai != null && this.partialTotalizations != null) {
                                        int indice = this.getIndiceFieldPai(campo, valores);

                                        PartialTotalization totalizParcial = this.partialTotalizations.getTotalizacaoParcial(valores, pai);
                                        if (totalizParcial != null) {
                                            soma = totalizParcial.getPartialTotalization();

                                            if (pai.isApplyTotalizationExpression()) {
                                                Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, pai, registroTotalizado);
                                                soma = registroTotalizado.getDouble(pai.getFieldId());
                                            }
                                            totalizParcial.setPartialTotalization(soma);
                                            this.indicator.setPartialTotalizations(this.partialTotalizations);

                                            if (this.getAccumulatedLine(indice) != 0) {
                                                if (campo.getTitle().equals("AH Participação Acumulada")) {
                                                    if (participParcialHorizontal == null) {
                                                        participParcialHorizontal = new HashMap<>();
                                                    }
                                                    if (participParcialHorizontal.get(pai) != null) {
                                                        soma += Double.parseDouble((String) participParcialHorizontal.get(pai));
                                                    }
                                                    participParcialHorizontal.put(pai, String.valueOf(soma));
                                                    this.indicator.getColorAlerts().buscaAplicaAlertaValor((soma / this.getAccumulatedLine(indice)) * 100, celula, linha, pai, ColorAlert.PARTICIPACAO_ACUMULADA_HORIZONTAL, 2, this, true);
                                                } else {
                                                    this.indicator.getColorAlerts().buscaAplicaAlertaValor((soma / this.getAccumulatedLine(indice)) * 100, celula, linha, pai, ColorAlert.PARTICIPACAO_HORIZONTAL, 2, this, true);
                                                }
                                                conteudo = BIUtil.formatDoubleToText(soma / this.getAccumulatedLine(indice), 2);
                                            }
                                        }
                                    }
                                } else if (campo.isHorizontalAnalysis() && campo.getTitle().contains("AH%")) {
                                    Field pai = this.getFieldPai(campo, valores);
                                    if (pai.isPartialTotalization()) {
                                        PartialTotalization totalizParcial = this.partialTotalizations.getTotalizacaoParcial(valores, pai);
                                        if (totalizParcial != null) {
                                            double fin = totalizParcial.getPartialTotalization();

                                            if (pai.isApplyTotalizationExpression()) {
                                                Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, pai, registroTotalizado);
                                                fin = registroTotalizado.getDouble(pai.getFieldId());
                                            }
                                            totalizParcial.setPartialTotalization(fin);
                                            this.indicator.setPartialTotalizations(this.partialTotalizations);
                                            
                                            List<Object> valoresField = participHorizontais.get(pai);
                                            valoresField.add(fin);
                                            double ini;
                                            if (Constants.DYNAMIC_HORIZONTAL_ANALYSIS.equals(pai.getHorizontalAnalysisType())) {
                                                ini = (Double) valoresField.get(valoresField.size() - 2);
                                            } else {
                                                ini = (Double) valoresField.get(0);
                                            }

                                            if (ini != fin) {
                                                if (ini != 0) {
                                                    double res = (ini - fin) / ini;
                                                    conteudo = BIUtil.formatDoubleToText(-1 * res, 2);
                                                } else {
                                                    if (fin > 0) {
                                                        conteudo = BIUtil.formatDoubleToText(1, 2);
                                                    } else if (fin < 0) {
                                                        conteudo = BIUtil.formatDoubleToText(-1, 2);
                                                    }
                                                }
                                            } else {
                                                conteudo = BIUtil.formatDoubleToText(0, 2);
                                            }
                                        } else {
                                            conteudo = "-";
                                        }
                                    } else {
                                        conteudo = "-";
                                    }
                                } else {
                                    if (campo.isChildField() && (campo.getTitle().startsWith("%"))) {
                                        if (this.partialTotalizations != null) {
                                            PartialTotalization totalizParcial = this.partialTotalizations.getTotalizacaoParcial(valores, campo);
                                            if (totalizParcial != null) {
                                                soma = totalizParcial.getPartialTotalization();
                                                
                                                if (campo.isApplyTotalizationExpression()) {
                                                    Expression.aplicaExpressaoNoRegistroTotalizado(this.indicator, campo, registroTotalizado);
                                                    soma = registroTotalizado.getDouble(campo.getFieldId());
                                                }
                                                totalizParcial.setPartialTotalization(soma);
                                                this.indicator.setPartialTotalizations(this.partialTotalizations);
                                                conteudo = BIUtil.formatDoubleToText(soma, 2);
                                            }
                                        }
                                    }
                                }
                                celula.setAlignment(campo.getColumnAlignment());
                                celula.setContent(conteudo);
                            }
                        }
                    }
                }
            }
            if (linha.isAppliedAlert()) {
                linha.resetCellStyles();
            }
        }
        return total;
    }

    public Field getFieldPai(Field filho, Object[][] valores) {
        Field retorno = null;
        for (int i = 3; i < valores[0].length; i++) {
            Field campo = (Field) valores[0][i];
            if (campo != null) {
                if (campo.getFieldId() == filho.getFieldId() && !campo.isChildField()) {
                    retorno = campo;
                    break;
                }
            } else {
                break;
            }
        }
        return retorno;
    }

    public int getIndiceFieldPai(Field filho, Object[][] valores) {
        int retorno = -1;
        for (int i = 3; i < valores[0].length; i++) {
            Field campo = (Field) valores[0][i];
            if (campo != null) {
                if (campo.getFieldId() == filho.getFieldId() && !campo.isChildField()) {
                    retorno = i;
                    break;
                }
            } else {
                break;
            }
        }
        return retorno;
    }
    
    public CachedResults geraRegistroTotalizado(Object[][] valores, CachedResults registroTotalizado, int sequencia, List<DimensionTotalized> dimensoesTotalizadas, Dimension dimensaoLinha) throws BIException {

        Field campo;
        Field campoMetrica = null;
        int indiceMetrica = 0;
        for (int ii = 3; ii < valores[0].length; ii++) {
            campo = (Field) valores[0][ii];
            double somaParcial = 0;
            if ((campo.getFieldType() == null && !campo.isTotalizingField()) || Constants.METRIC.equals(campo.getFieldType())) {

                if (campo.isPartialTotalization() || campo.isTotalizingField()) {
                    if (!("".equals(campo.getName()))) {
                        campoMetrica = campo;
                        indiceMetrica = ii;
                        for (int iii = 1; iii < valores.length; iii++) {
                            somaParcial += (Double) valores[iii][ii];
                        }
                        this.setaTotalizacao(campo, valores, sequencia, somaParcial, registroTotalizado);
                    }
                }
                if (campo.isVerticalAnalysis()) {
                    double somaParticipacao = 0;
                    double somaParametroPercentual = 0;

                    if (campoMetrica.getVerticalAnalysisType().equals(Constants.VERTICAL_ANALYSIS_PARTIAL_TYPE)) {
                        this.getSomaParametroPercentual(dimensoesTotalizadas, campoMetrica, dimensaoLinha);
                        somaParametroPercentual = this.parentValue;
                        this.parentValue = 0;
                        this.parentValueFound = false;
                    } else {
                        if (valores.length > 1)
                            somaParametroPercentual = (Double) valores[1][indiceMetrica + 1];
                    }
                    if (!("".equals(campo.getName()))) {
                        for (int iii = 1; iii < valores.length; iii++) {
                            double valorField = (Double) valores[iii][ii];
                            somaParticipacao += (valorField / somaParametroPercentual);
                        }
                        this.setaTotalizacao((Field) valores[0][ii + 2], valores, sequencia, somaParticipacao, registroTotalizado);
                    }
                }
            }
        }

        return registroTotalizado;
    }

    public double getValorDimensaoGeral(Field metrica, Dimension dimensaoConsulta, List<DimensionTotalized> dimensoesTotalizadasRaiz) {
        double valorTotal = 0;
        for (DimensionTotalized biDimensionTotalizada : dimensoesTotalizadasRaiz) {
            PartialTotalization totalizacaoFilha = biDimensionTotalizada.getTotalizacaoParcial(metrica, dimensaoConsulta);
            valorTotal += totalizacaoFilha.getPartialTotalization();
        }
        return valorTotal;
    }

    private void getSomaParametroPercentual(List<DimensionTotalized> dimensoesTotalizadas, Field campo, Dimension dimensaoLinha) {
        for (int x = 0; x < dimensoesTotalizadas.size() && !this.parentValueFound; x++) {
            DimensionTotalized dimensaoTotalizada = dimensoesTotalizadas.get(x);
            if (dimensaoTotalizada != null) {
                if (dimensaoTotalizada.getChildDimensions() != null && !dimensaoTotalizada.getChildDimensions().isEmpty()) {
                    if (this.getValorDimensao().equals(dimensaoTotalizada.getValorDimensao()) && Arrays.deepEquals(this.getResults(), dimensaoTotalizada.getResults())) {
                        DimensionTotalized pai = (DimensionTotalized) dimensaoTotalizada.getParentDimension();

                        if (pai != null) {
                            this.parentValueFound = true;
                            PartialTotalization totalizacaoParcial = pai.getTotalizacaoParcial(campo, dimensaoLinha);
                            this.parentValue = totalizacaoParcial.getPartialTotalization();
                        } else {
                            this.parentValue = this.getValorDimensaoGeral(campo, dimensaoLinha, dimensoesTotalizadas);
                        }
                    } else {
                        this.getSomaParametroPercentual(dimensaoTotalizada.getChildDimensions(), campo, dimensaoLinha);
                    }
                }
            }
        }
    }

    private void setaTotalizacao(Field campo, Object[][] valores, int sequencia, double totalizacao, CachedResults registroTotalizado) throws BIException {
        PartialTotalization totalizParcial = new PartialTotalization();
        totalizParcial.setField(campo);
        totalizParcial.setValues(valores);
        totalizParcial.setSequence(sequencia);
        totalizParcial.setPartialTotalization(totalizacao);

        if (this.partialTotalizations == null) {
            this.partialTotalizations = new PartialTotalizations();
        }
        this.partialTotalizations.addTotalizacao(totalizParcial);
        registroTotalizado.setDouble(totalizacao, campo.getFieldId());
        this.indicator.setPartialTotalizations(this.partialTotalizations);
    }

    public Object getValorDimensao() {
        return this.value.getValor(0);
    }

    public double getAccumulatedLine(int posicao) {
        return accumulatedLine[posicao];
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }


    public List<Field> getFieldsDaDimensao() {
        List<Field> result = new ArrayList<>();
        if (this.results != null && this.results[0] != null) {
            for (Object obj : this.results[0]) {
                if (obj instanceof Field) {
                    result.add((Field) obj);
                }
            }
        }
        return result;
    }

    public ConsultResult[] getResultadoConsultaExpressao() {
        ConsultResult[] retorno;
        if (this.accumulatedLine != null) {
            retorno = new ConsultResult[this.accumulatedLine.length];
            int cont = 0;
            for (int i = 2; i < this.results[0].length; i++) {
                Field campo = (Field) this.results[0][i];
                if (campo.isMetric()) {
                    if (this.accumulatedLine != null) {
                        retorno[cont] = ConsultResultFactory.factory(campo);
                        retorno[cont++].addValor(this.accumulatedLine[i]);
                    }
                }
            }
        } else {
            retorno = null;
        }
        return retorno;
    }

    public double calculaExpressaoAcumulado(Field campo, int indice) throws BIException {
        ConsultResult[] resultAux = this.getResultadoConsultaExpressao();
        Double valorDaExpressao = null;
        if (campo != null && campo.isExpression() && campo.getAccumulatedLine().equals("E")) {
            Expression expressao = indicator.getFieldExpression(campo.getName());
            if (StringHelper.hasAllExpressionFields(this.getFieldsDaDimensao(), campo, indicator)) {
                CachedResults cacheResult = new CachedResults(resultAux);
                cacheResult.next();
                if (campo.isExpression() && (campo.getName().toUpperCase().trim().indexOf("SE(") == 0 || campo.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
                    ConditionalExpression expressaoCondicional = StringHelper.getExpressionConditional(campo.getName(), indicator);
                    valorDaExpressao = StringHelper.calculateConditionalExpression(expressaoCondicional, cacheResult);
                } else {
                    valorDaExpressao = expressao.calculaExpressao(cacheResult);
                }
                this.lineExpression[indice] = valorDaExpressao;
            }
        }
        if (valorDaExpressao == null) {
            return 0;
        }
        return valorDaExpressao;
    }

    public double calculaExpressaoAcumuladoExcel(Field campo, int indice) throws BIException {
        ConsultResult[] resultAux = this.getResultadoConsultaExpressao();
        Double valorDaExpressao = null;
        if (campo != null && campo.isExpression() && campo.getAccumulatedLine().equals("E")) {
            Expression expressao = indicator.getFieldExpression(campo.getName());
            if (StringHelper.hasAllExpressionFields(this.getFieldsDaDimensao(), campo, indicator)) {
                CachedResults cacheResult = new CachedResults(resultAux);
                cacheResult.next();
                if (campo.isExpression() && (campo.getName().toUpperCase().trim().indexOf("SE(") == 0 || campo.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
                    ConditionalExpression expressaoCondicional = StringHelper.getExpressionConditional(campo.getName(), indicator);
                    valorDaExpressao = StringHelper.calculateConditionalExpression(expressaoCondicional, cacheResult);
                } else {
                    valorDaExpressao = expressao.calculaExpressao(cacheResult);
                }
                this.lineExpression[indice] = valorDaExpressao;
            }
        }
        if (valorDaExpressao == null) {
            return 0;
        }
        return valorDaExpressao;
    }

    public int getQuantidadeColunas() {
        int retorno = 0;
        for (Dimension biDimension : this.bottomDimensions) {
            if (biDimension != null) {
                if (!biDimension.temDimensoesAbaixo()) {
                    retorno++;
                } else {
                    retorno += biDimension.getQuantidadeColunas();
                }
            } else {
                break;
            }
        }
        return retorno;
    }

    public double getAcumuladoLinhaAtualizado(int indiceField) {
        double retorno = 0;
        if (!this.filterBySequence) {
            if (this.temDimensoesAbaixo()) {
                for (Dimension bottomDimension : this.bottomDimensions) {
                    if (bottomDimension != null) {
                        retorno += bottomDimension.getAcumuladoLinhaAtualizado(indiceField);
                    } else {
                        break;
                    }
                }
                return retorno;
            } else {
                if (this.accumulatedLine != null && (!this.isFilterByAccumulated() && !this.isFilterBySequence())) {
                    if (this.getValue().getField().isExpression()) {
                        this.accumulatedLine[indiceField] = this.lineExpression[indiceField];
                    }
                    retorno = this.accumulatedLine[indiceField];
                }
            }
        }
        return retorno;
    }

    public double getAcumuladoLinhaOutros(int indiceField) {
        double retorno = 0;
        if (this.temDimensoesAbaixo()) {
            if (this.isFilterBySequence()) {
                return this.accumulatedLine[indiceField];
            } else {
                for (Dimension biDimension : this.bottomDimensions) {
                    if (biDimension != null) {
                        retorno += biDimension.getAcumuladoLinhaOutros(indiceField);
                    }
                }
            }
        } else {
            if (this.isFilterBySequence() || this.isFilterByAccumulated()) {
                return this.accumulatedLine[indiceField];
            }
        }
        return retorno;
    }

    public double getValorTotalDimensao(int indiceField) {
        double retorno = 0;
        for (int i = 1; i < this.results.length; i++) {
            retorno += (Double) this.results[i][indiceField];
        }
        return retorno;
    }

    public Object[][] aplicaFiltroSequencia(Object[][] resultados, boolean filtradoAcumulado, Dimension[] dimensoesLinha, Dimension colunaPai) {
        if (this.temMaisDimensoesVisualizadasAbaixo()) {
            Dimension[] dimensoes = this.bottomDimensions;
            for (int i = 0; i < this.bottomDimensions.length; i++) {
                if (dimensoes[i] != null) {
                    dimensoes[i].setIndicator(this.indicator);
                    resultados = dimensoes[i].aplicaFiltroSequencia(resultados, dimensoes[i].isFilterByAccumulated(), dimensoesLinha, colunaPai);
                    this.setFilterBySequence(true);
                } else {
                    break;
                }
            }
        } else {
            if (!filtradoAcumulado) {
                this.procuraValoresFiltrados(resultados, dimensoesLinha, colunaPai, 1);
                this.limpaResultadosDimensao();
                this.setFilterBySequence(true);
            }
        }
        return resultados;
    }

    public boolean validaAcumulado(Object[][] resultados, boolean filtradoSequencia, Dimension[] dimensoesLinha, Dimension colunaPai, boolean filtroAlgumaDimensao) {
        if (this.temMaisDimensoesAbaixo()) {
            Dimension[] dimensoes = this.bottomDimensions;
            int total = 0;
            int filtrado = 0;
            for (int i = 0; i < this.bottomDimensions.length; i++) {
                if (dimensoes[i] != null) {
                    total++;
                    dimensoes[i].setIndicator(this.indicator);
                    filtroAlgumaDimensao = dimensoes[i].validaAcumulado(resultados, filtradoSequencia, dimensoesLinha, colunaPai, filtroAlgumaDimensao);
                    if (dimensoes[i].isFilterByAccumulated() || dimensoes[i].isFilterBySequence()) {
                        filtrado++;
                    }
                } else {
                    break;
                }
            }
            if (total == filtrado && total > 0) {
                this.setFilterByAccumulated(true);
                filtroAlgumaDimensao = true;
            }
        } else {
            boolean filtradoAcumulado = false;
            if (this.indicator.getFiltersFunction() != null && this.indicator.getFiltersFunction().getFilterAccumulated() != null) {
                FilterAccumulated filtroAcumulado = this.indicator.getFiltersFunction().getFilterAccumulated();
                for (int i = 2; i < this.results[0].length; i++) {
                    Field campo = (Field) this.results[0][i];
                    if (campo.equals(filtroAcumulado.getField()) && !campo.getAccumulatedLine().equals("N")) {
                        double valorVerificado = this.getAccumulatedLine(i);
                        if (campo.isExpression() && campo.getAccumulatedLine().equals("E")) {
                            valorVerificado = this.calculaExpressaoField(campo, i);
                        }
                        if (!filtroAcumulado.verifyCondition(valorVerificado)) {
                            filtradoAcumulado = true;
                            filtroAlgumaDimensao = true;
                            break;
                        }

                    }
                }
            }
            if (filtradoAcumulado && !filtradoSequencia && !this.filterBySequence) {
                this.procuraValoresFiltrados(resultados, dimensoesLinha, colunaPai, 2);
                this.limpaResultadosDimensao();
            }
            this.setFilterByAccumulated(filtradoAcumulado);
        }
        return filtroAlgumaDimensao;
    }

    public void procuraValoresFiltrados(Object[][] resultados, Dimension[] dimensoesLinha, Dimension colunaPai, int tipo) {
        for (Dimension biDimension : dimensoesLinha) {
            if (biDimension != null) {
                if (biDimension.temDimensoesAbaixo()) {
                    this.procuraValoresFiltrados(resultados, biDimension.bottomDimensions, colunaPai, tipo);
                } else {
                    Object[][] result = biDimension.consulta(this.results);
                    for (int a = 0; a < result[0].length; a++) {
                        if (result[0][a] != null) {
                            Field campoAux = (Field) result[0][a];
                            for (int ii = 1; ii < result.length; ii++) {
                                if (campoAux.isMetric() && campoAux.isTotalizingField()) {
                                    double valor = (Double) result[ii][a];
                                    ArrayList<Dimension> chaveDimensao = this.getChavesDimensaoLinha(biDimension);
                                    this.removeValorTotalizacaoField(resultados, valor, campoAux, chaveDimensao, colunaPai, tipo);
                                }
                            }
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    public void removeValorTotalizacaoField(Object[][] resultados, double valor, Field campoValor, ArrayList<Dimension> chaveDimensao, Dimension colunaPai, int tipo) {
        Field campoRef = null;
        int indiceDimensaoLinhaAtual = -1;

        int indiceField = -1;

        Dimension dimensaoLinha = chaveDimensao.get(0);
        if (dimensaoLinha != null && dimensaoLinha.getValue().getField() != null) {

            campoRef = dimensaoLinha.getValue().getField();
        }

        for (int i = 0; i < resultados[0].length; i++) {
            Field campoAux = (Field) resultados[0][i];
            if (campoRef.getFieldId() == campoAux.getFieldId()) {
                indiceDimensaoLinhaAtual = i;
            } else if (campoValor.equals(campoAux)) {
                indiceField = i;
            }
        }

        if (indiceDimensaoLinhaAtual != -1 && indiceField != -1) {
            for (int i = 1; i < resultados.length; i++) {
                if (resultados[i][indiceDimensaoLinhaAtual] != null) {
                    Iterator<Dimension> iterator = chaveDimensao.iterator();
                    int indice = indiceDimensaoLinhaAtual;
                    int contador = 0;
                    Dimension dimensaoAtual = iterator.next();

                    while (dimensaoAtual != null && dimensaoAtual.getValue().getValor(0).equals(resultados[i][indice])) {
                        if (iterator.hasNext()) {
                            dimensaoAtual = iterator.next();
                        }
                        indice++;
                        contador++;
                    }

                    if (contador == chaveDimensao.size()) {
                        double valorOriginal = (Double) resultados[i][indiceField + 1];
                        double valorFinal = valorOriginal - valor;
                        resultados[i][indiceField + 1] = valorFinal;
                    }
                }
            }
        }
    }

    public void limpaResultadosDimensao() {
        for (int i = 1; i < this.results.length; i++) {
            for (int j = 0; j < this.results[0].length; j++) {
                Field campo = (Field) this.results[0][j];
                if (campo.isMetric() && campo.isPartialTotalization()) {
                    this.results[i][j] = (double) 0;
                }
            }
        }
    }

    public ArrayList<Dimension> getChavesDimensaoLinha(Dimension dimensao) {
        ArrayList<Dimension> retorno = new ArrayList<>();
        int tamanho = 0;
        if (dimensao != null) {
            retorno.add(dimensao);
            tamanho++;
            Dimension pai = dimensao;
            while (pai != null) {
                pai = dimensao.getParentDimension();
                if (pai != null) {
                    retorno.add(pai);
                    tamanho++;
                    dimensao = pai;
                }
            }
        }
        ArrayList<Dimension> retorno2 = (ArrayList<Dimension>) retorno.clone();
        for (Dimension obj : retorno2) {
            if (tamanho > 0) {
                retorno.set(--tamanho, obj);
            }
        }
        return retorno;
    }

    public double calculaExpressaoField(Field campo, int i) {
        ConsultResult[] resultAux = this.getResultadoConsultaExpressao();
        Double valorDaExpressao = null;
        if (campo != null && campo.isExpression() && campo.getAccumulatedLine().equals("E")) {
            Expression expressao = indicator.getFieldExpression(campo.getName());
            if (StringHelper.hasAllExpressionFields(this.getFieldsDaDimensao(), campo, indicator)) {
                CachedResults cacheResult;
                try {
                    cacheResult = new CachedResults(resultAux);
                    cacheResult.next();
                    if (campo.isExpression() && (campo.getName().toUpperCase().trim().indexOf("SE(") == 0 || campo.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
                        ConditionalExpression expressaoCondicional = StringHelper.getExpressionConditional(campo.getName(), indicator);
                        valorDaExpressao = StringHelper.calculateConditionalExpression(expressaoCondicional, cacheResult);
                    } else {
                        valorDaExpressao = expressao.calculaExpressao(cacheResult);
                    }
                    this.lineExpression[i] = valorDaExpressao;
                } catch (BIException e) {
                    e.printStackTrace();
                }
            }
        }
        return valorDaExpressao;
    }

    public Object[][] validaSequencia(FilterSequence filtroSequencia, Dimension[] dimensoesLinha, int quantidadeResultadosColuna, Dimension colunaPai, Object[][] resultados) {
        if ("S".equals(this.getValue().getField().getDefaultField())) {

            boolean filtradoSequencia = false;
            if (this.indicator.getFiltersFunction() != null && this.indicator.getFiltersFunction().getFilterSequence() != null && this.indicator.isUsesSequence()) {
                if (filtroSequencia != null) {
                    if (filtroSequencia.isRanking()) {
                        if (!filtroSequencia.verifyRanking(this.getCounter(), quantidadeResultadosColuna)) {
                            filtradoSequencia = true;
                        }
                    } else {
                        if (!filtroSequencia.verifyCondition(this.getCounter())) {
                            filtradoSequencia = true;
                        }
                    }
                }
            }
            if (filtradoSequencia) {
                this.aplicaFiltroSequencia(resultados, this.isFilterByAccumulated(), dimensoesLinha, colunaPai);
                this.setFilterBySequence(filtradoSequencia);
            }
        } else {
            int total = 0;
            int filtrado = 0;
            Dimension[] dimensoesAbaixo = this.getBottomDimensions();
            for (int j = 0; j < dimensoesAbaixo.length; j++) {
                if (dimensoesAbaixo[j] != null) {
                    total++;
                    this.bottomDimensions[j].setIndicator(this.indicator);
                    this.bottomDimensions[j].validaSequencia(filtroSequencia, dimensoesLinha, quantidadeResultadosColuna, colunaPai, resultados);
                    if (this.bottomDimensions[j].isFilterBySequence() || this.bottomDimensions[j].isFilterByAccumulated()) {
                        filtrado++;
                    }
                } else {
                    break;
                }
            }
            if (total == filtrado && total > 0) {
                this.setFilterBySequence(true);
            }
        }
        return resultados;
    }

    public int getQuantidadeResultadosExistentesColuna() {
        int retorno = 0;
        for (int i = 0; i < this.bottomDimensions.length; i++) {
            if (this.bottomDimensions[i] != null) {
                if ("T".equals(this.bottomDimensions[i].getValue().getField().getDefaultField())) {
                    retorno += this.bottomDimensions[i].getQuantidadeResultadosExistentesColuna();
                } else if (!this.bottomDimensions[i].isFilterByAccumulated()) {
                    retorno++;
                }
            } else {
                break;
            }
        }
        return retorno;
    }

    public int setContadoresDimensao(int cont) {
        for (int i = 0; i < this.bottomDimensions.length; i++) {
            if (this.bottomDimensions[i] != null) {
                this.bottomDimensions[i].setIndicator(this.indicator);
                if ("S".equals(this.bottomDimensions[i].getValue().getField().getDefaultField()) && !this.bottomDimensions[i].isFilterByAccumulated()) {
                    this.bottomDimensions[i].setCounter(cont);
                    if (this.indicator.getSequeceValuesRepository() != null) {
                        this.indicator.getSequeceValuesRepository().addValor(this.bottomDimensions[i].getCounter());
                    }
                    this.bottomDimensions[i].setCounterEnabled(true);
                    cont++;
                } else {
                    cont = this.bottomDimensions[i].setContadoresDimensao(cont);
                }
            } else {
                break;
            }
        }
        return cont;
    }

    public HashMap<String, Double> getTotalLines() {
        if (this.totalLines == null) {
            this.totalLines = new HashMap<String, Double>();
        }
        return this.totalLines;
    }

    public int calculaQuantidadeColunasLinha(Dimension[] dimensoesLinha) {
        int contador = 0;
        for (Dimension biDimension : dimensoesLinha) {
            if (biDimension != null) {
                if (biDimension.temDimensoesAbaixo()) {
                    contador += biDimension.calculaQuantidadeColunasLinha(biDimension.getBottomDimensions());
                } else {
                    contador++;
                }
            } else {
                break;
            }
        }
        return contador;
    }

    public void aplicaQuantidadeColunasLinha(Dimension[] dimensoesColuna, int quantidadeColunasLinha) {
        for (Dimension biDimension : dimensoesColuna) {
            if (biDimension != null) {
                biDimension.setColumnLineAmount(quantidadeColunasLinha);
                if (biDimension.temDimensoesAbaixo()) {
                    biDimension.aplicaQuantidadeColunasLinha(biDimension.getBottomDimensions(), quantidadeColunasLinha);
                }
            } else {
                break;
            }
        }
    }

    public void aplicaSomaLinha(Dimension[] dimensoesColuna, double somaLinha) {
        for (Dimension biDimension : dimensoesColuna) {
            if (biDimension != null) {
                biDimension.setLineSum(somaLinha);
                if (biDimension.temDimensoesAbaixo()) {
                    biDimension.aplicaSomaLinha(biDimension.getBottomDimensions(), somaLinha);
                }
            } else {
                break;
            }
        }
    }

    public void setAlertLineStyle(boolean alertLineStyle) {
        this.alertLineStyle = alertLineStyle;
        if (this.temDimensoesAbaixo()) {
            for (Dimension bottonDimension : this.bottomDimensions) {
                if (bottonDimension != null) {
                    bottonDimension.setAlertLineStyle(alertLineStyle);
                } else {
                    break;
                }
            }
        }
    }

    public void setLineAppliedStyle(Object lineAppliedStyle) {
        this.lineAppliedStyle = lineAppliedStyle;
        if (this.temDimensoesAbaixo()) {
            for (Dimension bottonDimension : this.bottomDimensions) {
                if (bottonDimension != null) {
                    bottonDimension.setLineAppliedStyle(lineAppliedStyle);
                } else {
                    break;
                }
            }
        }
    }

    public int getQuantidadeMetricasComAnaliseHorizontal(Field[] metricasLinha) {
        int retorno = 0;
        if (metricasLinha != null) {
            for (Field field : metricasLinha) {
                if (field != null && field.isHorizontalAnalysis() && field.getTitle().contains("AH%")) {
                    retorno++;
                }
            }
        }
        return retorno;
    }

    public void retiraObjetosMemoria() {
        if (this.bottomDimensions != null) {
            for (Dimension biDimension : this.bottomDimensions) {
                if (biDimension != null) {
                    biDimension.retiraObjetosMemoria();
                } else {
                    break;
                }
            }
            this.bottomDimensions = null;
        }

        this.columnDimension = null;

        this.lineDimension = null;

        this.value = null;
        this.parentDimension = null;

        this.results = null;
        this.index = 1;
        this.indicator = null;
        this.totalLines = null;
        this.horizontalParticipation = null;
        this.lineAppliedStyle = null;
    }

    public PartialTotalizations getPartialTotalizations() {
        if (this.partialTotalizations == null)
            this.partialTotalizations = new PartialTotalizations();
        return partialTotalizations;
    }

}
