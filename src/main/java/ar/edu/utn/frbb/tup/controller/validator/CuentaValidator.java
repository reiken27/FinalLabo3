package ar.edu.utn.frbb.tup.controller.validator;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import ar.edu.utn.frbb.tup.controller.CuentaDto;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;

@Component
public class CuentaValidator {

    public void validate(CuentaDto cuentaDto) {
        validarNumeroCuenta(String.valueOf(cuentaDto.getNumeroCuenta()));
        validarFechaCreacion(cuentaDto.getFechaCreacion());
        validarBalance(cuentaDto.getBalance());
        validarTipoCuenta(cuentaDto.getTipoCuenta());
        validarMoneda(cuentaDto.getMoneda());
    }

    private void validarNumeroCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.isBlank()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío");
        }

        if (!numeroCuenta.matches("\\d{10,12}")) {
            throw new IllegalArgumentException("El número de cuenta debe contener entre 10 y 12 dígitos");
        }
    }

    private void validarFechaCreacion(LocalDate fechaCreacion) {
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula");
        }

        if (fechaCreacion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de creación no puede estar en el futuro");
        }
    }

    private void validarBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("El balance no puede ser negativo");
        }
    }

    private void validarTipoCuenta(String tipoCuenta) {
        if (tipoCuenta == null || tipoCuenta.isBlank()) {
            throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo o vacío");
        }

        try {
            TipoCuenta.valueOf(tipoCuenta.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo de cuenta no es válido");
        }
    }

    private void validarMoneda(String moneda) {
        if (moneda == null || moneda.isBlank()) {
            throw new IllegalArgumentException("La moneda no puede ser nula o vacía");
        }

        try {
            TipoMoneda.valueOf(moneda.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La moneda no es válida");
        }
    }
}
