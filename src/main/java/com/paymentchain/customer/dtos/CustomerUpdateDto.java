package com.paymentchain.customer.dtos;

import jakarta.validation.constraints.NotNull;

public class CustomerUpdateDto extends CustomerCreationDto {
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long id;
}
