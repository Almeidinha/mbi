package com.msoft.mbi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BiAnalysisFieldId implements Serializable {

    @Serial
    private static final long serialVersionUID = -7474830327799591493L;

    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "field_id", nullable = false)
    private Integer fieldId;

    @NotNull
    @Column(name = "indicator_id", nullable = false)
    private Integer indicatorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BiAnalysisFieldId entity = (BiAnalysisFieldId) o;
        return Objects.equals(this.indicatorId, entity.indicatorId) &&
                Objects.equals(this.fieldId, entity.fieldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indicatorId, fieldId);
    }

}