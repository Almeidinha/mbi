package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_schedule", schema = "dbo", catalog = "BISERVER")
@IdClass(BIScheduleUserPK.class)
public class BIUserScheduleEntity {

    @Id
    @Column(name = "schedule_id", nullable = false)
    private int scheduleId;

    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id", nullable = false)
    private BIScheduleEntity biScheduleUserBySchedule;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private BIUserEntity biUserByUser;

}
