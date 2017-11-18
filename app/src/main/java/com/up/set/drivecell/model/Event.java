package com.up.set.drivecell.model;

/**
 * Created by amush on 12-Nov-17.
 */

public class Event {
    private String eventDescription,eventName,eventPostDate,eventPostTime,eventType,eventUploader;
    private Double eventLatitude,eventLongitude;

    public Event()
    {

    }

    public Event(String eventDescription,Double eventLatitude, Double eventLongitude, String eventName, String eventPostDate, String eventPostTime, String eventType, String eventUploader) {
        this.eventDescription = eventDescription;
        this.eventLatitude = eventLatitude;
        this.eventLongitude = eventLongitude;
        this.eventName = eventName;
        this.eventPostDate = eventPostDate;
        this.eventPostTime = eventPostTime;
        this.eventType = eventType;
        this.eventUploader = eventUploader;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Double getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(Double eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public Double getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(Double eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPostDate() {
        return eventPostDate;
    }

    public void setEventPostDate(String eventPostDate) {
        this.eventPostDate = eventPostDate;
    }

    public String getEventPostTime() {
        return eventPostTime;
    }

    public void setEventPostTime(String eventPostTime) {
        this.eventPostTime = eventPostTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventUploader() {
        return eventUploader;
    }

    public void setEventUploader(String eventUploader) {
        this.eventUploader = eventUploader;
    }
}
