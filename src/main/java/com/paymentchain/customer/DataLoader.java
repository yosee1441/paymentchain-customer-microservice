package com.paymentchain.customer;

import com.paymentchain.customer.dtos.CustomerCreationDto;
import com.paymentchain.customer.dtos.CustomerProductDto;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.service.CustomerService;
import com.paymentchain.customer.entities.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader {
    @Autowired
    private CustomerService customerService;

    @PostConstruct
    public void loadData() {
        CustomerProductDto product1 = CustomerProductDto.builder()
                .productId(1L)
                .build();

        CustomerProductDto product2 = CustomerProductDto.builder()
                .productId(2L)
                .build();

        CustomerCreationDto customer = CustomerCreationDto.builder()
                .code("01")
                .iba("ES12345678901234567890")
                .names("Mauricio Flor")
                .surnames("string")
                .phone("3017654321")
                .address("string")
                .products(List.of(product1, product2))
                .build();

        product1.setCustomer(customer);
        product2.setCustomer(customer);

        customerService.save(customer);
    }
}
