package com.paymentchain.customer.dtos;

import com.paymentchain.customer.entities.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerCreationDto {
    @NotBlank(message = "El código del cliente es obligatorio")
    @Size(min = 3, max = 10, message = "El código del cliente debe tener entre 3 y 10 caracteres")
    private String code;

    @NotBlank(message = "El IBA es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{20}$", message = "El IBA debe tener el formato correcto (2 letras y 20 números)")
    private String iba;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 50, message = "Los nombres deben tener entre 3 y 50 caracteres")
    private String names;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 3, max = 50, message = "Los apellidos deben tener entre 3 y 50 caracteres")
    private String surnames;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
    private String phone;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 255, message = "La dirección debe tener entre 5 y 255 caracteres")
    private String address;

    @NotNull(message = "La lista de productos es obligatoria")
    @Size(min = 1, message = "Debe incluir al menos un producto")
    private List<CustomerProductDto> products;
}
