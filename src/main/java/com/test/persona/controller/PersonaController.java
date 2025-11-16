package com.test.persona.controller;

import com.test.persona.form.BankForm;
import com.test.persona.form.PersonaForm;
import com.test.persona.model.Persona;
import com.test.persona.service.BankService;
import com.test.persona.service.IdentityService;
import com.test.persona.service.IdentityPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes("personas")
public class PersonaController {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private BankService bankService;

    @Autowired
    private IdentityPhotoService identityPhotoService;

    @ModelAttribute("personas")
    public List<Persona> personas() {
        return new ArrayList<>();
    }

    @GetMapping("/")
    public String listPersonas(Model model, @ModelAttribute("personas") List<Persona> personas) {
        model.addAttribute("personas", personas);
        return "list";
    }

    @GetMapping("/new-persona")
    public String showCreatePersonaForm(Model model) {
        model.addAttribute("personaForm", new PersonaForm());
        model.addAttribute("genres", List.of("Homme", "Femme"));
        return "new-persona";
    }

    @PostMapping("/create-persona")
    public String createPersona(@ModelAttribute PersonaForm form,
            @ModelAttribute("personas") List<Persona> personas) {
        Persona p = identityService.generatePersona(form.getGenre());
        personas.add(p);
        return "redirect:/";
    }

    @GetMapping("/generate-bank/{id}")
    public String showBankForm(@PathVariable String id,
            @ModelAttribute("personas") List<Persona> personas,
            Model model) {
        Persona p = personas.stream().filter(per -> per.getId().equals(id)).findFirst().orElse(null);
        if (p == null) {
            return "redirect:/";
        }
        model.addAttribute("persona", p);
        model.addAttribute("bankForm", new BankForm());
        model.addAttribute("banks", List.of("BNP", "SG", "CA"));
        model.addAttribute("agencies", List.of("00550", "12345", "67890"));
        return "bank-form";
    }

    @GetMapping("/generate-photo/{id}")
    public String generatePhotoForPersona(@PathVariable String id,
                                          @ModelAttribute("personas") List<Persona> personas) {
        Persona p = personas.stream().filter(per -> per.getId().equals(id)).findFirst().orElse(null);
        if (p == null) {
            return "redirect:/";
        }
        String url = identityPhotoService.generateIdentityPhotoUrl(p.getGender(), p.getBirthDate());
        p.setPhotoUrl(url);
        return "redirect:/";
    }

    @PostMapping("/generate-bank/{id}")
    public String generateBankForPersona(@PathVariable String id,
            @ModelAttribute BankForm bankForm,
            @ModelAttribute("personas") List<Persona> personas,
            Model model) {
        Persona p = personas.stream().filter(per -> per.getId().equals(id)).findFirst().orElse(null);
        if (p == null) {
            return "redirect:/";
        }

        String bankCode = bankService.getBankCode(bankForm.getBank());
        // Utiliser l'identifiant transmis dans la route comme identifiant client
        String fullAccountNumber = bankService.generateFullAccountNumber(
                p.getId(),
                bankCode,
                bankForm.getAgency());

        String iban = bankService.generateIban(
                bankCode,
                bankForm.getAgency(),
                fullAccountNumber);
        String bic = bankService.getBicForBank(bankForm.getBank());

        p.setBank(bankForm.getBank());
        p.setAgency(bankForm.getAgency());
        p.setIban(iban);
        p.setBic(bic);

        model.addAttribute("persona", p);
        return "bank-result";
    }
}
