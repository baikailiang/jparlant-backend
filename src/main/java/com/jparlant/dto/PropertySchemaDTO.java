package com.jparlant.dto;

import lombok.Data;

import java.util.List;

@Data
public class PropertySchemaDTO{


    String name;
    String type;
    String description;
    boolean isComplex;
    List<PropertySchemaDTO> children;

    public PropertySchemaDTO() {
    }

    public PropertySchemaDTO(String name, String type, String description, boolean isComplex, List<PropertySchemaDTO> children) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.isComplex = isComplex;
        this.children = children;
    }
}
