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
        return resourceMapper.toDTOList(resourceRepository.findAllByDeleted(false));
    }

    public ResourceDto findById(Long id) {
        return resourceMapper.toDto(resourceRepository.findByIdAndDeleted(id, false));
    }

    public ResourceDto create(ResourceDto dto) {
        return resourceMapper.toDto(resourceRepository.save(resourceMapper.toEntity(dto)));
    }

    public ResourceDto update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findByIdAndDeleted(id, false);
        resourceMapper.updateEntityFromDto(dto, resource);
        if (dto.actions() != null) {
            Set<Action> actions = dto.actions().stream()
                    .map(actionDto -> actionRepository.findByIdAndDeleted(actionDto.id(), false))
                    .collect(Collectors.toSet());
            resource.setActions(actions);
        }
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}
