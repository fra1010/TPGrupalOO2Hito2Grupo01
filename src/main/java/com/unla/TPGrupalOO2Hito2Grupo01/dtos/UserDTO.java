package com.unla.TPGrupalOO2Hito2Grupo01.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    // Lo genera la base de datos: es null en el formulario de alta
    private Integer id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 45, message = "El usuario no puede superar los 45 caracteres")
    private String username;

    // Clave en texto plano que escribe la persona en el formulario.
    // El service la encripta con BCrypt antes de guardar (la columna guarda el hash de 60 caracteres).
    // Al convertir de entidad a DTO NO se copia el hash: este campo siempre debe viajar vacio hacia la vista.
    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 6, max = 72, message = "La clave debe tener entre 6 y 72 caracteres")
    private String password;
}
