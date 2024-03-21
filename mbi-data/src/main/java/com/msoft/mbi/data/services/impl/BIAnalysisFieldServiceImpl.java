package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.indicators.BIAnalysisFieldDTO;
import com.msoft.mbi.data.api.mapper.indicators.BIAnalysisFieldMapper;
import com.msoft.mbi.data.api.mapper.indicators.BIAnalysisFieldToFieldMapper;
import com.msoft.mbi.data.repositories.BIAnalysisFieldRepository;
import com.msoft.mbi.data.services.BIAnalysisFieldService;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import com.msoft.mbi.model.BIIndEntity;
import com.msoft.mbi.model.BIUserEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BIAnalysisFieldServiceImpl implements BIAnalysisFieldService {

    private final  BIAnalysisFieldRepository analysisFieldRepository;
    private final BIAnalysisFieldMapper analysisFieldMapper;

    @Override
    public List<BIAnalysisFieldEntity> findAll() {
        return null;
    }

    @Override
    public BIAnalysisFieldEntity findById(Integer id) {
        Optional<BIAnalysisFieldEntity> biAnalysisField = this
                .analysisFieldRepository.findById((id));

        return biAnalysisField.orElse(null);
    }

    @Override
    public BIAnalysisFieldDTO findDtoById(Integer id) {
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

    @Override
    public BIAnalysisFieldEntity update(Integer id, BIAnalysisFieldEntity field) {
        field.setFieldId(id);
        return this.analysisFieldRepository.save(field);
    }

    public BIAnalysisFieldDTO updateDto(Integer id, BIAnalysisFieldDTO dto) {
        BIAnalysisFieldEntity field = this.update(id, this.analysisFieldMapper.dtoToEntity(dto));
        return this.analysisFieldMapper.biEntityToDTO(field);
    }

    @Override
    public void delete(BIAnalysisFieldEntity object) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public List<BIAnalysisFieldEntity> saveAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields) {
        return IterableUtils.toList(analysisFieldRepository.saveAll(bIAnalysisFields));
    }

    public void deleteAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields) {
        analysisFieldRepository.deleteAll(bIAnalysisFields);
    }
}
