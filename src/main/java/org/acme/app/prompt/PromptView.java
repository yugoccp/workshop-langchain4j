package org.acme.app;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("prompts")
public class PromptView {

    @Inject
    private Template promptsView;

    @Inject
    private PromptService promptService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        var prompts = promptService.getAllPrompts();
        return promptsView.data("promptList", prompts);
    }
}
