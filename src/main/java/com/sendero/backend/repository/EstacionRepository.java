package com.sendero.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sendero.backend.model.Estacion;
import java.util.Optional;

public interface EstacionRepository extends JpaRepository<Estacion, Long> {
    Optional<Estacion> findById(Long id);
}
