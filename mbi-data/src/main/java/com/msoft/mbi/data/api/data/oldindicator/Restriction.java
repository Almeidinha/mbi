package com.msoft.mbi.data.api.data.oldindicator;

import com.google.gson.JsonArray;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.DimensionFilterJDBC;
import com.msoft.mbi.data.api.data.filters.FilterFactory;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class Restriction {

    private int code;
    private Indicator indicator;
    private String description;
    private String sqlExpression;

    private ArrayList<Integer> userList;
    private ArrayList<Integer> groupList;
    private ArrayList<Integer> userPermissionIds;
    private ArrayList<Integer> groupPermissionIds;
    @Setter
    private Integer userId;
    @Getter
    private Map<Integer, DimensionFilter> filters = new HashMap<>();

    public Restriction() {
        this.groupList = new ArrayList<>();
        this.groupPermissionIds = new ArrayList<>();
        this.userList = new ArrayList<>();
        this.userPermissionIds = new ArrayList<>();
    }

    public void consulta() throws BIException {
        this.carregarUsuarios();
        this.carregarGrupos();
    }

    private void carregarUsuarios() throws BIException {
        StringBuilder sql = new StringBuilder(100);
        sql.append(" SELECT bi_rest_usuario.usuario, bi_rest_usuario.permissao_escrita")
                .append(" FROM bi_rest_usuario")
                .append(" WHERE ind = ").append(this.indicator.getCode())
                .append(" AND restricao = ").append(this.code);
        ResultSet results = null;

        ConnectionBean conexao = new ConnectionBean();
        try {
            results = conexao.executeSQL(sql.toString());

            while (results.next()) {
                int codigoUsuario = results.getInt(1);
                String permissao = results.getString(2);
                if (permissao.equals("S")) {
                    this.userPermissionIds.add(codigoUsuario);
                } else {
                    this.userList.add(codigoUsuario);
                }
            }

        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_rest_usuario");
            biSQLEx.setAction("Consultar");
            biSQLEx.setLocal(this.getClass(), "private void carregarUsuarios()");
            throw biSQLEx;
        } finally {
            BIUtil.closeResultSet(results);
            conexao.closeConnection();
        }
    }

    private void carregarGrupos() throws BIException {
        StringBuilder sql = new StringBuilder(100);
        sql.append(" SELECT bi_rest_grp_usu.grupo_usuario, bi_rest_grp_usu.permissao_escrita")
                .append(" FROM bi_rest_grp_usu")
                .append(" WHERE ind = ").append(this.indicator.getCode())
                .append(" AND restricao = ").append(this.code);

        ResultSet results = null;
        ConnectionBean conexao = new ConnectionBean();
        try {
            results = conexao.executeSQL(sql.toString());

            while (results.next()) {
                int codigoGrupo = results.getInt(1);
                String permissao = results.getString(2);
                if (permissao.equals("S")) {
                    this.groupPermissionIds.add(codigoGrupo);
                } else {
                    this.groupList.add(codigoGrupo);
                }
            }

        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao consultar os dados na bi_rest_grp_usu");
            biSQLEx.setAction("Consultar");
            biSQLEx.setLocal(this.getClass(), "private void carregarGrupos()");
            throw biSQLEx;
        } finally {
            BIUtil.closeResultSet(results);
            conexao.closeConnection();
        }
    }

    public boolean hasRestricao(Integer userId) {
        // TODO Carregar usuário do banco
        /*for (GrupoUsuario group : this.groupList) {
            if (group.getCodigo() == usuario.getGrupo()) {
                return true;
            }
        }*/

        for (Integer id : this.userList) {
            if (Objects.equals(id, userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermissaoAlteracao(Integer userId) {
        // TODO Carregar usuário do banco
        /*for (GrupoUsuario group : this.groupPermissionIds) {
            if (group.getCodigo() == usuario.getGrupo()) {
                return true;
            }
        }*/

        for (Integer id : this.userPermissionIds) {
            if (Objects.equals(id, userId)) {
                return true;
            }
        }
        return false;
    }


    public String getSqlExpression() {
        if (sqlExpression == null)
            return "";
        return sqlExpression.trim() + "\r\n";
    }

    private void converteExpressaoEmFiltros(String expressao) throws BIException {
        if (expressao != null && !"".equals(expressao)) {
            expressao = expressao.trim();
            if (expressao.toUpperCase().indexOf("AND") == 0 || expressao.toUpperCase().indexOf("OR") == 0) {

                String regex = expressao.substring(0, expressao.indexOf("}") + 1);
                regex = regex.replaceAll("\\{", "\\\\{");
                regex = regex.replaceAll("\\(", "\\\\(");
                regex = regex.replaceAll("\\)", "\\\\)");
                regex = regex.replaceAll("\\}", "\\\\}");
                regex += "[\\s]*[<>=like]{1,2}[\\s]*[^\\{]*";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(expressao);
                boolean pI = false;
                boolean pF = false;

                DimensionFilter filter = new DimensionFilterJDBC();

                if (m.find()) {
                    String expressaoOriginal = m.group();
                    expressaoOriginal = expressaoOriginal.trim();

                    int lastOR = expressaoOriginal.toUpperCase().lastIndexOf("OR ");
                    int lastAND = expressaoOriginal.toUpperCase().lastIndexOf("AND ");
                    int iniString = expressaoOriginal.toUpperCase().indexOf("'");
                    int finString = expressaoOriginal.toUpperCase().lastIndexOf("'");

                    if (lastOR >= iniString && lastOR <= finString) {
                        lastOR = 0;
                    }

                    if (lastAND >= iniString && lastAND <= finString) {
                        lastAND = 0;
                    }

                    if (lastOR > lastAND) {
                        lastAND = 0;
                    }

                    if (lastAND > lastOR) {
                        lastOR = 0;
                    }

                    if (lastOR > 0) {
                        expressaoOriginal = expressaoOriginal.substring(0, lastOR - 1).trim();
                    }

                    if (lastAND > 0) {
                        expressaoOriginal = expressaoOriginal.substring(0, lastAND - 1).trim();
                    }

                    if (expressaoOriginal.endsWith("(")) {
                        expressaoOriginal = expressaoOriginal.substring(0, expressaoOriginal.length() - 1).trim();
                    }

                    if (expressaoOriginal.lastIndexOf("AND") == (expressaoOriginal.length() - 3)) {
                        expressaoOriginal = expressaoOriginal.substring(0, expressaoOriginal.length() - 3);
                    }

                    if (expressaoOriginal.lastIndexOf("OR") == (expressaoOriginal.length() - 2)) {
                        expressaoOriginal = expressaoOriginal.substring(0, expressaoOriginal.length() - 2);
                    }

                    if ((expressaoOriginal.startsWith("AND (") || expressaoOriginal.startsWith("OR ("))) {
                        pI = true;
                    }

                    if ((expressaoOriginal.endsWith(")")) || (expressaoOriginal.endsWith(")\r\n"))) {
                        pF = true;
                    }

                    int countParentI = StringUtils.countMatches(expressaoOriginal, "(");
                    int countParentF = StringUtils.countMatches(expressaoOriginal, ")");

                    String exp = expressaoOriginal.trim();

                    p = Pattern.compile("^AND|^OR|^and|^or");
                    m = p.matcher(exp);
                    if (m.find()) {
                        String conector = m.group();
                        filter.setConnector(conector.toLowerCase());
                        filter.setStartParentheses(pI);
                        filter.setEndParentheses(pF);
                        filter.setStartParentCount(countParentI);
                        filter.setEndParentCount(countParentF);

                        p = Pattern.compile("\\{.*}");
                        m = p.matcher(exp);
                        if (m.find()) {
                            String campo = m.group();
                            campo = campo.substring(1, campo.length() - 1);
                            if (campo.contains(".")) {
                                String nomeTabelaCampo = campo.substring(0, campo.indexOf("."));
                                String nomeCampo = campo.substring(campo.indexOf(".") + 1);
                                Field objetoCampo = this.indicator.buscaFieldPorNome(nomeTabelaCampo, nomeCampo);

                                p = Pattern.compile(">=|<=|=|<>|>|<|like");
                                m = p.matcher(exp);
                                if (m.find()) {
                                    String operador = m.group();
                                    int sizeOperador = operador.length();
                                    String valor = exp.substring(exp.indexOf(operador) + sizeOperador);
                                    valor = valor.trim();
                                    if (valor.contains("'")) {
                                        valor = valor.substring(valor.indexOf("'") + 1, valor.lastIndexOf("'"));
                                    }
                                    if (valor.contains("\"")) {
                                        valor = valor.substring(valor.indexOf("\"") + 1, valor.lastIndexOf("\""));
                                    }
                                    valor = valor.trim();
                                    ConditionJDBC condicao = new ConditionJDBC(objetoCampo, new Operator(operador), valor);
                                    filter.setCondition(condicao);
                                    if (valor.contains("@|")) {
                                        String valorMacro = BIUtil.getMacroValues(valor, this.userId);

                                        filter.removeAll();
                                        filter.addDimensionFilter(FilterFactory.createDimensionFilter(objetoCampo, operador, valorMacro));
                                    }
                                    this.filters.put(this.filters.size() + 1, filter);
                                }
                            }
                        }
                    }
                    expressao = expressao.substring(expressaoOriginal.length());
                    this.converteExpressaoEmFiltros(expressao);
                }
            }
        }
    }

    public Map<Integer, DimensionFilter> getFiltrosRestricao() throws BIException {
        this.filters = new HashMap<Integer, DimensionFilter>();
        this.converteExpressaoEmFiltros(this.getSqlExpression());
        return this.filters;
    }


    public String getDescription() {
        if (description == null)
            return "";
        return this.description;
    }


    public void addPermissaoEscritaGrupo(int codigoGrupo) throws BIException {
        this.groupPermissionIds.add(codigoGrupo);
    }


    public void addPermissaoEscritaUsuario(int codigoUsuario) throws BIException {
        if (this.userPermissionIds.stream().noneMatch(p -> p == codigoUsuario)) {
            this.userPermissionIds.add(codigoUsuario);
        }
    }

    public void addRestricaoGrupo(int codigoGrupo) throws BIException {
        this.groupList.add(codigoGrupo);
    }


    public void addRestricaoUsuario(int codigoUsuario) throws BIException {
        this.userList.add(codigoUsuario);
    }

    public boolean removeRestricaoUsuario(Integer userId) throws BIException {
        for (int i = 0; i < this.userList.size(); i++) {
            if (this.userList.get(i).equals(userId)) {
                this.userList.remove(i);
                return true;
            }
        }
        return false;
    }

    protected void incluir(ConnectionBean conexao) throws BIException {
        String sql = "INSERT INTO bi_restricao (ind, restricao, expressao, des_restricao) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.setInt(2, this.code);
            stmt.setString(3, this.sqlExpression);
            stmt.setString(4, this.description);
            stmt.executeUpdate();
            this.incluirUsuarios(conexao);
            this.incluirGrupos(conexao);
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao incluir dados na bi_restricao \nSQL: " + sql);
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

    private void incluirGrupos(ConnectionBean conexao) throws BIException {
        String sql = "INSERT INTO bi_rest_grp_usu (ind, restricao, grupo_usuario, permissao_escrita) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.setInt(2, this.code);

            stmt.setString(4, "N");
            for (Integer groupId : this.groupList) {
                stmt.setInt(3, groupId);
                stmt.executeUpdate();
            }

            stmt.setString(4, "S");
            for (Integer groupId : this.groupPermissionIds) {
                stmt.setInt(3, groupId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao incluir dados na bi_rest_grp_usu \nSQL: " + sql);
            biSQLEx.setAction("Incluir");
            biSQLEx.setLocal(this.getClass(), "private void incluirGrupos(ConexaoBean conexao)");
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

    private void incluirUsuarios(ConnectionBean conexao) throws BIException {
        String sql = "INSERT INTO bi_rest_usuario (ind, restricao, usuario, permissao_escrita) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.setInt(2, this.code);

            stmt.setString(4, "N");
            for (Integer userId : this.userList) {
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }

            stmt.setString(4, "S");
            for (Integer userId : this.userPermissionIds) {
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao incluir dados na bi_rest_usuario \nSQL: " + sql);
            biSQLEx.setAction("Incluir");
            biSQLEx.setLocal(this.getClass(), "private void incluirUsuarios(ConexaoBean conexao)");
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

    protected void removerGrupos(ConnectionBean conexao) throws BIException {
        String sql = "DELETE FROM bi_rest_grp_usu WHERE ind = ? AND restricao = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.setInt(2, this.code);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_rest_grp_usu");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void removerGrupos()");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    protected void removerUsuarios(ConnectionBean conexao) throws BIException {
        String sql = "DELETE FROM bi_rest_usuario WHERE ind = ? AND restricao = ?";
        PreparedStatement stmt = null;

        try {
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, this.indicator.getCode());
            stmt.setInt(2, this.code);
            stmt.executeUpdate();
        } catch (SQLException e) {
            BISQLException biSQLEx = new BISQLException(e, "Erro ao excluir dados na bi_rest_usuario");
            biSQLEx.setAction("Excluir");
            biSQLEx.setLocal(this.getClass(), "private void removerUsuarios()");
            throw biSQLEx;
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    public boolean existeRestricaoUsuario() {
        return !this.getUserList().isEmpty();
    }

    public boolean existeRestricaoGrupo() {
        return !this.getGroupList().isEmpty();
    }

    public boolean existePermissaoUsuario() {
        return !this.getUserPermissionIds().isEmpty();
    }

    public boolean existePermissaoGrupo() {
        return !this.getGroupPermissionIds().isEmpty();
    }

    public boolean isInvalida() {
        return this.getDescription().isEmpty() || (!this.existeRestricaoGrupo() && !this.existeRestricaoUsuario());
    }

    public String toString() {
        return this.code + " - " + this.description;
    }

    public String getSQL() {
        int size = this.filters.size();
        StringBuilder sql = new StringBuilder();
        for (int i = 1; i <= size; i++) {
            DimensionFilter f = this.filters.get(i);
            sql.append(f.getConnector());

            if (i == 1) {
                sql.append(" (");
            }
            sql.append(StringUtils.repeat("(", f.getStartParentCount()));
            sql.append(" ").append(f.toString());
            sql.append(StringUtils.repeat(")", f.getEndParentCount()));

            if (i < size) {
                sql.append("\r\n");
            }
        }
        if (!sql.toString().equalsIgnoreCase("")) {
            sql.append(")\r\n");
        }
        return sql.toString();
    }

    public String getGroupAndUsers(String permission) {
        JsonArray json_response = new JsonArray();

        // TODO load user and groups from DB
        /*for (BIUsuario user : permission.equals("restricted") ? this.getUserList() : this.getUserPermissionIds()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("ID", user.getCode());
            obj.addProperty("Type", "Usuários");
            obj.addProperty("NAME", user.getNome());
            json_response.add(obj);
        }*/

        /*for (GrupoUsuario group : permission.equals("restricted") ? this.getGroupList() : this.getGroupPermissionIds()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("ID", group.getCod());
            obj.addProperty("Type", "Grupos");
            obj.addProperty("NAME", group.getDescricao());
            json_response.add(obj);
        }*/
        return json_response.toString();
    }
}
