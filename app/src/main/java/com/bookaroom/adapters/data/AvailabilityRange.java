package com.bookaroom.adapters.data;

import com.bookaroom.interfaces.RequestStringConvertible;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class AvailabilityRange implements RequestStringConvertible {
    private Date from;
    private Date to;

    public AvailabilityRange() {}

    public AvailabilityRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public String toRequestString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(from) + "," + new SimpleDateFormat("dd" +
                                                                                                    "/MM/yyyy").format(to);
    }
}
