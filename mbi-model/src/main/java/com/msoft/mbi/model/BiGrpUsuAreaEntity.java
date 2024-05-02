package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_grp_usu_area", schema = "biserver", catalog = "biserver")
@IdClass(BiGrpUsuAreaEntityPK.class)
public class BiGrpUsuAreaEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "grupo_usuario", nullable = false)
    private int grupoUsuario;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "area", nullable = false)
    private int area;

    public int getGrupoUsuario() {
        return grupoUsuario;
    }

    public void setGrupoUsuario(int grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiGrpUsuAreaEntity that = (BiGrpUsuAreaEntity) o;
        return grupoUsuario == that.grupoUsuario && area == that.area;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grupoUsuario, area);
    }
}
