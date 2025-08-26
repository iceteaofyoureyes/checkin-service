package com.wiinvent.checkinservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiinvent.checkinservice.dto.CheckinConfigDto;
import com.wiinvent.checkinservice.dto.CheckinConfigPayload;
import com.wiinvent.checkinservice.dto.TimeWindowDto;
import com.wiinvent.checkinservice.entity.CheckinConfig;
import com.wiinvent.checkinservice.util.JsonUtils;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CheckinConfigMapper {

    default CheckinConfigDto toCheckinConfigDto(CheckinConfig entity) throws JsonProcessingException {
        CheckinConfigPayload payload = JsonUtils.fromJson(entity.getConfig(), CheckinConfigPayload.class);

        List<TimeWindowDto> windows = JsonUtils.fromJson(
                entity.getTimeWindows(),
                new TypeReference<List<TimeWindowDto>>() {
                }
        );

        return CheckinConfigDto.builder()
                .isActive(entity.isActive())
                .monthlyLimitDays(entity.getMonthlyLimitDays())
                .payload(payload)
                .timeWindows(windows)
                .updatedAt(entity.getUpdatedAt())
                .build();

    }
}
