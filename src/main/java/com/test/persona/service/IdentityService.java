package com.test.persona.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.test.persona.model.Persona;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IdentityService {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String RANDOMUSER_API = "https://randomuser.me/api/";

    public Persona generatePersona(String gender) {
        String url = UriComponentsBuilder.fromHttpUrl(RANDOMUSER_API)
                .queryParam("gender", gender.equalsIgnoreCase("femme") ? "female" : "male")
                .queryParam("nat", "fr")
                .build()
                .toUriString();

        Map response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> user = ((List<Map<String, Object>>) response.get("results")).get(0);

        Map<String, String> name = (Map<String, String>) user.get("name");
        String firstName = name.get("first");
        String lastName = name.get("last");

        String dobStr = (String) ((Map<String, Object>) user.get("dob")).get("date");
        LocalDate birthDate = LocalDate.parse(dobStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Persona p = new Persona();
        p.setId(UUID.randomUUID().toString());
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setBirthDate(birthDate);
        p.setGender(gender); // conserver le genre saisi (Homme/Femme)
        return p;
    }
}