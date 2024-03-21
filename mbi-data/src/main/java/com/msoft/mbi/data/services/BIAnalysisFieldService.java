package com.msoft.mbi.data.services;


import com.msoft.mbi.data.api.dtos.indicators.BIAnalysisFieldDTO;
import com.msoft.mbi.model.BIAnalysisFieldEntity;

import java.util.List;

public interface BIAnalysisFieldService extends CrudService<BIAnalysisFieldEntity, Integer> {

    List<BIAnalysisFieldEntity> saveAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields);

    void deleteAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields);

    BIAnalysisFieldDTO findDtoById(Integer integer);

    BIAnalysisFieldDTO saveDto(BIAnalysisFieldDTO dto);

    BIAnalysisFieldDTO updateDto(Integer integer, BIAnalysisFieldDTO dto);

}
