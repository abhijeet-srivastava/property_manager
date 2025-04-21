package com.twisha.property_manager.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotBookRequest {

    @JsonProperty
    private LocalDateTime startTime;


    @JsonProperty
    private String userId;
}
