package ar.edu.utn.frbb.tup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import ar.edu.utn.frbb.tup.model.entity.Cliente;
import ar.edu.utn.frbb.tup.model.entity.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoPersonaInvalidoException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;

@Service
public class ClienteService {

    @Autowired
    @Lazy
    private ClienteDao clienteDao;

    public ClienteService(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public ClienteService() {
    }

    public Cliente darDeAltaCliente(Cliente cliente) throws ClienteAlreadyExistsException, TipoPersonaInvalidoException{
        // Verifica si el cliente ya existe en la base de datos en memoria
        if (clienteDao.find(cliente.getDni(), false) != null) {
            throw new ClienteAlreadyExistsException("Ya existe un cliente con DNI " + cliente.getDni());
        }

        if (cliente.getEdad() < 18) {
            throw new IllegalArgumentException("El cliente debe ser mayor a 18 años");
        }

        if (!"fisica".equalsIgnoreCase(cliente.getTipoPersona()) && !"juridica".equalsIgnoreCase(cliente.getTipoPersona())) {
            throw new TipoPersonaInvalidoException("El tipo de persona debe ser 'fisica' o 'juridica'");
        }
        clienteDao.save(cliente);
        return cliente;
    }

    public void agregarCuenta(Cuenta cuenta, long dni) throws ClienteAlreadyExistsException, TipoCuentaAlreadyExistsException {
        Cliente cliente = clienteDao.find(dni, false);
        if (cliente == null) {
            throw new ClienteAlreadyExistsException("Cliente no encontrado con DNI: " + dni);
        }

        if (cliente.tieneCuenta(cuenta.getTipoCuenta(), cuenta.getMoneda())) {
            throw new TipoCuentaAlreadyExistsException("El cliente ya tiene una cuenta de este tipo y moneda");
        }

        cliente.addCuenta(cuenta);
        clienteDao.save(cliente);
    }

    public Cliente buscarClientePorDni(long dni) {
        return clienteDao.find(dni, true);
    }

    public List<Cliente> obtenerTodosClientes() {
        return clienteDao.findAll();
    }
}