package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIAnalysisFieldDTO;
import com.msoft.mbi.data.api.mapper.indicators.entities.BIAnalysisFieldMapper;
import com.msoft.mbi.data.repositories.BIAnalysisFieldRepository;
import com.msoft.mbi.data.services.BIAnalysisFieldService;
import com.msoft.mbi.model.BIAnalysisFieldEntity;

import java.util.*;

import com.msoft.mbi.model.BiAnalysisFieldId;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BIAnalysisFieldServiceImpl implements BIAnalysisFieldService {

    private final  BIAnalysisFieldRepository analysisFieldRepository;
    private final BIAnalysisFieldMapper analysisFieldMapper;

    @Override
    public List<BIAnalysisFieldEntity> findAll() {
        return Collections.emptyList();
    }

    @Override
    public BIAnalysisFieldEntity findById(BiAnalysisFieldId id) {
        Optional<BIAnalysisFieldEntity> biAnalysisField = this
                .analysisFieldRepository.findById((id));

        return biAnalysisField.orElse(null);
    }

    @Override
    public BIAnalysisFieldDTO findDtoById(BiAnalysisFieldId id) {
        BIAnalysisFieldEntity biUserIndEntity = this.findById(id);

        return this.analysisFieldMapper.biEntityToDTO(biUserIndEntity);
    }


    @Override
    public BIAnalysisFieldEntity save(BIAnalysisFieldEntity biAnalysisField) {
        return this.analysisFieldRepository.save(biAnalysisField);
    }

    @Override
    public BIAnalysisFieldDTO saveDto(BIAnalysisFieldDTO dto) {
        BIAnalysisFieldEntity field = this.save(this.analysisFieldMapper.dtoToEntity(dto));
        return this.analysisFieldMapper.biEntityToDTO(field);
    }

    /* TODO Handle filterSequence change
    * If the field changes to Metric,filter sequence must be set to 0
    * If it changes to Dimension, filter sequence must be set to The next sequence
    */
    @Override
    public BIAnalysisFieldEntity update(BiAnalysisFieldId id, BIAnalysisFieldEntity field) {
        return this.analysisFieldRepository.findById(id)
                .map(entity -> {
                    if (field.getTitle() != null) {
                        entity.setTitle(field.getTitle());
                    }
                    if (field.getFilterSequence() != null) {
                        entity.setFilterSequence(field.getFilterSequence());
                    }
                    if (field.getFieldType() != null) {
                        entity.setFieldType(field.getFieldType());
                    }
                    if (field.getDataType() != null) {
                        entity.setDataType(field.getDataType());
                    }
                    if (field.getDefaultField() != null) {
                        entity.setDefaultField(field.getDefaultField());
                    }
                    if (field.getColumnWidth() != null) {
                        entity.setColumnWidth(field.getColumnWidth());
                    }
                    if (field.getColumnAlignment() != null) {
                        entity.setColumnAlignment(field.getColumnAlignment());
                    }
                    if (field.getDateMask() != null) {
                        entity.setDateMask(field.getDateMask());
                    }
                    if (field.getDelegateOrder() != null) {
                        entity.setDelegateOrder(field.getDelegateOrder());
                    }
                    if (field.getFieldOrder() != null) {
                        entity.setFieldOrder(field.getFieldOrder());
                    }
                    if (field.getVisualizationSequence() != null) {
                        entity.setVisualizationSequence(field.getVisualizationSequence());
                    }
                    if (field.getDecimalPositions() != null) {
                        entity.setDecimalPositions(field.getDecimalPositions());
                    }
                    if (field.getVertical() != null) {
                        entity.setVertical(field.getVertical());
                    }
                    if (field.getAccumulatedLineField() != null) {
                        entity.setAccumulatedLineField(field.getAccumulatedLineField());
                    }
                    if (field.getHorizontal() != null) {
                        entity.setHorizontal(field.getHorizontal());
                    }
                    if (field.getAggregationType() != null) {
                        entity.setAggregationType(field.getAggregationType());
                    }

                    entity.setDrillDown(field.isDrillDown());
                    entity.setDirection(field.getDirection());
                    entity.setFieldOrder(field.getFieldOrder());
                    entity.setFieldTotalization(field.getFieldTotalization());
                    entity.setAccumulatedParticipation(field.isAccumulatedParticipation());
                    entity.setAccumulatedValue(field.isAccumulatedValue());
                    entity.setLineFieldTotalization(field.isLineFieldTotalization());
                    entity.setUsesMediaLine(field.isUsesMediaLine());
                    entity.setHorizontalParticipation(field.isHorizontalParticipation());
                    entity.setHorizontalParticipationAccumulated(field.isHorizontalParticipationAccumulated());

                    return this.analysisFieldRepository.save(entity);
                })
                .orElse(null);
    }

    public BIAnalysisFieldDTO updateDto(BIAnalysisFieldDTO dto) {
        BIAnalysisFieldEntity field = this.update(
                BiAnalysisFieldId.builder().fieldId(dto.getId().getFieldId()).indicatorId(dto.getId().getIndicatorId()).build(),
                this.analysisFieldMapper.dtoToEntity(dto));
        return this.analysisFieldMapper.biEntityToDTO(field);
    }

    public List<BIAnalysisFieldDTO> updateDtoList(List<BIAnalysisFieldDTO> dtos) {
        List<BIAnalysisFieldDTO> savedDtos = new ArrayList<>();
        for (BIAnalysisFieldDTO dto : dtos) {
            savedDtos.add(this.updateDto(dto));
        }
        return savedDtos;
    }

    @Override
    public void delete(BIAnalysisFieldEntity entity) {
        this.analysisFieldRepository.delete(entity);
    }

    @Override
    public void deleteById(BiAnalysisFieldId fieldId) {
        this.analysisFieldRepository.deleteById(fieldId);
    }

    @Override
    public List<BIAnalysisFieldEntity> saveAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields) {
        return IterableUtils.toList(analysisFieldRepository.saveAll(bIAnalysisFields));
    }

    public void deleteAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields) {
        analysisFieldRepository.deleteAll(bIAnalysisFields);
    }
}
