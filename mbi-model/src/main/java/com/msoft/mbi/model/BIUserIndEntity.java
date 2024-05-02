package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_user_indicator", schema = "biserver", catalog = "biserver")
@IdClass(BIUserIndPK.class)
public class BIUserIndEntity {

    @Id @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Id @Basic
    @Column(name = "indicator_id", nullable = false)
    private Integer indicatorId;

    @Basic
    @Column(name = "can_change", nullable = false)
    private boolean canChange;

    @Basic
    @Column(name = "is_favorite", nullable = false)
    private boolean isFavorite;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIUserEntity biUserByUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIUserIndEntity )) return false;
        return biIndByInd.getId().equals(((BIUserIndEntity) o).biIndByInd.getId())
                && biUserByUser.getId().equals(((BIUserIndEntity) o).biUserByUser.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
