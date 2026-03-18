package com.btg.prueba_tecnica_seti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Constructor para respuesta exitosa
    public ApiResponse(T data) {
        this.success = true;
        this.message = "Operación exitosa";
        this.data = data;
    }

    // Constructor para respuesta exitosa con mensaje personalizado
    public ApiResponse(String message, T data) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    // Constructor para respuesta de error
    public ApiResponse(String message) {
        this.success = false;
        this.message = message;
        this.data = null;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
