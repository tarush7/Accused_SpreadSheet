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
@Table(name = "t_fir_accused_id_marks", schema = "fir")
@Getter
@Setter
public class TFirAccusedIdMarksEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fir_acc_id_marks_srno")
    private Long firAccIdMarksSrno;

    @Column(name = "lang_cd", nullable = false)
    private Integer langCd;

    @Column(name = "accused_vid",insertable = false,updatable = false)
    private Long accusedVid;

    @Column(name = "id_marks_type_cd")
    private Integer idMarksTypeCd;

    @Column(name = "body_part_loc_cd")
    private Integer bodyPartLocCd;

    @Column(name = "tattoo_type_cd")
    private Integer tattooTypeCd;

    @Column(name = "tattoo_mark_desc", length = 1000)
    private String tattooMarkDesc;

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
