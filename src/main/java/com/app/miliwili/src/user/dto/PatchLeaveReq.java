package com.app.miliwili.src.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class PatchLeaveReq {
    private String title;
    private Integer useDays;
    private Integer totalDays;
}