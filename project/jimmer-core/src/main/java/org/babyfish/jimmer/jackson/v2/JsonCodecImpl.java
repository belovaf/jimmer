package org.babyfish.jimmer.jackson.v2;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.babyfish.jimmer.jackson.codec.*;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.babyfish.jimmer.jackson.v2.ModulesRegistrar.registerWellKnownModules;

public class JsonCodecImpl implements JsonCodec<JavaType> {
    private final JsonMapper mapper;
    private final JsonTypeFactory<JavaType> typeFactory;
    private final JsonConverter converter;

    public JsonCodecImpl() {
        this(createDefaultMapper());
    }

    public JsonCodecImpl(JsonMapper mapper) {
        this.mapper = mapper;
        this.typeFactory = new JsonTypeFactoryImpl(mapper.getTypeFactory());
        this.converter = new JsonConverterImpl(mapper);
    }

    private static JsonMapper createDefaultMapper() {
        JsonMapper.Builder builder = JsonMapper.builder()
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);

        registerWellKnownModules(builder);

        return builder.build();
    }

    @Override
    public JsonCodec<JavaType> withCustomizations(JsonCodecCustomization... customizations) {
        JsonMapper.Builder builder = mapper.rebuild();
        for (JsonCodecCustomization c : customizations) {
            c.customizeV2(builder);
        }
        return new JsonCodecImpl(builder.build());
    }

    @Override
    public JsonConverter converter() {
        return converter;
    }

    @Override
    public <T> JsonReader<T> readerFor(Class<T> clazz) {
        return new JsonReaderImpl<>(mapper.readerFor(clazz));
    }

    @Override
    public <T> JsonReader<T> readerFor(TypeCreator<JavaType> typeCreator) {
        return new JsonReaderImpl<>(mapper.readerFor(typeCreator.createType(typeFactory)));
    }

    @Override
    public <T> JsonReader<T[]> readerForArrayOf(Class<T> componentType) {
        return new JsonReaderImpl<>(mapper.readerForArrayOf(componentType));
    }

    @Override
    public <T> JsonReader<List<T>> readerForListOf(Class<T> elementType) {
        return new JsonReaderImpl<>(mapper.readerForListOf(elementType));
    }

    @Override
    public <V> JsonReader<Map<String, V>> readerForMapOf(Class<V> valueType) {
        return new JsonReaderImpl<>(mapper.readerForMapOf(valueType));
    }

    @Override
    public JsonReader<Node> treeReader() {
        return new MappingJsonReader<>(new JsonReaderImpl<>(mapper.readerFor(JsonNode.class)), NodeImpl::new);
    }

    @Override
    public JsonWriter writer() {
        return new JsonWriterImpl(mapper.writer());
    }

    @Override
    public JsonWriter writerFor(Class<?> clazz) {
        return new JsonWriterImpl(mapper.writerFor(clazz));
    }

    @Override
    public JsonWriter writerFor(TypeCreator<JavaType> typeCreator) {
        return new JsonWriterImpl(mapper.writerFor(typeCreator.createType(typeFactory)));
    }
}
