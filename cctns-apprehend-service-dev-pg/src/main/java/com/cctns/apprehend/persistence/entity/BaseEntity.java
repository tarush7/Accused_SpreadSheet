package com.cctns.apprehend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

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

    @Column(name = "record_sync_from")
    private String recordSyncFrom;

    @Column(name = "record_sync_on")
    private LocalDateTime recordSyncOn;
}
