package com.test.persona.service;

import java.time.LocalDate;
import java.time.Period;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IdentityPhotoService {

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Génère une URL de photo d'identité synthétique cohérente avec le genre et l'âge.
     * @param gender Sexe ("male" ou "female" ou libellés FR)
     * @param birthDate Date de naissance
     * @return URL de photo synthétique non réelle
     */
        public String generateIdentityPhotoUrl(String gender, LocalDate birthDate) {
        if (gender == null || birthDate == null) {
            return null;
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();

        int minAge, maxAge;
        if (age < 18) {
            minAge = Math.max(5, age);
            maxAge = Math.min(17, age + 4);
        } else if (age >= 60) {
            minAge = 60;
            maxAge = 90;
        } else {
            minAge = 18;
            maxAge = 59;
        }

        String apiGender = ("femme".equalsIgnoreCase(gender) || "female".equalsIgnoreCase(gender)) ? "female" : "male";

        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://fakeface.rest/face/json")
                    .queryParam("gender", apiGender)
                    .queryParam("minimum_age", minAge)
                    .queryParam("maximum_age", maxAge)
                    .toUriString();

            @SuppressWarnings("unchecked")
            var response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && response.get("image_url") instanceof String imageUrl && !imageUrl.isEmpty()) {
                return imageUrl;
            }
        } catch (Exception e) {
            // Ignorer erreurs et passer au fallback
        }

        String seed = birthDate.toString() + ":" + apiGender;
        String dicebearUrl = "https://api.dicebear.com/8.x/personas/png?seed="
                + URLEncoder.encode(seed, StandardCharsets.UTF_8)
                + "&size=128";
        return dicebearUrl;
    }
}
