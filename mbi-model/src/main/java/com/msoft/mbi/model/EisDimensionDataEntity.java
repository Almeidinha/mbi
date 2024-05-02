package com.msoft.mbi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "eis_dimension_data", schema = "biserver", catalog = "biserver")
public class EisDimensionDataEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "ano_dat", nullable = false)
    private int anoDat;

    @Basic
    @Column(name = "mes_extenso", nullable = false, length = 9)
    private String mesExtenso;

    @Basic
    @Column(name = "mes_abrev", nullable = false, length = 3)
    private String mesAbrev;

    @Basic
    @Column(name = "num_mes", nullable = false)
    private int numMes;

    @Basic
    @Column(name = "dia_dat", nullable = false)
    private int diaDat;

    @Basic
    @Column(name = "ano_mes_dat", length = 7)
    private String anoMesDat;

    @Basic
    @Column(name = "num_semestre", nullable = false)
    private int numSemestre;

    @Basic
    @Column(name = "num_trimestre", nullable = false)
    private int numTrimestre;

    @Basic
    @Column(name = "num_bimestre", nullable = false)
    private int numBimestre;

    @Basic
    @Column(name = "dia_semana", nullable = false, length = 13)
    private String diaSemana;

    @Basic
    @Column(name = "num_dia_ano", nullable = false)
    private int numDiaAno;

    @Basic
    @Column(name = "num_semana_ano", nullable = false)
    private int numSemanaAno;

    @Basic
    @Column(name = "num_semana_mes", nullable = false)
    private int numSemanaMes;

    @Basic
    @Column(name = "eh_feriado", nullable = false, length = 1)
    private String ehFeriado;

    @Basic
    @Column(name = "eh_ultimo_dia_mes", nullable = false, length = 1)
    private String ehUltimoDiaMes;

    @Basic
    @Column(name = "num_dia_semana")
    private Integer numDiaSemana;

}
