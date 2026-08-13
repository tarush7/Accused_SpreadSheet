package com.cctns.apprehend.persistence.entity.apprehend;
import com.cctns.apprehend.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_apprehend_witness_addr", schema = "apprehend")
public class TApprehendWitnessAddrEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appr_witn_addr_srno")
    private Long apprWitnAddrSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "address_cd_migr")
    private Long addressCdMigr;

//    @Column(name = "appr_witns_srno", nullable = false, insertable = true, updatable = false)
//    private Long apprWitnsSrno;

    @Column(name = "appr_witns_srno_migr")
    private Long apprWitnsSrnoMigr;

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

    @Column(name = "is_perm_addr_same")
    private Boolean isPermAddrSame;

    @Column(name = "outside_india_addr")
    private String outsideIndiaAddr;

    @Column(name = "address_eng")
    private String addressEng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appr_witns_srno", nullable = false)
    private TApprehendWitnessEntity apprehendWitness;

}
