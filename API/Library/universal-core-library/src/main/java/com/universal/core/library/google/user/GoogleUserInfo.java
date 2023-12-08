package com.universal.core.library.google.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserInfo {
    String sub;
    String name;
    String given_name;
    String family_name;
    String picture;
    String email;
    Boolean verified_email;
    String locale;
}
