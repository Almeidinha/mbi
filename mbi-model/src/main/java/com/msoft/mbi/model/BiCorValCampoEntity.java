package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_cor_val_campo", schema = "biserver", catalog = "BISERVER")
public class BiCorValCampoEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "sequencia_val", nullable = false)
    private int sequenciaVal;
    @Basic
    @Column(name = "ind", nullable = false)
    private int ind;
    @Basic
    @Column(name = "campo_analise", nullable = false)
    private int campoAnalise;
    @Basic
    @Column(name = "val", nullable = false, length = 2000)
    private String val;
    @Basic
    @Column(name = "cor", nullable = true, length = 7)
    private String cor;
    @Basic
    @Column(name = "aplicacao", nullable = true, length = 1)
    private String aplicacao;

    public int getSequenciaVal() {
        return sequenciaVal;
    }

    public void setSequenciaVal(int sequenciaVal) {
        this.sequenciaVal = sequenciaVal;
    }

    public int getInd() {
        return ind;
    }

    public void setInd(int ind) {
        this.ind = ind;
    }

    public int getCampoAnalise() {
        return campoAnalise;
    }

    public void setCampoAnalise(int campoAnalise) {
        this.campoAnalise = campoAnalise;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(String aplicacao) {
        this.aplicacao = aplicacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiCorValCampoEntity that = (BiCorValCampoEntity) o;
        return sequenciaVal == that.sequenciaVal && ind == that.ind && campoAnalise == that.campoAnalise && Objects.equals(val, that.val) && Objects.equals(cor, that.cor) && Objects.equals(aplicacao, that.aplicacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequenciaVal, ind, campoAnalise, val, cor, aplicacao);
    }
}
