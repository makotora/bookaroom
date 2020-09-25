package com.bookaroom.models;

import java.util.Date;

public class ReservationRequest
{
    private Long listingId;

    private Date checkIn;

    private Date checkOut;

    private Integer numberOfGuests;

    public ReservationRequest()
    {
        super();
    }

    public ReservationRequest(Long listingId, Date checkIn, Date checkOut, Integer numberOfGuests)
    {
        super();
        this.listingId = listingId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
    }

    public Long getListingId()
    {
        return listingId;
    }

    public void setListingId(Long listingId)
    {
        this.listingId = listingId;
    }

    public Date getCheckIn()
    {
        return checkIn;
    }

    public void setCheckIn(Date checkIn)
    {
        this.checkIn = checkIn;
    }

    public Date getCheckOut()
    {
        return checkOut;
    }

    public void setCheckOut(Date checkOut)
    {
        this.checkOut = checkOut;
    }

    public Integer getNumberOfGuests()
    {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests)
    {
        this.numberOfGuests = numberOfGuests;
    }

    @Override
    public String toString()
    {
        return "ReservationRequest [listingId="
                + listingId
                + ", checkIn="
                + checkIn
                + ", checkOut="
                + checkOut
                + ", numberOfGuests="
                + numberOfGuests
                + "]";
    }

}

