package com.msoft.mbi.data.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.*;
import com.msoft.mbi.data.api.data.inputs.AnalysisInput;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Indicator;
import com.msoft.mbi.data.api.dtos.indicators.FieldDTO;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.api.mapper.indicators.IndicatorMapper;
import com.msoft.mbi.data.api.mapper.indicators.entities.BIAnalysisFieldToFieldDTOMapper;
import com.msoft.mbi.data.api.mapper.indicators.entities.BIIndToIndicatorDTOMapper;
import com.msoft.mbi.data.connection.ConnectionManager;
import com.msoft.mbi.data.services.*;
import com.msoft.mbi.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final BIUserIndService userIndService;
    private final BIUserGroupIndService userGroupIndService;
    private final BIIndService indService;

    private final ConnectionManager connectionManager;
    private final BITenantService tenantService;
    private final BICompanyService companyService;
    private final BIUserService userService;
    private final BIIndToIndicatorDTOMapper biIndToIndicatorDTOMapper;
    private final IndicatorMapper indicatorMapper;
    private final BIAnalysisFieldToFieldDTOMapper biAnalysisFieldToFieldDTOMapper;

    @Override
    public IndicatorDTO createAnalysis(AnalysisInput analysisInput, String tenantId) {

        BIIndEntity biIndEntity = this.buildBIInd(analysisInput);

        List<BIAnalysisFieldEntity> fields = this.buildEntityFieldsFromFieldDto(analysisInput.getFields());

        fields.forEach(biIndEntity::addField);

        BIIndEntity savedEntity = this.indService.save(biIndEntity);

        analysisInput.setId(savedEntity.getId());

        this.savePermissions(savedEntity, analysisInput);

        return this.biIndToIndicatorDTOMapper.biEntityToDTO(savedEntity);
    }

    @Override
    public IndicatorDTO updateAnalysis(AnalysisInput analysisInput, int id) {
        BIIndEntity biIndEntity = this.indService.findById(id);

        int currentUserId = this.userService.getCurrentUserId();
        biIndEntity.setLastUpdatedUser(currentUserId);

        biIndEntity.setTableType(analysisInput.getTableType());

        if (!analysisInput.getFields().isEmpty()) {
            List<BIAnalysisFieldEntity> fields = buildEntityFieldsFromFieldDto(analysisInput.getFields());
            fields.forEach(biIndEntity::updateField);
        }

        this.indService.update(id, biIndEntity);

        return this.biIndToIndicatorDTOMapper.biEntityToDTO(biIndEntity);
    }

    @Override
    public ObjectNode getTableAsJson(IndicatorDTO dto) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            BITenantEntity biTenant = tenantService.findById(dto.getTenantId());
            Indicator ind = indicatorMapper.dtoToIndicator(dto);

            String sql = ind.getSqlExpression(biTenant.getDatabaseType(), false);
            log.info("Executing SQL: {}", sql);
            JdbcTemplate jdbcTemplate = connectionManager.getNewConnection(biTenant);
            ind.startTableProcess();

            jdbcTemplate.query(sql, resultSet -> {
                try {
                    ind.processCube(resultSet);
                } catch (BIException e) {
                    log.error("Error while executing query in class {}, method {}, line number {}: {}",
                            e.getStackTrace()[0].getClassName(),
                            e.getStackTrace()[0].getMethodName(),
                            e.getStackTrace()[0].getLineNumber(),
                            e.getMessage());
                    throw new RuntimeException(e);
                }
                return null;
            });

            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.set("table", ind.getJsonTable(true));
            resultNode.set("indicator", objectMapper.valueToTree(dto));

            return resultNode;

        } catch (BIException e) {
            return objectMapper.createObjectNode().put("error", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void savePermissions(BIIndEntity biIndEntity, AnalysisInput analysisInput) {
        List<BIUserGroupIndEntity> userGroupIndEntities =  getUserGroupPermissions(analysisInput);
        this.userGroupIndService.saveAll(userGroupIndEntities);
        biIndEntity.setBiUserGroupIndicators(userGroupIndEntities);

        List<BIUserIndEntity> userIndEntities =  getUserPermissions(analysisInput);
        this.userIndService.saveAll(userIndEntities);
        biIndEntity.setBiUserIndicators(userIndEntities);
    }

    private List<BIUserGroupIndEntity> getUserGroupPermissions(AnalysisInput analysisInput) {
        List<BIUserGroupIndEntity> result = new ArrayList<>();

        if (Optional.of(analysisInput.getPermissions()).get().stream().anyMatch(permissions -> permissions.getType().equals(PermissionType.GROUP))) {
            result =  this.buildUserGroupPermissions(analysisInput);
        }
        return result;
    }

    private List<BIUserIndEntity> getUserPermissions(AnalysisInput analysisInput) {
        List<BIUserIndEntity> result = new ArrayList<>();
        if (Optional.of(analysisInput.getPermissions()).get().stream().anyMatch(permissions -> permissions.getType().equals(PermissionType.USER))) {
            result =  this.buildUserPermissions(analysisInput);
        }
        return result;
    }

    List<BIUserGroupIndEntity> buildUserGroupPermissions(AnalysisInput analysisInput) {
        return analysisInput.getPermissions().stream()
                .filter(permissions -> permissions.getType().equals(PermissionType.GROUP))
                .map(permission ->
                        BIUserGroupIndEntity.builder()
                                .indicatorId(analysisInput.getId())
                                .userGroupId(permission.getGroupId())
                                .canEdit(permission.getLevel().equals(PermissionLevel.WRITE))
                                .build()
                ).toList();
    }

    List<BIUserIndEntity> buildUserPermissions(AnalysisInput analysisInput) {
        return analysisInput.getPermissions().stream()
                .filter(permissions -> permissions.getType().equals(PermissionType.USER))
                .map(permission ->
                        BIUserIndEntity.builder()
                                .indicatorId(analysisInput.getId())
                                .userId(permission.getUserId())
                                .canChange(permission.getLevel().equals(PermissionLevel.WRITE))
                                .favorite(permission.isFavorite())
                                .build()
                ).toList();
    }

    private BIIndEntity buildBIInd(AnalysisInput analysisInput) {

        int companyId = companyService.getCurrentUserCompanyId();

        BISearchClauseEntity biSearchClause = BISearchClauseEntity.builder()
                .sqlText(analysisInput.getBiSearchClause().getSqlText()).build();
        BIFromClauseEntity bIbiFromClause = BIFromClauseEntity.builder()
                .sqlText(analysisInput.getBiFromClause().getSqlText()).build();
        BIWhereClauseEntity biWhereClause = BIWhereClauseEntity.builder()
                .sqlText(analysisInput.getBiWhereClause().getSqlText()).build();

        if (analysisInput.getId() != null) {
            biSearchClause.setId(analysisInput.getBiSearchClause().getId());
            bIbiFromClause.setId(analysisInput.getBiFromClause().getId());
            biWhereClause.setId(analysisInput.getBiWhereClause().getId());
        }

        return BIIndEntity.builder()
                .companyIdByCompany(BICompanyEntity.builder().id(companyId).build())
                .biAreaByArea(BIAreaEntity.builder().id(analysisInput.getBiAreaByArea().getId()).build())
                .name(analysisInput.getName())
                .connectionId(UUID.fromString(analysisInput.getConnectionId()))
                .tableType(analysisInput.getTableType())
                .defaultDisplay(analysisInput.getDefaultDisplay())
                .biSearchClause(biSearchClause)
                .biFromClause(bIbiFromClause)
                .biWhereClause(biWhereClause)
                .build();
    }

    private List<BIAnalysisFieldEntity> buildEntityFieldsFromFieldDto(List<FieldDTO> analysisFieldDTOS) {

        if (analysisFieldDTOS == null || analysisFieldDTOS.isEmpty()) {
            return Collections.emptyList();
        }

        return biAnalysisFieldToFieldDTOMapper.setDTOToEntity(analysisFieldDTOS);
    }

}
