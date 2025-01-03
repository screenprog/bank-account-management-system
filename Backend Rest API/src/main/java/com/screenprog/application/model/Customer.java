package com.screenprog.application.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.screenprog.application.email_service.EmailDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_seq",
            allocationSize = 1,
            initialValue = 1001
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_seq"
    )
    private Long customerID;

    @Column(
            name = "first_name",
            columnDefinition = "VARCHAR(10)"
    )
    private String firstName;

    @Column(
            name = "last_name",
            columnDefinition = "VARCHAR(10)"
    )
    private String lastName;

    @Column(
            columnDefinition = "DATE"
    )
    private LocalDate dob;

    @Column(
            columnDefinition = "TEXT"
    )
    private String address;

    @Column(
            name = "contact_number",
            columnDefinition = "VARCHAR(15)"
    )
    private String phoneNumber;

    @Column(
            columnDefinition = "VARCHAR(40)",
            unique = true
    )
    private String email;

    private String password;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Account> account;

    @CreatedDate
    @Column(
            updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(
            columnDefinition = "TIMESTAMP WITH TIME ZONE"
    )
    private LocalDateTime lastModifiedAt;
    @Lob
    private byte[] image;

    public Customer(String firstName, String lastName, LocalDate dob, String address, String password, String phoneNumber, String email, byte[] image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.address = address;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.image = image;
    }

    public Users toUser() {
        return new Users(null,this.customerID.toString(), this.password, List.of("USER"));
    }

    public EmailDTO toApplicationVerifiedEmail() {
        return new EmailDTO(email, "Application Verified",
                String.format("""
                             Dear %s,
                                 Congratulations! Your application has been verified and now
                                 you are our customer, you can create your account and start
                                 using our services.
                                 Your customer id is %s
                                 Best Regards!",
                                 We look forward to serving you."
                             """
                                ,firstName, customerID.toString()));
    }

    public EmailDTO toCustomerEmail() {
        return new EmailDTO(email, "Customer Verified",
                String.format("""
                        Dear %s,
                            Congratulations! For your customer verification,
                            Login into your account and start using our services,
                            Make sure to change your default password.,
                            And create your account to start using our services.,
                            Here is your customer id: %s,
                            Best Regards!,
                            We look forward to serving you.
                        """, firstName, customerID.toString()));
    }

    public EmailDTO toAccountOpenedEmailDTO() {
        Long acId = null;
        if(account.size() == 1)
            acId = account.getFirst().getAccountNumber();
        else
            acId = account.getLast().getAccountNumber();
        String message = """
                Dear %s,
                    Your account has been opened successfully and is ready to use.
                    Now you are able to use services but make sure to deposit
                    an amount in you account to start using our services.
                    Here is your account id: %s
                    Bank Staff
                """;
        return new EmailDTO(email, "Account Opened", String.format(message,firstName, acId.toString()));
    }


//    public Users toUser() {
//        return new Users(this.customerID.toString(), this.password, List.of("USER"));
//    }

//    @Column(
//            name = "privileges",
//            columnDefinition = "VARCHAR(20)"
//    )
//    private Privilege privilege = USER;

}
