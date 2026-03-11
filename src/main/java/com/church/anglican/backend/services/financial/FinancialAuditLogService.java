package com.church.anglican.backend.services.financial;

import java.util.UUID;

public interface FinancialAuditLogService {
    void log(UUID actorPersonId, UUID roleId, String action, String entityType, UUID entityId, String previousValue, String newValue);
}
