package com.screenprog.application.controller;

import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.email_service.OtpService;
import com.screenprog.application.model.*;
import com.screenprog.application.service.CenteralisedService;
import com.screenprog.application.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CenteralisedService service;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @PostMapping("email")
    private ResponseEntity<Map<String, String>> email(@RequestBody EmailRequest emailRequest){
        Map<String, String> response = new HashMap<>();
        response.put("message", emailService.sendOTP(emailRequest.email()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("verify-email")
    private ResponseEntity<String> verifyOTP(@RequestParam("email") String email,@RequestParam("otp") String otp){
        if(otpService.verifyOtp(email, otp))
            return ResponseEntity.ok(otpService.clearOtp(email));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid OTP");
    }

    /*DONE*/
    @PostMapping("register")
    private ResponseEntity<Map<String, String>> applyToBeACustomer(@ModelAttribute ApplicationDTO applicationDTO,
                                                      @RequestParam("signature") MultipartFile signature,
                                                      @RequestParam("image") MultipartFile image,
                                                      @RequestParam("card") MultipartFile card
                                                      ){
        LOGGER.info("Inside method");
        try{
            String s = userService.applyCustomer(applicationDTO, image, card, signature);
            Map<String, String> response = new HashMap<>();
            response.put("message", s);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("dashboard")
    private ResponseEntity<Customer> dashboard(@RequestParam Long id){
        return userService.getCustomer(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    /*TODO: Test this end point :o DONE*/
    /*TODO: This endpoint is working well*/
    @PostMapping("open-account")
    private ResponseEntity<Account> openAccount(@RequestBody AccountDTO accountDTO){
        LOGGER.info("In openAccount");
            return ResponseEntity.ok(userService.openAccount(accountDTO));
    }


    @GetMapping("check-balance")
    private ResponseEntity<String> checkBalance(@RequestParam Long accountNumber){
        Double balance = userService.getBalance(accountNumber);
        if(balance == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account number is incorrect");
        return ResponseEntity.ok(balance.toString());
    }

    /*TODO: This end-point might get suspension */
    @PutMapping("withdraw")
    private ResponseEntity<Transaction> withdraw(@RequestBody WithdrawDTO withdrawDTO){
        Transaction transaction = userService.withdraw(withdrawDTO);
        if( transaction == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

    /*TODO: This end-point might get suspension*/
    @PostMapping("deposit")
    public ResponseEntity<String> deposit(@RequestBody WithdrawDTO withdrawDTO){
        try{
            Transaction transaction = userService.deposit(withdrawDTO);
            if(transaction == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok("Deposit Successful");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /*TODO: Test this endpoint and it's functionality o: DONE*/
    /*This end-point is working well*/
    @PutMapping("transfer")
    private ResponseEntity<Transaction> transferAmount(@RequestBody TransferDTO transferDTO){
        Transaction transaction = userService.transferAmount(transferDTO);
        if(transaction.getAccountId() == null)
            ResponseEntity.notFound().build();
        return ResponseEntity.ok(transaction);
    }

    /*TODO: Add functionality to check whether the one who is
     * changing the password is the one who is logged in :o DONE*/
    /*THis end-point is working well*/
    @PostMapping("change-password")
    private ResponseEntity<Users> changePassword(@RequestBody ChangePasswordDTO user){
        Users userWithUpdatedPassword = service.changePassword(user);
        if(userWithUpdatedPassword == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userWithUpdatedPassword);
    }

    /*TODO: Test this end point :o DONE*/
    /*test passed*/
    @GetMapping("transaction-history")// ?accountNumber=************
    private ResponseEntity<List<Transaction>> transactions(@RequestParam("id") Long accountNumber){
        LOGGER.info("In transaction history");
        return ResponseEntity.ok(userService.getTransactionHistory(accountNumber));
    }

}
