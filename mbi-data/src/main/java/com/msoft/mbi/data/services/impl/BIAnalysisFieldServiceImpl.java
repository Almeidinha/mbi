package com.msoft.mbi.data.services.impl;

import com.msoft.mbi.data.repositories.BIAnalysisFieldRepository;
import com.msoft.mbi.data.services.BIAnalysisFieldService;
import com.msoft.mbi.model.BIAnalysisFieldEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BIAnalysisFieldServiceImpl implements BIAnalysisFieldService {

    private final  BIAnalysisFieldRepository analysisFieldRepository;

    @Override
    public List<BIAnalysisFieldEntity> findAll() {
        return null;
    }

    @Override
    public BIAnalysisFieldEntity findById(Integer integer) {
        return null;
    }

    @Override
    public BIAnalysisFieldEntity save(BIAnalysisFieldEntity biAnalysisField) {
        return analysisFieldRepository.save(biAnalysisField);
    }

    @Override
    public BIAnalysisFieldEntity update(Integer integer, BIAnalysisFieldEntity object) {
        return null;
    }

    @Override
    public void delete(BIAnalysisFieldEntity object) {

    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public List<BIAnalysisFieldEntity> saveAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields) {
        return IterableUtils.toList(analysisFieldRepository.saveAll(bIAnalysisFields));
    }

    public void deleteAll(Iterable<BIAnalysisFieldEntity> bIAnalysisFields) {
        analysisFieldRepository.deleteAll(bIAnalysisFields);
    }
}
