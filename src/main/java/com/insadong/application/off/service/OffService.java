package com.insadong.application.off.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.insadong.application.common.entity.Employee;
import com.insadong.application.common.entity.Off;
import com.insadong.application.employee.dto.EmpDTOImplUS;
import com.insadong.application.employee.dto.EmployeeDTO;
import com.insadong.application.employee.repository.EmployeeRepository;
import com.insadong.application.off.dto.EmpOffDTO;
import com.insadong.application.off.dto.OffDTO;
import com.insadong.application.off.repository.EmpOffRepository;
import com.insadong.application.off.repository.OffRepository;

import lombok.extern.slf4j.Slf4j;

@Service
public class OffService {

	private final OffRepository offRepository;
	private final EmpOffRepository empOffRepository;
	private final EmployeeRepository employeeRepository; 
	private final EmpOffService empOffService;
	private final ModelMapper modelMapper;

	public OffService(OffRepository offRepository, EmpOffRepository empOffRepository, 
			EmployeeRepository employeeRepository, EmpOffService empOffService, ModelMapper modelMapper) {
		this.offRepository = offRepository;
		this.empOffRepository = empOffRepository;
		this.employeeRepository = employeeRepository;
		this.empOffService = empOffService;
		this.modelMapper = modelMapper;
	}

	/* 연차 신청 */
	@Transactional
	public void applyOff(OffDTO offDTO, EmpDTOImplUS loggedInUser) {

		
		LocalDate offStart = offDTO.getOffStart();
		LocalDate offEnd = offDTO.getOffEnd();
		
		// 신청자 설정
		Employee foundEmp = empOffRepository.findById(loggedInUser.getEmpCode()).orElseThrow(() -> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));
		// 신청자 타입 변환 (Employee -> EmployeeDTO)
		EmployeeDTO empDTO = modelMapper.map(foundEmp, EmployeeDTO.class);
		// 신청자 EmpOffDTO 가져오기
		EmpOffDTO empOffDTO = empOffService.showMyOff(foundEmp.getEmpCode());
		
		
		// 중복 여부 확인
	    if (checkExistingOff(foundEmp, offStart, offEnd)) {
	        throw new IllegalArgumentException("이미 신청한 연차가 존재합니다.");
	    }

		// 연차 일수 계산
		long days = ChronoUnit.DAYS.between(offStart, offEnd) + 1;

		// 반차면 0.5개로
		double offDay = offDTO.getOffDiv().contains("반차") ? 0.5 : days;
		
		// 신청하려는 연차가 남은 연차보다 큰 경우
	    if (offDay > empOffDTO.getRemainingOff()) {
	        throw new IllegalArgumentException("남은 연차보다 많은 연차를 신청할 수 없습니다.");
	    }

	
		// 결재자 = 신청자 부서 팀장
		Employee payer = empOffRepository.findTeamLeaderByDept(foundEmp.getDept());
		
		
		// 결재자 타입 변환 (Employee -> EmployeeDTO)
		EmployeeDTO payerDTO = modelMapper.map(payer, EmployeeDTO.class);

		// 신청일 설정
		LocalDate requestDate = LocalDate.now();
	

		OffDTO offDTOs = new OffDTO();
		offDTOs.setOffStart(offStart);
		offDTOs.setOffEnd(offEnd);
		offDTOs.setOffDiv(offDTO.getOffDiv());
		offDTOs.setSignReason(offDTO.getSignReason());
		offDTOs.setSignStatus("대기"); // 고정값
		offDTOs.setOffDay(offDay);
		offDTOs.setSignRequester(empDTO);
		offDTOs.setSignPayer(payerDTO);
		offDTOs.setRequestDate(requestDate);
		
		
		offRepository.save(modelMapper.map(offDTOs, Off.class));

	}
	
	/*2. 연차 중복 조회 */
	public boolean checkExistingOff(Employee emp, LocalDate offStart, LocalDate offEnd) {
		
	    List<String> signStatusList = Arrays.asList("승인", "대기");
	    
	    return offRepository
	    		.existsBySignRequesterAndOffStartLessThanEqualAndOffEndGreaterThanEqualAndSignStatusIn(emp, offStart, offEnd, signStatusList);
	}
	
	/* 3,4. 내 연차 조회 */
	public List<OffDTO> myOffList(Long empCode) {
		List<Off> offList = offRepository.findBySignRequester_EmpCodeOrSignPayer_EmpCode(empCode, empCode, Sort.by("offStart"));
		//log.info("offList : {} ", offList);

		List<OffDTO> offDTOList = offList.stream()
	            .filter(off -> off.getSignRequester().getEmpCode().equals(empCode)) // 신청자가 자신인 경우만 필터링
	            .map(off -> modelMapper.map(off, OffDTO.class))
	            .collect(Collectors.toList());

		return offDTOList;

	}
	
	/* 3-1,4-1. 내 연차 상세 조회 */
	public OffDTO myOffDetail(Long signCode, Long empCode) {
		 
		Employee signRequester = employeeRepository.findById(empCode)
			        .orElseThrow(() -> new NoSuchElementException("해당 구성원을 찾을 수 없습니다. " + empCode));
		 
		Off off = offRepository.findBySignCodeAndSignRequester(signCode, signRequester);
		
		OffDTO offDTO = modelMapper.map(off, OffDTO.class);
		
		
		return offDTO;

	}


	/* 5. 연차 취소*/
	public void deleteOff(Long signCode, EmpDTOImplUS loggedInUser) {
		
		empOffRepository.findById(loggedInUser.getEmpCode()).orElseThrow(() -> new IllegalArgumentException("해당 구성원을 찾을 수 없습니다."));

	    offRepository.deleteById(signCode);
	}

	
	/* 6. 연차 신청 내역 조회(팀장) */
	public Page<OffDTO> mySignOffList(Long empCode, int page) {
		
		PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "requestDate"));
		
		Page<Off> offList = offRepository.findBySignPayer_EmpCode(empCode, pageRequest);
		
		Page<OffDTO> offDTIList = offList.map(off -> modelMapper.map(off, OffDTO.class));
		
		return offDTIList;
	}
	
	/* 6-1. 연차 신청 내역 검색 by 신청자, 승인상태 (팀장)*/

	public Page<OffDTO> searchOffByRequesterAndStatus(int page, String searchOption, String searchKeyword) {

		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("requestDate").descending());
		
		
		if (searchOption.equals("empName")) {
			 Page<Off> offList = offRepository.findBySignRequester_EmpNameContains(pageable, searchKeyword);
			Page<OffDTO> offDTOList = offList.map(off -> modelMapper.map(off, OffDTO.class));
			//log.info("[OffService] searchOffByRequesterAndStatus.getContent(): {}", offDTOList.getContent());
			return offDTOList;
		} else if (searchOption.equals("signStatus")) {
			Page<Off> offList = offRepository.findBySignStatus(pageable, searchKeyword);
			Page<OffDTO> offDTOList = offList.map(off -> modelMapper.map(off, OffDTO.class));
			//log.info("[OffService] searchOffByRequesterAndStatus.getContent(): {}", offDTOList.getContent());
			return offDTOList;
		} else {
			throw new IllegalArgumentException("유효하지 않은 검색 옵션입니다.");
		}
		
	}


	/* 7.  연차 승인 처리 (팀장)*/
	@Transactional
	public void signUpOff(Long signCode, OffDTO offDTO) {

    	
		// 승인일 설정
		LocalDate handleDate = LocalDate.now();
		
		Off off = offRepository.findById(signCode).orElseThrow(() -> new RuntimeException("연차 신청이 없습니다."));
		
	    off.setSignStatus(offDTO.getSignStatus());
	    off.setReturnReason(offDTO.getReturnReason());
	    off.setHandleDate(handleDate);
	    
	    offRepository.save(off);

		
	}




	
}
