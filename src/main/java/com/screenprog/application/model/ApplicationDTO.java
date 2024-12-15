package com.screenprog.application.model;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static com.screenprog.application.security.BCryptEncryption.encoder;

public record ApplicationDTO(
        String firstName,
        String lastName,
        String email,
        String phone,
        String dob,
        Gender gender,
        String address,
        String password)
{


    /*Converting this DTO into Application object to work store it into database*/
    public Application toApplication() {
       return new Application(null,firstName(), lastName(), email(), phone(), gender(), LocalDate.parse(dob()), address(), encoder.encode(password()), null, null, null, ApplicationStatus.PENDING);
    }
}