package com.mindspore.ide.toolkit.smartcomplete.grpc;

import com.mindspore.ide.toolkit.protomessage.CompleteReply;
import com.mindspore.ide.toolkit.smartcomplete.ModelManager;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SCAIPublicUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SCAIPublicUtils.class);

    public SCAIPublicUtils() {
    }

    public static Optional<CompleteReply> predictNextToken(String before, String options) throws CompletionException {
        ManagedChannel channel = ModelManager.INSTANCE.getChannel();
        Optional var5;
        try {
            SmartCompletionClient client = new SmartCompletionClient(channel);
            Optional<CompleteReply> completions = client.retrieveCompletions(before, options);
            var5 = completions;
        } finally {
            channel.shutdownNow();
        }
        return var5;
    }
}