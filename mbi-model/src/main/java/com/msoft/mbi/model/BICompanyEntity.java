package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bi_company", schema = "biserver", catalog = "biserver")
public class BICompanyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "name", nullable = false)
    private String name;

    @Basic
    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Basic
    @Column(name = "phone")
    private String phone;

    @Basic
    @Column(name = "address")
    private String address;

    @ManyToMany(mappedBy = "biCompanies")
    private List<BIUserGroupEntity> biUserGroups;

    @OneToMany(mappedBy = "biCompany")
    private List<BIUserEntity> biUsers;

}
