package com.msoft.mbi.data.api.data.indicator;

import lombok.Setter;

import java.util.Hashtable;
import java.util.List;

public class SelectHashtable extends Select {

    private Hashtable<?, ?>[]						hashArray;
    private List<Hashtable<String, String>>	hashList;
    private final String keyValue;
    private final String keyLabel;
    @Setter
    private String selectedValue;

    public SelectHashtable(Hashtable<?, ?>[] hash, String chaveValor, String chaveLabel) {
        this.hashArray = hash;
        this.keyValue = chaveValor;
        this.keyLabel = chaveLabel;
    }

    public SelectHashtable(List<Hashtable<String, String>> hash, String chaveValor, String chaveLabel) {
        this.hashList = hash;
        this.keyValue = chaveValor;
        this.keyLabel = chaveLabel;
    }

    public void montaComponente() {
        this.setInicioComponente();
        if (hashArray != null) {
            for (Hashtable<?, ?> hashtable : this.hashArray) {
                if (hashtable != null) {
                    this.addElement(hashtable.get(keyValue), hashtable.get(keyLabel), hashtable.get(keyValue).equals(selectedValue));
                }
            }
        } else if (hashList != null) {
            for (Hashtable<String, String> list : this.hashList) {
                this.addElement(list.get(keyValue), list.get(keyLabel), list.get(keyValue).equals(selectedValue));
            }
        }
    }
}
