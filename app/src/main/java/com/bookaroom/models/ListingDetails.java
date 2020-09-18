package com.bookaroom.models;

import com.bookaroom.adapters.data.AvailabilityRange;
import com.bookaroom.enums.ListingType;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ListingDetails {
    private String address;

    private Double longitude;

    private Double latitude;

    private Integer maxGuests;

    private Double minPrice;

    private Double costPerExtraGuest;

    private ListingType type;

    private String rules;

    private String description;

    private Integer numberOfBeds;

    private Integer numberOfBathrooms;

    private Integer numberOfBedrooms;

    private Integer area;

    private boolean hasLivingRoom;

    private ArrayList<AvailabilityRange> availabilityRanges;

    private File mainPicture;

    private ArrayList<File> additionalPictures;

    public ListingDetails(String address, Double longitude, Double latitude, Integer maxGuests, Double minPrice, Double costPerExtraGuest, ListingType type, String rules, String description, Integer numberOfBeds, Integer numberOfBathrooms, Integer numberOfBedrooms,
                          Integer area, boolean hasLivingRoom, ArrayList<AvailabilityRange> availabilityRanges, File mainPicture, ArrayList<File> additionalPictures) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.maxGuests = maxGuests;
        this.minPrice = minPrice;
        this.costPerExtraGuest = costPerExtraGuest;
        this.type = type;
        this.rules = rules;
        this.description = description;
        this.numberOfBeds = numberOfBeds;
        this.numberOfBathrooms = numberOfBathrooms;
        this.numberOfBedrooms = numberOfBedrooms;
        this.area = area;
        this.hasLivingRoom = hasLivingRoom;
        this.availabilityRanges = availabilityRanges;
        this.mainPicture = mainPicture;
        this.additionalPictures = additionalPictures;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getCostPerExtraGuest() {
        return costPerExtraGuest;
    }

    public void setCostPerExtraGuest(Double costPerExtraGuest) {
        this.costPerExtraGuest = costPerExtraGuest;
    }

    public ListingType getType() {
        return type;
    }

    public void setType(ListingType type) {
        this.type = type;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(Integer numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public Integer getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(Integer numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public boolean isHasLivingRoom() {
        return hasLivingRoom;
    }

    public void setHasLivingRoom(boolean hasLivingRoom) {
        this.hasLivingRoom = hasLivingRoom;
    }

    public ArrayList<AvailabilityRange> getAvailabilityRanges() {
        return availabilityRanges;
    }

    public void setAvailabilityRanges(ArrayList<AvailabilityRange> availabilityRanges) {
        this.availabilityRanges = availabilityRanges;
    }

    public File getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(File mainPicture) {
        this.mainPicture = mainPicture;
    }

    public ArrayList<File> getAdditionalPictures() {
        return additionalPictures;
    }

    public void setAdditionalPictures(ArrayList<File> additionalPictures) {
        this.additionalPictures = additionalPictures;
    }
}
