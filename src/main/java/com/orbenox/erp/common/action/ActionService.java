package com.orbenox.erp.common.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {
    private final ActionRepository actionRepository;
    private final ActionMapper actionMapper;

    public List<ActionDto> findAll() {
        return actionMapper.toDTOList(actionRepository.findAllByDeletedFalseOrderByIdAsc());
    }
}
