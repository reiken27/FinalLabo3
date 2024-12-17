package ar.edu.utn.frbb.tup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frbb.tup.controller.validator.ClienteValidator;
import ar.edu.utn.frbb.tup.model.entity.Cliente;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoPersonaInvalidoException;
import ar.edu.utn.frbb.tup.service.ClienteService;

// Controlador que maneja las solicitudes de clientes.
// Permite crear un nuevo cliente, obtener un cliente por su DNI y listar todos los clientes.
// Tambien maneja las excepciones y respuestas de error
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    @Lazy
    private ClienteService clienteService;

    @Autowired
    private ClienteValidator clienteValidator;

    // En este POST se crea un nuevo cliente. Primero, verifica si el tipo de persona es válido ('fisica' o 'juridica').
    // Luego, convierte la información del cliente desde el DTO a un objeto Cliente y lo guarda en el servicio.
    @PostMapping("/crearCliente")
    public Cliente crearCliente(@RequestBody ClienteDto clienteDto) throws ClienteAlreadyExistsException, TipoPersonaInvalidoException {
        // Validar ClienteDto
        clienteValidator.validate(clienteDto);

        // Convertir ClienteDto a Cliente
        Cliente cliente = new Cliente();
        cliente.setDni(clienteDto.getDni());
        cliente.setNombre(clienteDto.getNombre());
        cliente.setApellido(clienteDto.getApellido());
        cliente.setFechaNacimiento(clienteDto.getFechaNacimiento());
        cliente.setTipoPersona(clienteDto.getTipoPersona());
        cliente.setBanco(clienteDto.getBanco());
        cliente.setFechaAlta(java.time.LocalDate.now());

        return clienteService.darDeAltaCliente(cliente);
    }

    // Este GET busca un cliente por su DNI. Si lo encuentra, devuelve la información del cliente.
    // Si no encuentra al cliente, devuelve un error 404.
    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> obtenerClientePorDni(@PathVariable long dni) {
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

    // GET que obtiene todos los clientes, llama a clienteservice para obtener la lista de clientes.
    @GetMapping
    public List<Cliente> obtenerTodosClientes() {
        return clienteService.obtenerTodosClientes();
    }

    // Maneja la excepción de cuando se intenta crear un cliente que ya existe. Devuelve un error con código 1001.
    @ExceptionHandler(ClienteAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleClienteAlreadyExistsException(ClienteAlreadyExistsException ex) {
        return new ErrorResponse(1001, ex.getMessage());
    }

    // Maneja la excepción de cuando el tipo de persona no es válido. Devuelve un error con código 1002.
    @ExceptionHandler(TipoPersonaInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleInvalidTipoPersonaException(TipoPersonaInvalidoException ex) {
        return new ErrorResponse(1002, ex.getMessage());
    }

    // para estructurar la respuesta de error, define como se tiene que ver la respuesta del error
    static class ErrorResponse {
        private int errorCode;
        private String errorMessage;

        public ErrorResponse(int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}