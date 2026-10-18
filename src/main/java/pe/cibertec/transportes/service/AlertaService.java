package pe.cibertec.transportes.service;

import pe.cibertec.transportes.entity.AlertaTrafico;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.repository.AlertaTraficoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertaService {

    @Autowired
    private AlertaTraficoRepository alertaRepository;

    public List<AlertaTrafico> listarTodas() {
        return alertaRepository.findAllByOrderByCreatedAtDesc();
    }

    public AlertaTrafico guardar(AlertaTrafico alerta) {
        return alertaRepository.save(alerta);
    }

    public AlertaTrafico ultimaActiva() {
        return alertaRepository.findTopByOrderByCreatedAtDesc().orElse(null);
    }

    public void eliminar(Integer id) {
        AlertaTrafico alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Alerta no encontrada"));
        alertaRepository.delete(alerta);
    }
}