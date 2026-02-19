package com.orbenox.erp.domain.resource;

import com.orbenox.erp.enums.Action;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.RESOURCES;
import static com.orbenox.erp.common.Utilities.isBlank;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Cacheable(RESOURCES)
    public Slice<ResourceItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return resourceRepository.getAllItems(PageRequest.of(page, size));
        return resourceRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public ResourceData getItemById(Long id) {
        ResourceItem resourceItem = resourceRepository.getItemById(id);
        List<String> actionItems = resourceRepository.getActionItemsByResourceId(id);
        ResourceData resourceData = new ResourceData();
        resourceData.setResource(resourceItem);
        resourceData.setActions(actionItems);
        return resourceData;
    }

    @CacheEvict(value = {RESOURCES, LOOKUPS}, allEntries = true)
    public ResourceItem create(ResourceCreateDto dto) {
        Resource resource = resourceMapper.toEntity(dto);
        resource.setActions(dto.actions().stream().map(Action::valueOf).collect(Collectors.toSet()));
        Resource saved = resourceRepository.save(resource);
        return resourceRepository.getItemById(saved.getId());
    }

    @CacheEvict(value = {RESOURCES, LOOKUPS}, allEntries = true)
    @Transactional
    public ResourceItem update(Long id, ResourceUpdateDto dto) {
        Resource resource = resourceRepository.findByIdAndDeletedFalse(id);
        resourceMapper.updateEntityFromDto(dto, resource);
        Set<Action> actions = dto.actions().stream().map(Action::valueOf).collect(Collectors.toSet());
        resource.setActions(actions);
        return resourceRepository.getItemById(id);
    }

    @CacheEvict(value = {RESOURCES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Resource entity = resourceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}

