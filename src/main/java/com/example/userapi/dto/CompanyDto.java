package com.example.userapi.dto;

public class CompanyDto {
    private String name;
    private String catchPhrase;
    private String bs;

    public CompanyDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCatchPhrase() { return catchPhrase; }
    public void setCatchPhrase(String catchPhrase) { this.catchPhrase = catchPhrase; }

    public String getBs() { return bs; }
    public void setBs(String bs) { this.bs = bs; }
}
