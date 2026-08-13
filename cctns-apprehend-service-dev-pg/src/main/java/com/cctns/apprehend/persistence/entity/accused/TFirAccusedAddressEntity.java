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
@Table(name = "t_fir_accused_address", schema = "fir")
@Getter
@Setter
    public class TFirAccusedAddressEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "fir_acc_addr_srno")
        private Long firAccAddrSrno;

        @Column(name = "lang_cd", nullable = false)
        private Integer langCd;

        @Column(name = "accused_vid",insertable = false,updatable = false)
        private Long accusedVid;

        @Column(name = "address_type_cd")
        private Integer addressTypeCd;

        @Column(name = "address_line_1", length = 120)
        private String addressLine1;

        @Column(name = "address_line_2", length = 120)
        private String addressLine2;

        @Column(name = "address_line_3", length = 120)
        private String addressLine3;

        @Column(name = "sub_district_cd")
        private Integer subDistrictCd;

        @Column(name = "village_cd")
        private Long villageCd;

        @Column(name = "village", length = 120)
        private String village;

        @Column(name = "tehsil", length = 120)
        private String tehsil;

        @Column(name = "country_cd")
        private Integer countryCd;

        @Column(name = "pincode")
        private Integer pincode;

        @Column(name = "is_perm_addr_same")
        private Boolean isPermAddrSame;

        @Column(name = "is_comm_addr")
        private Boolean isCommAddr;

        @Column(name = "address_eng", length = 400)
        private String addressEng;

        @Column(name = "outside_india_addr", length = 400)
        private String outsideIndiaAddr;

        @Column(name = "lg_district_cd")
        private Integer lgDistrictCd;

        @Column(name = "ps_id")
        private Long psId;

        @Column(name = "state_id")
        private Long stateId;

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