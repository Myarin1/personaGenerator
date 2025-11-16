package com.test.persona.service;

import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class BankService {
    public String getBankCode(String bankKey) {
        return switch (bankKey) {
            case "BNP" -> "30004";
            case "SG" -> "30003";
            case "CA" -> "18206";
            default -> "00000";
        };
    }

    public String computeRibKey(String bankCode, String branchCode, String accountNumber) {
        StringBuilder accConverted = new StringBuilder();
        for (char c : accountNumber.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                accConverted.append((int) c - 64); 
            } else {
                accConverted.append(c);
            }
        }

        BigInteger bank = new BigInteger(bankCode);
        BigInteger branch = new BigInteger(branchCode);
        BigInteger account = new BigInteger(accConverted.toString());

        BigInteger mod = bank.multiply(BigInteger.valueOf(89))
                .add(branch.multiply(BigInteger.valueOf(15)))
                .add(account.multiply(BigInteger.valueOf(3)))
                .mod(BigInteger.valueOf(97));
        int key = 97 - mod.intValue();
        return String.format("%02d", key);
    }

    public String generateFullAccountNumber(String clientId, String bankCode, String branchCode) {

        String digits = String.valueOf(Math.abs(clientId.hashCode()));
        digits = String.format("%011d", Long.parseLong(digits));
        digits = digits.substring(0, 11);

        String ribKey = computeRibKey(bankCode, branchCode, digits);

        return digits + ribKey; 
    }

    public String generateIban(String bankCode, String branchCode, String fullAccountNumber) {
        try {
            if (fullAccountNumber == null || fullAccountNumber.length() != 13) {
                return "IBAN invalide";
            }
            String account11 = fullAccountNumber.substring(0, 11);
            String ribKey = fullAccountNumber.substring(11, 13);

            Iban iban = new Iban.Builder()
                    .countryCode(CountryCode.FR)
                    .bankCode(bankCode)
                    .branchCode(branchCode)
                    .accountNumber(account11)
                    .nationalCheckDigit(ribKey)
                    .build();
            return iban.toString();
        } catch (IbanFormatException e) {
            e.printStackTrace();
            return "IBAN invalide";
        }
    }

    public String getBicForBank(String bankKey) {
        return switch (bankKey) {
            case "BNP" -> "BNPAFRPP";
            case "SG" -> "SOGEFRPP";
            case "CA" -> "AGRIFRPP";
            default -> "BICINCONNU";
        };
    }
}
