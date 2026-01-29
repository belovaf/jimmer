package org.babyfish.jimmer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.babyfish.jimmer.jackson.ImmutableModuleRequiredException;
import org.babyfish.jimmer.model.BookDraft;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.babyfish.jimmer.jackson.codec.JsonCodec.jsonCodecWithoutImmutableModule;

public class NoImmutableModuleTest {

    @Test
    public void test() throws JsonProcessingException {
        Throwable ex = Assertions.assertThrows(Throwable.class, () -> {
            jsonCodecWithoutImmutableModule().writer().writeAsString(
                    BookDraft.$.produce(draft -> {
                    })
            );
        });
        Assertions.assertInstanceOf(
                ImmutableModuleRequiredException.class,
                ex.getCause()
        );
    }
}
