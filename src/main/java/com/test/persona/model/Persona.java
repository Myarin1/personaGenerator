package com.test.persona.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Persona {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String bank;
    private String agency;
    private String iban;
    private String bic;

}
