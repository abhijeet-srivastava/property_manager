package com.twisha.property_manager.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Slot {

    @JsonProperty
    private UUID propertyId;
    @JsonProperty
    private LocalDateTime timestamp;

    @JsonProperty
    private Integer availableCount;

}
