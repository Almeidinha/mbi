package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(
        name = "bi_cnf_graph_ind", schema = "biserver", catalog = "biserver",
        indexes = {
                @Index(name = "ix1_bi_cnf_graf_in", columnList = "indicator_id"),
                @Index(name = "ix2_bi_cnf_graf_in", columnList = "graph_id")
        }
)
@IdClass(BICnfGraphIndPK.class)
public class BICnfGraphIndEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "graph_id", nullable = false)
    private int graphId;

    @Basic
    @Column(name = "graph_title", length = 100)
    private String graphTitle;

    @Basic
    @Column(name = "x_axis_title", length = 100)
    private String xAxisTitle;

    @Basic
    @Column(name = "y_axis_title", length = 100)
    private String yAxisTitle;

    @Basic
    @Column(name = "has_legend")
    private boolean hasLegend;

    @Basic
    @Column(name = "legend_position")
    private Integer legendPosition;

    @Basic
    @Column(name = "graph_size")
    private Integer graphSize;

    @Basic
    @Column(name = "lim_max_graph")
    private Integer limMaxGraph;

    @Basic
    @Column(name = "lim_min_graph")
    private Integer limMinGraph;

    @Basic
    @Column(name = "scale_interval")
    private Integer scaleInterval;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false)
    private BIIndEntity biIndByInd;

}
