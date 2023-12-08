package com.cksiow.ai.advisor.user.model;

import com.universal.core.library.base.model.BaseModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@MappedSuperclass
public class UserData extends BaseModel {
    private static final long serialVersionUID = 1L;
    @Column(updatable = false)
    private String uniqueId;

    @Builder.Default
    @Column(insertable = false, updatable = false)
    private Integer credit = 3;

    @Builder.Default
    @Column(insertable = false, updatable = false)
    private Integer usedCredit = 0;

    @Builder.Default
    @Column
    private String buildNumber = "0";

    @Builder.Default
    @Column
    private String languageCode = "en";


}
