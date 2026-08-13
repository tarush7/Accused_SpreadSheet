package com.cctns.apprehend.persistence.entity.accused;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "t_fir_accused_info", schema = "fir")
public class FirAccusedInfoUpdateEntity {

    @Id
    @Column(name = "accused_vid")
    private Long accusedVid;

    @Column(name = "record_status", length = 1)
    private String recordStatus;

    @Column(name = "record_updated_on")
    private LocalDateTime recordUpdatedOn;

    @Column(name = "record_updated_by")
    private Long recordUpdatedBy;
}
