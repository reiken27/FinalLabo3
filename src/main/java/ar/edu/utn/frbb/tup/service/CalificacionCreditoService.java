package ar.edu.utn.frbb.tup.service;

import org.springframework.stereotype.Service;

@Service
public class CalificacionCreditoService {
    public boolean verificarCalificacion(String dni) {
        // Simula que el 90% de los clientes tienen buena calificación.
        return Math.random() > 0.10;
    }
}