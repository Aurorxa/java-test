package com.github.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// @JsonPropertyOrder({ })
public class CsvRecord {

    private String id;

    @JsonProperty("event_time")
    private String eventTime;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("category_code")
    private String categoryCode;

    private String brand;
    private double price;

    @JsonProperty("user_id")
    private String userId;

    private double age;
    private String sex;
    private String local;

}