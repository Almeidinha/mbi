package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "bi_user_group_schedule", schema = "biserver", catalog = "biserver")
@IdClass(BIUserGroupSchedulePK.class)
public class BIUserGroupScheduleEntity {

    @Id
    @Column(name = "schedule_id", nullable = false)
    private int scheduleId;

    @Id
    @Column(name = "user_group_id", nullable = false)
    private int userGroupId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id", nullable = false)
    private BIScheduleEntity biScheduleUserBySchedule;

    @ManyToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "id", nullable = false)
    private BIUserGroupEntity biUserGroupByUserGroup;

}
