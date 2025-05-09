package org.babyfish.jimmer.sql.exception;

import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseValidationException extends ExecutionException {

    private final List<Item> items;

    public DatabaseValidationException(List<Item> items) {
        super(message(items));
        this.items = Collections.unmodifiableList(
                new ArrayList<>(items)
        );
    }

    @NotNull
    public List<Item> getItems() {
        return items;
    }

    private static String message(List<Item> items) {
        StringBuilder builder = new StringBuilder("Failed to validate database: \n");
        for (Item item : items) {
            builder.append("- ");
            if (item.getProp() != null) {
                builder.append(item.getProp());
            } else {
                builder.append(item.getType());
            }
            builder.append(": ").append(item.getMessage()).append('\n');
        }
        return builder.toString();
    }

    public static class Item {

        private final ImmutableType type;

        private final ImmutableProp prop;

        private final String message;

        public Item(ImmutableType type, ImmutableProp prop, String message) {
            this.type = type;
            this.prop = prop;
            this.message = message;
        }

        @NotNull
        public ImmutableType getType() {
            return type;
        }

        @Nullable
        public ImmutableProp getProp() {
            return prop;
        }

        @NotNull
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "type=" + type +
                    ", prop=" + prop +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
