package org.acme.app.repository;

import org.acme.app.dto.PromptDto;

import java.util.List;

public interface PromptRepository {
    List<PromptDto> findAll();
    PromptDto findPrompt(String promptName);
}
