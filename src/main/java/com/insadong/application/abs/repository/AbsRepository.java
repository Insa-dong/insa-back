package com.insadong.application.abs.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.insadong.application.common.entity.Abs;



public interface AbsRepository extends JpaRepository<Abs, Long>{
	
	/* 1. 전체 조회(TEST) */
	
	/* 1-1 내 근태 조회*/
	Page<Abs> findByEmpCode(Long empCode, Pageable pageable);
	
	
	/* 2. 출퇴근 입력  test*/
    Optional<Abs> findByEmpCode_EmpCodeAndAbsDate(Long empCode, LocalDate localDate);
    
    
    /* 근무일 검색 */
    Page<Abs> findByAbsDate(LocalDate absDate, Pageable pageable);

	


    



}
	
	


