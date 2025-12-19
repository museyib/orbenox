package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.action.ActionItem;
import com.orbenox.erp.common.action.ActionRepository;
import jakarta.transaction.Transactional;
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

    public List<ResourceItem> findAll() {
        return resourceRepository.getAllItems();
    }

    public ResourceData findById(Long id) {
        ResourceItem resourceItem = resourceRepository.getItemById(id);
        List<ActionItem> actionItems = resourceRepository.getActionItemsByResourceId(id);
        ResourceData resourceData = new ResourceData();
        resourceData.setResource(resourceItem);
        resourceData.setActions(actionItems);
        return resourceData;
    }

    public ResourceItem create(ResourceDto dto) {
        Resource resource = resourceRepository.save(resourceMapper.toEntity(dto));
        return resourceRepository.getItemById(resource.getId());
    }

    @Transactional
    public ResourceItem update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findByIdAndDeletedFalse(id);
        resourceMapper.updateEntityFromDto(dto, resource);
        if (dto.actions() != null) {
            Set<Action> actions = dto.actions().stream()
                    .map(actionDto -> actionRepository.findByIdAndDeletedFalse(actionDto.id()))
                    .collect(Collectors.toSet());
            resource.setActions(actions);
        }
        return resourceRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Resource entity = resourceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
