package com.cctns.apprehend.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "m_reg_seq_num", schema = "admin",
        uniqueConstraints = {
                @UniqueConstraint(name = "unq_m_reg_seq_num",
                        columnNames = {"reg_type_cd", "reg_year", "ps_cd"})
        })
public class MRegSeqNumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_srno")
    private Long seqSrno;

    @Column(name = "reg_type_cd", nullable = false)
    private Integer regTypeCd;

    @Column(name = "reg_year", nullable = false)
    private Integer regYear;

    @Column(name = "ps_cd", nullable = false)
    private Integer psCd;

    @Column(name = "reg_type_desc")
    private String regTypeDesc;

    @Column(name = "next_seq_srno")
    private Integer nextSeqSrno;

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

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

}
