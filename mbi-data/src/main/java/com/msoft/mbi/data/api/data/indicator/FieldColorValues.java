package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Iterator;

@Getter
@Setter
public class FieldColorValues extends ArrayList<Object> {

    private Indicator indicator;
    private Field field;

    public FieldColorValues() {
    }

    public FieldColorValues(Indicator indicator, Field campo) {
        this.indicator = indicator;
        this.field = campo;
    }

    public FieldColorValues consult(Indicator indicator, Field field, NamedParameterJdbcTemplate jdbcTemplate) throws BIException {
        // TODO Get color list from DB
        /*for (FieldColorValue: fieldColorValue in dbList) {
            this.add(fieldColorValue);
        }*/
        return null;
    }

    public void setCor(String cor, int indice) throws BIException {
        if (indice < this.size()) {
            FieldColorValue corValorField = (FieldColorValue) this.get(indice);
            corValorField.setColor(cor);
        } else {
            throw new BIGeneralException("Indice " + indice + " n�o existente para os valor das cores do campo.");
        }
    }

    public void deleteAll(ConnectionBean conexao) throws Exception {
        Iterator<Object> i = this.iterator();
        // TODO iterate ove i and delete in DB
    }

    public String getColor(String valor) {
        String result = null;
        for (Object o : this) {
            FieldColorValue corValorField = (FieldColorValue) o;
            if (corValorField != null && corValorField.getValue().trim().equalsIgnoreCase(valor.trim())) {
                result = corValorField.getColor();
            }
        }
        return result;
    }

    public static void excluirGeral(Indicator indicator, Field campo, ConnectionBean conexao) throws BIException {
        if (indicator != null && campo != null) {
            excluirGeral(indicator.getCode(), campo.getFieldId(), conexao);
        }
    }

    public static void excluirGeral(int indicator, int campo, ConnectionBean conexao) throws BIException {

        //dbservice.excluirGeral(indicator, campo, conexao);
    }

    public void incluir(ConnectionBean conexao) throws BIException {
        for (Object o : this) {
            FieldColorValue corValorField = (FieldColorValue) o;
            corValorField.excluir(corValorField, conexao);
            corValorField.incluir(corValorField, conexao);
        }
    }
    
    public static void excluirGeral(int indicator, ConnectionBean conexao) throws BIException {
        // dbservice.excluirGeral(indicator, conexao);
    }

    public Object clone(Indicator indicator, Field campo) {
        FieldColorValues result = new FieldColorValues();
        result.setIndicator(indicator);
        result.setField(campo);
        for (Object o : this) {
            FieldColorValue corValorField = (FieldColorValue) o;
            if (corValorField != null) {
                result.add(corValorField.clone(indicator, campo));
            }
        }
        return result;
    }

}
