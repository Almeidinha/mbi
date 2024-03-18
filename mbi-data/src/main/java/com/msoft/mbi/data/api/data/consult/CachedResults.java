package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class CachedResults {

    @Setter
    private ConsultResult[] consultResults;
    private int index = -1;
    private ArrayList<String> filteredIndices;

    public CachedResults(ConsultResult[] consultResults) throws BIException {
        int tamanho = 0;
        int tamanho_aux = 0;
        if (consultResults.length > 0) {
            tamanho = consultResults[0].getValues().size();
        }
        for (ConsultResult consultResult : consultResults) {
            if (consultResult != null) {
                tamanho_aux = consultResult.getValues().size();
            }

            if (tamanho != tamanho_aux) {
                throw new BIGeneralException("Não foi possivel carregar o CachedResultado, pois a quantidade de registros entre os campos eram incompatáveis.");
            }
        }

        this.consultResults = consultResults;
    }

    public void delete() {
        for (ConsultResult consultResult : consultResults) {
            consultResult.removeValor(this.index);
        }
        if (this.index == 0) {
            this.index = -1;
        } else {
            this.previous();
        }
    }

    public void remove(int index) {
        for (ConsultResult consultResult : consultResults) {
            consultResult.removeValor(index);
        }
    }

    public int length() {
        return consultResults.length;
    }

    public int getValorLength() {
        return consultResults[0].getValues().size();
    }

    public boolean next() {
        try {
            if ((this.index + 1) < consultResults[0].getValues().size()) {
                this.index++;
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public int contNext() {
        int cont = 0;
        while (this.next()) {
            cont++;
        }
        this.beforeFirst();
        return cont;
    }

    public boolean previous() {
        try {
            if ((this.index - 1) >= 0) {
                this.index--;
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public void first() {
        this.index = 0;
    }

    public void beforeFirst() {
        this.index = -1;
    }

    public boolean isFirst() {
        return (this.index == 0);
    }

    public boolean isLast() {
        return (this.index == consultResults[0].getValues().size() - 1);
    }

    public void setString(String valor, int fieldCode) throws BIException {
        Field field;
        Indicator indicator;
        int codigoOriginal = 0;

        int indice_campo = -1;

        for (int i = 0; i < consultResults.length; i++) {
            if (fieldCode == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {

            if (consultResults.length > 0) {
                field = consultResults[0].getField();

                indicator = field.getIndicator();

                if (indicator != null) {
                    codigoOriginal = indicator.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo tipo String método setString, com o código: " + fieldCode + ". Referente a análise : " + codigoOriginal);
        }
        consultResults[indice_campo].setValues(valor, this.index);
    }

    public String getString(int codigo_campo) throws BIException {
        Field field;
        Indicator indicator;
        int codigoOriginal = 0;

        int indice_campo = -1;
        for (int i = 0; i < consultResults.length; i++) {
            if (codigo_campo == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {
            if (consultResults.length > 0) {
                field = consultResults[0].getField();

                indicator = field.getIndicator();

                if (indicator != null) {
                    codigoOriginal = indicator.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo tipo String método getString, com o código: " + codigo_campo + ". Referente a análise : " + codigoOriginal);
        }
        return consultResults[indice_campo].getValor(this.index).toString();
    }

    public String getValorFormatado(int codigo_campo) throws BIException {
        Field field;
        Indicator indicator;
        int codigoOriginal = 0;

        int indice_campo = -1;
        for (int i = 0; i < consultResults.length; i++) {
            if (codigo_campo == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {
            if (consultResults.length > 0) {
                field = consultResults[0].getField();

                indicator = field.getIndicator();

                if (indicator != null) {
                    codigoOriginal = indicator.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo no método getValorFormatado, com o código: " + codigo_campo + ". Referente a análise : " + codigoOriginal);
        }
        String valor = consultResults[indice_campo].getFormattedValue(this.index).toString();
        if (valor == null || "".equalsIgnoreCase(valor.trim()))
            valor = "&nbsp;";
        return valor;
    }

    public String getValor(int fieldCode) {
        int indiceField = -1;
        for (int i = 0; i < consultResults.length; i++) {
            if (fieldCode == consultResults[i].getField().getFieldId()) {
                indiceField = i;
                break;
            }
        }
        return consultResults[indiceField].getValor(this.index).toString();
    }

    public void setDouble(Double valor, int fieldCode) throws BIException {
        Field field;
        Indicator indicator;
        int codigoOriginal = 0;

        int indice_campo = -1;
        for (int i = 0; i < consultResults.length; i++) {
            if (fieldCode == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {
            if (consultResults.length > 0) {
                field = consultResults[0].getField();

                indicator = field.getIndicator();

                if (indicator != null) {
                    codigoOriginal = indicator.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo tipo Double no método setDouble, com o código: " + fieldCode + ". Referente a análise : " + codigoOriginal);
        }
        consultResults[indice_campo].setValues(valor, this.index);
    }

    public Double getDouble(int fieldCode) throws BIException {
        Field campo;
        Indicator indicador;
        Double valor;
        int codigoOriginal = 0;
        int indice_campo = -1;

        for (int i = 0; i < consultResults.length; i++) {
            if (fieldCode == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {
            if (consultResults.length > 0) {
                campo = consultResults[0].getField();

                indicador = campo.getIndicator();

                if (indicador != null) {
                    codigoOriginal = indicador.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo tipo Double no método getDouble, com o código: " + fieldCode + ". Referente a análise : " + codigoOriginal);
        }

        try {
            campo = consultResults[indice_campo].getField();

            if (campo != null) {
                indicador = campo.getIndicator();

                if (indicador != null) {
                    codigoOriginal = indicador.getOriginalCode();
                }
            }

            valor = (Double) consultResults[indice_campo].getValor(this.index);
        } catch (Exception ex) {
            throw new BIGeneralException(ex, "Não foi possível converter o valor do campo com o codigo: " + fieldCode + " para o tipo Double.  Referente a análise : " + codigoOriginal);
        }
        return valor;
    }

    public void setDate(java.sql.Date valor, int fieldCode) throws BIException {
        Field campo;
        Indicator indicador;
        int codigoOriginal = 0;

        int indice_campo = -1;
        for (int i = 0; i < consultResults.length; i++) {
            if (fieldCode == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {
            if (consultResults.length > 0) {
                campo = consultResults[0].getField();

                indicador = campo.getIndicator();

                if (indicador != null) {
                    codigoOriginal = indicador.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo tipo Date no método setDate, com o código: " + fieldCode + ". Referente a análise : " + codigoOriginal);
        }
        consultResults[indice_campo].setValues(valor, this.index);
    }

    public java.sql.Date getDate(int codigo_campo) throws BIException {
        Field campo;
        Indicator indicador;
        int codigoOriginal = 0;

        int indice_campo = -1;
        for (int i = 0; i < consultResults.length; i++) {
            if (codigo_campo == consultResults[i].getField().getFieldId()) {
                indice_campo = i;
                break;
            }
        }

        if (indice_campo == -1) {
            if (consultResults.length > 0) {
                campo = consultResults[0].getField();

                indicador = campo.getIndicator();

                if (indicador != null) {
                    codigoOriginal = indicador.getOriginalCode();
                }
            }

            throw new BIGeneralException("Não foi possível encontrar o campo tipo Date no método getDate, com o código: " + codigo_campo + ". Referente a análise : " + codigoOriginal);
        }

        if (consultResults[indice_campo].getValor(this.index) == null || String.valueOf(consultResults[indice_campo].getValor(this.index)).trim().isEmpty()) {
            return null;
        }
        if (!(consultResults[indice_campo].getValor(this.index) instanceof java.sql.Date)) {
            throw new BIGeneralException("O valor do campo com o codigo: " + codigo_campo + " não é do tipo java.sql.Date.");
        }
        return (java.sql.Date) consultResults[indice_campo].getValor(this.index);
    }

    public ConsultResult getConsultResult(int index) {
        return this.consultResults[index];
    }

    public CachedResults getRegistroTotalizado() throws BIException {
        this.beforeFirst();

        double[] d = new double[this.consultResults.length];
        Double valor = null;

        while (this.next()) {
            for (int y = 0; y < this.consultResults.length; y++) {
                if (this.getConsultResult(y).getField().getDataType().equalsIgnoreCase("N")) {
                    if (this.getConsultResult(y).getField().isExpression() && (
                            this.getConsultResult(y).getField().getName().toUpperCase().trim().indexOf("SE(") == 0
                                    || this.getConsultResult(y).getField().getName().toUpperCase().trim().indexOf("IF(") == 0)
                            && (this.getString(this.getConsultResult(y).getField().getFieldId()) == null || this.getString(this.getConsultResult(y).getField().getFieldId()).isEmpty())) {
                        d[y] += 0;
                    } else {
                        valor = this.getDouble(this.getConsultResult(y).getField().getFieldId());
                        if (valor != null) {
                            d[y] += valor;
                        } else {
                            d[y] += 0;
                        }
                    }
                } else {
                    d[y] += 0;
                }
            }
        }

        ConsultResult[] resultadosTotalizados = new ConsultResult[this.consultResults.length];
        for (int y = 0; y < this.consultResults.length; y++) {
            resultadosTotalizados[y] = ConsultResultFactory.factory(this.getConsultResult(y).getField(), d[y]);
        }

        CachedResults registroTotalizado = new CachedResults(resultadosTotalizados);

        this.beforeFirst();
        return registroTotalizado;
    }

    public ConsultRegister getRegistroAtual() {
        ConsultRegister registro = new ConsultRegister();
        for (ConsultResult consultResult : consultResults) {
            registro.add(consultResult.getField(), consultResult.getValor(this.index));
        }

        return registro;
    }

    public void ordena(int codigo_campo) {
    }

    public String toString() {
        return Arrays.toString(this.consultResults);
    }

    public void addIndiceFiltrado(int indice) {
        if (this.filteredIndices == null) {
            this.filteredIndices = new ArrayList<String>();
        }
        this.filteredIndices.add(String.valueOf(indice));
    }

    public boolean isFiltradoSequencia() {
        boolean retorno = false;
        if (this.index > -1 && this.filteredIndices != null) {
            for (String filteredIndex : this.filteredIndices) {
                int indice = Integer.parseInt(filteredIndex);
                if (indice == this.index) {
                    retorno = true;
                    break;
                }
            }
        }
        return retorno;
    }
}
