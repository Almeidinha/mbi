package com.msoft.mbi.data.api.dtos.schedule;

import com.msoft.mbi.data.api.dtos.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BIScheduleDTO extends BaseDTO {

    private Integer id;

    private int indicatorId;

    private boolean isPeriodicDelivery;

    private String recipient;

    private Date deliveryTime;

    private Date startDeliveryDate;

    private Date endDeliveryDate;

    private Integer deliveryAmount;

    private Integer deliveredAmount;

    private Integer deliveryFrequency;

    private boolean sundayDeliver;

    private boolean mondayDeliver;

    private boolean tuesdayDeliver;

    private boolean wednesdayDeliver;

    private boolean thursdayDeliver;

    private boolean fridayDeliver;

    private boolean saturdayDeliver;

    private Short deliveryDay;

    private Short deliveryWeek;

    private Short deliveryMonth;

    private String user;

    private Integer owner;

    private Date lastDateDelivered;

    private boolean sendTable;

    private Short graphType;

    private boolean sendAttachedFile;

    private boolean sendComment;

    private String sendAttachedSpreadsheet;

    private Integer scheduleBlocked;

    private String assemblySequence;

    private String blocksEmptyEmail;

    private List<BIUserGroupScheduleDTO> biScheduleGroups;

    private List<BIUserScheduleDTO> biScheduleUsers;

}
