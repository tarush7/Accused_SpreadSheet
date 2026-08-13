package com.cctns.apprehend.persistence.entity.apprehend;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_apprehend_witn_nationality", schema = "apprehend")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TApprehendWitnessNationalityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_witn_nat_srno", nullable = false)
    private Long apprWitnNatSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

//    @Column(name = "appr_witns_srno", nullable = false)
//    private Long apprWitnsSrno;

    @Column(name = "appr_witns_srno_migr")
    private Long apprWitnsSrnoMigr;

    @Column(name = "national_id_type_cd")
    private Integer nationalIdTypeCd;

    @Column(name = "national_id_num", length = 50)
    private String nationalIdNum;

    @Column(name = "passport_issue_dt")
    private LocalDateTime passportIssueDt;

    @Column(name = "passport_issue_plc", length = 200)
    private String passportIssuePlc;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "record_created_on")
    private LocalDateTime recordCreatedOn;

    @Column(name = "record_created_by")
    private Long recordCreatedBy;

    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;

    @Column(name = "record_sync_from", length = 35)
    private String recordSyncFrom;

    @Column(name = "record_sync_on")
    private LocalDateTime recordSyncOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appr_witns_srno", nullable = false)
    private TApprehendWitnessEntity apprehendWitness;
}