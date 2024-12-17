package ar.edu.utn.frbb.tup.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.frbb.tup.controller.PrestamoClienteResponse;
import ar.edu.utn.frbb.tup.controller.PrestamoDto;
import ar.edu.utn.frbb.tup.controller.PrestamoRequest;
import ar.edu.utn.frbb.tup.controller.PrestamoResponse;
import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.model.entity.Cliente;
import ar.edu.utn.frbb.tup.model.entity.Cuenta;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import ar.edu.utn.frbb.tup.persistence.PrestamoDao;

@Service
public class PrestamoService {
    @Autowired
    private ClienteDao clienteDao;
    @Autowired
    private CuentaService cuentaService;
    @Autowired
    private PrestamoDao prestamoDao;
    @Autowired
    private CalificacionCreditoService CalCredService;

        public PrestamoResponse SolicitarPrestamo(PrestamoRequest request){
            Cliente cliente = clienteDao.find(request.getDniCliente(),true);
            if (cliente == null) {
               throw new IllegalArgumentException("El cliente solicitado no existe");
            }
            List<Prestamo> prestamosActivos = prestamoDao.findByClienteId(cliente.getId())
                    .stream()
                    .filter(prestamo -> prestamo.getSaldoRestante()>0)
                    .collect(Collectors.toList());
            if (!prestamosActivos.isEmpty()) {
                throw new IllegalArgumentException("El cliente ya tiene un préstamo activo");
            }

            Cuenta cuenta = cuentaService.findCuentaByClienteAndMoneda(cliente, request.getMoneda());
            if (cuenta == null) {
                throw new IllegalArgumentException("El cliente no tiene una cuenta en la moneda solicitada");
            }

            boolean calificacionAprobada = CalCredService.verificarCalificacion(String.valueOf(cliente.getDni()));
            if (!calificacionAprobada) {
                throw new IllegalArgumentException("El cliente no tiene una buena calificación crediticia");
            }

            // Crea un nuevo prestamo
            Prestamo prestamo = new Prestamo();
            prestamo.setCliente(cliente);
            prestamo.setMonto(request.getMonto());
            prestamo.setMoneda(request.getMoneda());
            prestamo.setPlazoMeses(request.getPlazo());
            prestamo.setFechaInicio(LocalDate.now());

            // Calcula el plan de pagos y el saldo restante con intereses
            List<Prestamo.PlanPago> planPagos = calcularPlanPagos(prestamo);
            prestamo.setPlanPagos(planPagos);

            // Calcula el saldo restante del prestamo
            double saldoRestante = planPagos.stream().mapToDouble(Prestamo.PlanPago::getMonto).sum();
            BigDecimal saldoRestanteRounded = new BigDecimal(saldoRestante).setScale(2, RoundingMode.HALF_UP);
            prestamo.setSaldoRestante(saldoRestanteRounded.doubleValue());

            prestamoDao.save(prestamo);

            // Actualiza saldo de la cuenta
            cuentaService.actualizarSaldo(cuenta.getNumeroCuenta(), cuenta.getBalance() + request.getMonto());

            // la respuesta del préstamo
            PrestamoResponse response = new PrestamoResponse();
            response.setEstado("APROBADO");
            response.setMensaje("El monto del préstamo fue acreditado en su cuenta");
            response.setPlanPagos(planPagos.stream().map(this::convertirPlanPago).collect(Collectors.toList()));

            return response;
        }
    private List<Prestamo.PlanPago> calcularPlanPagos(Prestamo prestamo) {
        List<Prestamo.PlanPago> planPagos = new ArrayList<>();
        double montoPrestamo = prestamo.getMonto();
        double tasaInteresAnual = 0.05;
        double tasaInteresMensual = tasaInteresAnual / 12;
        int plazoMeses = prestamo.getPlazoMeses();

// Calcula el pago mensual utilizando la formula de la cuota fija
        double pagoMensual = (montoPrestamo * tasaInteresMensual * Math.pow(1 + tasaInteresMensual, plazoMeses)) /
                (Math.pow(1 + tasaInteresMensual, plazoMeses) - 1);
        // esto lo agregué para que redondee el pago mensual a dos decimales
        BigDecimal pagoMensualRounded = new BigDecimal(pagoMensual).setScale(2, RoundingMode.HALF_UP);

        // Crea el plan de pagos
        for (int i = 1; i <= plazoMeses; i++) {
            Prestamo.PlanPago cuota = new Prestamo.PlanPago();
            cuota.setCuotaNro(i);
            cuota.setMonto(pagoMensualRounded.doubleValue());
            planPagos.add(cuota);
        }
        return planPagos;
    }
    private PrestamoResponse.PlanPago convertirPlanPago(Prestamo.PlanPago planPago) {
        PrestamoResponse.PlanPago planPagoDto = new PrestamoResponse.PlanPago();
        planPagoDto.setCuotaNro(planPago.getCuotaNro());

        BigDecimal montoRounded = new BigDecimal(planPago.getMonto()).setScale(2, RoundingMode.HALF_UP);
        planPagoDto.setMonto(montoRounded.doubleValue());

        return planPagoDto;
    }
    public PrestamoClienteResponse obtenerPrestamosPorCliente(Long clienteId) {
        List<Prestamo> prestamos = prestamoDao.findByClienteId(clienteId);

        // Mapea los préstamos al formato requerido
        List<PrestamoDto> prestamoDtos = prestamos.stream().map(prestamo -> {
            PrestamoDto dto = new PrestamoDto();
            dto.setMonto(prestamo.getMonto());
            dto.setPlazoMeses(prestamo.getPlazoMeses());
            dto.setPagosRealizados(prestamo.getPagosRealizados()); // Ajustado según el estado real de los pagos
            dto.setSaldoRestante(prestamo.getSaldoRestante()); // Ajustado según el saldo real
            return dto;
        }).collect(Collectors.toList());

        PrestamoClienteResponse response = new PrestamoClienteResponse();
        response.setNumeroCliente(clienteId);
        response.setPrestamos(prestamoDtos);
        return response;
    }

    // resgistra el pago de cuotas de un prestamo
    public void registrarPagoCuota(Long prestamoId, int numeroCuotas) {
        Prestamo prestamo = prestamoDao.findById(prestamoId);
        if (prestamo == null) {
            throw new IllegalArgumentException("Préstamo no encontrado");
        }

        if (numeroCuotas <= 0) {
            throw new IllegalArgumentException("El número de cuotas debe ser mayor que cero");
        }

        // Calcula las cuotas restantes del préstamo
        int cuotasRestantes = prestamo.getPlazoMeses() - prestamo.getPagosRealizados();
        if (numeroCuotas > cuotasRestantes) {
            throw new IllegalArgumentException("No se pueden pagar más cuotas de las que quedan en el préstamo");
        }

        // obtiene el monto de la cuota con intereses
        double montoPorCuota = prestamo.getPlanPagos().get(0).getMonto(); // Obtener el monto de la cuota con intereses
        double montoTotalPago = montoPorCuota * numeroCuotas;

        // Actualiza el saldo restante
        double saldoRestante = prestamo.getSaldoRestante() - montoTotalPago;
        BigDecimal saldoRestanteRounded = new BigDecimal(saldoRestante).setScale(2, RoundingMode.HALF_UP);
        prestamo.setSaldoRestante(saldoRestanteRounded.doubleValue());

        // Actualiza los pagos realizados
        int cuotasPagadas = prestamo.getPagosRealizados();
        cuotasPagadas += numeroCuotas; // Incrementamos el número de cuotas pagadas
        prestamo.setPagosRealizados(cuotasPagadas);

        prestamoDao.save(prestamo);
    }


    //para encontrar un prestamo por ID.
    public Prestamo findById(Long id) {
        return prestamoDao.findById(id);
    }
}

