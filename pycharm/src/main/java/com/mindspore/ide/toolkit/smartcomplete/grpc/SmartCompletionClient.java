package com.mindspore.ide.toolkit.smartcomplete.grpc;

import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.protomessage.CompleteRequest;
import com.mindspore.ide.toolkit.protomessage.GreeterGrpc;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SmartCompletionClient {
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public SmartCompletionClient(Channel channel) {
        this.blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public Optional<CompleteReply> retrieveCompletions(String before, String prefix) {
        CompleteRequest request = CompleteRequest.newBuilder().setPrefix(prefix).setBefore(before).setAfter("").build();

        try {
            long startTime = System.currentTimeMillis();
            Optional<CompleteReply> response =
                    Optional.of((this.blockingStub.withDeadlineAfter(300L, TimeUnit.MILLISECONDS))
                            .getRecommendation(request));
            long endTime = System.currentTimeMillis();
            log.info("retrieveCompletion cost: {}", endTime - startTime);
            return response;
        } catch (StatusRuntimeException statusRuntimeException) {
            log.warn("SmartCompletionClient.retrieveCompletions failed.", statusRuntimeException);
            return Optional.empty();
        }
    }
}