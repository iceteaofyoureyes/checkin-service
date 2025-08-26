package com.wiinvent.checkinservice.service;

import com.wiinvent.checkinservice.dto.CheckinConfigDto;

public interface CheckinConfigService {

    CheckinConfigDto getActiveConfig();
    int resolvePointForNth(int NthDate);
}
