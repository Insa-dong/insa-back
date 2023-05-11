package com.insadong.application.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insadong.application.common.ResponseDTO;
import com.insadong.application.employee.dto.EmployeeDTO;
import com.insadong.application.member.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	/* 로그인 */
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody EmployeeDTO employeeDTO) {

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "로그인 완료", authService.login(employeeDTO)));
	}

}