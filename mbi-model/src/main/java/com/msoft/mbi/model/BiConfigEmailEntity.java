package com.msoft.mbi.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bi_config_email", schema = "biserver", catalog = "BISERVER")
public class BiConfigEmailEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "config_email", nullable = false)
    private short configEmail;
    @Basic
    @Column(name = "servidor_smtp", nullable = false, length = 200)
    private String servidorSmtp;
    @Basic
    @Column(name = "login_usuario", nullable = true, length = 100)
    private String loginUsuario;
    @Basic
    @Column(name = "senha_usuario", nullable = true, length = 50)
    private String senhaUsuario;
    @Basic
    @Column(name = "eh_servidor_padrao", nullable = false, length = 1)
    private String ehServidorPadrao;
    @Basic
    @Column(name = "num_porta", nullable = false)
    private int numPorta;
    @Basic
    @Column(name = "usa_tls", nullable = true, length = 1)
    private String usaTls;
    @Basic
    @Column(name = "usa_ssl", nullable = true, length = 1)
    private String usaSsl;
    @Basic
    @Column(name = "email", nullable = true, length = 75)
    private String email;
    @Basic
    @Column(name = "nome_exibicao", nullable = true, length = 75)
    private String nomeExibicao;

    public short getConfigEmail() {
        return configEmail;
    }

    public void setConfigEmail(short configEmail) {
        this.configEmail = configEmail;
    }

    public String getServidorSmtp() {
        return servidorSmtp;
    }

    public void setServidorSmtp(String servidorSmtp) {
        this.servidorSmtp = servidorSmtp;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public String getEhServidorPadrao() {
        return ehServidorPadrao;
    }

    public void setEhServidorPadrao(String ehServidorPadrao) {
        this.ehServidorPadrao = ehServidorPadrao;
    }

    public int getNumPorta() {
        return numPorta;
    }

    public void setNumPorta(int numPorta) {
        this.numPorta = numPorta;
    }

    public String getUsaTls() {
        return usaTls;
    }

    public void setUsaTls(String usaTls) {
        this.usaTls = usaTls;
    }

    public String getUsaSsl() {
        return usaSsl;
    }

    public void setUsaSsl(String usaSsl) {
        this.usaSsl = usaSsl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public void setNomeExibicao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiConfigEmailEntity that = (BiConfigEmailEntity) o;
        return configEmail == that.configEmail && numPorta == that.numPorta && Objects.equals(servidorSmtp, that.servidorSmtp) && Objects.equals(loginUsuario, that.loginUsuario) && Objects.equals(senhaUsuario, that.senhaUsuario) && Objects.equals(ehServidorPadrao, that.ehServidorPadrao) && Objects.equals(usaTls, that.usaTls) && Objects.equals(usaSsl, that.usaSsl) && Objects.equals(email, that.email) && Objects.equals(nomeExibicao, that.nomeExibicao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configEmail, servidorSmtp, loginUsuario, senhaUsuario, ehServidorPadrao, numPorta, usaTls, usaSsl, email, nomeExibicao);
    }
}
