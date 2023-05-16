package com.insadong.application.studyInfo.service;

import com.insadong.application.common.entity.StudyInfo;
import com.insadong.application.employee.repository.EmployeeRepository;
import com.insadong.application.study.repository.StudyRepository;
import com.insadong.application.studyInfo.dto.StudyInfoDTO;
import com.insadong.application.studyInfo.repository.StudyInfoRepository;
import com.insadong.application.training.repository.TrainingRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudyInfoService {
	private final StudyRepository studyRepository;
	private final StudyInfoRepository studyInfoRepository;
	private final EmployeeRepository employeeRepository;
	private final TrainingRepository trainingRepository;
	private final ModelMapper modelMapper;

	public StudyInfoService(StudyRepository studyRepository, StudyInfoRepository studyInfoRepository, EmployeeRepository employeeRepository, TrainingRepository trainingRepository, ModelMapper modelMapper) {
		this.studyRepository = studyRepository;
		this.studyInfoRepository = studyInfoRepository;
		this.employeeRepository = employeeRepository;
		this.trainingRepository = trainingRepository;
		this.modelMapper = modelMapper;
	}

	public Page<StudyInfoDTO> viewStudyInfoList(int page) {

		Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("study.studyCode").descending());
		Page<StudyInfo> foundStudyInfoList = studyInfoRepository.findByStudyStudyDeleteYn(pageable, "N");

		return foundStudyInfoList.map(studyInfo -> modelMapper.map(studyInfo, StudyInfoDTO.class));
	}
}
