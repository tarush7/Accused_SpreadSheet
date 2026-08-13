package com.cctns.apprehend.persistence.entity.accused;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_fir_acc_national_id", schema = "fir")
@Getter
@Setter

public class TFirAccusedNationalIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "national_id_srno")
    private Long nationalIdSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "accused_vid",insertable = false,updatable = false)
    private Long accusedVid;

    @Column(name = "national_id_type_cd")
    private Integer nationalIdTypeCd;

    @Column(name = "national_id_num", length = 50)
    private String nationalIdNum;

    @Column(name = "passport_issue_dt")
    private LocalDate passportIssueDt;

    @Column(name = "passport_issue_plc", length = 200)
    private String passportIssuePlc;

    @Column(name = "record_status", length = 1)
    private String recordStatus;

    @CreationTimestamp
    @Column(name = "record_created_on", updatable = false)
    private LocalDateTime recordCreatedOn;

    @Column(name = "record_created_by")
    private Long recordCreatedBy;

    @UpdateTimestamp
    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;

    @ManyToOne
    @JoinColumn(name = "accused_vid")
    private TFirAccusedInfoEntity accused;

}