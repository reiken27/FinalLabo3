package ar.edu.utn.frbb.tup.model.exception;

public class ClienteAlreadyExistsException extends Throwable{
    public ClienteAlreadyExistsException(String msg) {
        super(msg);
    }
}
