package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.response.CheckinResponse;
import com.wiinvent.checkinservice.dto.response.MonthCheckinResponse;

import java.time.ZoneId;

public interface CheckinService {
    CheckinResponse checkin(String username, ZoneId userZone);
    MonthCheckinResponse getMonthCheckins(String username, ZoneId userZone);
}
