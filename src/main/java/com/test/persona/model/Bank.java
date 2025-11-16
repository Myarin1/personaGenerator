package com.test.persona.model;

public enum Bank {
    BNP("BNPAFRPP", "01"),
    SOCIETE_GENERALE("SGPARDEF", "02"),
    CREDIT_AGRICOLE("AGRIFRPP", "03");

    private final String bic;
    private final String agencyCode;

    Bank(String bic, String agencyCode) {
        this.bic = bic;
        this.agencyCode = agencyCode;
    }

    public String getBic() {
        return bic;
    }

    public String getAgencyCode() {
        return agencyCode;
    }
}
