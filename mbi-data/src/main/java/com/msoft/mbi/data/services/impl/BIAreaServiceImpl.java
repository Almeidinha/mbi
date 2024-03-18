package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.repositories.BIAreaRepository;
import com.msoft.mbi.data.services.BIAreaService;
import com.msoft.mbi.model.BIAreaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BIAreaServiceImpl implements BIAreaService {

    private final BIAreaRepository areaRepository;

    @Override
    public List<BIAreaEntity> findAll() {
        return this.areaRepository.findAll(Sort.by(Sort.Direction.DESC, "description"));
    }

    @Override
    public BIAreaEntity findById(Integer id) {
        return this.areaRepository.findById(id).orElse(null);
    }

    @Override
    public BIAreaEntity save(BIAreaEntity biArea) {
        return this.areaRepository.save(biArea);
    }

    @Override
    public BIAreaEntity update(Integer id, BIAreaEntity biArea) {
        biArea.setId(id);
        return this.areaRepository.save(biArea);
    }

    @Override
    public void delete(BIAreaEntity biArea) {
        this.areaRepository.delete(biArea);
    }

    @Override
    public void deleteById(Integer id) {
        this.areaRepository.deleteById(id);
    }

}
