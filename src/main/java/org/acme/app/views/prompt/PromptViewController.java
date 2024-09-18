package org.acme.app.views.prompt;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.app.repository.PromptRepository;

@Path("prompt-view")
public class PromptViewController {

    @Inject
    Template promptsView;

    @Inject
    PromptRepository promptRepository;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getView() {
        var prompts = promptRepository.findAll();
        return promptsView.data("promptList", prompts);
    }

}
