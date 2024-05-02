package com.msoft.mbi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_user_group_indicator", schema = "biserver", catalog = "biserver")
@IdClass(BIUserGroupIndicatorPK.class)
public class BIUserGroupIndEntity {

    @Id @Basic
    @Column(name = "indicator_id", nullable = false)
    private Integer indicatorId;

    @Id @Basic
    @Column(name = "user_group_id", nullable = false)
    private Integer userGroupId;

    @Basic
    @Column(name = "can_edit", nullable = false)
    private boolean canEdit;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIUserGroupIndEntity )) return false;
        return biIndByInd.getId().equals(((BIUserGroupIndEntity) o).getBiIndByInd().getId())
                && biUserGroupByUserGroup.getId().equals(((BIUserGroupIndEntity) o).getBiUserGroupByUserGroup().getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
