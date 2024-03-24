package org.acme.app.prompt;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("prompt-view")
public class PromptViewController {
    @Inject
    private Template promptsView;
    @Inject
    private PromptService promptService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getView() {
        var prompts = promptService.getAllPrompts();
        return promptsView.data("promptList", prompts);
    }

}
