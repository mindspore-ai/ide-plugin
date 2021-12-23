package com.mindspore.ide.toolkit.smartcomplete.grpc;

import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.protomessage.CompleteRequest;
import com.mindspore.ide.toolkit.protomessage.GreeterGrpc;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmartCompletionClient {
    private static final Logger logger = Logger.getLogger(SmartCompletionClient.class.getName());
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public SmartCompletionClient(Channel channel) {
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public Optional<CompleteReply> retrieveCompletions(String before, String prefix) {
        CompleteRequest request = CompleteRequest.newBuilder().setPrefix(prefix).setBefore(before).setAfter("").build();

        try {
            long b = System.currentTimeMillis();
            Optional<CompleteReply> response = Optional.of(((GreeterGrpc.GreeterBlockingStub) this.blockingStub.withDeadlineAfter(300L, TimeUnit.MILLISECONDS)).getRecommendation(request));
            long a = System.currentTimeMillis();
            long c = a - b;
            logger.log(Level.INFO, "retrieveCompletion cost: {0}", c);
            return response;
        } catch (StatusRuntimeException var11) {
            logger.log(Level.WARNING, "RPC failed: {0}", var11.getStatus());
            return Optional.empty();
        }
    }
}