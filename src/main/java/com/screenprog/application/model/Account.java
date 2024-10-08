package com.screenprog.application.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
public class Account {
    @Id
    @SequenceGenerator(
            name = "account_sequence",
            sequenceName = "account_sequence",
            initialValue = 1001,
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_sequence"
    )
    private Long accountNumber;

    @ManyToOne
    @JoinColumn(
            name = "customer_id"
    )
    @JsonBackReference
    private Customer customer;

    private Double balance;

    @CreatedDate
    @Column(
          updatable = false
    )
    private LocalDateTime openDate;

    private LocalDateTime lastUpdateAt;


    @Column(columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Transaction> transactions;

    @PreUpdate
    protected void onUpdate(){
        lastUpdateAt = LocalDateTime.now();
    }
}

enum AccountType{
    CURRENT, SAVING
}
enum Status {
    ACTIVE, CLOSED, SUSPENDED, PENDING
}
