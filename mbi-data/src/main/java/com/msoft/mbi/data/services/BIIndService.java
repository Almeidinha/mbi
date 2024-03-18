package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;
import com.msoft.mbi.model.BIIndEntity;

import java.util.List;

public interface BIIndService extends CrudService<BIIndEntity, Integer> {

    BIIndInfoDTO getBIIndDTO(int biIndId);

    List<BIIndInfoDTO> findAllDTOs();


    BIIndLogicDTO getBIIndLogicDTO(int biIndId);

    List<BIIndLogicDTO> findAllBIIndLogicDTOs();

    void changeSequence(int id, boolean hasSequence);

}
