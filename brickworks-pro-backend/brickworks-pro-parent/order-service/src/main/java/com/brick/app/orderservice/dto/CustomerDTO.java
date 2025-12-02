package com.brick.app.orderservice.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
}
