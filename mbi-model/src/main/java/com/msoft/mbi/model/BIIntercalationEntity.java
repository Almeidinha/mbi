package com.msoft.mbi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "bi_intercalation", schema = "biserver", catalog = "biserver")
public class BIIntercalationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Basic
    @Column(name = "intercalation_time", nullable = false)
    private int intercalationTime;

    @OneToMany(mappedBy = "biIntercalationByIntercalation")
    private Collection<BIPanelIntercalationEntity> biInterPanelsByIntercalation;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIUserEntity biUserByUser;

}
