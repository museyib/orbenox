package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.action.Action;
import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.common.action.ActionItem;
import com.orbenox.erp.common.action.ActionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public ResourceItem update(Long id, UpdateResourceRequest request) {
        Resource resource = resourceRepository.findByIdAndDeletedFalse(id);
        resourceMapper.updateEntityFromDto(request, resource);
        Set<ActionDto> toRemove = request.getActionsToRemove();
        resourceActionRepository.deleteByResourceIdAndActions(id, toRemove.stream().map(ActionDto::id).collect(Collectors.toSet()));
        List<ResourceAction> resourceActions = request.getActionsToAdd().stream()
                .map(actionDto -> {
                    ResourceAction resourceAction = new ResourceAction();
                    Action action = em.getReference(Action.class, actionDto.id());
                    resourceAction.setAction(action);
                    resourceAction.setResource(resource);
                    return resourceAction;
                }).toList();
        List<ResourceAction> existingActions = resourceRepository.getActionItemsByResourceId(id).stream().map(item -> {
            ResourceAction resourceAction = new ResourceAction();
            Action action = em.getReference(Action.class, item.getId());
            resourceAction.setAction(action);
            resourceAction.setResource(resource);
            return resourceAction;
        }).toList();

        List<ResourceAction> allActions = new ArrayList<>();
        allActions.addAll(resourceActions);
        allActions.addAll(existingActions);

        if (!allActions.isEmpty())
            resourceActionRepository.saveAll(allActions);

        Set<Action> actions = request.getActionsToAdd().stream()
                .map(actionDto -> actionRepository.findByIdAndDeletedFalse(actionDto.id()))
                .collect(Collectors.toSet());
        resource.setActions(actions);

        return resourceRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Resource entity = resourceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
