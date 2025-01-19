package com.paymentchain.customer.dtos;

import com.paymentchain.customer.dtos.CustomerCreationDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerProductDto {
    @NotNull(message = "El ID del product es obligatorio")
    private Long productId;

    private CustomerCreationDto customer;
}
