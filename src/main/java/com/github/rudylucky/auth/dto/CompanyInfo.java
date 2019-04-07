package com.github.rudylucky.auth.dto;

public class CompanyInfo {

    private String companyName;
    private String description;
    private String companyType;
    private String unifiedSocialCreditCode;
    private String legalPerson;
    private String contactEmail;

    public CompanyInfo(String companyName, String companyType, String unifiedSocialCreditCode, String legalPerson, String contactEmail, String description){
        this.companyName = companyName;
        this.companyType = companyType;
        this.unifiedSocialCreditCode = unifiedSocialCreditCode;
        this.legalPerson = legalPerson;
        this.contactEmail = contactEmail;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getUnifiedSocialCreditCode() {
        return unifiedSocialCreditCode;
    }

    public void setUnifiedSocialCreditCode(String unifiedSocialCreditCode) {
        this.unifiedSocialCreditCode = unifiedSocialCreditCode;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
