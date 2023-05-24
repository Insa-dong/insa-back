package com.insadong.application.emporg.service;

import com.insadong.application.common.entity.Dept;
import com.insadong.application.common.entity.Employee;
import com.insadong.application.common.entity.HR;
import com.insadong.application.common.entity.Job;
import com.insadong.application.employee.dto.EmployeeDTO;
import com.insadong.application.employee.repository.EmployeeRepository;
import com.insadong.application.emporg.dto.EmpHRDTO;
import com.insadong.application.emporg.repository.EmpDeptRepository;
import com.insadong.application.emporg.repository.EmpHRRepository;
import com.insadong.application.emporg.repository.EmpJobRepository;
import com.insadong.application.emporg.repository.EmpRepository;
import com.insadong.application.study.repository.StudyInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmpService {

	private final EmpRepository empRepository;
	private final ModelMapper modelMapper;
	private final EmpDeptRepository empDeptRepository;
	private final EmpJobRepository empJobRepository;
	private final EmployeeRepository employeeRepository;
	private final StudyInfoRepository studyInfoRepository;

	private final EmpHRRepository empHRRepository;

	public EmpService(EmpRepository empRepository, ModelMapper modelMapper, EmpDeptRepository empDeptRepository, EmpJobRepository empJobRepository, EmployeeRepository employeeRepository
			, StudyInfoRepository studyInfoRepository, EmpHRRepository empHRRepository) {
		this.empRepository = empRepository;
		this.modelMapper = modelMapper;
		this.empDeptRepository = empDeptRepository;
		this.empJobRepository = empJobRepository;
		this.employeeRepository = employeeRepository;
		this.studyInfoRepository = studyInfoRepository;
		this.empHRRepository = empHRRepository;
	}

	/*1. 구성원 전체 조회*/
	public Page<EmployeeDTO> selectEmpList(int page, List<String> empStates) {
		log.info("[EmpService] selectEmpList start ==============================");

		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("empCode").descending());

		Page<Employee> empList = empRepository.findByEmpStateIn(pageable, empStates);
		Page<EmployeeDTO> empDTOList = empList.map(emp -> modelMapper.map(emp, EmployeeDTO.class));

		log.info("[EmpService] selectEmpList.getContent(): {}", empDTOList.getContent());

		log.info("[EmpService] selectEmpList end ===============================");

		return empDTOList;
	}


	/*2. 구성원 부서별 조회*/
	public Page<EmployeeDTO> selectEmpListByDept(int page, String deptCode) {
		log.info("[EmpService] selectEmpListByDept start ==============================");

		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("empCode").descending());

		/*dept 엔티티 조회*/
		Dept findDept = empDeptRepository.findById(deptCode)
				.orElseThrow(() -> new IllegalArgumentException("해당 부서가 없습니다. deptCode =" + deptCode));

		List<String> empStates = Arrays.asList("재직중", "휴직중");
		Page<Employee> empList = empRepository.findByDeptAndEmpStateIn(pageable, findDept, empStates);
		Page<EmployeeDTO> empDTOList = empList.map(emp -> modelMapper.map(emp, EmployeeDTO.class));

		log.info("[EmpService] selectEmpListByDept.getContent(): {}", empDTOList.getContent());

		log.info("[EmpService] selectEmpListByDept end ===============================");

		return empDTOList;
	}


	/*3. 구성원 검색*/
	public Page<EmployeeDTO> searchEmpByNameAndDeptAndJob(int page, String searchOption, String searchKeyword) {
		log.info("[EmpService] searchEmpByNameAndDeptAndJob start ==============================");

		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("empCode").descending());
		List<String> empStates = Arrays.asList("재직중", "휴직중");

		if (searchOption.equals("name")) {
			Page<Employee> empList = empRepository.findByEmpNameContainsAndEmpStateIn(pageable, searchKeyword, empStates);
			Page<EmployeeDTO> empDTOList = empList.map(emp -> modelMapper.map(emp, EmployeeDTO.class));
			log.info("[EmpService] searchEmpByNameAndDeptAndJob.getContent(): {}", empDTOList.getContent());
			return empDTOList;
		} else if (searchOption.equals("dept")) {
			List<String> findDeptCodeList = empDeptRepository.findByDeptNameContains(searchKeyword);
			Page<Employee> empList = empRepository.findByDeptDeptCodeInAndEmpStateIn(pageable, findDeptCodeList, empStates);
			Page<EmployeeDTO> empDTOList = empList.map(emp -> modelMapper.map(emp, EmployeeDTO.class));
			log.info("[EmpService] searchEmpByNameAndDeptAndJob.getContent(): {}", empDTOList.getContent());
			return empDTOList;
		} else if (searchOption.equals("job")) {
			List<String> findJobCodeList = empJobRepository.findByJobNameContains(searchKeyword);
			Page<Employee> empList = empRepository.findByJobJobCodeInAndEmpStateIn(pageable, findJobCodeList, empStates);
			Page<EmployeeDTO> empDTOList = empList.map(emp -> modelMapper.map(emp, EmployeeDTO.class));
			log.info("[EmpService] searchEmpByNameAndDeptAndJob.getContent(): {}", empDTOList.getContent());
			return empDTOList;
		} else {
			throw new IllegalArgumentException("유효하지 않은 검색 옵션입니다.");
		}
	}



	/*4. 부서, 직책 조회*/
	public Map<String, Object> selectEmpDeptJobList() {
		List<Dept> deptList = empDeptRepository.findAll();
		List<Job> jobList = empJobRepository.findAll();

		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("deptList", deptList);
		resultMap.put("jobList", jobList);

		return resultMap;
	}

	/* 5. 구성원 등록 */
	@Transactional
	public void insertEmp(EmployeeDTO employeeDTO) {
		log.info("[EmpService] insertEmp : {}", employeeDTO);
		empRepository.save(modelMapper.map(employeeDTO, Employee.class));
	}

	/* 6. 구성원 상세 조회 */
	public EmployeeDTO selectEmpDetail(Long empCode) {

		Employee employee = employeeRepository.findById(empCode)
				.orElseThrow(() -> new IllegalArgumentException("해당 구성원이 없습니다. empCode=" + empCode));

		EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);

		return employeeDTO;

	}

	/* 7. 인사이력 조회*/
	public Page<EmpHRDTO> selectEmpRecord(int page, Long empCode) {

		Employee employee = empRepository.findByEmpCode(empCode);

		log.info("service start ========== ");
		Pageable pageable = PageRequest.of(page - 1, 4, Sort.by("hrCode").descending());
		Page<HR> empHRList = empHRRepository.findByEmployee(pageable, employee);
		log.info("service end=============");
		return empHRList.map(hr -> modelMapper.map(hr, EmpHRDTO.class));
	}

	/* 8. 구성원 부서이동*/
	@Transactional
	public void updateEmpDept(EmployeeDTO employeeDTO){
		log.info("[EmpService] updateEmpDept start ============================== ");
		log.info("[EmpService] employeeDTO : {}", employeeDTO);

		Employee originEmployee = empRepository.findById(employeeDTO.getEmpCode())
				.orElseThrow(() -> new IllegalArgumentException("해당 구성원이 없습니다. empCode = " + employeeDTO.getEmpCode()));

		empHRRepository.save(new HR(originEmployee, "부서이동", originEmployee.getDept()));

		originEmployee.updateDept(
				modelMapper.map(employeeDTO.getDept(), Dept.class)
		);

		log.info("[EmpService] updateEmpDept end ============================== ");
	}

	/* 8. 구성원 직책변동*/
	@Transactional
	public void updateEmpJob(EmployeeDTO employeeDTO){
		log.info("[EmpService] updateEmpDept start ============================== ");
		log.info("[EmpService] employeeDTO : {}", employeeDTO);

		Employee originEmployee = empRepository.findById(employeeDTO.getEmpCode())
				.orElseThrow(() -> new IllegalArgumentException("해당 구성원이 없습니다. empCode = " + employeeDTO.getEmpCode()));

		empHRRepository.save(new HR(originEmployee, "직책변경", originEmployee.getJob()));

		originEmployee.updateDept(
				modelMapper.map(employeeDTO.getDept(), Dept.class)
		);

		log.info("[EmpService] updateEmpDept end ============================== ");
	}






	public List<com.insadong.application.study.dto.EmpDTO> viewTeacherList() {

		return empRepository.findByDeptDeptCode("DE0003").stream().map(teacher -> modelMapper.map(teacher, com.insadong.application.study.dto.EmpDTO.class)).collect(Collectors.toList());
	}
}
