package com.orbenox.erp.domain.action;

import com.orbenox.erp.enums.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {

    @Cacheable("actions")
    public List<String> getAllItems() {
        return Arrays.stream(Action.values()).map(Enum::name).toList();
    }
}
