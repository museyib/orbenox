package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.action.ActionRepository;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ActionRepository actionRepository;
    private final LocalizationService i18n;

    public List<ResourceDto> findAll() {
        return resourceMapper.toDtoList(resourceRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public ResourceDto findById(Long id) {
        return resourceMapper.toDto(resourceRepository.findByIdAndDeletedFalse(id));
    }

    public ResourceDto create(ResourceDto dto) {
        return resourceMapper.toDto(resourceRepository.save(resourceMapper.toEntity(dto)));
    }

    public ResourceDto update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findByIdAndDeletedFalse(id);
        resourceMapper.updateEntityFromDto(dto, resource);
        if (dto.actions() != null) {
            Set<Action> actions = dto.actions().stream()
                    .map(actionDto -> actionRepository.findByIdAndDeletedFalse(actionDto.id()))
                    .collect(Collectors.toSet());
            resource.setActions(actions);
        }
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    public void softDelete(Long id) {
        Resource entity = resourceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Resource saved  = resourceRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
