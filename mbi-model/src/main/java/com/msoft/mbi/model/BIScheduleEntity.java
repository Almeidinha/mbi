package com.msoft.mbi.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "bi_schedule", schema = "biserver", catalog = "biserver")
public class BIScheduleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "indicator_id", nullable = false)
    private int indicatorId;

    @Basic
    @Column(name = "periodic_delivery")
    private boolean isPeriodicDelivery;

    @Basic
    @Column(name = "recipient", length = 200)
    private String recipient;

    @Basic
    @Column(name = "delivery_time")
    private Date deliveryTime;

    @Basic
    @Column(name = "start_delivery_date")
    private Date startDeliveryDate;

    @Basic
    @Column(name = "end_delivery_date")
    private Date endDeliveryDate;

    @Basic
    @Column(name = "delivery_amount")
    private Integer deliveryAmount;

    @Basic
    @Column(name = "delivered_amount")
    private Integer deliveredAmount;

    @Basic
    @Column(name = "delivery_frequency")
    private Integer deliveryFrequency;

    @Basic
    @Column(name = "sunday_deliver")
    private boolean sundayDeliver;

    @Basic
    @Column(name = "monday_deliver")
    private boolean mondayDeliver;

    @Basic
    @Column(name = "tuesday_deliver")
    private boolean tuesdayDeliver;

    @Basic
    @Column(name = "wednesday_deliver")
    private boolean wednesdayDeliver;

    @Basic
    @Column(name = "thursday_deliver")
    private boolean thursdayDeliver;

    @Basic
    @Column(name = "friday_deliver")
    private boolean fridayDeliver;

    @Basic
    @Column(name = "saturday_deliver")
    private boolean saturdayDeliver;

    @Basic
    @Column(name = "delivery_day")
    private Short deliveryDay;

    @Basic
    @Column(name = "delivery_week")
    private Short deliveryWeek;

    @Basic
    @Column(name = "delivery_month")
    private Short deliveryMonth;

    @Basic
    @Column(name = "user", length = 8)
    private String user;

    @Basic
    @Column(name = "owner")
    private Integer owner;

    @Basic
    @Column(name = "last_date_delivered")
    private Date lastDateDelivered;

    @Basic
    @Column(name = "send_table")
    private boolean sendTable;

    @Basic
    @Column(name = "graph_type")
    private Short graphType;

    @Basic
    @Column(name = "send_attached_file")
    private boolean sendAttachedFile;

    @Basic
    @Column(name = "send_comment")
    private boolean sendComment;

    @Basic
    @Column(name = "send_attached_spreadsheet", length = 1)
    private String sendAttachedSpreadsheet;

    @Basic
    @Column(name = "schedule_blocked")
    private Integer scheduleBlocked;

    @Basic
    @Column(name = "assembly_sequence", length = 8)
    private String assemblySequence;

    @Basic
    @Column(name = "blocks_empty_email", length = 1)
    private String blocksEmptyEmail;

    @ManyToOne
    @JoinColumn(name = "indicator_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private BIIndEntity biIndByInd;

    @OneToMany(mappedBy = "biScheduleUserBySchedule")
    private Collection<BIUserGroupScheduleEntity> biScheduleGroups;

    @OneToMany(mappedBy = "biScheduleUserBySchedule")
    private Collection<BIUserScheduleEntity> biScheduleUsers;

}
