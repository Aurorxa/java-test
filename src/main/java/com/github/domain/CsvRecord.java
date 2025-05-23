package com.github.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonPropertyOrder(value = {"id", "event_time",
        "order_id", "product_id",
        "category_id", "category_code",
        "brand", "price",
        "user_id", "age",
        "sex", "local"})
public class CsvRecord {

    @JsonProperty("id")
    private String id;

    @JsonProperty("event_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    private LocalDateTime eventTime;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("category_id")
    private String categoryId;

    @JsonProperty("category_code")
    private String categoryCode;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("price")
    private double price;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("age")
    private double age;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("local")
    private String local;

}