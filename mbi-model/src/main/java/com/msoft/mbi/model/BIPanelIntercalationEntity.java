package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_panel_intercalation", schema = "biserver", catalog = "biserver")
@IdClass(BIPanelIntercalationPK.class)
public class BIPanelIntercalationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "intercalation_id", nullable = false)
    private int intercalationId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "panel_id", nullable = false)
    private int panelId;

    @Basic
    @Column(name = "sequence", nullable = false)
    private int sequence;

    @ManyToOne
    @JoinColumn(name = "intercalation_id", referencedColumnName = "id", nullable = false)
    private BIIntercalationEntity biIntercalationByIntercalation;

    @ManyToOne
    @JoinColumn(name = "painel_id", referencedColumnName = "id", nullable = false)
    private BIPanelEntity biPanelByPanel;
}
