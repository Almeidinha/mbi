package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupDTO;
import com.msoft.mbi.data.api.mapper.user.BIUserGroupMapper;
import com.msoft.mbi.data.repositories.BIUserGroupRepository;
import com.msoft.mbi.data.services.BICompanyService;
import com.msoft.mbi.data.services.BIUserGroupService;
import com.msoft.mbi.model.BICompanyEntity;
import com.msoft.mbi.model.BIUserGroupEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BIUserGroupServiceImpl implements BIUserGroupService {

    private final BIUserGroupRepository userGroupRepository;
    private final BIUserGroupMapper userGroupMapper;
    private final BICompanyService companyService;

    @Override
    public List<BIUserGroupDTO> findAll() {
        return userGroupRepository.findAll().stream()
                .map(userGroupMapper::biUserGroupEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BIUserGroupDTO> findByBiCompanies() {

        final int companyId = this.companyService.getCurrentUserCompanyId();

        return userGroupRepository.findByBiCompanies_Id(companyId).stream()
                .map(userGroupMapper::biUserGroupEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BIUserGroupDTO findById(Integer id) {
        return this.userGroupMapper
                .biUserGroupEntityToDTO(this
                        .userGroupRepository.findById(Long.valueOf(id)).orElse(null));

    }

    @Override
    public BIUserGroupDTO save(BIUserGroupDTO biUserGroupDTO) {

        int companyId = companyService.getCurrentUserCompanyId();

        BIUserGroupEntity biUserGroup = this.userGroupMapper.dtoToBIUserGroupEntity(biUserGroupDTO);
        biUserGroup.addCompany(BICompanyEntity.builder().id(companyId).build());

        final BIUserGroupEntity newBiUserGroup = this.userGroupRepository.save(biUserGroup);
        return this.userGroupMapper.biUserGroupEntityToDTO(newBiUserGroup);
    }

    @Override
    public BIUserGroupDTO update(Integer id, BIUserGroupDTO userGroupDTO) {
        BIUserGroupEntity biUserGroup = userGroupMapper.dtoToBIUserGroupEntity(userGroupDTO);
        biUserGroup.setId(id);

        this.userGroupRepository.save(biUserGroup);

        return userGroupMapper.biUserGroupEntityToDTO(biUserGroup);
    }

    @Override
    public void delete(BIUserGroupDTO biUserGroupDTO) {
        this.userGroupRepository.delete(this.userGroupMapper.dtoToBIUserGroupEntity(biUserGroupDTO));
    }

    @Override
    public void deleteById(Integer id) {
        this.userGroupRepository.deleteById(Long.valueOf(id));
    }
}
