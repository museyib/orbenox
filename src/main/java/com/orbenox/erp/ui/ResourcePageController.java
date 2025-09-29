package com.orbenox.erp.ui;

import com.orbenox.erp.common.action.ActionDto;
import com.orbenox.erp.common.action.ActionService;
import com.orbenox.erp.common.resource.ResourceDto;
import com.orbenox.erp.common.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class ResourcePageController {
    private final ResourceService resourceService;
    private final ActionService actionService;

    @GetMapping("/resources")
    public String getResources(Model model) {
        model.addAttribute("resources", resourceService.findAll());
        return "resource/resources";
    }

    @GetMapping("/resources/create")
    public String createResource(Model model) {
        return "resource/create";
    }

    @GetMapping("/resources/edit/{id}")
    public String editResource(@PathVariable Long id, Model model) {
        ResourceDto resourceDTO = resourceService.findById(id);
        List<ActionDto> actions = actionService.findAll();
        actions.removeAll(resourceDTO.getActions());
        model.addAttribute("resource", resourceDTO);
        model.addAttribute("actions", actions);
        return "resource/edit";
    }
}
