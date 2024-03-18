package com.msoft.mbi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.msoft.mbi.model.support.Cuid;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bi_multitenant_model")
public class BIMultitenantModelEntity {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

    @Column(nullable = false, columnDefinition = "text")
    private String tenant;

    public BIMultitenantModelEntity(String tenant) {
        this.tenant = tenant;
    }

    @PrePersist
    protected void prePersist() {
        id = UUID.fromString(Cuid.createCuid());
        createdAt = Instant.now();
    }
}
