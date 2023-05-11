package com.insadong.application.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "TB_EVA")
@SequenceGenerator(name = "EVA_SEQ_GENERATOR",
		sequenceName = "SEQ_EVA_CODE",
		initialValue = 1, allocationSize = 1)
public class Eva {

	@Id
	@Column(name = "EVA_CODE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVA_SEQ_GENERATOR")
	private Long evaCode;

	@ManyToOne
	@JoinColumn(name = "CLASS_INFO_CODE")
	private ClassInfo ClassInfo;

	@ManyToOne
	@JoinColumn(name = "STU_CODE")
	private Student student;

	@Column(name = "EVA_WRITE_CONTENT")
	private String evaWriteContent;

	@Column(name = "EVA_WRITE_DATE")
	private Date evaWriteDate;

	@Column(name = "EVA_UPDATE_TIME")
	private String evaUpdateTime;

	@Column(name = "EVA_DELETE_STATUS")
	private String evaDeleteStatus;

}
