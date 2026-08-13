package com.cctns.apprehend.persistence.entity;

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
@Table(name = "app_reg_seq_num", schema = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRegSeqNum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_srno")
    private Long seqSrno;

    @Column(name = "ps_cd", nullable = false)
    private Integer psCd;

    @Column(name = "reg_year", nullable = false)
    private Integer regYear;

    @Column(name = "seq_num", nullable = false)
    private Long seqNum;

    @Column(name = "seq_type", nullable = false)
    private Integer seqType;

    @Column(name = "seq_type_desc", nullable = false, length = 30)
    private String seqTypeDesc;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

}
