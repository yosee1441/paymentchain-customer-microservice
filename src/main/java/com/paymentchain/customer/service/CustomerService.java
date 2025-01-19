package com.paymentchain.customer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.dtos.CustomerCreationDto;
import com.paymentchain.customer.dtos.CustomerUpdateDto;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.model.Transaction;
import com.paymentchain.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public ResponseEntity<Customer> findById(Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public Mono<ResponseEntity<Customer>> findByCode(String code) {
        return Mono.justOrEmpty(customerRepository.findByCode(code))
            .flatMap(customer -> {
                return Flux.zip(
                        Flux.fromIterable(customer.getProducts())
                                .flatMap(product ->
                                    findProductNameById(product.getId())
                                        .map(productName -> {
                                            product.setProductName(productName);
                                            return product;
                                        })
                                ).collectList(),
                        findAllTransactions(customer.getIba())
                                .collectList()
                )
                .map(tuple -> {
                    customer.setProducts(tuple.getT1());
                    customer.setTransactions(tuple.getT2());
                    return customer;
                })
                .singleOrEmpty();
            })
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Customer> update(Long id, CustomerUpdateDto dto) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);

        if (existingCustomer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer savedCustomer = existingCustomer.get();
        savedCustomer.setNames(dto.getNames());
        savedCustomer.setPhone(dto.getPhone());
        savedCustomer.setCode(dto.getCode());
        savedCustomer.setSurnames(dto.getSurnames());

        customerRepository.save(savedCustomer);

        return ResponseEntity.ok(savedCustomer);
    }

    public ResponseEntity<Customer> save(CustomerCreationDto dto) {
        Customer customer = Customer.builder()
                .phone(dto.getPhone())
                .names(dto.getNames())
                .address(dto.getAddress())
                .surnames(dto.getSurnames())
                .iba(dto.getIba())
                .code(dto.getCode())
                .build();

        List<CustomerProduct> products = dto.getProducts().stream()
                .map(productDto -> CustomerProduct.builder()
                        .productId(productDto.getProductId())
                        .customer(customer)
                        .build())
                .collect(Collectors.toList());

        customer.setProducts(products);

        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    public ResponseEntity<Void> delete(Long id) {
        if (!customerRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Mono<String> findProductNameById(Long productId) {
        WebClient client = webClientBuilder
                .baseUrl("http://localhost:8082/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.method(HttpMethod.GET)
                .uri("/{id}", productId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error del cliente: " + errorBody)))
                )
                .onStatus(status -> status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error del servidor: " + errorBody)))
                )
                .bodyToMono(JsonNode.class)
                .map(response -> response.get("name").asText())
                .onErrorResume(error -> {
                    System.err.println("Error al recuperar el nombre del producto: " + error.getMessage());
                    return Mono.empty();
                });
    }

    private Flux<Transaction> findAllTransactions(String iban){
        WebClient client = webClientBuilder
                .baseUrl("http://localhost:8083/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
                .path("/customer/transactions")
                .queryParam("ibaAccount", iban)
                .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                String message = String.format("Error del cliente: %s", errorBody);
                                return Mono.error(new RuntimeException(message));
                            });
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                String message = String.format("Error del servidor: %s", errorBody);
                                return Mono.error(new RuntimeException(message));
                            });
                })
                .bodyToFlux(Transaction.class)
                .onErrorResume(error -> {
                    System.err.println("Ocurrió un error al recuperar las transacciones: " + error.getMessage());
                    return Mono.empty();
                });
    }
}
