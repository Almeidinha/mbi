package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_grupo_usuario_slideshow", schema = "biserver", catalog = "BISERVER")
@IdClass(BiGrupoUsuarioSlideshowEntityPK.class)
public class BiGrupoUsuarioSlideshowEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "slideshow", nullable = false)
    private int slideshow;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "grupo_usuario", nullable = false)
    private int grupoUsuario;
    @Basic
    @Column(name = "permite_modificacao", nullable = false, length = 1)
    private String permiteModificacao;

    public int getSlideshow() {
        return slideshow;
    }

    public void setSlideshow(int slideshow) {
        this.slideshow = slideshow;
    }

    public int getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(int grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    public String getPermiteModificacao() {
        return permiteModificacao;
    }

    public void setPermiteModificacao(String permiteModificacao) {
        this.permiteModificacao = permiteModificacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiGrupoUsuarioSlideshowEntity that = (BiGrupoUsuarioSlideshowEntity) o;
        return slideshow == that.slideshow && grupoUsuario == that.grupoUsuario && Objects.equals(permiteModificacao, that.permiteModificacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slideshow, grupoUsuario, permiteModificacao);
    }
}
