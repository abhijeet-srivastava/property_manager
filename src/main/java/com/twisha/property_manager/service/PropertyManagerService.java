package com.twisha.property_manager.service;


import com.twisha.property_manager.model.Property;
import com.twisha.property_manager.model.Slot;
import com.twisha.property_manager.model.SlotBookRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PropertyManagerService {

    private static final LocalTime SLOT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime LAST_SLOT_START_TIME = LocalTime.of(17, 30);
    private static final LocalTime SLOT_END_TIME = LocalTime.of(18, 0);

    private static final Long SLOT_DURATION_MINS = 30L;
    private static final Long SLOT_PRE_REGISTER_DAYS = 3L;

    private Map<UUID, Property> propertyRepo;

    public PropertyManagerService() {
        this.propertyRepo = new HashMap<>();
    }

    //List all Properties - {"id", "Name", "desc"}
    // Register a property - Req{"name", ""/desc} resp - {"id", "Name", "desc"}
    // find Slots for a property - List<Slot> - {datetime, propertyid, avaialbleCount}
    // find Slots of all the property - datetime: {propertyid: slot}
    // Book a Slot: propertyid, datetime, userid

    public List<Property> listProperties() {
        return propertyRepo.values().stream().toList();
    }

    public Property registerProperty(Property registerRequest) {
        Property prop =  new Property(registerRequest.getName(), registerRequest.getDescription());
        this.propertyRepo.put(prop.getPropertyId(), prop);
        return prop;
    }

    public Slot bookSlot(UUID propertyId, SlotBookRequest bookRequest) {
        if(!validBookingRequest(propertyId, bookRequest)) {
            throw new IllegalArgumentException("Invalid slot booking request");
        }
        Property property = this.propertyRepo.get(propertyId);
        property.bookSlot(bookRequest.getStartTime(), bookRequest.getUserId());
        return Slot.builder()
                .propertyId(property.getPropertyId())
                .timestamp(bookRequest.getStartTime())
                .build();
    }

    private boolean validBookingRequest(UUID propertyId, SlotBookRequest bookRequest) {
        if(!this.propertyRepo.containsKey(propertyId)) {
            return false;
        }
        Integer mins = bookRequest.getStartTime().getMinute();
        if(!(mins == 0 || mins == 30)) {
            return false;
        }
        LocalTime requestedStartTime = bookRequest.getStartTime().toLocalTime();
        if(requestedStartTime.isBefore(SLOT_START_TIME) || requestedStartTime.isAfter(LAST_SLOT_START_TIME)) {
            return false;
        }
        LocalDateTime currTime = LocalDateTime.now();
        LocalDateTime lastSlotStartTime = currTime.plusDays(SLOT_PRE_REGISTER_DAYS).toLocalDate().atTime(LAST_SLOT_START_TIME);
        if(bookRequest.getStartTime().isBefore(currTime)
        || bookRequest.getStartTime().isAfter(lastSlotStartTime)) {
            return false;
        }
        Property property = this.propertyRepo.get(propertyId);
        if(!property.isSlotAvailable(bookRequest.getStartTime())) {
            return false;
        }
        return true;
    }

    public List<Slot> findAvailableSlotsForProperty(UUID propertyId) {
        if(!propertyRepo.containsKey(propertyId)) {
            throw new IllegalArgumentException("Unable to find property");
        }
        List<Slot> availableSlots = new ArrayList<>();
        Property property = this.propertyRepo.get(propertyId);
        property.truncatePastSlots();
        LocalDateTime currTimeStamp = LocalDateTime.now();
        LocalDateTime startTime = roundUpToLatestSlotStart(currTimeStamp);
        LocalDateTime lastSlotEndTime = startTime.plusDays(SLOT_PRE_REGISTER_DAYS).toLocalDate().atTime(SLOT_END_TIME);
        while (startTime.isBefore(lastSlotEndTime)) {
            if(property.isSlotAvailable(startTime)) {
                Slot slot = Slot.builder()
                        .propertyId(propertyId)
                        .timestamp(startTime)
                        .availableCount(property.getSlotAvailability(startTime)).build();
                availableSlots.add(slot);

            }
            startTime = roundUpToLatestSlotStart(startTime.plusMinutes(SLOT_DURATION_MINS));
        }
        return availableSlots;
    }

    public Map<LocalDateTime, List<Slot>> findAllAvailableSlots() {
        Map<LocalDateTime, List<Slot>> availableSlots = new HashMap<>();
        for(var propEntry : this.propertyRepo.entrySet()) {
            List<Slot> propAvailableSlots = this.findAvailableSlotsForProperty(propEntry.getKey());
            for(Slot slot: propAvailableSlots) {
                availableSlots.computeIfAbsent(slot.getTimestamp(), e -> new ArrayList<>()).add(slot);
            }
        }
        return availableSlots;
    }

    private LocalDateTime roundUpToLatestSlotStart(LocalDateTime currTimeStamp) {
        LocalTime currTime = currTimeStamp.toLocalTime();
        if(currTime.isBefore(SLOT_START_TIME)) {
            return currTimeStamp.toLocalDate().atTime(SLOT_START_TIME);
        } else if(currTime.plusMinutes(SLOT_DURATION_MINS).isAfter(SLOT_END_TIME)) {
            LocalDate nextDate = currTimeStamp.toLocalDate().plusDays(1l);
            return nextDate.atTime(SLOT_START_TIME);
        }
        int minutes = currTimeStamp.getMinute();
        if(minutes == 0 || minutes == SLOT_DURATION_MINS) {
            return currTimeStamp;
        }
        return currTimeStamp.plusMinutes(SLOT_DURATION_MINS - minutes%SLOT_DURATION_MINS).truncatedTo(ChronoUnit.MINUTES);
    }

}
