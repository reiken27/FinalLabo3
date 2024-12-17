package ar.edu.utn.frbb.tup.controller;
//DTO que va a representar los detalles de una cuota en un plan de pago.
public class PlanDePagoDto {
    private int cuotaNro;// número de la cuota en el plan de pago
    private double monto; // monto a pagar en la cuota


    public int getCuotaNro() {
        return cuotaNro;
    }
    //Permite obtener el número de la cuota.
    public void setCuotaNro(int cuotaNro) {
        this.cuotaNro = cuotaNro;
    }

    public double getMonto() {
        return monto;
    }
    //Permite obtener el monto de la cuota.
    public void setMonto(double monto) {
        this.monto = monto;
    }

}