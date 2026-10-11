package com.farmeazy.common.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmAlertEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long farmId;
    private String farmName;
    private Long userId;
    private String userEmail;
    private String userPhone;
    private String ruleName;
    private String sensorType; // SOIL_MOISTURE, TEMPERATURE, HUMIDITY, etc.
    private Object readingValue;
    private String unit;
    private String severity; // INFO, WARNING, CRITICAL
    private String alertMessage;
    private String idempotencyKey;
}
