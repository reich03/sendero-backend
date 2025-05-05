package com.sendero.backend.repository;

import com.sendero.backend.model.Zona;
import com.sendero.backend.model.ZonaTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Long> {
    List<Zona> findByTipo(ZonaTipo tipo);


}
