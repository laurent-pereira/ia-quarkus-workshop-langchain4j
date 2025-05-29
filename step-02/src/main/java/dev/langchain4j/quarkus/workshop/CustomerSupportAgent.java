package dev.langchain4j.quarkus.workshop;

import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@SessionScoped
@RegisterAiService(modelName = "mistral")
public interface CustomerSupportAgent {

    String chat(String userMessage);
}
