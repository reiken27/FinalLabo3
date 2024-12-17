# tup2024
Ejemplo de uso

_CREAR CLIENTE_
Endpoint POST /cliente/crearCliente Ejemplo:
{
"dni": 38531657,
"nombre": "Emiliano",
"apellido": "Correa",
"fechaNacimiento": "1995-10-31",
"tipoPersona": "FISICA",
"banco": "Banco Nación"
}
_GET CLIENTE_
GET http://127.0.0.1:8080/cliente/38531657

_GET LISTA CLIENTES_
GET http://127.0.0.1:8080/cliente

Endpoint POST /cuenta/crearCuenta Ejemplo:
{ "numeroCuenta": "1234567890", "fechaCreacion": "2024-12-17", "balance": 10000.50, "tipoCuenta": "CAJA_AHORRO", "moneda": "DOLARES", "titularDni": "38531657" }

_GET LISTA CUENTAS_
http://127.0.0.1:8080/cuenta/cuentas

*Prestamo*
Endpoint POST /api/prestamo Ejemplo
{
"dniCliente": 38531657,
"plazo": 12,
"monto": 5000.0,
"moneda": "DOLARES"
}
GET
api/prestamo/{dni}

pago prestamo
POST
api/prestamos/{dni}/pagar
{"numeroCuotas":4}
