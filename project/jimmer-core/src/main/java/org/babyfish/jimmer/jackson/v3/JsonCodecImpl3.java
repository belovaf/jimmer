package org.babyfish.jimmer.jackson.v3;

import org.babyfish.jimmer.jackson.codec.*;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.babyfish.jimmer.jackson.v3.ModulesRegistrar3.registerWellKnownModules;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

public class JsonCodecImpl3 implements JsonCodec<JavaType> {
    private final JsonMapper mapper;
    private final JsonTypeFactory<JavaType> typeFactory;
    private final JsonConverter converter;

    public JsonCodecImpl3() {
        this(createDefaultMapper());
    }

    public JsonCodecImpl3(JsonMapper mapper) {
        this.mapper = mapper;
        this.typeFactory = new JsonTypeFactoryImpl3(mapper.getTypeFactory());
        this.converter = new JsonConverterImpl3(mapper);
    }

    private static JsonMapper createDefaultMapper() {
        JsonMapper.Builder builder = JsonMapper.builder()
                .disable(FAIL_ON_TRAILING_TOKENS)
                .disable(SORT_PROPERTIES_ALPHABETICALLY);

        registerWellKnownModules(builder);

        return builder.build();
    }

    @Override
    public JsonCodec<JavaType> withCustomizations(JsonCodecCustomization... customizations) {
        JsonMapper.Builder builder = mapper.rebuild();
        for (JsonCodecCustomization c : customizations) {
            c.customizeV3(builder);
        }
        return new JsonCodecImpl3(builder.build());
    }

    @Override
    public JsonConverter converter() {
        return converter;
    }

    @Override
    public <T> JsonReader<T> readerFor(Class<T> clazz) {
        return new JsonReaderImpl3<>(mapper.readerFor(clazz));
    }

    @Override
    public <T> JsonReader<T> readerFor(TypeCreator<JavaType> typeCreator) {
        return new JsonReaderImpl3<>(mapper.readerFor(typeCreator.createType(typeFactory)));
    }

    @Override
    public <T> JsonReader<T[]> readerForArrayOf(Class<T> componentType) {
        return new JsonReaderImpl3<>(mapper.readerForArrayOf(componentType));
    }

    @Override
    public <T> JsonReader<List<T>> readerForListOf(Class<T> elementType) {
        return new JsonReaderImpl3<>(mapper.readerForListOf(elementType));
    }

    @Override
    public <V> JsonReader<Map<String, V>> readerForMapOf(Class<V> valueType) {
        return new JsonReaderImpl3<>(mapper.readerForMapOf(valueType));
    }

    @Override
    public JsonReader<Node> treeReader() {
        return new MappingJsonReader<>(new JsonReaderImpl3<>(mapper.readerFor(JsonNode.class)), NodeImpl3::new);
    }

    @Override
    public JsonWriter writer() {
        return new JsonWriterImpl3(mapper.writer());
    }

    @Override
    public JsonWriter writerFor(Class<?> clazz) {
        return new JsonWriterImpl3(mapper.writerFor(clazz));
    }

    @Override
    public JsonWriter writerFor(TypeCreator<JavaType> typeCreator) {
        return new JsonWriterImpl3(mapper.writerFor(typeCreator.createType(typeFactory)));
    }
}
