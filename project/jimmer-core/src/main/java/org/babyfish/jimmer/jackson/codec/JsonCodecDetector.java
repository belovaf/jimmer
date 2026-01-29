package org.babyfish.jimmer.jackson.codec;

import org.babyfish.jimmer.jackson.v2.JsonCodecImpl;
import org.babyfish.jimmer.jackson.v3.JsonCodecImpl3;

import static org.babyfish.jimmer.jackson.ClassUtils.classExists;

class JsonCodecDetector {
    static final JsonCodec<?> JSON_CODEC;
    static final JsonCodec<?> JSON_CODEC_WITHOUT_IMMUTABLE_MODULE;

    static {
        if (classExists("tools.jackson.databind.ObjectMapper")) {
            JSON_CODEC_WITHOUT_IMMUTABLE_MODULE = new JsonCodecImpl3();
        } else if (classExists("com.fasterxml.jackson.databind.ObjectMapper")) {
            JSON_CODEC_WITHOUT_IMMUTABLE_MODULE = new JsonCodecImpl();
        } else {
            throw new IllegalStateException("Jackson is required");
        }
        JSON_CODEC = JSON_CODEC_WITHOUT_IMMUTABLE_MODULE.withCustomizations(new ImmutableModuleCustomization());
    }
}
