package com.insadong.application.study.controller;

import com.insadong.application.common.ResponseDTO;
import com.insadong.application.employee.dto.EmpDTOImplUS;
import com.insadong.application.paging.Pagenation;
import com.insadong.application.paging.PagingButtonInfo;
import com.insadong.application.paging.ResponseDTOWithPaging;
import com.insadong.application.study.dto.PetiteStudyDTO;
import com.insadong.application.study.dto.PetiteStudyInfoDTO;
import com.insadong.application.study.dto.StudyInfoDTO;
import com.insadong.application.study.service.StudyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("insa/v1")
public class StudyInfoController {

	private final StudyInfoService studyInfoService;

	public StudyInfoController(StudyInfoService studyInfoService) {
		this.studyInfoService = studyInfoService;
	}

	@GetMapping("/studyInfoList")
	public ResponseEntity<ResponseDTO> viewStudyList(@RequestParam(name = "page", defaultValue = "1") int page) {

		Page<StudyInfoDTO> data = studyInfoService.viewStudyInfoList(page);
		PagingButtonInfo pagingButtonInfo = Pagenation.getPagingButtonInfo(data);

		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageInfo(pagingButtonInfo);
		responseDTOWithPaging.setData(data.getContent());

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", responseDTOWithPaging));
	}

	@GetMapping("/studyInfoList/search")
	public ResponseEntity<ResponseDTO> searchStudy(
			@RequestParam(name = "search") String search,
			@RequestParam(name = "page") int page,
			@RequestParam(name = "category") String category) {

		Page<StudyInfoDTO> data = studyInfoService.searchStudy(search, page, category);
		PagingButtonInfo pagingButtonInfo = Pagenation.getPagingButtonInfo(data);


		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageInfo(pagingButtonInfo);
		responseDTOWithPaging.setData(data.getContent());

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", responseDTOWithPaging));
	}

	@GetMapping("/PetiteStudyInfo/{studyInfoCode}")
	public ResponseEntity<ResponseDTO> viewStudyInfo(@PathVariable Long studyInfoCode) {

		PetiteStudyInfoDTO data = studyInfoService.viewPetiteStudyInfo(studyInfoCode);
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", data));
	}

	@PutMapping("/studyInfo/{studyInfoCode}")
	public ResponseEntity<ResponseDTO> modifyStudyInfo(@PathVariable Long studyInfoCode, @RequestBody PetiteStudyInfoDTO studyInfo, @AuthenticationPrincipal EmpDTOImplUS emp) {


		PetiteStudyDTO study = studyInfo.getStudy();
		study.setStudyModifyDate(new Date());

		studyInfoService.modifyStudyInfo(studyInfo, studyInfoCode, emp.getEmpCode());

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "수정 완료"));
	}

	@PostMapping("/studyInsert")
	public ResponseEntity<ResponseDTO> insertStudyInfo(@RequestBody PetiteStudyInfoDTO studyInfo, @AuthenticationPrincipal EmpDTOImplUS emp) {

		PetiteStudyDTO study = studyInfo.getStudy();
		study.setStudyDate(new Date());
		studyInfo.setStudy(study);

		studyInfoService.insertStudy(studyInfo, emp.getEmpCode());

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "등록 완료"));
	}


	/* 강사 강의 리스트 조회 */
	@GetMapping("/emp/teacherStudyList")
	public ResponseEntity<ResponseDTO> selectTeacherStudyListByEmpCode(@RequestParam(name = "page", defaultValue = "1") int page,
	                                                                   @AuthenticationPrincipal EmpDTOImplUS employeeDTO) {

		log.info("[EmpController] employeeDTO: {}", employeeDTO);
		log.info("[EmpController] : selectTeacherStudyListByEmpCode start =============================== ");
		log.info("[EmpController] : page : {}", page);

		Page<StudyInfoDTO> studyInfoDTOList = studyInfoService.selectTeacherStudyListByEmpCode(page, employeeDTO.getEmpCode());
		PagingButtonInfo pageInfo = Pagenation.getPagingButtonInfo(studyInfoDTOList);

		log.info("[EmpController] : pageInfo : {}", pageInfo);

		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageInfo(pageInfo);
		responseDTOWithPaging.setData(studyInfoDTOList.getContent());

		log.info("[EmpController] : selectTeacherStudyListByEmpCode end =============================== ");

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", responseDTOWithPaging));
	}

	@DeleteMapping("/studyInfo")
	public ResponseEntity<ResponseDTO> deleteStudyByStudyCode(@RequestBody List<Long> studyInfoCode) {

		log.info("studyInfoCode : {} ", studyInfoCode);
		studyInfoService.deleteStudyByStudyCode(studyInfoCode);

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "삭제 성공"));
	}
}