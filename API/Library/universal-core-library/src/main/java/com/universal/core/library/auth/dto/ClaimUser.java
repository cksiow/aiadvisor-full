package com.universal.core.library.auth.dto;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ClaimUser extends User {

    public ClaimUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                     List<Claim> claimList) {
        super(username, password, authorities);
        this.claimList = claimList;
    }

    @Builder.Default
    List<Claim> claimList = new ArrayList<>();

    public List<Claim> getClaimList() {
        return claimList;
    }
}
