package com.orbenox.erp.domain.resource;

import com.orbenox.erp.domain.action.Action;
import com.orbenox.erp.domain.action.ActionItem;
import com.orbenox.erp.domain.action.ActionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ActionMapper actionMapper;

    public List<ResourceItem> getAllItems() {
        return resourceRepository.getAllItems();
    }

    public ResourceData getItemById(Long id) {
        ResourceItem resourceItem = resourceRepository.getItemById(id);
        List<ActionItem> actionItems = resourceRepository.getActionItemsByResourceId(id);
        ResourceData resourceData = new ResourceData();
        resourceData.setResource(resourceItem);
        resourceData.setActions(actionItems);
        return resourceData;
    }

    public ResourceItem create(ResourceDto dto) {
        Resource resource = resourceMapper.toEntity(dto);
        resource.setActions(actionMapper.toEntityList(dto.actions()));
        Resource saved = resourceRepository.save(resource);
        return resourceRepository.getItemById(saved.getId());
    }

    @Transactional
    public ResourceItem update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findByIdAndDeletedFalse(id);
        resourceMapper.updateEntityFromDto(dto, resource);
        Set<Action> actions = actionMapper.toEntityList(dto.actions());
        resource.setActions(actions);
        return resourceRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Resource entity = resourceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
