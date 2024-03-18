package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;
import com.msoft.mbi.data.api.data.exception.BIParametrosConexaoException;
import com.msoft.mbi.data.services.BITenantService;
import com.msoft.mbi.model.BITenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.UUID;

public class ConnectionBean {

    @Getter
    private transient Connection connection;
    private String user;
    private String password;
    private String driver;
    private String url;
    public static ConnectionParameters connectionParameters;
    @Setter
    @Getter
    private boolean inTransaction;

    public static final String DB2_DRIVER = "DB2";
    public static final String INFORMIX_DRIVER = "INFORMIX";
    public static final String MYSQL_DRIVER = "MYSQL";
    public static final String ORACLE_DRIVER = "ORACLE";
    public static final String SQLSERVER_DRIVER = "SQLSERVER";
    public static final String OPENEDGE_DRIVER = "OPENEDGE";

    public static final String DB2_BANCO = "DB2";
    public static final String INFORMIX_BANCO = "INFORMIX";
    public static final String MYSQL_BANCO = "MYSQL";
    public static final String ORACLE_BANCO = "ORACLE";
    public static final String SQLSERVER_BANCO = "SQLSERVER";
    public static final String OPENEDGE_BANCO = "OPENEDGE";

    private static void loadConfiguration() throws BIException {
        try {
            // TODO Get this from database
            ConnectionBean.connectionParameters = new ConnectionParameters("name");
        } catch (Exception e) {
            throw new BIGeneralException(e);
        }
    }

    public ConnectionBean() throws BIException {
        if (ConnectionBean.connectionParameters == null) {
            ConnectionBean.loadConfiguration();
        }
        if (ConnectionBean.connectionParameters != null) {
            this.user = ConnectionBean.connectionParameters.getUser();
            if (!"".equals(ConnectionBean.connectionParameters.getPassword()) && ConnectionBean.connectionParameters.getPassword() != null) {
                this.password = EncryptionBase64.decryptionRSA(ConnectionBean.connectionParameters.getPassword());
            }
            this.driver = ConnectionBean.connectionParameters.getDriver();
            this.url = ConnectionBean.connectionParameters.getUrl();
            this.newConnection(driver, url, user, password);
        } else {
            throw new BIGeneralException("Nao foi possível conectar ao banco. Conexão padrão não encontrada.");
        }
    }

    public ConnectionBean(String driver, String url, String user, String password) throws BIException {
        this.newConnection(driver, url, user, password);
    }

    public ConnectionBean(String connectionId) throws BIException {
        // TODO Get from database

        String user = "";
        String password = "";
        /*if (password != null && !password.isEmpty()) {
            password = EncryptionBase64.decryptionRSA(password);
        }*/
        String driver = connectionParameters.getDriver();
        String url = connectionParameters.getUrl();
        this.newConnection(driver, url, user, password);
    }

    public static boolean textConnection(String connectionName, String user, String password) {
        ConnectionBean conexao = null;
        try {
            conexao = new ConnectionBean(connectionName, user, password);
        } catch (Exception e) {
            return false;
        } finally {
            if (conexao != null) {
                conexao.closeConnection();
            }
        }
        return true;
    }

    public ConnectionBean(String connectionName, String user, String password) throws BIException {

        ConnectionParameters connectionParameters  = new ConnectionParameters(connectionName);
        String driver = connectionParameters.getDriver();
        String url = connectionParameters.getUrl();

        try {
            this.newConnection(driver, url, user, password);
        } catch (Exception e) {
            this.newConnection(driver, url, connectionParameters.getUser(),
                    EncryptionBase64.decryptionRSA(connectionParameters.getPassword()));
        }
    }

    public void newConnection(String driver, String url, String user, String password) throws BIException {
        this.driver = driver;

        String newUrl = this.url + ";encrypt=true;trustServerCertificate=true";
        try {
            Class.forName(driver);
            if (user == null || user.isEmpty()) {
                connection = DriverManager.getConnection(newUrl);
            } else {
                connection = DriverManager.getConnection(newUrl, user, password);
            }

            this.user = user;
            this.password = password;

            if (this.driver.toUpperCase().contains("ORACLE")) {
                this.changeOracleDefaultDate();
            }
        } catch (ClassNotFoundException e) {
            BIParametrosConexaoException biex = new BIParametrosConexaoException(
                    "Provavelmente o driver de conexão não existe.");
            biex.setErrorCode(BIParametrosConexaoException.ERRO_DRIVER);
            connection = null;
            throw biex;
        } catch (SQLException e) {
            BIParametrosConexaoException biex = new BIParametrosConexaoException(e, "Ocorreu um problema de SQL. Erro: "
                    + e + " Mensagem de Erro: " + e.getMessage() + " . Código do Erro: " + e.getErrorCode());
            biex.setErrorCode(BIParametrosConexaoException.ERRO_URL);
            connection = null;
            throw biex;
        } catch (Exception e) {
            BIParametrosConexaoException biex = new BIParametrosConexaoException(e);
            biex.setErrorCode(BIParametrosConexaoException.ERRO_PADRAO);
            connection = null;
            throw biex;
        }
    }

    private void changeOracleDefaultDate() throws BIException, SQLException {
        Statement stmt = null;
        try {
            stmt = this.connection.createStatement();
            stmt.executeUpdate("alter session set nls_date_format='dd/mm/yyyy hh24:mi:ss'");
        } finally {
            BIUtil.closeStatement(stmt);
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.setDirtyRead();
        return connection.prepareStatement(sql);
    }

    public void commit() throws SQLException {
        this.setInTransaction(false);
        connection.commit();
    }

    public void rollback() throws SQLException {
        this.setInTransaction(false);
        connection.rollback();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.setInTransaction(!autoCommit);
        connection.setAutoCommit(autoCommit);
    }

    public ResultSet executeSQL(String sql) throws SQLException {
        this.setDirtyRead();
        return this.connection.createStatement().executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        Statement stmt = this.connection.createStatement();
        int result = stmt.executeUpdate(sql);
        stmt.close();
        return result;
    }

    public void closeConnection() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexão: " + e + ". Mensagem de Erro: " + e.getMessage()
                    + ". Código do Erro: " + e.getErrorCode());
        }
    }

    public void setDirtyRead() throws SQLException {
        if (this.driver.toUpperCase().contains("INFORMIX"))
            this.executeUpdate("set isolation to dirty read");
        else if (this.driver.toUpperCase().contains("JTURBO"))
            this.executeUpdate("SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED");
    }

    public String getDatabase() {
        String database;

        if (this.driver.toUpperCase().contains(ConnectionBean.DB2_DRIVER)) {
            database = ConnectionBean.DB2_BANCO;
        } else if (this.driver.toUpperCase().contains(ConnectionBean.INFORMIX_DRIVER)) {
            database = ConnectionBean.INFORMIX_BANCO;
        } else if (this.driver.toUpperCase().contains(ConnectionBean.MYSQL_DRIVER)) {
            database = ConnectionBean.MYSQL_BANCO;
        } else if (this.driver.toUpperCase().contains(ConnectionBean.ORACLE_DRIVER)) {
            database = ConnectionBean.ORACLE_BANCO;
        } else if (this.driver.toUpperCase().contains(ConnectionBean.SQLSERVER_DRIVER)) {
            database = ConnectionBean.SQLSERVER_BANCO;
        }else if (this.driver.toUpperCase().contains(ConnectionBean.OPENEDGE_DRIVER)){
            database = ConnectionBean.OPENEDGE_BANCO;
        } else {
            database = ConnectionBean.INFORMIX_DRIVER;
        }
        return database;
    }

    public static boolean testaConexao(String connectionName) throws BIException {
        ConnectionBean conexao = null;
        // TODO Get from database
        ConnectionParameters connectionParameters  = new ConnectionParameters(connectionName);
        String user = connectionParameters.getUser();
        String password = connectionParameters.getPassword();
        if (!"".equals(password) && password != null) {
            password = EncryptionBase64.decryptionRSA(connectionParameters.getPassword());
        }
        try {
            conexao = new ConnectionBean(connectionName, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conexao != null) {
                conexao.closeConnection();
            }
        }
        return true;
    }
}
