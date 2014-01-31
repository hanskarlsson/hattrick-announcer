package com.jayway.messagecounter.domain;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jayway.messagecounter.infrastructure.messaging.protocol.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MessageCounter {
    private static final Logger log = LoggerFactory.getLogger(MessageCounter.class);

    private AtomicLong numberOfTotalMessages = new AtomicLong();
    private AtomicLong numberOfLogMessages = new AtomicLong();
    private AtomicLong numberOfServiceMessages = new AtomicLong();
    private AtomicLong numberOfGameMessages = new AtomicLong();

    // For idempotency
    LoadingCache<String, Boolean> messageIdCache;

    public MessageCounter(long maxMessageIdsToCache, long maxTimeToCacheMessageIds, TimeUnit timeUnit) {
        messageIdCache = CacheBuilder.newBuilder()
                .maximumSize(maxMessageIdsToCache)
                .expireAfterWrite(maxTimeToCacheMessageIds, timeUnit)
                .build(new CacheLoader<String, Boolean>() {
                    @Override
                    public Boolean load(String key) throws Exception {
                        return false;
                    }
                });
    }

    public void messageReceived(String messageId, final String route) {
        if (Strings.isNullOrEmpty(messageId)) {
            log.info("Message was received without message id (current message count is {})", numberOfTotalMessages.incrementAndGet());
            increaseRouteSpecificCounter(route);
        } else {
            try {
                messageIdCache.get(messageId, new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        increaseRouteSpecificCounter(route);
                        log.info("Current number of received messages: {}", numberOfTotalMessages.incrementAndGet());
                        return true;
                    }
                });
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void increaseRouteSpecificCounter(String route) {
        Topic topic = Topic.valueOfIgnoreCase(route);
        if (topic != null) {
            switch (topic) {
                case GAME:
                    numberOfGameMessages.incrementAndGet();
                    break;
                case LOG:
                    numberOfLogMessages.incrementAndGet();
                    break;
                case SERVICE:
                    numberOfServiceMessages.incrementAndGet();
                    break;
            }
        }
    }

    public MessageStatistics getStatistics() {
        return new MessageStatistics(numberOfTotalMessages.get(), numberOfLogMessages.get(), numberOfGameMessages.get(), numberOfServiceMessages.get());
    }
}
