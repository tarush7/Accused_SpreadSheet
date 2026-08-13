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

import java.time.LocalDateTime;

@Entity
@Table(name = "t_fir_accused_bank_dtls", schema = "fir")
@Getter
@Setter
public class TFirAccusedBankDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bankcard_id_srno")
    private Long bankcardIdSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "accused_vid",insertable = false,updatable = false)
    private Long accusedVid;

    @Column(name = "bank_cd")
    private Integer bankCd;

    @Column(name = "account_type_cd")
    private Integer accountTypeCd;

    @Column(name = "account_num", length = 25)
    private String accountNum;

    @Column(name = "bankother_info")
    private String bankotherInfo;

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
