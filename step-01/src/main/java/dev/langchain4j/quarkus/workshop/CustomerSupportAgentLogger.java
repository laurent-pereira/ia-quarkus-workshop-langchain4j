package dev.langchain4j.quarkus.workshop;

import jakarta.enterprise.context.SessionScoped;
import org.jboss.logging.Logger;

@SessionScoped
public class CustomerSupportAgentLogger {
  private static final Logger LOG = Logger.getLogger(CustomerSupportAgentLogger.class);

  public void logPrompt(String prompt) {
    LOG.infof("Prompt envoyé à l'agent: %s", prompt);
  }

  public void logResponse(String response) {
    LOG.infof("Réponse reçue de l'agent: %s", response);
  }
}
