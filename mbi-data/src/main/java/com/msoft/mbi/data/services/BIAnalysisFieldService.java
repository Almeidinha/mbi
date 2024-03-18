package com.msoft.mbi.data.services;


import com.msoft.mbi.model.BIAnalysisFieldEntity;

import java.util.List;

public interface BIAnalysisFieldService extends CrudService<BIAnalysisFieldEntity, Integer> {

    List<BIAnalysisFieldEntity> saveAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields);

    void deleteAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields);

}
