package com.twisha.property_manager.controller;


import com.twisha.property_manager.model.Property;
import com.twisha.property_manager.model.Slot;
import com.twisha.property_manager.model.SlotBookRequest;
import com.twisha.property_manager.service.PropertyManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class PropertyController {

    private PropertyManagerService service;

    @Autowired
    public PropertyController(PropertyManagerService service) {
        this.service = service;
    }

    @GetMapping("/properties")
    public List<Property> listProperties() {
        return this.service.listProperties();
    }

    @PostMapping("/properties")
    public Property registerProperty(@RequestBody Property propRegReq) {
        return this.service.registerProperty(propRegReq);
    }

    @PostMapping("/properties/{propertyId}/slots")
    public Slot bookSlot(@PathVariable UUID propertyId, @RequestBody  SlotBookRequest slotBookRequest) {
        return this.service.bookSlot(propertyId, slotBookRequest);
    }

    @GetMapping("/properties/{propertyId}/slots")
    public List<Slot> findAvaialableSlotForProperty(@PathVariable UUID propertyId) {
        return this.service.findAvailableSlotsForProperty(propertyId);
    }

    @GetMapping("/properties/slots")
    public Map<LocalDateTime, List<Slot>> findAllAvaialableSlots() {
        return this.service.findAllAvailableSlots();
    }
}
