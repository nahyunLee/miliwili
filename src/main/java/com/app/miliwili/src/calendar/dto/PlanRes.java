package com.app.miliwili.src.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class PlanRes {
    private final Long planId;
    private final String color;
    private final String planType;
    private final String title;
    private final String startDate;
    private final String endDate;
    private final String push;
    private final List<PlanVacationRes> planVacation;
    private final List<WorkRes> work;
}