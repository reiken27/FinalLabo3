package ar.edu.utn.frbb.tup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.model.exception.DniClienteNotValidException;
import ar.edu.utn.frbb.tup.service.PrestamoService;

//controlador que maneja los endpoints relacionados con los préstamos, permitiendo a los
// clientes solicitar préstamos, realizar pagos y consultar sus préstamos.
@RestController
@RequestMapping("/api")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    // POST QUE permite solicitar un préstamo. Toma los datos del préstamo desde la solicitud
    // (PrestamoRequest) y llama a prestamoservicio para procesar la solicitud.
    @PostMapping("/prestamo")
    public ResponseEntity<PrestamoResponse> SolicitarPrestamo(@RequestBody PrestamoRequest request) {
        if (request.getDniCliente() == null) {
            throw new DniClienteNotValidException("El DNI del cliente no puede ser nulo");
        }
        PrestamoResponse response = prestamoService.SolicitarPrestamo(request);
        return ResponseEntity.ok(response);
    }

    // GET que obtiene los préstamos asociados a un cliente específico. Busca los préstamos usando el ID del cliente
    // pasado en la URL.
    @GetMapping("/prestamo/{id}")
    public ResponseEntity<PrestamoClienteResponse> getPrestamosByClienteId(@PathVariable Long id) {
        try {
            PrestamoClienteResponse response = prestamoService.obtenerPrestamosPorCliente(id);
            if (response == null || response.getPrestamos().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Añade esto para depuración
            return ResponseEntity.status(500).build();
        }
    }

    // POST para registrar el pago de cuotas del prestamo. Utiliza el ID del préstamo pasado en la URL y
    // el número de cuotas a pagar en la solicitud (CuotasPagoRequest). Si el pago se realiza
    // exitosamente, devuelve un mensaje de éxito. Si hay un problema con los datos proporcionados, devuelve un error 400
    // (Bad Request) con el mensaje de error.
    @PostMapping("/prestamos/{id}/pagar")
    public ResponseEntity<String> pagarCuotas(@PathVariable Long id, @RequestBody CuotasPagoRequest request) {
        try {
            prestamoService.registrarPagoCuota(id, request.getNumeroCuotas());
            return ResponseEntity.ok("Pago de cuotas registrado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
