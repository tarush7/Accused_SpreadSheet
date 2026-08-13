package com.cctns.apprehend.persistence.entity.disposal;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_juv_disposal", schema = "apprehend")
public class TJuvDisposalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juv_disposal_srno")
    private Long juvDisposalSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "fir_reg_num")
    private Long firRegNum;

    @Column(name = "apprehend_srno")
    private Long apprehendSrno;

    @Column(name = "juvenile_srno")
    private Long juvenileSrno;

    @Column(name = "jjb_name", length = 200)
    private String jjbName;

    @Column(name = "jjb_address", length = 500)
    private String jjbAddress;

    @Column(name = "jjb_magistrate_name", length = 200)
    private String jjbMagistrateName;

    @Column(name = "final_order_dtls")
    private String finalOrderDtls;

    @Column(name = "final_order_num")
    private String finalOrderNum;

    @Column(name = "final_order_dt")
    private LocalDate finalOrderDt;

    @Column(name = "jjb_estbl_name")
    private String jjbEstblName;

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

    @Column(name = "record_sync_from", length = 35)
    private String recordSyncFrom;

    @Column(name = "record_sync_on")
    private LocalDateTime recordSyncOn;

    @OneToMany(mappedBy = "juvDisposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TJuvDisposalFilesEntity> fileList;
}