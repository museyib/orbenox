package com.orbenox.erp.ui;

import com.orbenox.erp.common.action.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class ActionPageController {
    private final ActionService actionService;

    @GetMapping("/actions")
    public String getActions(Model model) {
        model.addAttribute("actions", actionService.findAll());
        return "action/actions";
    }
}
