package com.achhecode.browser_pilot.website.linkedin.page.games.zip;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController 
@RequestMapping ("/api/helper/zip")
public class ZipCommandController {

    private final ZipCommandService zipCommandService;

    public ZipCommandController(
            ZipCommandService zipCommandService
    ) {
        this.zipCommandService = zipCommandService;
    }

    @PostMapping ("/instruction")
    public ZipInstructionResponse instruction(
            @Valid @RequestBody ZipInstructionRequest request
    ) {

        log.info("Received instruction: {}", request.instruction());

        String expanded =
        zipCommandService.expandInstruction(
                request.instruction()
        );

        String reversed =
                zipCommandService.reverseInstruction(
                        expanded
                );

        int commandCount = expanded.split(",").length;

        return new ZipInstructionResponse(
                expanded,
                reversed,
                commandCount
        );
    }
}