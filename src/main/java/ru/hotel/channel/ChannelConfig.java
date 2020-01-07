package ru.hotel.channel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.hotel.domain.Book;

@IntegrationComponentScan
@SuppressWarnings({"resource", "Duplicates", "InfiniteLoopStatement"})
@ComponentScan
@Configuration
@EnableIntegration
public class ChannelConfig {

    @Bean
    public QueueChannel booksChannel() {
        return MessageChannels.queue(10).get();
    }

    @Bean
    public PublishSubscribeChannel interestingBookChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean
    public PublishSubscribeChannel nonInterestingBookChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean (name = PollerMetadata.DEFAULT_POLLER )
    public PollerMetadata poller () {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2).get() ;
    }

    @Bean
    public IntegrationFlow bookFlow() {
        return IntegrationFlows.from("booksChannel")
                .split()
                .routeToRecipients(r -> r.recipient("interestingBookChannel", this::isInteresting).recipient("nonInterestingBookChannel", this::notInteresting))
                .get();
    }

    @Bean
    public IntegrationFlow bookFlowInteresting() {
        return IntegrationFlows.from("interestingBookChannel")
                .handle("bookInterestingService", "isInteresting")
                .get();
    }

    @Bean
    public IntegrationFlow bookFlowNonInteresting() {
        return IntegrationFlows.from("nonInterestingBookChannel")
                .handle("bookInterestingService", "isNonInteresting")
                .get();
    }

    boolean isInteresting(Book book) {
        return book.getGenre().stream().anyMatch(g -> g.equalsIgnoreCase("Фантастика"));
    }

    boolean notInteresting(Book book) {
        return !isInteresting(book);
    }
}
