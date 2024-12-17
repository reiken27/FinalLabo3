package ar.edu.utn.frbb.tup.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.entity.Cliente;
import ar.edu.utn.frbb.tup.model.entity.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaNoSoportadaException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.persistence.entity.CuentaEntity;

@Service
public class CuentaService {

    @Autowired
    private CuentaDao cuentaDao;

    @Autowired
    private ClienteService clienteService;

    public void darDeAltaCuenta(Cuenta cuenta, long dni) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, ClienteAlreadyExistsException {
        Cuenta cuentaExistente = cuentaDao.find(cuenta.getNumeroCuenta());
        if (cuentaExistente != null) {
            throw new CuentaAlreadyExistsException("Cuenta ya existe con el número de cuenta: " + cuenta.getNumeroCuenta());
        }
        Cliente cliente = clienteService.buscarClientePorDni(dni);
        if (cliente == null) {
            throw new ClienteAlreadyExistsException("Cliente no encontrado con el DNI: " + dni);
        }
        for (Cuenta c : cliente.getCuentas()) {
            if (c.getTipoCuenta().equals(cuenta.getTipoCuenta())) {
                throw new TipoCuentaAlreadyExistsException("El cliente ya tiene una cuenta de tipo: " + cuenta.getTipoCuenta());
            }
        }

        cuenta.setTitular(cliente);
        cuentaDao.save(cuenta);
        clienteService.agregarCuenta(cuenta, dni);
    }

    public void validarCuentaSoportada(TipoCuenta tipoCuenta) throws CuentaNoSoportadaException {
        if (!tipoDeCuentaSoportada(tipoCuenta.toString())) {
            throw new CuentaNoSoportadaException("Tipo de cuenta no soportado: " + tipoCuenta);
        }
    }

    public Cuenta find(long id) {
        CuentaEntity cuentaEntity = cuentaDao.findById(id);
        if (cuentaEntity == null) {
            return null;
        }
        return cuentaEntity.toCuenta(clienteService);
    }

    public boolean tipoDeCuentaSoportada(String tipoCuenta) {
        if (tipoCuenta == null) {
            return false;
        }
        return switch (tipoCuenta) {
            case "CA$", "CC$", "CAU$S" ->
                true;
            default ->
                false;
        };
    }

    public List<Cuenta> obtenerTodasLasCuentas() {
        try {
            List<CuentaEntity> cuentasEntities = cuentaDao.findAll();
            return cuentasEntities.stream()
                    .map(entity -> entity.toCuenta(clienteService))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener todas las cuentas", e);
        }
    }

    public Cuenta findCuentaByClienteAndMoneda(Cliente cliente, String moneda) {
        for (Cuenta cuenta : cliente.getCuentas()) {
            if (cuenta.getMoneda().toString().equalsIgnoreCase(moneda)) {
                return cuenta;
            }
        }
        return null;
    }

    public void actualizarSaldo(Long numeroCuenta, double nuevoSaldo) {
        CuentaEntity cuentaEntity = cuentaDao.findById(numeroCuenta);
        if (cuentaEntity != null) {
            cuentaEntity.setBalance(nuevoSaldo);
            cuentaDao.save(cuentaEntity);
        }
    }
}
