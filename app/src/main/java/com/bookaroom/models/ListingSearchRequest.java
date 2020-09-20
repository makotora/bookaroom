package com.bookaroom.models;

import java.util.Date;

public class ListingSearchRequest {
    private String address;
    private Date checkIn;
    private Date checkOut;
    private Integer numberOfGuests;

    public ListingSearchRequest(
            String address,
            Date checkIn,
            Date checkOut,
            Integer numberOfGuests) {
        this.address = address;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
}
