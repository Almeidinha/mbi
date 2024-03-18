package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Getter
@Setter
public class analysisGroupPermissions {


    private int indicatorCode;
    private int userGroup;
    private String allowChange;

    public static final String PERMITE_MODIFICAR = "S";
    public static final String NAO_PERMITE_MODIFICAR = "N";

    public analysisGroupPermissions() {

    }

    public analysisGroupPermissions(int indicador, int grupoUsuario, ConnectionBean connectionBean) throws BIException {
        this.indicatorCode = indicador;
        this.userGroup = grupoUsuario;
        this.consulta();
    }

    public boolean consulta(int codigo, int grupo) throws BIException {
        this.indicatorCode = codigo;
        this.userGroup = grupo;
        return this.consulta();
    }

    private boolean consulta() throws BIException {
        boolean retorno;
        String sql = "SELECT permite_modific FROM bi_grp_usuario_ind WHERE ind = ? AND grupo_usuario = ?";
        ResultSet resultados = null;
        PreparedStatement stmt = null;
        ConnectionBean conexao = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicatorCode);
            stmt.setInt(2, this.userGroup);
            resultados = stmt.executeQuery();
            if (resultados.next()) {
                this.allowChange = resultados.getString(1);
                retorno = true;
            } else {
                retorno = false;
            }
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_grp_usuario_ind");
            biSQLEx.setAction("Consultar");
            biSQLEx.setLocal(this.getClass(), "private void consulta()");
            throw biSQLEx;
        } finally {
            BIUtil.closeResultSet(resultados);
            BIUtil.closeStatement(stmt);

            if (conexao != null) {
                conexao.closeConnection();
            }

        }
        return retorno;
    }

    public void incluir(int codigo, int grupo, String permiteModificar, ConnectionBean conexao) throws BIException {
        this.indicatorCode = codigo;
        this.userGroup = grupo;
        this.allowChange = permiteModificar;
        this.incluir(conexao);
    }

    private void incluir(ConnectionBean conexao) throws BIException {
        String sql = "INSERT INTO bi_grp_usuario_ind (ind, grupo_usuario, permite_modific) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicatorCode);
            stmt.setInt(2, this.userGroup);
            stmt.setString(3, this.allowChange);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao incluir dados na bi_grp_usuario_ind \nSQL: " + sql);
            biSQLEx.setAction("Incluir");
            biSQLEx.setLocal(this.getClass(), "private void incluir()");
            try {
                conexao.rollback();
            } catch (SQLException ex) {
                System.out.println("Erro no rollback!!");
            }
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    public void excluir(int indicador, int grupo) throws BIException {
        this.indicatorCode = indicador;
        this.userGroup = grupo;
        this.excluir();
    }

    public boolean excluiGrupoUsuarioIndicador(int grupoUsuario) throws BIException {
        boolean retorno;
        String sql = "delete from bi_grp_usuario_ind where grupo_usuario = ?";
        ConnectionBean conexao = null;
        PreparedStatement stmt = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, grupoUsuario);

            if (stmt.executeUpdate() == 0) {
                retorno = false;
            } else {
                retorno = true;
            }
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, sql);
            biex.setAction("excluir grupo de usu�rio de um indicador");
            biex.setLocal(this.getClass(), "excluiGrupoUsuarioIndicador");
            throw biex;
        } finally {
            BIUtil.closeStatement(stmt);

            if (conexao != null) {
                conexao.closeConnection();
            }
        }
        return retorno;
    }

    private void excluir() throws BIException {
        String sql = "DELETE FROM bi_grp_usuario_ind WHERE ind = ? AND grupo_usuario = ?";
        PreparedStatement stmt = null;
        ConnectionBean conexao = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicatorCode);
            stmt.setInt(2, this.userGroup);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_grp_usuario_ind");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void excluir()");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);

            if (conexao != null) {
                conexao.closeConnection();
            }
        }
    }

    public void excluirPermissoesAnalise(int codIndicador) throws BIException {
        String sql = "DELETE FROM bi_grp_usuario_ind WHERE ind = ?";
        PreparedStatement stmt = null;
        ConnectionBean conexao = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, codIndicador);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_grp_usuario_ind");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void excluirPermissoesAnalise(int codIndicador)");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);

            if (conexao != null) {
                conexao.closeConnection();
            }
        }
    }

    public ArrayList<String> getPermissoes(int codigoIndicador, String permiteModificar) throws BIException {
        ArrayList<String> retorno = new ArrayList<>();
        String sql = "SELECT grupo_usuario FROM bi_grp_usuario_ind WHERE permite_modific = ? AND ind = ?";
        PreparedStatement stmt = null;
        ResultSet resultados = null;
        ConnectionBean conexao = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, permiteModificar);
            stmt.setInt(2, codigoIndicador);
            resultados = stmt.executeQuery();
            while (resultados.next()) {
                retorno.add(String.valueOf(resultados.getInt(1)));
            }
        } catch (SQLException e) {
            BISQLException sqlex = new BISQLException(e, sql);
            sqlex.setLocal(this.getClass(), "public ArrayList getPermissoes(int codigoIndicador, String permiteModificar)");
            throw sqlex;
        } finally {
            BIUtil.closeResultSet(resultados);
            BIUtil.closeStatement(stmt);
            if (conexao != null) {
                conexao.closeConnection();
            }
        }
        return retorno;
    }

    public ArrayList<String> getPermissoesEscrita(int codigoIndicador) throws BIException {
        return getPermissoes(codigoIndicador, analysisGroupPermissions.PERMITE_MODIFICAR);
    }

    public ArrayList<String> getPermissoesLeitura(int codigoIndicador) throws BIException {
        return getPermissoes(codigoIndicador, analysisGroupPermissions.NAO_PERMITE_MODIFICAR);
    }
}
