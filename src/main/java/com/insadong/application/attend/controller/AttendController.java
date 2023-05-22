package com.insadong.application.attend.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insadong.application.attend.dto.AttendDTO;
import com.insadong.application.attend.service.AttendService;
import com.insadong.application.common.ResponseDTO;
import com.insadong.application.paging.Pagenation;
import com.insadong.application.paging.PagingButtonInfo;
import com.insadong.application.paging.ResponseDTOWithPaging;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/insa/v1")
public class AttendController {
	
	private final AttendService attendService;
	
	public AttendController(AttendService attendService) {
		this.attendService = attendService;
	}
	
	/* 수강생 출결 조회 */
	@GetMapping("/students/attend/{stuCode}")
	public ResponseEntity<ResponseDTO> selectAttendListByStudent(
			@RequestParam(name="page", defaultValue="1") int page, @PathVariable Long stuCode) {
		
		log.info("[AttendController] : selectAttendListByStudent start ==================================== ");
		log.info("[AttendController] : page : {}", page);
		
		Page<AttendDTO> attendDTOList = attendService.selectAttendListByStudent(page, stuCode);
		PagingButtonInfo pageInfo = Pagenation.getPagingButtonInfo(attendDTOList);
		
		ResponseDTOWithPaging responseDTOWithPaging = new ResponseDTOWithPaging();
		responseDTOWithPaging.setPageInfo(pageInfo);
		responseDTOWithPaging.setData(attendDTOList.getContent());
		
		log.info("[AttendController] : selectAttendListByStudent end ==================================== ");
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", responseDTOWithPaging));
	}
	
	
	/* 수강생 출결 등록 */
	@PostMapping("/students/attend")
	public ResponseEntity<ResponseDTO> insertAttend(@RequestBody AttendDTO attendDto ) {
		
		/* 강사만 등록할수 있는 권한 추가 예정 */
		attendService.insertAttend(attendDto);
		
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "출결 등록 성공"));
		
	}
	
	/* 수강생 출결 수정 */
	@PutMapping("/students/attend")
	public ResponseEntity<ResponseDTO> updateAttend(@RequestBody AttendDTO attendDto) {
		
		attendService.updateAttend(attendDto);
		
		return ResponseEntity
				.ok()
				.body(new ResponseDTO(HttpStatus.OK, "수강생 출결 수정 성공"));
		
	}
	
	
	/* 수강생 출결 삭제 */
	@DeleteMapping("/students/attend/{stuCode}")
	public ResponseEntity<ResponseDTO> deleteAttend(@PathVariable Long stuCode) {
	    attendService.deleteAttend(stuCode);
	    return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "상담일지 삭제 성공"));
	}
}