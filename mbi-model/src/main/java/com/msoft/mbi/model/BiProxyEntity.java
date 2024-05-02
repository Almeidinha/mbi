package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_proxy", schema = "biserver", catalog = "biserver")
public class BiProxyEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "proxy", nullable = false)
    private int proxy;
    @Basic
    @Column(name = "url", nullable = true, length = 30)
    private String url;
    @Basic
    @Column(name = "porta", nullable = true)
    private Integer porta;
    @Basic
    @Column(name = "usuario", nullable = true, length = 20)
    private String usuario;
    @Basic
    @Column(name = "senha", nullable = true, length = 20)
    private String senha;

    public int getProxy() {
        return proxy;
    }

    public void setProxy(int proxy) {
        this.proxy = proxy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiProxyEntity that = (BiProxyEntity) o;
        return proxy == that.proxy && Objects.equals(url, that.url) && Objects.equals(porta, that.porta) && Objects.equals(usuario, that.usuario) && Objects.equals(senha, that.senha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxy, url, porta, usuario, senha);
    }
}
