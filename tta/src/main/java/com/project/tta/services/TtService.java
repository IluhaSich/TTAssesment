package com.project.tta.services;

import com.project.tta.dtos.TtGradeDto;
import com.project.tta.models.TtGrade;
import com.project.tta.repositories.TtRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TtService {
    @Autowired
    private final TtRepository ttRepository;
    @Autowired
    ModelMapper modelMapper = new ModelMapper();

    public TtService(TtRepository ttRepository) {
        this.ttRepository = ttRepository;
    }

    public TtGradeDto save(TtGrade ttGrade) {
        ttRepository.save(ttGrade);
        return modelMapper.map(ttGrade, TtGradeDto.class);
    }

    public TtGrade getGradeById(int gradeId) {
        return ttRepository.findById(gradeId).orElse(null);
    }

    public List<TtGradeDto> getAllGrades() {
        return ttRepository.findAll().stream().map(grade -> modelMapper.map(grade, TtGradeDto.class)).toList();
    }

    public boolean existsById(int gradeId) {
        return ttRepository.existsById(gradeId);
    }

    public void deleteGrade(int gradeId) {
        ttRepository.deleteById(gradeId);
    }
}
