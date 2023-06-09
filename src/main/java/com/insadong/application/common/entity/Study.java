package com.insadong.application.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "TB_STUDY")
@SequenceGenerator(name = "STUDY_SEQ_GEN", sequenceName = "SEQ_STUDY_CODE", allocationSize = 1)
public class Study {

	@Id
	@Column(name = "STUDY_CODE")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDY_SEQ_GEN")
	private Long studyCode;

	@OneToMany
	@JoinColumn(name = "STUDY_CODE")
	private List<StudyTime> studyTimes;

	@ManyToOne
	@JoinColumn(name = "TRAINING_CODE")
	private Training training;

	@Column(name = "STUDY_MAX_PEOPLE")
	private Long studyMaxPeople;

	@ManyToOne
	@JoinColumn(name = "STUDY_MODIFIER")
	private Employee studyModifier;

	@Column(name = "STUDY_COUNT")
	private Long studyCount;

	@Column(name = "STUDY_MODIFY_DATE")
	private Date studyModifyDate;

	@Column(name = "STUDY_DELETE_YN")
	private String studyDeleteYn;

	@Column(name = "STUDY_DATE")
	private Date studyDate;

	@JsonIgnore
	@OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
	private List<StudyInfo> studyInfo;

	@JsonIgnore
	@OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
	private List<StudyStu> studyStu;

	@JsonIgnore
	@OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
	private List<Attend> Attend;

}
