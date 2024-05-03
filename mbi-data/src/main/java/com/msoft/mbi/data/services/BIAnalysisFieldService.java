package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.indicators.entities.BIAnalysisFieldDTO;
import com.msoft.mbi.data.api.dtos.indicators.entities.BiAnalysisFieldIdDTO;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import com.msoft.mbi.model.BiAnalysisFieldId;

import java.util.List;

public interface BIAnalysisFieldService extends CrudService<BIAnalysisFieldEntity, BiAnalysisFieldId> {

    List<BIAnalysisFieldEntity> saveAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields);

    void deleteAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields);

    BIAnalysisFieldDTO findDtoById(BiAnalysisFieldId integer);

    BIAnalysisFieldDTO saveDto(BIAnalysisFieldDTO dto);

    BIAnalysisFieldDTO updateDto(BIAnalysisFieldDTO dto);

    List<BIAnalysisFieldDTO> updateDtoList(List<BIAnalysisFieldDTO> dtos);

}
