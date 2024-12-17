package ar.edu.utn.frbb.tup.controller.validator;

import ar.edu.utn.frbb.tup.controller.ClienteDto;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class ClienteValidator {

    public void validate(ClienteDto clienteDto) {
        validarTipoPersona(clienteDto.getTipoPersona());
        validarFechaNacimiento(clienteDto.getFechaNacimiento());
        validarDni(clienteDto.getDni());
        validarNombre(clienteDto.getNombre());
        validarApellido(clienteDto.getApellido());
        validarBanco(clienteDto.getBanco());
    }

    private void validarTipoPersona(String tipoPersona) {
        if (tipoPersona == null || tipoPersona.isBlank()) {
            throw new IllegalArgumentException("El tipo de persona no puede ser nulo o vacío");
        }

        try {
            TipoPersona.valueOf(tipoPersona.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo de persona debe ser 'fisica' o 'juridica'");
        }
    }

    private void validarFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }

        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede estar en el futuro");
        }

        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor de 18 años");
        }
    }

    private void validarDni(long dni) {
        if (dni < 10000000 || dni > 99999999) {
            throw new IllegalArgumentException("El DNI debe ser un número positivo de 8 dígitos");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s\\-]+")) {
            throw new IllegalArgumentException("El nombre debe contener solo letras, espacios o guiones");
        }

        if (nombre.length() < 2 || nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres");
        }
    }

    private void validarApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío");
        }

        if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s\\-]+")) {
            throw new IllegalArgumentException("El apellido debe contener solo letras, espacios o guiones");
        }

        if (apellido.length() < 2 || apellido.length() > 50) {
            throw new IllegalArgumentException("El apellido debe tener entre 2 y 50 caracteres");
        }
    }

    private void validarBanco(String banco) {
        if (banco == null || banco.isBlank()) {
            throw new IllegalArgumentException("El banco no puede ser nulo o vacío");
        }

        if (!banco.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s\\-]+")) {
            throw new IllegalArgumentException("El banco debe contener solo letras, espacios o guiones");
        }

        if (banco.length() < 2 || banco.length() > 50) {
            throw new IllegalArgumentException("El banco debe tener entre 2 y 50 caracteres");
        }
    }
}
