package com.cctns.apprehend.persistence.entity.accused;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Table(name = "t_fir_multi_accused", schema = "fir")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TFirMultiAccusedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fir_multi_acc_srno")
    private Long firMultiAccSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "crm_multi_acc_srno_migr")
    private Long crmMultiAccSrnoMigr;

    @Column(name = "fir_reg_num", nullable = false)
    private Long firRegNum;

    @Column(name = "accused_vid")
    private Long accusedVid;

    @Column(name = "accused_srno")
    private Long accusedSrno;

    @Column(name = "exist_fir_reg_num")
    private Long existFirRegNum;

    @Column(name = "exist_accused_srno")
    private Long existAccusedSrno;

    @Column(name = "exist_accused_uniq_num")
    private Long existAccusedUniqNum;

    @Column(name = "full_name", length = 420)
    private String fullName;

    @Column(name = "record_status", length = 1)
    private String recordStatus;

    @Column(name = "record_created_on")
    private LocalDateTime recordCreatedOn;

    @Column(name = "record_created_by")
    private Long recordCreatedBy;

    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;
}