package com.tuconnect.dorm_connect.dto;

import lombok.Data;

@Data
public class DormDTO {
    private Long id;
    private String name;
    private String address;
    private String blockNumber;
    private String amenitiesJson;
    private Double price;
}
