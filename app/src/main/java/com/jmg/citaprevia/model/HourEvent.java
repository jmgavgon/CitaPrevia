package com.jmg.citaprevia.model;

import java.io.Serializable;
import java.time.LocalTime;

public class HourEvent {
    LocalTime time;
    Appointment appointment;
    boolean blocked;

    public HourEvent(LocalTime time, Appointment appointment, boolean blocked) {
        this.time = time;
        this.appointment = appointment;
        this.blocked = blocked;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}