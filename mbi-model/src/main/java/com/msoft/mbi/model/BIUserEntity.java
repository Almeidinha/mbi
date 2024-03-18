package com.msoft.mbi.model;

import lombok.*;

import jakarta.persistence.*;

import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_user", schema = "dbo", catalog = "BISERVER")
public class BIUserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "password", length = 64)
    private String password;

    @Basic
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Basic
    @Column(name = "last_name", length = 20)
    private String lastName;

    @Basic
    @Column(name = "email", length = 60, nullable = false, unique = true)
    private String email;

    @Basic
    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Boolean isAdmin;

    @Basic
    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive;

    @Basic
    @Column(name = "password_updated", columnDefinition = "boolean default false")
    private Boolean passwordUpdated;

    @Basic
    @Column(name = "email_verified", columnDefinition = "boolean default false")
    private Boolean emailVerified;

    @Basic
    @Column(name = "default_panel")
    private Integer defaultPanel;

    @ManyToOne
    @JoinColumn(name = "user_group_id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private BICompanyEntity biCompany;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIIntercalationEntity> biIntercalationsByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserDocumentsEntity> biUserDocumentsByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIAnalysisParameterEntity> biAnalysisParametersByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIAccessRestrictionInterfaceEntity> biAccessRestrictionInterfacesByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserRestrictionEntity> biUserRestrictionsByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserScheduleEntity> biScheduleUsersByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserInterfaceEntity> biUserInterfacesByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIAnalysisUserAccessEntity> biAnalysisUsersByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserPanelEntity> biUserPanelsByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserIndEntity> biUserIndicatorsByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserDefaultIndicatorEntity> biUserDefaultIndicatorsByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIPanelUserEntity> biPanelUsersByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserDefaultPanelEntity> biUserDefaultPanelByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserRuleEntity> biUserRuleByUser;

    @OneToMany(mappedBy = "biUserByUser")
    private List<BIUserSlideshowDefaultEntity> biUserSlideshowDefaultsByUser;

    public Boolean getAdmin() {
        return this.biUserGroupByUserGroup != null
                && this.biUserGroupByUserGroup.getRoleCode() != null
                && this.biUserGroupByUserGroup.getRoleCode().equals("ADMIN");
    }

}
