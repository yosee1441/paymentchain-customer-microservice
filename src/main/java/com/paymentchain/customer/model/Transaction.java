package com.paymentchain.customer.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Long id;
    private String reference;
    private String accountIban;
    private LocalDateTime date;
    private Double amount;
    private Double fee;
    private String description;
    private String status;
    private String channel;
}
