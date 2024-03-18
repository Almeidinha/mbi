package com.msoft.mbi.data.api.mapper;

import com.msoft.mbi.data.api.dtos.BICompanyDTO;
import com.msoft.mbi.model.BICompanyEntity;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface BICompanyMapper {

    BICompanyMapper BI_COMPANY_MAPPER = Mappers.getMapper(BICompanyMapper.class);

    BICompanyDTO biEntityToDTO(BICompanyEntity entity);

    BICompanyEntity dtoToEntity(BICompanyDTO dto);

    List<BICompanyDTO> listEntityToDTOs(List<BICompanyEntity> entities);

    List<BICompanyEntity> listDTOToEntities(List<BICompanyDTO> dtos);
}
