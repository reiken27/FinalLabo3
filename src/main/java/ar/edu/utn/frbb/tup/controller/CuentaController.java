package ar.edu.utn.frbb.tup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.controller.validator.CuentaValidator;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.entity.Cliente;
import ar.edu.utn.frbb.tup.model.entity.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.service.ClienteService;
import ar.edu.utn.frbb.tup.service.CuentaService;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaValidator cuentaValidator;

    //Este POST es para crear una cuenta
    @PostMapping("/crearCuenta")
    public ResponseEntity<String> crearCuenta(@RequestBody CuentaDto cuentaDto) {
        try {
            // Validaciones delegadas al validador
            cuentaValidator.validate(cuentaDto);

            Cliente cliente = clienteService.buscarClientePorDni(cuentaDto.getTitularDni());
            if (cliente == null) {
                return ResponseEntity.badRequest().body("Cliente no encontrado");
            }

            Cuenta cuenta = new Cuenta();
            cuenta.setNumeroCuenta(cuentaDto.getNumeroCuenta());
            cuenta.setFechaCreacion(cuentaDto.getFechaCreacion().atStartOfDay());
            cuenta.setBalance(cuentaDto.getBalance());
            cuenta.setTipoCuenta(TipoCuenta.valueOf(cuentaDto.getTipoCuenta()));
            cuenta.setMoneda(TipoMoneda.valueOf(cuentaDto.getMoneda().toUpperCase()));
            cuenta.setTitular(cliente);

            cuentaService.darDeAltaCuenta(cuenta, cliente.getDni());
            return ResponseEntity.ok("Cuenta creada exitosamente.");
        } catch (CuentaAlreadyExistsException | TipoCuentaAlreadyExistsException | ClienteAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error en los datos proporcionados: " + e.getMessage());
        }
    }

    //Este GET obtiene todas las cuentas existentes.
    @GetMapping("/cuentas")
    public ResponseEntity<List<Cuenta>> obtenerTodasLasCuentas() {
        try {
            List<Cuenta> cuentas = cuentaService.obtenerTodasLasCuentas();
            if (cuentas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
