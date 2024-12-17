package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class PrestamoDao {
    private Map<Long, Prestamo> database = new HashMap<>();

    public Prestamo save(Prestamo prestamo) {
        Long id = prestamo.getCliente().getDni();
        prestamo.setId(id);
        database.put(id, prestamo);
        return prestamo;
    }

    public Prestamo findById(Long id) {
        return database.get(id);
    }

    public List<Prestamo> findByClienteId(Long clienteId) {
        return database.values().stream()
                .filter(prestamo -> prestamo.getCliente().getDni() == clienteId)
                .collect(Collectors.toList());
    }

    public Map<Long, Prestamo> findAll() {
        return database;
    }
}