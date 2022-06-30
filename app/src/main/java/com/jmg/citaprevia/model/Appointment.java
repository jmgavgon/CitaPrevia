package com.jmg.citaprevia.model;

import com.jmg.citaprevia.R;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Appointment implements Serializable {

    private UUID id;
    private String title;
    private String description;
    private Date date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration;
    private String host;
    private int hour;
    private int minute;

    public Appointment() {
    }

    public Appointment(UUID id, String title, String description, Date date, int duration, String host, int hour, int minute) {
        this.id = id;
        this.title =title;
        this.description = description;
        this.date = date;
        this.duration = duration;
        this.host = host;
        this.hour = hour;
        this.minute = minute;
    }

    public Appointment(String title, String description, Date date, int duration, String host, int hour, int minute) {
        this.id = UUID.randomUUID();
        this.title =title;
        this.description = description;
        this.date = date;
        this.duration = duration;
        this.host = host;
        this.hour = hour;
        this.minute = minute;
    }

    public LocalDateTime getStartTime(){
        if(this.startTime == null){
            this.startTime = this.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().atTime(this.hour, this.minute);
        }
        return this.startTime;
    }

    public LocalDateTime getEndTime(){
        if(this.endTime == null){
            this.endTime = getStartTime().plusMinutes(duration);
        }
        return this.endTime;
    }
    public UUID getId(){
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getHost() {
        return host;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


}
