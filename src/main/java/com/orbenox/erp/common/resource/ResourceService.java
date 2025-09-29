package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.action.ActionRepository;
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

    public List<ResourceDto> findAll() {
        return resourceMapper.toDTOList(resourceRepository.findAll());
    }

    public ResourceDto findById(Long id) {
        return resourceMapper.toDto(resourceRepository.findById(id).orElseThrow());
    }

    public ResourceDto create(ResourceDto dto) {
        return resourceMapper.toDto(resourceRepository.save(resourceMapper.toEntity(dto)));
    }

    public ResourceDto update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findById(id).orElseThrow();
        resourceMapper.updateEntityFromDto(dto, resource);
        if (dto.getActions() != null) {
            Set<Action> actions = dto.getActions().stream()
                    .map(actionDto -> actionRepository.findById(actionDto.getId()).orElseThrow())
                    .collect(Collectors.toSet());
            resource.setActions(actions);
        }
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}
