package com.msoft.mbi.data.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.inputs.AnalysisInput;
import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;
import org.springframework.transaction.annotation.Transactional;


public interface AnalysisService {

    @Transactional(rollbackFor = Exception.class)
    BIIndLogicDTO createAnalysis(AnalysisInput analysisInput, String tenantId);

    @Transactional(rollbackFor = Exception.class)
    BIIndLogicDTO updateAnalysis(AnalysisInput analysisInput, int id);

    ObjectNode getTableAsJson(BIIndLogicDTO dto);

}
