package com.screenprog.application.service;

import com.screenprog.application.email_service.EmailDTO;
import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.model.*;
import com.screenprog.application.repo.AccountRepository;
import com.screenprog.application.repo.ApplicationsRepository;
import com.screenprog.application.repo.CustomerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {


    @Autowired
    private ApplicationsRepository applicationsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CustomerRepository customerRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public String applyCustomer(ApplicationDTO applyDTO, MultipartFile image, MultipartFile verificationId, MultipartFile signatureImage) {
        Application apply = applyDTO.toApply();
        try {
            apply.setImage(image.getBytes());
            LOGGER.info("image is converted");
            apply.setVerificationId(verificationId.getBytes());
            LOGGER.info("card is converted");
            apply.setSignatureImage(signatureImage.getBytes());
            LOGGER.info("signature is converted");
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert images into bytes");
        }
        applicationsRepository.save(apply);
        emailService.sendEmail(apply.toApplicationReceivedEmailDTO());
        return "Wait for your verification";
    }

    public Double getBalance(Long accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(Account::getBalance)
                .orElse(null);
    }

    @Transactional
    public Account openAccount(AccountDTO accountDto) {
        Optional<Customer> byId = customerRepository.findById(accountDto.customerId());
        if(byId.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");

        Customer customer = byId.get();
        if(customer.getAccount().size() == 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't open more than 2 accounts");
        if (customer.getAccount().size() == 1 && customer.getAccount().getFirst().getType().equals(accountDto.type()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't open more than one account of same type");
        Account account = new Account();
        account.setCustomer(customer);
        account.setBalance(
                accountDto.balance() == null ?
                        accountDto.type().equals(AccountType.SAVING) ? 100.00 : 500.00 :
                        accountDto.balance());
        account.setStatus(accountDto.status() == null ? Status.PENDING : accountDto.status());
        account.setType(accountDto.type());
        Account savedAccount = accountRepository.save(account);
        emailService.sendEmail(savedAccount.toAccountOpenEmailDTO(customer.getEmail()));
        return savedAccount;

    }
}
