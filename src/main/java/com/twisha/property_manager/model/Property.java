package com.twisha.property_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {
    private static final Integer MAX_CONCURRENT_BOOKING = 2;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID propertyId;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;

    @JsonIgnore
    private Map<LocalDateTime, List<String>> bookedSlots;

    public Property(String name, String description) {
        this.propertyId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.bookedSlots = new HashMap<>();
    }

    public void truncatePastSlots() {
        LocalDateTime currTime = LocalDateTime.now();
        Iterator<Map.Entry<LocalDateTime, List<String>>> itr = this.bookedSlots.entrySet().iterator();
        while (itr.hasNext()) {
            var entry = itr.next();
            if(entry.getKey().isBefore(currTime)) {
                itr.remove();
            }
        }
    }

    public boolean isSlotAvailable(LocalDateTime startTime) {
        return !this.bookedSlots.containsKey(startTime) || this.bookedSlots.get(startTime).size() < MAX_CONCURRENT_BOOKING;
    }

    public Integer getSlotAvailability(LocalDateTime startTime) {
        return MAX_CONCURRENT_BOOKING - this.bookedSlots.getOrDefault(startTime, Collections.EMPTY_LIST).size();
    }

    public void bookSlot(LocalDateTime startTime, String userId) {
        this.bookedSlots.computeIfAbsent(startTime, e -> new ArrayList<>()).add(userId);
    }
}
