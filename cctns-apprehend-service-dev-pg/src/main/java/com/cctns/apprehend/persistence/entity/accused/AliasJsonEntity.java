package com.cctns.apprehend.persistence.entity.accused;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AliasJsonEntity {
    private String id;
    private String alias;
    private String aliasEng;
}
