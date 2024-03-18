package com.msoft.mbi.data.api.data.indicator;

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
import java.util.List;

@Getter
@Setter
public class analysisUserPermission {

    private int indicatorCode;
    private int userId;
    private String allowChange;
    private String favorite;

    public static final String PERMITE_MODIFICAR = "S";
    public static final String NAO_PERMITE_MODIFICAR = "N";

    public analysisUserPermission(int indicador, int usuario) throws BIException {
        this.indicatorCode = indicador;
        this.userId = usuario;
        this.consulta();
    }

    public analysisUserPermission() {
    }

    public boolean consulta(int codigo, int usuario) throws BIException {
        this.indicatorCode = codigo;
        this.userId = usuario;
        return this.consulta();
    }

    private boolean consulta() throws BIException {
        boolean retorno;
        String sql = "SELECT permite_modific FROM bi_usuario_ind WHERE ind = ? AND usuario = ?";
        ConnectionBean conexao = null;
        ResultSet resultados = null;
        PreparedStatement stmt = null;

        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicatorCode);
            stmt.setInt(2, this.userId);
            resultados = stmt.executeQuery();
            if (resultados.next()) {
                this.allowChange = resultados.getString(1);
                retorno = true;
            } else {
                retorno = false;
            }
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_usuario_ind");
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

    public List<analysisUserPermission> consultaPermissoesIndicador(int codIndicador) throws BIException {
        List<analysisUserPermission> retorno = new ArrayList<>();
        String sql = "SELECT ind, usuario, permite_modific, favorita FROM bi_usuario_ind WHERE ind = ?";
        ConnectionBean conexao = null;
        ResultSet resultados = null;
        PreparedStatement stmt = null;

        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, codIndicador);
            resultados = stmt.executeQuery();
            analysisUserPermission permissaoUsuario = null;
            while (resultados.next()) {
                permissaoUsuario = new analysisUserPermission();
                permissaoUsuario.setIndicatorCode(resultados.getInt(1));
                permissaoUsuario.setUserId(resultados.getInt(2));
                permissaoUsuario.setAllowChange(resultados.getString(3));
                permissaoUsuario.setFavorite(resultados.getString(4));
                retorno.add(permissaoUsuario);
            }
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_usuario_ind");
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

    public void incluir(int codigo, int usuario, String permiteModificar, String favorita, ConnectionBean conexao) throws BIException {
        this.indicatorCode = codigo;
        this.userId = usuario;
        this.allowChange = permiteModificar;
        this.favorite = favorita;
        this.incluir(conexao);
    }

    public void incluir(int codigo, int usuario, String permiteModificar, ConnectionBean conexao) throws BIException {
        this.incluir(codigo, usuario, permiteModificar, "N", conexao);
    }

    public void incluir(ConnectionBean conexao) throws BIException {
        String sql = "INSERT INTO bi_usuario_ind (ind, usuario, permite_modific, favorita) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicatorCode);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.allowChange);
            stmt.setString(4, this.favorite);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao incluir dados na bi_usuario_ind \nSQL: " + sql);
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

    public void excluir(int indicador, int usuario) throws BIException {
        this.indicatorCode = indicador;
        this.userId = usuario;
        this.excluir();
    }

    private void excluir() throws BIException {
        String sql = "DELETE FROM bi_usuario_ind WHERE ind = ? AND usuario = ?";
        PreparedStatement stmt = null;
        ConnectionBean conexao = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicatorCode);
            stmt.setInt(2, this.userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_usuario_ind");
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
        String sql = "DELETE FROM bi_usuario_ind WHERE ind = ?";
        ConnectionBean conexao = null;
        PreparedStatement stmt = null;
        try {
            conexao = new ConnectionBean();
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, codIndicador);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_usuario_ind");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void excluirPermissoesUsuario(int usuario)");
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
        String sql = "SELECT usuario FROM bi_usuario_ind WHERE permite_modific = ? AND ind = ?";
        ConnectionBean conexao = null;
        PreparedStatement stmt = null;
        ResultSet resultados = null;
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
        return getPermissoes(codigoIndicador, analysisUserPermission.PERMITE_MODIFICAR);
    }

    public ArrayList<String> getPermissoesLeitura(int codigoIndicador) throws BIException {
        return getPermissoes(codigoIndicador, analysisUserPermission.NAO_PERMITE_MODIFICAR);
    }

}
