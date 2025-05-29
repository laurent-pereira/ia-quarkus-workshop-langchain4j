package dev.langchain4j.quarkus.workshop;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;

@WebSocket(path = "/customer-support-agent")
public class CustomerSupportAgentWebSocket {

    private final CustomerSupportAgent customerSupportAgent;

    @Inject
    CustomerSupportAgentLogger logger;

    public CustomerSupportAgentWebSocket(CustomerSupportAgent customerSupportAgent) {
        this.customerSupportAgent = customerSupportAgent;
    }

    @OnOpen
    public String onOpen() {
        return "Welcome to Miles of Smiles! How can I help you today?";
    }

    @OnTextMessage
    public String onTextMessage(String message) {
        logger.logPrompt(message);
        String response = customerSupportAgent.chat(message);
        logger.logResponse(response);
        return response;
    }
}
