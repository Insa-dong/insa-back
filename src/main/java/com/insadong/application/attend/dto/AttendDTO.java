package com.insadong.application.attend.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.insadong.application.student.dto.StudentDTO;
import com.insadong.application.study.dto.StudyDTO;

import lombok.Data;

@Data
public class AttendDTO {

	private Long attendCode;
	private StudyDTO study;
	private StudentDTO student;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date attendDate;
	private String attendStatus;
	
}
