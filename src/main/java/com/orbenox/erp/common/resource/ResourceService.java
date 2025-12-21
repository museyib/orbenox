package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.common.action.ActionItem;
import com.orbenox.erp.common.action.ActionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ActionRepository actionRepository;
    private final ResourceActionRepository resourceActionRepository;
    private final EntityManager em;

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
        Resource resource = resourceRepository.save(resourceMapper.toEntity(dto));
        return resourceRepository.getItemById(resource.getId());
    }

    @Transactional
    public ResourceItem update(Long id, ResourceDto dto) {
        Resource resource = resourceRepository.findByIdAndDeletedFalse(id);
        resourceMapper.updateEntityFromDto(dto, resource);
        Set<ActionDto> incomingActions = dto.actions();
        Set<ActionDto> existingActions = resourceMapper.toDto(resource).actions();
        Set<ActionDto> toRemove = new HashSet<>(existingActions);
        Set<ActionDto> toAdd = new HashSet<>(incomingActions);
        toRemove.removeAll(incomingActions);
        toAdd.removeAll(existingActions);
        resourceActionRepository.deleteByResourceIdAndActions(id, toRemove.stream().map(ActionDto::id).collect(Collectors.toSet()));

        List<ResourceAction> resourceActions = dto.actions().stream()
                .filter(toAdd::contains)
                .map(actionDto -> {
                    ResourceAction resourceAction = new ResourceAction();
                    Action action = em.getReference(Action.class, actionDto.id());
                    resourceAction.setAction(action);
                    resourceAction.setResource(resource);
                    return resourceAction;
                }).toList();
        resourceActionRepository.saveAll(resourceActions);

        Set<Action> actions = dto.actions().stream()
                .map(actionDto -> actionRepository.findByIdAndDeletedFalse(actionDto.id()))
                .collect(Collectors.toSet());
        resource.setActions(actions);

        resourceRepository.save(resource);
        return resourceRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Resource entity = resourceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
