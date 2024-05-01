package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_portlets", schema = "biserver", catalog = "BISERVER")
public class BiPortletsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "portlets", nullable = false)
    private int portlets;
    @Basic
    @Column(name = "nom_portlets", nullable = true, length = 100)
    private String nomPortlets;
    @Basic
    @Column(name = "tip_portlets", nullable = true)
    private Integer tipPortlets;
    @Basic
    @Column(name = "largura", nullable = true)
    private Integer largura;
    @Basic
    @Column(name = "altura", nullable = true)
    private Integer altura;
    @Basic
    @Column(name = "usuario", nullable = true)
    private Integer usuario;
    @Basic
    @Column(name = "conteudo", nullable = true, length = 2500)
    private String conteudo;
    @Basic
    @Column(name = "publico", nullable = true)
    private Integer publico;

    public int getPortlets() {
        return portlets;
    }

    public void setPortlets(int portlets) {
        this.portlets = portlets;
    }

    public String getNomPortlets() {
        return nomPortlets;
    }

    public void setNomPortlets(String nomPortlets) {
        this.nomPortlets = nomPortlets;
    }

    public Integer getTipPortlets() {
        return tipPortlets;
    }

    public void setTipPortlets(Integer tipPortlets) {
        this.tipPortlets = tipPortlets;
    }

    public Integer getLargura() {
        return largura;
    }

    public void setLargura(Integer largura) {
        this.largura = largura;
    }

    public Integer getAltura() {
        return altura;
    }

    public void setAltura(Integer altura) {
        this.altura = altura;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Integer getPublico() {
        return publico;
    }

    public void setPublico(Integer publico) {
        this.publico = publico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiPortletsEntity that = (BiPortletsEntity) o;
        return portlets == that.portlets && Objects.equals(nomPortlets, that.nomPortlets) && Objects.equals(tipPortlets, that.tipPortlets) && Objects.equals(largura, that.largura) && Objects.equals(altura, that.altura) && Objects.equals(usuario, that.usuario) && Objects.equals(conteudo, that.conteudo) && Objects.equals(publico, that.publico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portlets, nomPortlets, tipPortlets, largura, altura, usuario, conteudo, publico);
    }
}
