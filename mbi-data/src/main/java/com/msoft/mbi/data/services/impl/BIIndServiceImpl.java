package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.api.dtos.indicators.BIIndInfoDTO;
import com.msoft.mbi.data.api.dtos.indicators.IndicatorDTO;
import com.msoft.mbi.data.api.mapper.indicators.BIIndInfoMapper;
import com.msoft.mbi.data.api.mapper.indicators.entities.BIIndToIndicatorDTOMapper;
import com.msoft.mbi.data.repositories.BIIndRepository;
import com.msoft.mbi.data.services.BIIndService;
import com.msoft.mbi.model.BIIndEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BIIndServiceImpl implements BIIndService {

    private final BIIndRepository indRepository;

    private final BIIndInfoMapper indMapper;

    private final BIIndToIndicatorDTOMapper biIndToIndicatorDTOMapper;

    @Override
    public List<BIIndEntity> findAll() {
        return null;
    }

    @Override
    public BIIndEntity findById(Integer id) {
        Optional<BIIndEntity> biIndEntity = this
                .indRepository.findById((id));

        return biIndEntity.orElse(null);
    }

    @Override
    public BIIndEntity save(BIIndEntity biIndEntity) {
        return this.indRepository.save(biIndEntity);
    }

    @Override
    public BIIndEntity update(Integer id, BIIndEntity biInd) {
        biInd.setId(id);
        return this.indRepository.save(biInd);
    }

    @Override
    public void delete(BIIndEntity object) {
        this.indRepository.delete(object);
    }

    @Override
    public void deleteById(Integer id) {
        this.indRepository.deleteById(id);
    }

    @Override
    public BIIndInfoDTO getBIIndDTO(int biIndId) {
        Optional<BIIndEntity> biIndEntity = this
                .indRepository.findById((biIndId));

        return this.indMapper.biEntityToDTO(biIndEntity.orElse(null));
    }

    @Override
    public List<BIIndInfoDTO> findAllDTOs() {
        return  this.indRepository.findAll().stream()
                .map(this.indMapper::biEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IndicatorDTO getBIIndLogicDTO(int biIndId) {
        Optional<BIIndEntity> biIndEntity = this
                .indRepository.findById((biIndId));

        return this.biIndToIndicatorDTOMapper.biEntityToDTO(biIndEntity.orElse(null));
    }

    @Override
    public List<IndicatorDTO> findAllBIIndLogicDTOs() {
        return  this.indRepository.findAll().stream()
                .map(this.biIndToIndicatorDTOMapper::biEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void changeSequence(int id, boolean hasSequence) {
        this.indRepository.changeSequence(id, hasSequence);
    }
}
