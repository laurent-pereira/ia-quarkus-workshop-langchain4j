package dev.langchain4j.quarkus.workshop;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.jboss.logging.Logger;

public class RagRetriever {

    private static final Logger LOG = Logger.getLogger(RagRetriever.class);

    @Produces
    @ApplicationScoped
    public RetrievalAugmentor create(EmbeddingStore store, EmbeddingModel model) {
        var contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(3)
                .build();

        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(new ContentInjector() {
                    @Override
                    public UserMessage inject(List<Content> list, ChatMessage chatMessage) {
                        StringBuffer prompt = new StringBuffer(((UserMessage) chatMessage).singleText());
                        prompt.append("\nPlease, only use the following information:\n");
                        list.forEach(content -> prompt.append("- ").append(content.textSegment().text()).append("\n"));
                        String promptStr = prompt.toString();
                        LOG.infof("Prompt RAG généré : %s", promptStr);
                        return new UserMessage(promptStr);
                    }
                })
                .build();
    }
}
