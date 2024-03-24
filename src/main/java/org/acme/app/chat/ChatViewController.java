package org.acme.app.chat;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.app.prompt.PromptService;

import java.net.URI;
import java.util.Map;

@Path("chat-view")
public class ChatViewController {
    @Inject
    private Template chatView;
    @Inject
    private ChatService chatService;
    @Inject
    private PromptService promptService;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getView() {
        var chatMessages = chatService.getMessages();
        return chatView.data("chatMessages", chatMessages);
    }

    @GET()
    @Path("newChat")
    public Response startNewChat(@QueryParam("prompt") String selectedPrompt) {
        var promptData = promptService.getPrompt(selectedPrompt);
        chatService.startNewChat(promptData.text());
        return Response.temporaryRedirect(URI.create("/chat-view")).build();
    }

    @POST()
    @Path("send")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance sendMessage(@FormParam("text") String messageText) {
        chatService.chat(messageText);
        var chatMessages = chatService.getMessages();
        return chatView.data("chatMessages", chatMessages);
    }
}