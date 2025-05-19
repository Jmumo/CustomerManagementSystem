package com.Jmumo.CustomerService.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID",unique = true, nullable = false,updatable = false)
    private Long customerId;
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;
    @Column(name = "SECOND_NAME", nullable = false)
    private String secondName;
    @Column(name = "OTHER_NAME")
    private String otherNames;
}
