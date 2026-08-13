package com.cctns.apprehend.persistence.entity.apprehend;

import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_addresses", schema = "apprehend")
public class TApprehendAddressesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_addr_srno")
    private Long apprAddrSrno;
 //   private Long id;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "appr_addr_srno_migr")
    private Long apprAddrSrnoMigr;

    @Column(name = "apprehend_srno", nullable = false, insertable = true, updatable = false)
    private Long apprehendSrno;

    @Column(name = "juvenile_srno")
    private Long juvenileSrno;

    @Column(name = "juvenile_vid")
    private Integer juvenileVid;

    @Column(name = "juvenile_srno_migr")
    private Long juvenileSrnoMigr;

    @Column(name = "address_type_cd")
    private Integer addressTypeCd;

    @Column(name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "address_line_3")
    private String addressLine3;

    @Column(name = "sub_district_cd")
    private Integer subDistrictCd;

    @Column(name = "village_cd")
    private Long villageCd;

    @Column(name = "village")
    private String village;

    @Column(name = "tehsil")
    private String tehsil;

    @Column(name = "country_cd")
    private Integer countryCd;

    @Column(name = "lg_district_cd")
    private Integer lgDistrictCd;

    @Column(name = "ps_id")
    private Long psId;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "pincode")
    private Integer pincode;

    @Column(name = "is_comm_addr")
    private Boolean isCommAddr;

    @Column(name = "outside_india_addr")
    private String outsideIndiaAddr;

    @Column(name = "address_eng")
    private String addressEng;

    @Column(name = "is_perm_addr_same")
    private Boolean isPermAddrSame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprehend_srno",referencedColumnName = "apprehend_srno", insertable = false, updatable = false)
    private TApprehendMemoEntity apprehendMemo;

}
