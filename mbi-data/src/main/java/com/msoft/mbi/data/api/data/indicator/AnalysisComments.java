package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIArrayIndexOutOfBoundsException;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.htmlbuilder.*;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.data.util.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class AnalysisComments {

    @Setter
    @Getter
    private Comment[] comments;

    @Getter
    private int indicatorCode;

    @Getter
    private String indicatorName;
    private String searchType;
    @Setter
    private String message = null;

    public void consul() throws BIException {
        String clausula = "";
        BIDate data = new BIDate();
        boolean filtraPorData = false;
        if (this.getSearchType().equals("T")) {
            clausula = " AND 1 = 1";
        } else if (this.getSearchType().equals("N")) {
            filtraPorData = true;
            clausula = " AND dat_expiracao >= ? ";
        } else if (this.getSearchType().equals("V")) {
            filtraPorData = true;
            clausula = " AND dat_expiracao < ? ";
        }
        String sql = "SELECT bi_coment.coment, bi_coment.ind, bi_coment.usuario, bi_coment.dat_hor_postagem, bi_coment.texto_coment, "
                + "bi_coment.dat_expiracao, bi_coment.envia_email FROM bi_coment WHERE bi_coment.ind = ? " + clausula + " ORDER BY bi_coment.coment DESC";
        ResultSet resultSet = null;
        Comment comment;
        ConnectionBean connectionBean = null;
        try {
            connectionBean = new ConnectionBean();
            PreparedStatement stmt = connectionBean.prepareStatement(sql);
            stmt.setInt(1, indicatorCode);
            if (filtraPorData) {
                stmt.setDate(2, new Date(data.getTimeInMillis()));
            }
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                comment = new Comment();
                comment.setCode(resultSet.getInt(1));
                comment.setIndicatorCode(resultSet.getInt(2));
                comment.setDateHour(resultSet.getTimestamp(4));
                comment.setCommentText(resultSet.getString(5).trim());
                // comment.setUser(new BIUserEntity(resultSet.getInt(3), connectionBean));
                if (resultSet.getDate(6) != null) {
                    comment.getExpirationDate().setTimeInMillis(resultSet.getDate(6).getTime());
                }
                comment.setSendEmail(resultSet.getString(7));
                comment.setSaved(true);
                this.addComment(comment);
            }
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, sql);
            biex.setAction("buscar comentarios da analise.");
            biex.setLocal(this.getClass(), "consulta(int)");
            throw biex;
        } finally {
            BIUtil.closeResultSet(resultSet);
            if (connectionBean != null) {
                connectionBean.closeConnection();
            }
        }
    }

    public void consultForEmail(ConnectionBean connectionBean) throws BIException {
        this.comments = null;
        String sql = "SELECT coment, ind, usuario, dat_hor_postagem, texto_coment, dat_expiracao, envia_email "
                + "FROM bi_coment WHERE ind = ? and dat_expiracao >= ? and envia_email = ?  ORDER BY coment DESC";
        ResultSet resultSet = null;
        Comment comment;
        try {
            PreparedStatement stmt = connectionBean.prepareStatement(sql);
            stmt.setInt(1, indicatorCode);
            stmt.setDate(2, new Date(new BIDate().getTimeInMillis()));
            stmt.setString(3, "S");
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                comment = new Comment();
                comment.setCode(resultSet.getInt(1));
                comment.setIndicatorCode(resultSet.getInt(2));
                comment.setDateHour(resultSet.getTimestamp(4));
                comment.setCommentText(resultSet.getString(5).trim());
                //comment.setUser(new BIUserEntity(resultSet.getInt(3), connectionBean));
                if (resultSet.getTimestamp(6) != null) {
                    comment.getExpirationDate().setTimeInMillis(resultSet.getTimestamp(6).getTime());
                }
                comment.setSendEmail(resultSet.getString(7));
                comment.setSaved(true);
                this.addComment(comment);
            }
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, sql);
            biex.setAction("buscar comentarios da analise.");
            biex.setLocal(this.getClass(), "consulta(int)");
            throw biex;
        } finally {
            BIUtil.closeResultSet(resultSet);
        }
    }

    public void addComment(Comment comment) {
        int indice = -1;
        if (this.comments == null) {
            this.comments = new Comment[5];
            indice = 0;
        } else {
            for (int i = 0; i < this.comments.length; i++) {
                if (this.comments[i] == null) {
                    indice = i;
                    break;
                }
            }
            if (indice == -1) {
                Comment[] commentAux = new Comment[this.comments.length + 1];
                System.arraycopy(this.comments, 0, commentAux, 0, this.comments.length);
                indice = commentAux.length - 1;
                this.comments = commentAux;
            }
        }
        this.comments[indice] = comment;
    }

    public String getCommentComponent(String panelIndex, String indicatorIndex) throws BIException {
        return this.getCommentComponent(panelIndex, indicatorIndex, false, 0);
    }

    public String getCommentComponent(boolean maintenance) throws BIException {
        return this.getCommentComponent("", "", maintenance, 0);
    }

    public String getCommentComponent(String panelIndex, String indicatorIndex, boolean maintenance, int userId) throws BIException {
        if (this.comments != null) {
            BIComparator.ordenaComentario(this.comments, Comment.DATAHORA);
            BINavegador navegador = new BINavegador(this.comments);
            Comment comment;
            TabelaHTML tabela = new TabelaHTML();
            tabela.setLargura("100%");
            int cont = 0;
            while (navegador.temProximo()) {
                if (comments[cont] != null) {
                    tabela.addLinha(new LinhaHTML());
                    tabela.getLinhaAtual().addCelula(new CelulaHTML());
                    tabela.getLinhaAtual().getCelulaAtual().setLargura("3%");
                    if (maintenance) {
                        tabela.getLinhaAtual().getCelulaAtual().setConteudo("<input type=\"checkbox\" name=\"c" + cont + "\" value=\"" + cont + "\">");
                    } else {
                        tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;");
                    }
                    tabela.getLinhaAtual().addCelula(new CelulaHTML());
                    tabela.getLinhaAtual().getCelulaAtual().setLargura("90%");
                    tabela.getLinhaAtual().getCelulaAtual().setClasse("pretoBranco");
                    comment = (Comment) navegador.proximo();
                    tabela.getLinhaAtual().getCelulaAtual().setConteudo(maintenance
                            ? new LinkHTML("javascript:janela_comentario('biComentarioAnalise.jsp', '" + panelIndex + "', '"
                            + indicatorIndex + "', '" + cont + "');", comment.getSummaryMaintenance()) : comment.getComment());
                    tabela.getLinhaAtual().addCelula(new CelulaHTML());
                    tabela.getLinhaAtual().getCelulaAtual().setLargura("7%");
                    tabela.getLinhaAtual().getCelulaAtual().setClasse("pretoBranco");
                    tabela.getLinhaAtual().getCelulaAtual().setAlinhamento("center");

                    if (maintenance) {
                        ImagemHTML imgExcluir = new ImagemHTML("imagens\\excluir.gif");
                        imgExcluir.setTextoAlternativo("Excluir Coment�rio");
                        LinkHTML linkExcluir = new LinkHTML("javascript:excluirComentario(" + cont + ");", imgExcluir);

                        if (userId == comment.getUserCode()) {
                            tabela.getLinhaAtual().getCelulaAtual().setConteudo(linkExcluir);
                        } else {
                            tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;");
                        }
                    }
                }
                cont++;
            }
            return tabela.toString();
        } else
            return "";
    }

    public String getComponenteComentarioCompleto() throws BIException {
        if (this.comments != null) {
            BIComparator.ordenaComentario(this.comments, Comment.DATAHORA);
            Comment comentario;
            TabelaHTML tabela = new TabelaHTML();
            tabela.setLargura("100%");

            EstiloHTML estiloCabecalho = new EstiloHTML();
            estiloCabecalho.setFontFamily("Verdana, Arial, Helvetica, sans-serif");
            estiloCabecalho.setFontSize(12);
            estiloCabecalho.setFontColor("#FFFFFF");
            estiloCabecalho.setBackgroundColor("#7AA7DE");

            EstiloHTML estiloTitulo = new EstiloHTML();
            estiloTitulo.setFontFamily("Verdana, Arial, Helvetica, sans-serif");
            estiloTitulo.setFontSize(10);
            estiloTitulo.setFontColor("#FFFFFF");
            estiloTitulo.setBackgroundColor("#3377CC");

            tabela.addLinha(new LinhaHTML());
            tabela.getLinhaAtual().addCelula(new CelulaHTML());
            tabela.getLinhaAtual().getCelulaAtual().setAlinhamento("right");
            tabela.getLinhaAtual().getCelulaAtual().setCorBorda("#FFFFFF");
            tabela.getLinhaAtual().getCelulaAtual().setConteudo("Comentários da Análise");
            tabela.getLinhaAtual().getCelulaAtual().setAlinhamento("center");
            tabela.getLinhaAtual().getCelulaAtual().setEstilo(estiloCabecalho);

            tabela.getLinhaAtual().getCelulaAtual().setAltura("30");
            tabela.getLinhaAtual().getCelulaAtual().setColspan(3);
            tabela.getLinhaAtual().getCelulaAtual().setNowrap(true);

            tabela.addLinha(new LinhaHTML());
            tabela.getLinhaAtual().addCelula(new CelulaHTML());
            tabela.getLinhaAtual().getCelulaAtual().setCorBorda("#FFFFFF");
            tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;Data e Hora");
            tabela.getLinhaAtual().getCelulaAtual().setEstilo(estiloTitulo);
            tabela.getLinhaAtual().getCelulaAtual().setAltura("30");
            tabela.getLinhaAtual().getCelulaAtual().setNowrap(true);

            tabela.getLinhaAtual().addCelula(new CelulaHTML());
            tabela.getLinhaAtual().getCelulaAtual().setCorBorda("#FFFFFF");
            tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;Postado por");
            tabela.getLinhaAtual().getCelulaAtual().setEstilo(estiloTitulo);
            tabela.getLinhaAtual().getCelulaAtual().setAltura("30");
            tabela.getLinhaAtual().getCelulaAtual().setNowrap(true);

            tabela.getLinhaAtual().addCelula(new CelulaHTML());
            tabela.getLinhaAtual().getCelulaAtual().setCorBorda("#FFFFFF");
            tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;Mensagem");
            tabela.getLinhaAtual().getCelulaAtual().setEstilo(estiloTitulo);
            tabela.getLinhaAtual().getCelulaAtual().setAltura("30");
            tabela.getLinhaAtual().getCelulaAtual().setNowrap(true);

            for (Comment comment : this.comments) {
                comentario = comment;
                if (comentario != null) {
                    EstiloHTML estilo = new EstiloHTML();
                    estilo.setFontFamily("Verdana, Arial, Helvetica, sans-serif");
                    estilo.setFontSize(9);
                    estilo.setFontColor("#003399");
                    estilo.setBackgroundColor("#E6EEFF");

                    tabela.addLinha(new LinhaHTML());
                    tabela.getLinhaAtual().addCelula(new CelulaHTML());
                    tabela.getLinhaAtual().getCelulaAtual().setLargura("18%");
                    tabela.getLinhaAtual().getCelulaAtual().setClasse("texto");
                    tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;" + comentario.getDateHourToString());
                    tabela.getLinhaAtual().getCelulaAtual().setAltura("20");
                    tabela.getLinhaAtual().getCelulaAtual().setNowrap(true);
                    tabela.getLinhaAtual().setEstilo(estilo);

                    tabela.getLinhaAtual().addCelula(new CelulaHTML());
                    tabela.getLinhaAtual().getCelulaAtual().setClasse("texto");
                    tabela.getLinhaAtual().getCelulaAtual().setLargura("32%");
                    tabela.getLinhaAtual().getCelulaAtual().setConteudo("&nbsp;" + comentario.getUserCode());
                    tabela.getLinhaAtual().getCelulaAtual().setAltura("20");
                    tabela.getLinhaAtual().addCelula(new CelulaHTML());
                    tabela.getLinhaAtual().getCelulaAtual().setLargura("50%");
                    tabela.getLinhaAtual().getCelulaAtual().setClasse("texto");
                    tabela.getLinhaAtual().getCelulaAtual()
                            .setConteudo((comentario.getTextoHTML() != null && !comentario.getTextoHTML().trim().isEmpty())
                                    ? comentario.getTextoHTML() : "&nbsp;");
                    tabela.getLinhaAtual().getCelulaAtual().setAltura("20");
                }
            }
            return tabela.toString();
        } else {
            return "";
        }
    }

    public Comment getComentario(int indice) throws BIArrayIndexOutOfBoundsException {
        if (this.comments == null) {
            return null;
        }
        try {
            return this.comments[indice];
        } catch (ArrayIndexOutOfBoundsException aex) {
            BIArrayIndexOutOfBoundsException biex = new BIArrayIndexOutOfBoundsException(aex);
            biex.setLocal(this.getClass(), "getComentarios(int)");
            biex.setAction("buscar item não existente.");
            throw biex;
        }
    }

    public void salvar(ConnectionBean connectionBean) throws BIException {
        if (this.comments != null) {
            for (Comment comment : this.comments) {
                if (comment != null) {
                    comment.save(connectionBean);
                }
            }
        }
    }

    public void salvar(ConnectionBean connectionBean, int newIndicatorCode) throws BIException {
        if (this.comments != null) {
            int codigoTemp = Integer.parseInt(BIUtil.getNewData("coment", "bi_coment", "where ind = " + newIndicatorCode));
            for (Comment comment : this.comments) {
                if (comment != null) {
                    comment.setIndicatorCode(newIndicatorCode);
                    comment.setCode(codigoTemp++);
                    comment.save(connectionBean);
                }
            }
        }
    }

    public void setIndicador(Indicator indicator) throws BIException {
        this.indicatorCode = indicator.getCode();
        this.indicatorName = indicator.getName();
    }

    public void setIndicatorCode(int indicatorCode) throws BIException {
        this.indicatorCode = indicatorCode;
        // TODO get this from db
        // this.indicatorName = indicador.getNome();

    }

    public void excluir(String itens) throws BIException {
        List<String> comentarios = BIUtil.stringtoList(itens);
        Iterator<String> i = comentarios.iterator();
        ConnectionBean conexao = new ConnectionBean();
        try {
            while (i.hasNext()) {
                int indice = Integer.parseInt(i.next());
                Comment.delete(conexao, this.comments[indice].getCode(), this.indicatorCode);
                this.comments[indice] = null;
            }
        } finally {
            conexao.closeConnection();
        }
    }

    public void atualizarEmail(String itens, String valor) throws BIException {
        List<String> comentarios = BIUtil.stringtoList(itens);
        Iterator<String> i = comentarios.iterator();
        ConnectionBean conexao = new ConnectionBean();
        try {
            while (i.hasNext()) {
                int indice = Integer.parseInt(i.next());
                Comment.updateEmail(conexao, this.comments[indice].getCode(), valor);
                this.comments[indice] = null;
            }
        } finally {
            conexao.closeConnection();
        }
    }

    public void inicializaComentarios() {
        this.comments = null;
    }

    public String getSearchType() {
        if (searchType == null) {
            return "T";
        }
        return searchType;
    }

    public void setSearchType(String searchType) throws BIException {
        this.searchType = searchType;
        this.inicializaComentarios();
        this.consul();
    }

    public String getMessage() {
        if (this.message != null) {
            return this.message;
        }
        return "";
    }

}
