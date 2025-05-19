package com.Jmumo.CustomerService.web;

import com.Jmumo.CustomerService.domain.Customer;
import com.Jmumo.CustomerService.domain.Dtos.CustomerCreationDto;
import com.Jmumo.CustomerService.domain.Dtos.CustomerResponseDto;
import com.Jmumo.CustomerService.services.CustomerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {


    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/search")
    public Mono<Page<CustomerResponseDto>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return customerService.searchCustomers(name, startDate,endDate,page, size);
    }

    @PostMapping
    public Mono<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerCreationDto customerCreationDto) {
        return customerService.createCustomer(customerCreationDto);
    }

    @GetMapping("/ByPublicId")
    public Mono<Customer> getCustomer(@RequestParam UUID id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public Mono<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        return customerService.updateCustomer(id, updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id);
    }
}
