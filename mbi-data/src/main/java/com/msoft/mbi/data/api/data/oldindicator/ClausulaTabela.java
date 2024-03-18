package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClausulaTabela {

    @Getter
    @Setter
    private Integer indicador;

    @Getter
    @Setter
    private String condicaoTabela;

    public ClausulaTabela(int indicador) {
        this.indicador = indicador;
    }

    public ClausulaTabela() {
        super();
    }

    public ClausulaTabela(int indicador, ConnectionBean conexaoBean) throws BIException {
        this.pesquisar(indicador, this, conexaoBean);
    }

    public void inserir(ConnectionBean conexaoBean) throws BIException {
        String sql = "INSERT INTO bi_ind_tabela (ind, texto_sql) VALUES(?, ?)";
        PreparedStatement statement;
        try {
            statement = conexaoBean.prepareStatement(sql);
            statement.setInt(1, this.indicador);
            statement.setString(2, this.condicaoTabela);
            statement.execute();
        } catch (SQLException sqle) {
            try {
                conexaoBean.rollback();
            } catch (SQLException sqlex) {
                System.out.println("Erro no rollback!!!");
            }
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Salvar cláusula tabela");
            biex.setLocal(getClass(), "inserir(ConexaoBean)");
            throw biex;
        }
    }

    public ClausulaTabela pesquisar(int indicador, ClausulaTabela clausulaTabela, ConnectionBean conexao) throws BIException {
        String sql = "SELECT ind, texto_sql FROM bi_ind_tabela WHERE ind=?";
        try {
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setInt(1, indicador);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                clausulaTabela.setIndicador(set.getInt(1));
                clausulaTabela.setCondicaoTabela(set.getString(2));
            }
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Pesquisar cláusula tabela");
            biex.setLocal(getClass(), "pesquisar(int, ClausulaTabela)");
            throw biex;
        }
        return clausulaTabela;
    }

    public void update(ConnectionBean conexaoBean) throws BIException {
        String sql = "UPDATE bi_ind_tabela SET texto_sql= ? WHERE ind = ?";
        PreparedStatement statement;
        try {
            statement = conexaoBean.prepareStatement(sql);
            statement.setString(1, this.condicaoTabela);
            statement.setInt(2, this.indicador);
            statement.execute();
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Atualizar cláusula tabela");
            biex.setLocal(getClass(), "update()");
            throw biex;
        }
    }

    public void delete(ConnectionBean conexaoBean) throws BIException {
        String sql = "DELETE FROM bi_ind_tabela WHERE ind = ?";
        PreparedStatement statement;
        try {
            statement = conexaoBean.prepareStatement(sql);
            statement.setInt(1, this.indicador);
            statement.execute();
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Excluir cláusula tabela");
            biex.setLocal(getClass(), "delete()");
            throw biex;
        }
    }

    public String getTextoTabelasUtilizadas() {
        String texto = "";
        if (this.condicaoTabela != null) {
            texto = this.condicaoTabela.substring(5).trim();
        }
        return texto;
    }
}
