package com.Jmumo.CustomerService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Basic
    @Column(name = "PUBLIC_ID", nullable = false, updatable = false,unique = true)
    @ColumnDefault("gen_random_uuid()")
    private UUID publicId;

    @JsonIgnore
    @Basic
    @Column(name = "SUBSIDIARY", updatable = false)
    private UUID subsidiary;

    @JsonIgnore
    @Basic
    @ColumnDefault("false")
    @Column(name = "DELETED")
    private boolean deleted;

    @CreationTimestamp
    @Basic
    @Column(name = "CREATE_DATE", updatable = false, nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Basic
    @Column(name = "UPDATE_DATE", insertable = false)
    private LocalDateTime updateDate;

    @JsonIgnore
    @Basic
    @Column(name = "DELETE_DATE", insertable = false)
    private LocalDateTime deleteDate;

    @Basic
    @JsonIgnore
    @Column(name = "CREATED_BY")
    private UUID createdBy;

    @Basic
    @JsonIgnore
    @Column(name = "UPDATED_BY")
    private UUID updatedBy;

    @Basic
    @JsonIgnore
    @Column(name = "DELETED_BY")
    private UUID deletedBy;

    @Basic
    @Version
    @Column(name = "VERSION")
    @ColumnDefault("0")
    private Integer version;

    @PrePersist
    public void prePersist() {
        this.deleted = false;
        this.publicId = this.publicId != null ? this.publicId : UUID.randomUUID();
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
