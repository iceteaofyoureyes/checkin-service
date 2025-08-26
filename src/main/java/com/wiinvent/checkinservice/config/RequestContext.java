package com.wiinvent.checkinservice.config;

import java.time.ZoneId;

public final class RequestContext {
    private static final ThreadLocal<ZoneId> ZONE = new ThreadLocal<>();

    private RequestContext() {}

    public static void setZoneId(ZoneId zoneId) {
        ZONE.set(zoneId);
    }

    public static ZoneId getZoneId() {
        ZoneId z = ZONE.get();
        return z == null ? ZoneId.of("UTC") : z;
    }

    public static void clear() {
        ZONE.remove();
    }
}
