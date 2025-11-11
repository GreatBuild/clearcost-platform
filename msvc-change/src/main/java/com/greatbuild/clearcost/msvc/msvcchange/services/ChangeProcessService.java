package com.greatbuild.clearcost.msvc.msvcchange.services;

import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.CreateChangeProcessDTO;
import com.greatbuild.clearcost.msvc.msvcchange.models.dtos.UpdateChangeProcessDTO;
import com.greatbuild.clearcost.msvc.msvcchange.models.entities.ChangeProcess;

import java.util.List;

/**
 * Servicio para gestión de solicitudes de cambio
 */
public interface ChangeProcessService {

    /**
     * Crea una nueva solicitud de cambio
     * Validaciones:
     * - El usuario debe ser el contractingEntity del proyecto
     * - El proyecto no debe estar en status APPROVED
     * - El proyecto debe existir
     */
    ChangeProcess createChangeProcess(CreateChangeProcessDTO dto, Long userId);

    /**
     * Actualiza (responde) una solicitud de cambio
     * Validaciones:
     * - El usuario debe ser COORDINATOR del proyecto
     * - La solicitud debe estar en status PENDING
     * - Debe cambiar el status a APPROVED o REJECTED
     */
    ChangeProcess updateChangeProcess(Long changeProcessId, UpdateChangeProcessDTO dto, Long userId);

    /**
     * Obtiene todas las solicitudes de cambio de un proyecto
     * Puede consultar:
     * - CONTRACTOR de la organización
     * - COORDINATOR del proyecto
     * - contractingEntity del proyecto
     */
    List<ChangeProcess> getChangeProcessesByProject(Long projectId, Long userId);
}
