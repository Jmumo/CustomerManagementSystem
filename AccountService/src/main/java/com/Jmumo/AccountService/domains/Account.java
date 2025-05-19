package com.Jmumo.AccountService.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "IBAN", nullable = false, unique = true)
    private String iban;

    @Column(name = "BIC_SWIFT", nullable = false)
    private String bicSwift;

    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long customerId;
}
