package org.babyfish.jimmer.client.meta.impl;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.babyfish.jimmer.client.meta.Schema;
import org.babyfish.jimmer.jackson.codec.JsonCodec;
import org.babyfish.jimmer.jackson.codec.JsonWriter;
import org.babyfish.jimmer.jackson.codec.SharedAttributesCustomization;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.singletonMap;
import static org.babyfish.jimmer.jackson.codec.JsonCodec.jsonCodec;

public class Schemas {

    public static final Object IGNORE_DEFINITIONS = new Object();

    private static final Object GROUPS = new Object();

    private static final JsonCodec<?> READ_SERVICES_JSON_CODEC = jsonCodec()
            .withCustomizations(new SharedAttributesCustomization(singletonMap(IGNORE_DEFINITIONS, true)));

    private static final JsonWriter WRITER = jsonCodec().writer().withDefaultPrettyPrinter();

    private Schemas() {
    }

    public static void writeTo(Schema schema, Writer writer) throws Exception {
        WRITER.write(writer, schema);
    }

    public static Schema readFrom(Reader reader) throws Exception {
        return readFrom(reader, null);
    }

    public static Schema readFrom(Reader reader, Set<String> groups) throws Exception {
        return jsonCodec()
                .withCustomizations(new SharedAttributesCustomization(singletonMap(GROUPS, groups)))
                .readerFor(SchemaImpl.class)
                .read(reader);
    }

    public static Schema readServicesFrom(Reader reader) throws Exception {
        return READ_SERVICES_JSON_CODEC.readerFor(SchemaImpl.class).read(reader);
    }

    @SuppressWarnings("unchecked")
    static boolean isAllowed(DeserializationContext ctx, Collection<String> elementGroups) {
        if (elementGroups == null) {
            return true;
        }
        Set<String> allowedGroups = (Set<String>) ctx.getAttribute(GROUPS);
        if (allowedGroups == null) {
            return true;
        }
        for (String elementGroup : elementGroups) {
            if (allowedGroups.contains(elementGroup)) {
                return true;
            }
        }
        return false;
    }
}
