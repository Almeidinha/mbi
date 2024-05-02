package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_slideshow_painel", schema = "biserver", catalog = "biserver")
@IdClass(BiSlideshowPainelEntityPK.class)
public class BiSlideshowPainelEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "slideshow", nullable = false)
    private int slideshow;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "painel", nullable = false)
    private int painel;
    @Basic
    @Column(name = "tempo_espera", nullable = true)
    private Integer tempoEspera;
    @Basic
    @Column(name = "sequencial_exibicao", nullable = false)
    private int sequencialExibicao;

    public int getSlideshow() {
        return slideshow;
    }

    public void setSlideshow(int slideshow) {
        this.slideshow = slideshow;
    }

    public int getPainel() {
        return painel;
    }

    public void setPainel(int painel) {
        this.painel = painel;
    }

    public Integer getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(Integer tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public int getSequencialExibicao() {
        return sequencialExibicao;
    }

    public void setSequencialExibicao(int sequencialExibicao) {
        this.sequencialExibicao = sequencialExibicao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiSlideshowPainelEntity that = (BiSlideshowPainelEntity) o;
        return slideshow == that.slideshow && painel == that.painel && sequencialExibicao == that.sequencialExibicao && Objects.equals(tempoEspera, that.tempoEspera);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slideshow, painel, tempoEspera, sequencialExibicao);
    }
}
