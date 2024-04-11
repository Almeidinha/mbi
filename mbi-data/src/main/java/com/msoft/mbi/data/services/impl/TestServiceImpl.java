package com.msoft.mbi.data.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.FiltersTree;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.api.mapper.filters.FiltersMapper;
import com.msoft.mbi.data.api.mapper.indicators.IndicatorMapper;
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
    private final BITenantService tenantService;
    private final FiltersMapper filtersMapper;
    private final IndicatorMapper indicatorMapper;

    @Override
    public List<Object> getObjects() {

        JdbcTemplate jdbcTemplate = connectionManager.getNewConnection("b5b6a0d9-d627-4884-978a-ffde5ba0a149");

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
            IndicatorDTO dto = this.indService.getBIIndLogicDTO(id);
            BITenantEntity biTenant = tenantService.findById(dto.getTenantId());

            Indicator ind =  indicatorMapper.dtoToIndicator(dto);

            String sql = ind.getSqlExpression(DatabaseType.MSSQL, false);

            JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(biTenant);

            ind.startTableProcess();

            jdbcTemplate.query(sql, resultSet1 -> {
                try {
                    ind.processCube(resultSet1);
                } catch (BIException e) {
                    throw new RuntimeException(e);
                }
                return null;
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

            IndicatorDTO dto = this.indService.getBIIndLogicDTO(id);
            BITenantEntity biTenant = tenantService.findById(dto.getTenantId());

            Indicator ind =  indicatorMapper.dtoToIndicator(dto);

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
        IndicatorDTO dto = this.indService.getBIIndLogicDTO(id);

        Indicator ind =  indicatorMapper.dtoToIndicator(dto);

        FiltersTree tree = new FiltersTree(ind.getFilters(), true);

        return tree.toString();

    }

    public FiltersDTO getFiltersDTO(Integer id) throws BIException {
        IndicatorDTO dto = this.indService.getBIIndLogicDTO(id);

        Indicator ind =  indicatorMapper.dtoToIndicator(dto);

        return filtersMapper.filterToDTO(ind.getFilters());

    }


}
