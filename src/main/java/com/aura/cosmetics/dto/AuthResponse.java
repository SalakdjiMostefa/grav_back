package com.aura.cosmetics.dto;

import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.enums.WholesaleStatus;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private WholesaleStatus wholesaleStatus = WholesaleStatus.NONE;
    private String companyName;
    private String tradeRegister;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String fullName, String email, String phone, String address, Role role) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public AuthResponse(String token, Long id, String fullName, String email, String phone, String address, Role role, WholesaleStatus wholesaleStatus, String companyName, String tradeRegister) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.wholesaleStatus = wholesaleStatus;
        this.companyName = companyName;
        this.tradeRegister = tradeRegister;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public WholesaleStatus getWholesaleStatus() { return wholesaleStatus; }
    public void setWholesaleStatus(WholesaleStatus wholesaleStatus) { this.wholesaleStatus = wholesaleStatus; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTradeRegister() { return tradeRegister; }
    public void setTradeRegister(String tradeRegister) { this.tradeRegister = tradeRegister; }
}
