package com.greatbuild.clearcost.msvc.msvcchange.repositories;

import com.greatbuild.clearcost.msvc.msvcchange.models.entities.ChangeProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para ChangeProcess
 */
@Repository
public interface ChangeProcessRepository extends JpaRepository<ChangeProcess, Long> {

    /**
     * Obtiene todas las solicitudes de cambio de un proyecto
     */
    List<ChangeProcess> findByProjectId(Long projectId);
}
