package com.jparlant.dto;

import lombok.Data;

import java.util.List;


@Data
public class BeanWithMethodsDTO {

    String beanName;
    String displayName;
    List<MethodMetadataDTO> methods;


    public BeanWithMethodsDTO() {
    }

    public BeanWithMethodsDTO(String beanName, String displayName, List<MethodMetadataDTO> methods) {
        this.beanName = beanName;
        this.displayName = displayName;
        this.methods = methods;
    }
}

