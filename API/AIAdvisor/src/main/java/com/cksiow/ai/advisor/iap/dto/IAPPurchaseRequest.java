package com.cksiow.ai.advisor.iap.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@MappedSuperclass
public class IAPPurchaseRequest {

    String token;
    String productId;
    String uniqueId;
    @Builder.Default
    private Integer quality = 1;
}
