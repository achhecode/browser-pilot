package com.achhecode.browser_pilot.screen;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mouse")
public class MouseController {

    private final MouseService mouseService;

    public MouseController(MouseService mouseService) {
        this.mouseService = mouseService;
    }


    // GET /api/mouse/click?x=500&y=300

    @GetMapping("/click")
    public String click(
            @RequestParam int x,
            @RequestParam int y) {

        mouseService.click(x, y);

        return "Clicked at (" + x + ", " + y + ")";
    }
}