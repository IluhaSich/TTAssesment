package com.project.tta.services;

import com.project.tta.dtos.TtGradeDto;
import com.project.tta.models.TtGrade;
import com.project.tta.repositories.TtRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TtService {
    private final TtRepository ttRepository;
    ModelMapper modelMapper = new ModelMapper();

    public TtService(TtRepository ttRepository, ModelMapper modelMapper) {
        this.ttRepository = ttRepository;
        this.modelMapper = modelMapper;
    }

    public TtGradeDto save(TtGrade ttGrade) {
        ttRepository.save(ttGrade);
        return modelMapper.map(ttGrade, TtGradeDto.class);
    }

    public TtGrade getGradeById(long gradeId) {
        return ttRepository.findById(gradeId).orElse(null);
    }

    public List<TtGradeDto> getAllGrades() {
        return ttRepository.findAll().stream().map(grade -> modelMapper.map(grade, TtGradeDto.class)).toList();
    }

    public boolean existsById(long gradeId) {
        return ttRepository.existsById(gradeId);
    }
}
