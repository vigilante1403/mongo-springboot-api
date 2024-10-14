package com.aptech.SemesterProject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GuideDto {
    public String value;
    public String label;
    public GuideDto(String id,String email,String name){
        this.value=id;
        this.label=name+'-'+email;
    }
}
