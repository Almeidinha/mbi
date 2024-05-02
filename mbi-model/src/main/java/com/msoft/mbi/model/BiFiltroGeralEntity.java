package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_filtro_geral", schema = "biserver", catalog = "biserver")
public class BiFiltroGeralEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "filtro", nullable = false)
    private int filtro;
    @Basic
    @Column(name = "descricao_filtro", nullable = false, length = 50)
    private String descricaoFiltro;
    @Basic
    @Column(name = "tip_dado", nullable = true, length = 1)
    private String tipDado;

    public int getFiltro() {
        return filtro;
    }

    public void setFiltro(int filtro) {
        this.filtro = filtro;
    }

    public String getDescricaoFiltro() {
        return descricaoFiltro;
    }

    public void setDescricaoFiltro(String descricaoFiltro) {
        this.descricaoFiltro = descricaoFiltro;
    }

    public String getTipDado() {
        return tipDado;
    }

    public void setTipDado(String tipDado) {
        this.tipDado = tipDado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiFiltroGeralEntity that = (BiFiltroGeralEntity) o;
        return filtro == that.filtro && Objects.equals(descricaoFiltro, that.descricaoFiltro) && Objects.equals(tipDado, that.tipDado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filtro, descricaoFiltro, tipDado);
    }
}
