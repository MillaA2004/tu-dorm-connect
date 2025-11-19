package com.tuconnect.dorm_connect.dto;




public record DormDTO (
     Long id,
     String name,
     String address,
     String blockNumber,
     String amenitiesJson,
     Double price
){}
