package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.CheckinConfigDto;

import java.time.LocalTime;

public interface CheckinConfigService {

    CheckinConfigDto getActiveConfig();
    int resolvePointForNth(int NthDate);
    boolean isWithinTimeWindow(LocalTime utcTimeFromUserZone);
}
