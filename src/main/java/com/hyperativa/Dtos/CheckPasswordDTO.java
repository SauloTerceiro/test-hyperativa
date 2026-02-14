package com.hyperativa.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPasswordDTO {

    public boolean correctPassword;
    public String jwtToken;
}
