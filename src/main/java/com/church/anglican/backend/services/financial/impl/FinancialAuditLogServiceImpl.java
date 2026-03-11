package com.church.anglican.backend.services.financial.impl;

import com.church.anglican.backend.entities.financial.FinancialAuditLog;
import com.church.anglican.backend.entities.identity.AppRole;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.repositories.financial.FinancialAuditLogRepository;
import com.church.anglican.backend.services.financial.FinancialAuditLogService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FinancialAuditLogServiceImpl implements FinancialAuditLogService {

    private final FinancialAuditLogRepository auditLogRepository;

    public FinancialAuditLogServiceImpl(FinancialAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void log(UUID actorPersonId, UUID roleId, String action, String entityType, UUID entityId, String previousValue, String newValue) {
        if (actorPersonId == null || roleId == null) {
            return;
        }
        FinancialAuditLog log = new FinancialAuditLog();
        Person actor = new Person();
        actor.setId(actorPersonId);
        AppRole role = new AppRole();
        role.setId(roleId);
        log.setActor(actor);
        log.setRoleAtTimeOfAction(role);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPreviousValue(previousValue);
        log.setNewValue(newValue);
        auditLogRepository.save(log);
    }
}
