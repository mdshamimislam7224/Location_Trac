package com.shamim.locationtrac;

public class LocationEvent {
    private static Double latitude;
    private static Double longitude;

    public LocationEvent(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public static Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
