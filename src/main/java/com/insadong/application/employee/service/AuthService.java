package com.insadong.application.employee.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.insadong.application.common.entity.Employee;
import com.insadong.application.employee.dto.EmployeeDTO;
import com.insadong.application.employee.dto.TokenDTO;
import com.insadong.application.employee.repository.EmployeeRepository;
import com.insadong.application.exception.IdsearchFailedException;
import com.insadong.application.exception.LoginFailedException;
import com.insadong.application.exception.UserNotFoundException;
import com.insadong.application.jwt.TokenProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final TokenProvider tokenProvider;

	public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
			TokenProvider tokenProvider) {
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
		this.modelMapper = modelMapper;
		this.tokenProvider = tokenProvider;
	}

	/* 로그인 */
	public TokenDTO login(EmployeeDTO employeeDTO) {

		log.info("[AuthService] login start ======================================");
		log.info("[AuthService] employeeDTO : {}", employeeDTO);

		// 1. 아이디로 DB에서 해당 유저가 있는지 조회
		Employee employee = employeeRepository.findByEmpId(employeeDTO.getEmpId())
				.orElseThrow(() -> new LoginFailedException("잘못 된 아이디 또는 비밀번호입니다."));

		// 2. 비밀번호 매칭 확인
		if (!passwordEncoder.matches(employeeDTO.getEmpPwd(), employee.getEmpPwd())) {
			throw new LoginFailedException("잘못 된 아이디 또는 비밀번호입니다.");
		}

		// 3. 토큰 발급
		TokenDTO tokenDTO = tokenProvider.generateTokenDTO(modelMapper.map(employee, EmployeeDTO.class));

		log.info("[AuthService] tokenDTO : {}", tokenDTO);

		log.info("[AuthService] login end ======================================");
		
		return tokenDTO;
	}
	
	/* 아이디 찾기 */
	public EmployeeDTO idSearch(EmployeeDTO employeeDTO) {
		
		Employee employee = employeeRepository.findByEmpNameAndEmpPhone(employeeDTO.getEmpName(), employeeDTO.getEmpPhone())
				.orElseThrow(() -> new IdsearchFailedException("입력하신 정보와 일치하는 아이디가 존재하지 않습니다."));
		
				
		return modelMapper.map(employee, EmployeeDTO.class);
	}

	/* 비밀번호 찾기 */
	public EmployeeDTO findById(EmployeeDTO employeeDTO) {

		Employee employee = employeeRepository.findByEmpId(employeeDTO.getEmpId())
				.orElseThrow(() -> new UserNotFoundException("해당 아이디와 일치하는 사용자가 없습니다."));

		if (employee.getEmpEmail().equals(employeeDTO.getEmpEmail())) {
			return modelMapper.map(employee, EmployeeDTO.class);
		} else {
			return null;
		}

	}
	
}
