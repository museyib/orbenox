package com.orbenox.erp.domain.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final ActionRepository actionRepository;

    public List<ActionItem> getAllItems() {
        return actionRepository.getAllItems();
    }
}
