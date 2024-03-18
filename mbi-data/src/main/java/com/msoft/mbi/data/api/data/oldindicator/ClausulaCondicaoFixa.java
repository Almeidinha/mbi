package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Setter
@Getter
public class ClausulaCondicaoFixa {

    private Integer indicador;
    private String condicaoFixa;

    public ClausulaCondicaoFixa(int indicador) {
        this.indicador = indicador;
    }

    public ClausulaCondicaoFixa() {
        super();
    }

    public ClausulaCondicaoFixa(int indicador, ConnectionBean connectionBean) throws BIException {
        this.pesquisar(indicador, this, connectionBean);
    }

    public void inserir(ConnectionBean conexaoBean) throws BIException {
        String sql = "INSERT INTO bi_ind_cond_fixa (ind, texto_sql) VALUES(?, ?)";
        PreparedStatement statement;
        try {
            statement = conexaoBean.prepareStatement(sql);
            statement.setInt(1, this.indicador);
            statement.setString(2, this.condicaoFixa);
            statement.execute();
        } catch (SQLException sqle) {
            try {
                conexaoBean.rollback();
            } catch (SQLException sqlex) {
                System.out.println("Erro no rollback!!!");
            }
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Salvar cláusula condição fixa");
            biex.setLocal(getClass(), "inserir(ConexaoBean)");
            throw biex;
        }
    }

    public void pesquisar(int indicador, ClausulaCondicaoFixa condicaoFixa, ConnectionBean conexao) throws BIException {
        String sql = "SELECT ind, texto_sql FROM bi_ind_cond_fixa WHERE ind=?";
        try {
            PreparedStatement statement = conexao.prepareStatement(sql);
            statement.setInt(1, indicador);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                condicaoFixa.setIndicador(set.getInt(1));
                condicaoFixa.setCondicaoFixa(set.getString(2));
            }
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Pesquisar Cláusulas condição fixa");
            biex.setLocal(getClass(), "pesquisar(int, ClausulaCondicaoFixa)");
            throw biex;
        }
    }

    public void update(ConnectionBean conexaoBean) throws BIException {
        String sql = "UPDATE bi_ind_cond_fixa SET texto_sql= ? WHERE ind = ?";
        PreparedStatement statement;
        try {
            statement = conexaoBean.prepareStatement(sql);
            statement.setString(1, this.condicaoFixa);
            statement.setInt(2, this.indicador);
            statement.execute();
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Atualizar Cláusulas condição fixa");
            biex.setLocal(getClass(), "update()");
            throw biex;
        }
    }

    public void delete(ConnectionBean conexaoBean) throws BIException {
        String sql = "DELETE FROM bi_ind_cond_fixa WHERE ind = ?";
        PreparedStatement statement;
        try {
            statement = conexaoBean.prepareStatement(sql);
            statement.setInt(1, this.indicador);
            statement.execute();
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.indicador + "\n" + sql);
            biex.setAction("Excluir Cláusulas condição fixa");
            biex.setLocal(getClass(), "delete()");
            throw biex;
        }
    }

}
