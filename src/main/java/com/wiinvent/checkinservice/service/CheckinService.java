package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.response.CheckinResponse;

import java.time.ZoneId;

public interface CheckinService {
    CheckinResponse checkin(Long userId, ZoneId userZone);
}
