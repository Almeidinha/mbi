package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_user_group", schema = "biserver", catalog = "biserver")
public class BIUserGroupEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Basic
    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "role_code", nullable = false)
    private String roleCode;

    @ManyToMany
    @JoinTable(
            name = "user_group_company",
            joinColumns = @JoinColumn(name = "user_group_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id"))
    private List<BICompanyEntity> biCompanies;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupDocumentsEntity> biUserGroupDocumentsByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupInterfaceEntity> biUserGroupInterfacesByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupScheduleEntity> biUserGroupSchedulesByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupPanelEntity> biUserGroupPanelsByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupSlideshowDefaultEntity> biUserGroupSlideshowDefaultsByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupIndEntity> biUserGroupIndAccessByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupDefaultPanelEntity> biUserGroupDefaultPanelsByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIGroupRuleEntity> biAreaRulesByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIAccessRestrictionInterfaceEntity> biAccessRestrictionInterfacesByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserGroupRestrictionEntity> biUserGroupRestrictionsByUserGroup;

    @OneToMany(mappedBy = "biUserGroupByUserGroup")
    private List<BIUserEntity> biUserByUserGroup;

    public void addCompany(BICompanyEntity entity) {
        if (this.biCompanies == null) {
            this.biCompanies = new ArrayList<>();
        }
        entity.setBiUserGroups(new ArrayList<>(List.of(this)));
        this.biCompanies.add(entity);

    }

}
