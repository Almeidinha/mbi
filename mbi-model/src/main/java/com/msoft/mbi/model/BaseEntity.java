package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {

    @Temporal(value = TemporalType.DATE)
    @Column(name = "CREATED_AT")
    @CreatedDate
    private Date createdAt;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "UPDATED_AT")
    @LastModifiedDate
    private Date updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
    }

}
