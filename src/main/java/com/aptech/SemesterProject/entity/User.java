package com.aptech.SemesterProject.entity;

import com.aptech.SemesterProject.constant.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String photo;
    private String fullName;
    private String nationality; // get the official name of user's country
    private String nationalID;//social id only nums
    private String countryFlag; // save svg image link
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    @CreatedDate
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createdAt;
    private Date lastLoginDate;
    @UpdateTimestamp
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date currentLoginDate;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Date passwordChangedAt;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordResetToken;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date passwordResetExpires;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String accountActivateToken;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isActive = false;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isNotLocked = true;
    private long tokenExpiresInMs;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorityList = Arrays.asList(new SimpleGrantedAuthority(this.role.name()));

        return grantedAuthorityList;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isNotLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

}
