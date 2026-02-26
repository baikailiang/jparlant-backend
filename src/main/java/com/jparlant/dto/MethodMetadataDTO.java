package com.jparlant.dto;

import lombok.Data;

import java.util.List;

@Data
public class MethodMetadataDTO {

    String methodName;
    String displayName;
    List<PropertySchemaDTO> parameters;
    List<PropertySchemaDTO> returnSchema;

    public MethodMetadataDTO() {
    }


    public MethodMetadataDTO(String methodName, String displayName, List<PropertySchemaDTO> parameters, List<PropertySchemaDTO> returnSchemas) {
        this.methodName = methodName;
        this.displayName = displayName;
        this.parameters = parameters;
        this.returnSchema = returnSchemas;
    }
}
