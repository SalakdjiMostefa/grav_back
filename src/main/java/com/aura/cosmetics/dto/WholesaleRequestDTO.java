package com.aura.cosmetics.dto;

import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.enums.WholesaleStatus;
import java.time.LocalDateTime;

public class WholesaleRequestDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String companyName;
    private String tradeRegister;
    private String businessAddress;
    private WholesaleStatus wholesaleStatus;
    private Role role;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    public WholesaleRequestDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTradeRegister() { return tradeRegister; }
    public void setTradeRegister(String tradeRegister) { this.tradeRegister = tradeRegister; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public WholesaleStatus getWholesaleStatus() { return wholesaleStatus; }
    public void setWholesaleStatus(WholesaleStatus wholesaleStatus) { this.wholesaleStatus = wholesaleStatus; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}
