package com.cctns.apprehend.web.dto.request.apprehend;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AliasJsonDTO {
    private String id;
    private String alias;
    private String aliasEng;
}
