package com.msoft.mbi.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BiSlideshowPainelEntityPK implements Serializable {

    @Column(name = "slideshow", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int slideshow;
    @Column(name = "painel", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int painel;

}
