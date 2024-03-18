package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;
import com.msoft.mbi.data.api.data.indicator.Field;
import lombok.Setter;

import java.util.ArrayList;

@Setter
public class ConsultRegister {

    private ArrayList<Field> fieldList;
    private ArrayList<Object> valueList;

    public ConsultRegister() {
        fieldList = new ArrayList<>();
        valueList = new ArrayList<>();
    }

    public ArrayList<Field> getFields() {
        return fieldList;
    }

    public ArrayList<Object> getValues() {
        return valueList;
    }

    public void add(Field campo, Object valor) {
        fieldList.add(campo);
        valueList.add(valor);
    }

    public Field getField(int codigo_campo) throws BIException {
        int indice_campo = -1;
        for (int i = 0; i < fieldList.size(); i++) {
            if (codigo_campo == fieldList.get(i).getFieldId()) {
                indice_campo = i;
            }
        }
        if (indice_campo == -1) {
            throw new BIGeneralException("Não foi possivel encontrar o campo com o codigo: " + codigo_campo + ".");
        }

        return fieldList.get(indice_campo);
    }

    public Object getValor(int codigo_campo) throws BIException {
        int indice_campo = -1;
        for (int i = 0; i < fieldList.size(); i++) {
            if (codigo_campo == ((Field) fieldList.get(i)).getFieldId()) {
                indice_campo = i;
            }
        }
        if (indice_campo == -1) {
            throw new BIGeneralException("Não foi possivel encontrar o campo com o codigo: " + codigo_campo + ".");
        }

        return valueList.get(indice_campo);
    }

    public ConsultResultKey getKey() {
        ConsultResultKey chave = new ConsultResultKey();
        Field campoAux;
        for (int i = 0; i < fieldList.size(); i++) {
            campoAux = fieldList.get(i);
            if (campoAux != null) {
                if (campoAux.getFieldType().equalsIgnoreCase("D") || (campoAux.getDataType().equalsIgnoreCase("V")
                        && campoAux.getDefaultField().equalsIgnoreCase("S") && (campoAux.getAggregationType().equalsIgnoreCase("VAZIO")
                        || campoAux.getAggregationType().trim().isEmpty()) && (!campoAux.getName().toUpperCase().contains("SE(") && !campoAux.getName().toUpperCase().contains("IF(")))) {
                    chave.put(campoAux, valueList.get(i));
                }
            }
        }
        return chave;
    }

}
