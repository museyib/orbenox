package com.orbenox.erp.enums;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.*;

@Service
public class EnumService {
    @Cacheable(ACTIONS)
    public List<String> getActions() {
        return Arrays.stream(Action.values()).map(Enum::name).toList();
    }

    @Cacheable(ACCOUNT_TYPES)
    public List<String> getAccountTypes() {
        return Arrays.stream(AccountType.values()).map(Enum::name).toList();
    }

    @Cacheable(APPROVAL_STATUSES)
    public List<String> getApprovalStatuses() {
        return Arrays.stream(ApprovalStatus.values()).map(Enum::name).toList();
    }

    @Cacheable(DOCUMENT_STATUSES)
    public List<String> getDocumentStatuses() {
        return Arrays.stream(DocumentStatus.values()).map(Enum::name).toList();
    }

    @Cacheable(PARTNER_ROLES)
    public List<String> getPartnerRoles() {
        return Arrays.stream(PartnerRole.values()).map(Enum::name).toList();
    }

    @Cacheable(PARTNER_TYPES)
    public List<String> getPartnerTypes() {
        return Arrays.stream(PartnerType.values()).map(Enum::name).toList();
    }
}

