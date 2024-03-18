package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.data.api.mapper.user.BIUserMapper;
import com.msoft.mbi.data.repositories.BIUserRepository;
import com.msoft.mbi.data.services.AuthenticationFacade;
import com.msoft.mbi.data.services.BICompanyService;
import com.msoft.mbi.data.services.BIUserService;
import com.msoft.mbi.model.BICompanyEntity;
import com.msoft.mbi.model.BIUserEntity;
import com.msoft.mbi.model.BIUserGroupEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BIUserServiceImpl implements BIUserService {

    private final BIUserRepository userRepository;
    private final BIUserMapper userMapper;
    private final BICompanyService companyService;
    private final AuthenticationFacade authenticationFacade;


    @Override
    public List<BIUserDTO> findAll() {
        return this.userRepository.findAll()
                .stream()
                .map(userMapper::biUserEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BIUserDTO findById(Integer id) {
        Optional<BIUserEntity> biUserIndEntity = this
                .userRepository.findById(Long.valueOf(id));

        return this.userMapper
                .biUserEntityToDTO(biUserIndEntity.orElse(null));
    }

    @Override
    public BIUserDTO save(BIUserDTO biUserDTO) {

        int companyId = this.companyService.getCurrentUserCompanyId();

        BIUserEntity entity = this.userMapper.dtoToBIUserEntity(biUserDTO);
        entity.setIsActive(true);
        entity.setPasswordUpdated(false);
        entity.setEmailVerified(false);
        entity.setBiCompany(BICompanyEntity.builder().id(companyId).build());
        entity.setBiUserGroupByUserGroup(BIUserGroupEntity.builder().id(biUserDTO.getUserGroupId()).build());
        // TODO this is default "password", ask user to change on first login
        entity.setPassword("$2a$10$nTnlwFms8RJVeb8KroNAMeU1C3z259VXombRJEARgicUuCjDZVv/2");

        final BIUserEntity biUser =  this.userRepository.save(entity);
        return this.userMapper.biUserEntityToDTO(biUser);
    }

    @Override
    public BIUserDTO update(Integer id, BIUserDTO biUserDTO) {
        BIUserEntity user = userMapper.dtoToBIUserEntity(biUserDTO);
        user.setId(id);

        userRepository.save(user);

        return userMapper.biUserEntityToDTO(user);
    }

    @Override
    public BIUserDTO patch(Integer id, BIUserDTO biUserDTO) {
        Optional<BIUserEntity> user = userRepository.findById(Long.valueOf(id));

        if (user.isPresent()) {
            user.get().setFirstName(biUserDTO.getFirstName());
            user.get().setLastName(biUserDTO.getLastName());
            user.get().setEmail(biUserDTO.getEmail());
            user.get().setBiUserGroupByUserGroup(BIUserGroupEntity.builder().id(biUserDTO.getUserGroupId()).build());

            return userMapper.biUserEntityToDTO(userRepository.save(user.get()));
        }

        return null;
    }

    @Override
    public void delete(BIUserDTO biUserDTO) {
        this.userRepository.delete(this.userMapper.dtoToBIUserEntity(biUserDTO));
    }

    @Override
    public void deleteById(Integer id) {
        this.userRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public BIUserDTO findByEmail(String email) {
        Optional<BIUserEntity> biUser  = this.userRepository.findByEmail(email);

        return this.userMapper
                .biUserEntityToDTO(biUser.orElse(null));
    }

    @Override
    public BIUserEntity findEntityByEmail(String email) {
        Optional<BIUserEntity> biUser  = this.userRepository.findByEmail(email);

        return biUser.orElse(null);
    }

    @Override
    public int findUseridByEmail(String email) {
        return  this.userRepository.findUseridByEmail(email);

    }

    @Override
    public BIUserEntity getCurrentUser() {
        Authentication authentication = this.authenticationFacade.getAuthentication();
        String email = authentication.getName();

        return this.findEntityByEmail(email);
    }

    @Override
    public int getCurrentUserId() {
        Authentication authentication = this.authenticationFacade.getAuthentication();
        String email = authentication.getName();

        return this.findUseridByEmail(email);
    }

}
