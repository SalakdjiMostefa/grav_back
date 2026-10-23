package com.aura.cosmetics.entity;

import com.aura.cosmetics.enums.Role;
import com.aura.cosmetics.enums.WholesaleStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Column(length = 1000)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_CLIENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WholesaleStatus wholesaleStatus = WholesaleStatus.NONE;

    private String companyName;

    private String tradeRegister;

    private String businessPhone;

    @Column(length = 1000)
    private String businessAddress;

    private LocalDateTime wholesaleRequestedAt;

    private LocalDateTime wholesaleApprovedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(String fullName, String email, String password, String phone, String address, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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
    public String getBusinessPhone() { return businessPhone; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public LocalDateTime getWholesaleRequestedAt() { return wholesaleRequestedAt; }
    public void setWholesaleRequestedAt(LocalDateTime wholesaleRequestedAt) { this.wholesaleRequestedAt = wholesaleRequestedAt; }
    public LocalDateTime getWholesaleApprovedAt() { return wholesaleApprovedAt; }
    public void setWholesaleApprovedAt(LocalDateTime wholesaleApprovedAt) { this.wholesaleApprovedAt = wholesaleApprovedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
