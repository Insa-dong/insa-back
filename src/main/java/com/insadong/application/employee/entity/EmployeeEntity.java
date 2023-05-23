package com.insadong.application.employee.entity;

import com.insadong.application.common.entity.EmpAuth;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@DynamicInsert
@Table(name = "TB_EMP")
@SequenceGenerator(name = "EMP_SEQ_GENERATOR",
		sequenceName = "SEQ_EMP_CODE",
		initialValue = 1, allocationSize = 1)
public class EmployeeEntity {

	@Id
	@Column(name = "EMP_CODE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMP_SEQ_GENERATOR")
	private Long empCode;

	@Column(name = "EMP_NAME")
	private String empName;

	@Column(name = "EMP_EMAIL")
	private String empEmail;

	@Column(name = "EMP_ID")
	private String empId;

	@Column(name = "EMP_PWD")
	private String empPwd;

	@Column(name = "EMP_PHONE")
	private String empPhone;

	@OneToMany
	@JoinColumn(name = "EMP_CODE")
	private List<EmpAuth> empAuthList;
}
