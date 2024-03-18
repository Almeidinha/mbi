package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.DimensionTextFilter;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import com.msoft.mbi.model.BIPanelIndicatorEntity;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Restrictions  {

    @Getter
    @Setter
    private Indicator indicator;
    @Setter
    @Getter
    private Integer userId;
    @SuppressWarnings("unused")
    private Integer groupId;

    @Getter
    private HashMap<String, Restriction> restricoesIndicador;
    @Getter
    private Map<Integer, Map<Integer, DimensionFilter>> restricoes = new HashMap<>();

    public Restrictions(Indicator indicador, Integer usuario) throws BIException {
        super();
        this.indicator = indicador;
        this.userId = usuario;
        this.groupId = 1; // TODO load from db;
        this.restricoesIndicador = new HashMap<>();
        this.carregarRestricoesIndicador(indicador);
    }

    public Restrictions() {
        this.restricoesIndicador = new HashMap<String, Restriction>();
    }


    private void carregarRestricoesIndicador(Indicator indicador) throws BIException {

        if (indicador == null) {
            return;
        }

        int codigo = indicador.getCode();
        boolean herdaRestricao = indicador.isInheritsRestrictions();
        int codigoPai = (indicador.getOriginalIndicator() == null) ? 0 : indicador.getOriginalIndicator();

        StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT bi_restricao.restricao, bi_restricao.des_restricao, expressao FROM bi_restricao WHERE ind = ").append(codigo);

        ResultSet resultados = null;
        Restriction rest;
        ConnectionBean conexao = new ConnectionBean();

        try {
            resultados = conexao.executeSQL(sql.toString());
            while (resultados.next()) {
                rest = new Restriction();
                rest.setIndicator(indicador);
                rest.setCode(resultados.getInt(1));
                rest.setDescription(resultados.getString(2).trim());
                rest.setSqlExpression(resultados.getString(3).trim() + "\n");

                rest.consulta();
                this.addRestricao(rest);
            }

            if (herdaRestricao && codigoPai != 0) {
                BIPanelIndicatorEntity ind = null;

                try {
                    ind = null;// TODO load from db using codigoPai
                } catch (Exception e) {
                    BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_ind.");
                    biSQLEx.setAction("Consultar");
                    biSQLEx.setLocal(this.getClass(), "private void carregarRestricoesIndicador()");
                    throw biSQLEx;
                }

                if (ind != null) {
                    Indicator indPai = new Indicator();
                    // indPai.setUserId(indicador.getUserId());
                    // BIUtil.convertePersistivelParaIndicador(indPai, ind);

                    carregarRestricoesIndicador(indPai);
                }
            }

        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_restricao");
            biSQLEx.setAction("Consultar");
            biSQLEx.setLocal(this.getClass(), "private void carregarRestricoesIndicador()");
            throw biSQLEx;
        } finally {
            BIUtil.closeResultSet(resultados);
            conexao.closeConnection();
        }
    }

    public Restriction getRestricao(int codigo) {
        return (Restriction) this.restricoesIndicador.get(String.valueOf(codigo));
    }

    public void addRestricao(Restriction rest) {
        this.restricoesIndicador.put(
                new String(String.valueOf(rest.getIndicator().getCode()) + rest.getCode()), rest);
    }

    public void removerRestricao(int codigo) {
        this.restricoesIndicador.remove(String.valueOf(this.indicator.getCode()) + codigo);
    }

    public void incluirRestricoes(ConnectionBean conexao) throws BIException {
        for(Restriction rest : this.restricoesIndicador.values()) {
            rest.incluir(conexao);
        }
    }

    public void excluirRestricoes(ConnectionBean conexao) throws BIException {
        String sql = "DELETE FROM bi_restricao WHERE ind = ?";
        PreparedStatement stmt = null;
        try {
            this.excluirGrupos(conexao);
            this.excluirUsuarios(conexao);

            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_restricao");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "public void excluirRestricoes()");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    private void excluirUsuarios(ConnectionBean conexao) throws BIException {
        String sql = "DELETE FROM bi_rest_usuario WHERE ind = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_rest_usuario");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void excluirUsuarios(ConexaoBean conexao)");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    private void excluirGrupos(ConnectionBean conexao) throws BIException {
        String sql = "DELETE FROM bi_rest_grp_usu WHERE ind = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_rest_grp_usu");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void excluirGrupos(ConexaoBean conexao)");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    public Restriction novaRestricao() throws BIException {
        Restriction restricao = new Restriction();
        restricao.setCode(this.getNovoCodigoRestricao());
        restricao.setIndicator(this.indicator);
        this.addRestricao(restricao);
        return restricao;
    }

    private int getNovoCodigoRestricao() {
        int novoCodigo = 0;
        for(Restriction rest : this.restricoesIndicador.values()) {
            if (rest.getCode() > novoCodigo) {
                novoCodigo = rest.getCode();
            }
        }
        return novoCodigo + 1;
    }

    public String getExpressaoRestricaoUsuarioConectado() throws BIException {
        return this.getExpressaoRestricaoUsuario(this.userId);
    }

    public String getExpressaoRestricaoUsuario(Integer userId) throws BIException {
        String retorno = "";

        Map<Integer, Map<Integer, DimensionFilter>> restricoes = new HashMap<>();

        for(Restriction rest : this.restricoesIndicador.values()) {
            rest.setUserId(userId);
            if (rest.hasRestricao(userId)) {
                restricoes.put(restricoes.size() + 1, rest.getFiltrosRestricao());
                retorno += rest.getSQL() + " ";
            }
        }

        if (!retorno.isEmpty()) {
            retorno = "AND(1=1\n" + retorno.trim() + "\n)";
        }
        this.restricoes = restricoes;
        return retorno;
    }

    public boolean existeRestricoes() {
        return !this.restricoesIndicador.isEmpty();
    }

    public int aplicaValores(PreparedStatement stmt, int posicao) throws BIException {
        int size = this.restricoes.size();
        for (int key = 1; key <= size; key++) {
            Map<Integer, DimensionFilter> filtro = this.restricoes.get(key);
            posicao = this.applyFilterValues(stmt, posicao, filtro);
        }
        return posicao;
    }

    private int applyFilterValues(PreparedStatement stmt, int posicao, Map<Integer, DimensionFilter> filtro)
            throws BIException {
        int size = filtro.size();
        for (int key = 1; key <= size; key++) {
            DimensionFilter f = filtro.get(key);
            posicao = (Integer) f.applyValues(stmt, posicao);
        }
        return posicao;
    }

    public String applyRestrictionValues(String sql) throws BIException {
        int size = this.restricoes.size();
        for (int key = 1; key <= size; key++) {
            Map<Integer, DimensionFilter> filtro = this.restricoes.get(key);
            sql = this.aplicaValorFiltro(sql, filtro);
        }
        return sql;

    }

    private String aplicaValorFiltro(String sql, Map<Integer, DimensionFilter> filtro) throws BIException {
        int size = filtro.size();
        for (int key = 1; key <= size; key++) {
            DimensionFilter f = new DimensionTextFilter(filtro.get(key));
            sql = String.valueOf(f.applyValues(sql, 0));
        }
        return sql;
    }

}
