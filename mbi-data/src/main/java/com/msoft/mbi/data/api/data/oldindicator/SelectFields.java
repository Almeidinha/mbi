package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.util.Constants;
import lombok.Setter;

import java.util.Hashtable;
import java.util.List;

public class SelectFields extends Select {

    private final List<Field> fields;
    @Setter
    private int selectedIndex = -1;
    private final Hashtable<String, String> fieldTypeClass;
    private boolean printDimensionsFirst = true;

    public SelectFields(List<Field> campos) {
        this.fields = campos;
        this.fieldTypeClass = new Hashtable<>(2);
        this.fieldTypeClass.put(Constants.DIMENSION, "fundoAzul");
        this.fieldTypeClass.put(Constants.METRIC, "fundoAzul");
    }

    public SelectFields(List<Field> campos, boolean primeiroDimensoes) {
        this(campos);
        this.printDimensionsFirst = primeiroDimensoes;
    }

    public void addClasseTipoField(String tipoField, String classe) {
        this.fieldTypeClass.put(tipoField, classe);
    }

    public void montaComponente() {
        this.setInicioComponente();
        if (fields != null) {
            if (this.printDimensionsFirst) {
                this.montaComponentePrimeiroDimensoes();
            } else {
                this.montaComponenteTodosFields();
            }
        }
    }

    private void montaComponenteTodosFields() {
        for (int i = 0; i < this.fields.size(); i++) {
            if (this.fields.get(i) != null) {
                String classeCSS = this.fieldTypeClass.get(this.fields.get(i).getFieldType());
                if ("T".equalsIgnoreCase(this.fields.get(i).getDefaultField())) {
                    if (this.fields.get(i).getFieldType().equals(Constants.DIMENSION)) {
                        classeCSS = "dimSomCalc";
                    } else {
                        classeCSS = "metSomCalc";
                    }
                }
                this.adicionaElemento(classeCSS, i);
            }
        }

    }

    private void montaComponentePrimeiroDimensoes() {
        for (int i = 0; i < this.fields.size(); i++) {
            if (this.fields.get(i) != null && this.fields.get(i).getFieldType().equals(Constants.DIMENSION)) {
                String classeCSS = this.fieldTypeClass.get(this.fields.get(i).getFieldType());
                if ("T".equalsIgnoreCase(this.fields.get(i).getDefaultField())) {
                    classeCSS = "dimSomCalc";
                }
                this.adicionaElemento(classeCSS, i);
            }
        }
        for (int i = 0; i < this.fields.size(); i++) {
            if (this.fields.get(i) != null && this.fields.get(i).getFieldType().equals(Constants.METRIC)) {
                String classeCSS = this.fieldTypeClass.get(this.fields.get(i).getFieldType());
                if ("T".equalsIgnoreCase(this.fields.get(i).getDefaultField())) {
                    classeCSS = "metSomCalc";
                }
                this.adicionaElemento(classeCSS, i);
            }
        }
    }

    private void adicionaElemento(String classeCSS, int indiceField) {
        this.addElement(String.valueOf(this.fields.get(indiceField).getCode()), this.fields.get(indiceField).getTitle(),
                this.selectedIndex == indiceField, classeCSS);
    }
}
