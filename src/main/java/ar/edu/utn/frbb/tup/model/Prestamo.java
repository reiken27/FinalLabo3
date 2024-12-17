package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.model.entity.Cliente;

import java.time.LocalDate;
import java.util.List;

public class Prestamo {
    private Long id;
    private Cliente cliente;
    private double monto;
    private String moneda;
    private int plazoMeses;
    private LocalDate fechaInicio;
    private List<PlanPago> planPagos;
    private double saldoRestante;
    private int pagosRealizados;

    public Prestamo(){}
    public Prestamo(Long id, Cliente cliente, double monto, String moneda,
                    int plazoMeses, LocalDate fechaInicio, List<PlanPago> planPagos,
                    double saldoRestante, int pagosRealizados) {
        this.id = id;
        this.cliente = cliente;
        this.monto = monto;
        this.moneda = moneda;
        this.plazoMeses = plazoMeses;
        this.fechaInicio = fechaInicio;
        this.planPagos = planPagos;
        this.saldoRestante = saldoRestante;
        this.pagosRealizados = pagosRealizados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double montoPrestamo) {
        this.monto = montoPrestamo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public List<PlanPago> getPlanPagos() {
        return planPagos;
    }

    public void setPlanPagos(List<PlanPago> planPagos) {
        this.planPagos = planPagos;
    }

    public double getSaldoRestante() {
        return saldoRestante;
    }

    public void setSaldoRestante(double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public int getPagosRealizados() {
        return pagosRealizados;
    }

    public void setPagosRealizados(int pagosRealizados) {
        this.pagosRealizados = pagosRealizados;
    }
    public static class PlanPago {
        private int cuotaNro;
        private double monto;

        public int getCuotaNro() {
            return cuotaNro;
        }
        public void setCuotaNro(int cuotaNro) {
            this.cuotaNro = cuotaNro;
        }
        public double getMonto() {
            return monto;
        }
        public void setMonto(double monto) {
            this.monto = monto;
        }
    }
}
