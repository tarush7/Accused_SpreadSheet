package com.cctns.apprehend.persistence.entity.apprehend;
import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_bank_dtls", schema = "apprehend")
public class TApprehendBankDtlsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_bank_srno")
    private Long apprBankSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "bankcard_id_srno_migr")
    private Long bankcardIdSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = false, updatable = false)
    private Long apprehendSrno;

    @Column(name = "bank_cd")
    private Integer bankCd;

    @Column(name = "account_type_cd")
    private Integer accountTypeCd;

    @Column(name = "account_num")
    private String accountNum;

    @Column(name = "bank_other_info")
    private String bankOtherInfo;


}
