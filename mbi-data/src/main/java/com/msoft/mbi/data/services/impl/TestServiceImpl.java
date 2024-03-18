package com.msoft.mbi.data.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.FiltersTree;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;
import com.msoft.mbi.data.api.mapper.filters.FiltersMapper;
import com.msoft.mbi.data.api.mapper.indicators.BIAnalysisFieldToFieldMapper;
import com.msoft.mbi.data.connection.ConnectionManager;
import com.msoft.mbi.data.repositories.TestRepository;
import com.msoft.mbi.data.services.BIIndService;
import com.msoft.mbi.data.services.BITenantService;
import com.msoft.mbi.data.services.TestService;
import com.msoft.mbi.model.BITenantEntity;
import com.msoft.mbi.model.support.DatabaseType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final ConnectionManager connectionManager;
    private final BIIndService indService;
    private final BIAnalysisFieldToFieldMapper analysisFieldToFieldMapper;
    private final BITenantService tenantService;
    private final FiltersMapper filtersMapper;

    @Override
    public List<Object> getObjects() {

        JdbcTemplate jdbcTemplate = connectionManager.getNewConnection("b5b6a0d9-d627-4884-978a-ffde5ba0a149");

        /*
        DataSource dataSource = DataSourceBuilder.create()
                .url("jdbc:sqlserver://localhost:1433;databaseName=thomas;encrypt=true;trustServerCertificate=true;")
                .username("almeida").password("thomas")
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .build();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        */

        /*
        List<Map<String, Object>> biInterfaces = jdbcTemplate
                .queryForList("select bui.grau_permissao,  bui.INTerf, bui.usuario from bi_usuario_INTerf bui" );
         */


        /*for (Map<String, Object> a : biInterfaces) {
            System.out.println(a);
        }*/

        try {
            return tabletest(jdbcTemplate);
        } catch (Exception ex) {
            return testRepository.getObjects();
        }
    }


    private List<Object> tabletest(JdbcTemplate jdbcTemplate) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        String catalog = dataSource.getConnection().getCatalog();
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
        String user = databaseMetaData.getUserName();

        List<Object> objects = new ArrayList<>();

        // new String[]{"TABLE"}
        String[] types = {"TABLE"};

        ResultSet result = databaseMetaData.getTables(catalog, null, "%", types);
        System.out.println("Lest see whats in...");
        while (result.next()) {
            String name = result.getString(3);
            objects.add(name);
            System.out.println(name);
        }

        return objects;
    }

    @Override
    public String getTable(Integer id) {

        DataSource dataSource = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            BIIndLogicDTO dto = this.indService.getBIIndLogicDTO(id);
            BITenantEntity biTenant = tenantService.findById(dto.getConnectionId());

            Indicator ind = new Indicator(id);

            this.setIndicatorValues(dto, ind);


            String sql = ind.getSqlExpression(DatabaseType.MSSQL, false);


            JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(biTenant);
            /*dataSource = jdbcTemplate.getDataSource();
            connection = DataSourceUtils.getConnection(Objects.requireNonNull(dataSource));
            statement = connection.createStatement();


            resultSet = statement.executeQuery(sql);
            ind.setResultSet(resultSet);

            return ind.getTabela(true);*/

            /*
            testes
            * */
            /*
            String countQuery = "Select count(*) " + ind.getFromClause() + " " + ind.getWhereClause();
             long count = jdbcTemplate.queryForObject(countQuery, Long.class);

            System.out.println("Total Count " + count);

            String newSql = "SELECT DimCustomer.CompanyName CompanyName,DimCustomer.CustomerLabel CustomerLabel,DimCustomer.Education Education,DimCustomer.EmailAddress EmailAddress " +
            "FROM DimCustomer DimCustomer, DimGeography DimGeography " +
            "WHERE DimGeography.GeographyKey = DimCustomer.GeographyKey " +
            "GROUP BY DimCustomer.CompanyName,DimCustomer.CustomerLabel,DimCustomer.Education,DimCustomer.EmailAddress";
            */

            /*
            end testes
            * */

            ind.startTableProcess();

            jdbcTemplate.query(sql, resultSet1 -> {
                try {
                    ind.processCube(resultSet1);
                } catch (BIException e) {
                    throw new RuntimeException(e);
                }
            });

            return ind.getStringTable(true);


        } catch (BIException e) {
            return "Fuuuuck";
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            DataSourceUtils.releaseConnection(connection, dataSource);
        }


    }

    @Override
    public ObjectNode getJsonTable(Integer id) {
        try {

            BIIndLogicDTO dto = this.indService.getBIIndLogicDTO(id);
            BITenantEntity biTenant = tenantService.findById(dto.getConnectionId());

            Indicator ind = new Indicator(id);

            this.setIndicatorValues(dto, ind);

            String sql = ind.getSqlExpression(DatabaseType.MSSQL, false);
            JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(biTenant);


            ind.startTableProcess();

            jdbcTemplate.query(sql, resultSet1 -> {
                try {
                    ind.processCube(resultSet1);
                } catch (BIException e) {
                    throw new RuntimeException(e);
                }
            });

            return ind.getJsonTable(true);


        } catch (BIException e) {
            return new ObjectMapper().createObjectNode().put("erro", "Fuuuuck!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getJsonTree(Integer id) throws BIException {
        BIIndLogicDTO dto = this.indService.getBIIndLogicDTO(id);

        Indicator ind = new Indicator(id);

        this.setIndicatorValues(dto, ind);

        FiltersTree tree = new FiltersTree(ind.getFilters(), true);

        return tree.toString();

    }

    public FiltersDTO getFiltersDTO(Integer id) throws BIException {
        BIIndLogicDTO dto = this.indService.getBIIndLogicDTO(id);

        Indicator ind = new Indicator(id);

        this.setIndicatorValues(dto, ind);

        return filtersMapper.filterToDTO(ind.getFilters());

    }

    private void setIndicatorValues(BIIndLogicDTO dto, Indicator indicator) throws BIException {
        indicator.setTenantId(dto.getConnectionId());
        indicator.setName(dto.getName());
        indicator.setSearchClause(dto.getBiSearchClause().getSqlText());
        indicator.setFromClause(dto.getBiFromClause().getSqlText());
        indicator.setWhereClause(dto.getBiWhereClause().getSqlText());

        indicator.setFields(analysisFieldToFieldMapper.setDTOToEntity(dto.getBiAnalysisFields()));

        if (dto.getBiDimensionFilter() != null) {
            indicator.setDimensionFilters(dto.getBiDimensionFilter().getSqlText());
        }
        if (dto.getBiIndSqlMetricFilter() != null) {
            indicator.setMetricSqlFilters(dto.getBiIndSqlMetricFilter().getSqlText());
        }
        if (dto.getBiIndMetricFilter() != null) {
            indicator.setMetricFilters(dto.getBiIndMetricFilter().getSqlText());
        }

    }


}
