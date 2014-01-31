package com.jayway.messagecounter.domain;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MessageCounter {
    private static final Logger log = LoggerFactory.getLogger(MessageCounter.class);

    private AtomicLong numberOfReceivedMessages = new AtomicLong();

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

    public void messageReceived(String messageId) throws ExecutionException {
        final boolean messageHasAlreadyBeenCounted;
        if (Strings.isNullOrEmpty(messageId)) {
            messageHasAlreadyBeenCounted = false;
        } else {
            messageHasAlreadyBeenCounted = messageIdCache.get(messageId, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return true;
                }
            });
        }

        if (!messageHasAlreadyBeenCounted) {
            log.info("Current number of received messages: {}", numberOfReceivedMessages.incrementAndGet());
        }
    }

    public long currentNumberOfReceivedMessages() {
        return numberOfReceivedMessages.get();
    }
}
