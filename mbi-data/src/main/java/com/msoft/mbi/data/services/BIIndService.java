package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIIndSummary;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.model.BIIndEntity;

import java.util.List;

public interface BIIndService extends CrudService<BIIndEntity, Integer> {

    BIIndInfoDTO getBIIndDTO(int biIndId);

    List<BIIndInfoDTO> findAllDTOs();


    IndicatorDTO getBIIndLogicDTO(int biIndId);

    List<IndicatorDTO> findAllBIIndLogicDTOs();

    void changeSequence(int id, boolean hasSequence);

    List<BIIndInfoDTO> getIndicatorListDescription();

    List<BIIndSummary> getBiSummary();

    List<BIIndSummary> findAllProjectedBy();

}
