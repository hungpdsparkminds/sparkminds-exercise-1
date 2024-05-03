package com.example.librarymanagement.model.entity;

import com.example.librarymanagement.listener.CustomAuditingEntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(CustomAuditingEntityListener.class)
public class AbstractAuditEntity {

    @CreationTimestamp
    private OffsetDateTime createdOn;

    @CreatedBy
    private String createdBy;

    @UpdateTimestamp
    private OffsetDateTime lastModifiedOn;

    @LastModifiedBy
    private String lastModifiedBy;
}