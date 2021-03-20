package com.app.miliwili.src.calendar.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PostScheduleReq {
    private String color;
    private String distinction;
    private String title;
    private String startDate;
    private String endDate;
    private String repetition;
    private String push;
    private String pushDeviceToken;
    private Long leaveId;
    private List<WorkReq> toDoList;
}