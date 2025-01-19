package com.paymentchain.customer.controller;

import com.paymentchain.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.paymentchain.customer.entities.Customer;
import reactor.core.publisher.Mono;
import com.paymentchain.customer.dtos.CustomerCreationDto;
import com.paymentchain.customer.dtos.CustomerUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping()
    public List<Customer> findAll(){
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable("id") Long id) {
        return customerService.findById(id);
    }

    @GetMapping("/code/{code}")
    public Mono<ResponseEntity<Customer>> findByCode(@PathVariable("code") String code) {
        return customerService.findByCode(code);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable("id") Long id, @Valid @RequestBody CustomerUpdateDto dto) {
        return customerService.update(id, dto);
    }

    @PostMapping
    public ResponseEntity<Customer> save(@Valid @RequestBody CustomerCreationDto dto) {
        return customerService.save(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return customerService.delete(id);
    }
}
