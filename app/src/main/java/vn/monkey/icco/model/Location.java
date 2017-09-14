package vn.monkey.icco.model;

import java.util.List;

/**
 * Created by Mlwayz on 5/29/2017.
 */

public class Location {
    private long id;
    private double latitude;
    private double longitude;
    private String title;
    private String locationLv1;
    private String locationLv2;
    private String wtImage;
    private String wtTemp;
    private String wtDescription;
    private String temp;
    private String humidity;
    private String soil;
    private String amoutOfRain;
    private String fertility;
    private String windSpeed;
    private String windDirection;
    private Long timestamp;
    private Integer wndspd;
    private Integer tMin;
    private Integer tMax;
    private Integer precipitation;

    private List<Location> events;
    private List<Location> histories;

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public Location() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocationLv1() {
        return locationLv1;
    }

    public void setLocationLv1(String locationLv1) {
        this.locationLv1 = locationLv1;
    }

    public String getLocationLv2() {
        return locationLv2;
    }

    public void setLocationLv2(String locationLv2) {
        this.locationLv2 = locationLv2;
    }

    public String getWtImage() {
        return wtImage;
    }

    public void setWtImage(String wtImage) {
        this.wtImage = wtImage;
    }

    public String getWtTemp() {
        return wtTemp;
    }

    public void setWtTemp(String wtTemp) {
        this.wtTemp = wtTemp;
    }

    public String getWtDescription() {
        return wtDescription;
    }

    public void setWtDescription(String wtDescription) {
        this.wtDescription = wtDescription;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getAmoutOfRain() {
        return amoutOfRain;
    }

    public void setAmoutOfRain(String amoutOfRain) {
        this.amoutOfRain = amoutOfRain;
    }

    public String getSoil() {
        return soil;
    }

    public void setSoil(String soil) {
        this.soil = soil;
    }

    public String getFertility() {
        return fertility;
    }

    public void setFertility(String fertility) {
        this.fertility = fertility;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Location> getEvents() {
        return events;
    }

    public void setEvents(List<Location> events) {
        this.events = events;
    }

    public Integer gettMin() {
        return tMin;
    }

    public void settMin(Integer tMin) {
        this.tMin = tMin;
    }

    public Integer gettMax() {
        return tMax;
    }

    public void settMax(Integer tMax) {
        this.tMax = tMax;
    }

    public Integer getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Integer precipitation) {
        this.precipitation = precipitation;
    }

    public List<Location> getHistories() {
        return histories;
    }

    public void setHistories(List<Location> histories) {
        this.histories = histories;
    }

    public Integer getWndspd() {
        return wndspd;
    }

    public void setWndspd(Integer wndspd) {
        this.wndspd = wndspd;
    }
}
