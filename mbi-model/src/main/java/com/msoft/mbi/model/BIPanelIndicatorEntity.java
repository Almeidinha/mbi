package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_panel_indicator", schema = "biserver", catalog = "BISERVER")
@IdClass(BIPanelIndicatorPK.class)
public class BIPanelIndicatorEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "panel_id", nullable = false)
    private int panelId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "graph_id", nullable = false)
    private int graphId;

    @Basic
    @Column(name = "left_coords", nullable = false)
    private int leftCoords;

    @Basic
    @Column(name = "top_coords", nullable = false)
    private int topCoords;

    @Basic
    @Column(name = "window_width", nullable = false)
    private int windowWidth;

    @Basic
    @Column(name = "window_height", nullable = false)
    private int windowHeight;

    @Basic
    @Column(name = "is_open", nullable = false, length = 1)
    private boolean isOpen;

    @Basic
    @Column(name = "is_maximized", nullable = false, length = 1)
    private String isMaximized;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

    @ManyToOne
    @JoinColumn(name = "panel_id", referencedColumnName = "id", nullable = false)
    private BIPanelEntity biPanelByPanel;

}
