package org.babyfish.jimmer.sql.loader.graphql.impl;

import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.runtime.ImmutableSpi;
import org.babyfish.jimmer.sql.ast.Executable;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.fetcher.FieldFilter;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class BatchCommand<S, T> implements Executable<Map<S, T>> {

    private final JSqlClientImplementor sqlClient;

    private final Connection con;

    private final ImmutableProp prop;

    private final FieldFilter<Table<ImmutableSpi>> filter;

    private final Collection<ImmutableSpi> sources;

    private final T defaultValue;

    public BatchCommand(
            JSqlClientImplementor sqlClient,
            Connection con,
            ImmutableProp prop,
            FieldFilter<Table<ImmutableSpi>> filter,
            Collection<ImmutableSpi> sources,
            T defaultValue
    ) {
        this.sqlClient = sqlClient;
        this.con = con;
        this.prop = prop;
        this.filter = filter;
        this.sources = sources;
        this.defaultValue = defaultValue;
    }

    @Override
    public Map<S, T> execute(Connection con) {
        return sqlClient
                .getConnectionManager()
                .execute(con == null ? this.con : con, this::executeImpl);
    }

    @SuppressWarnings("unchecked")
    private Map<S, T> executeImpl(Connection con) {
        Map<S, T> resultMap = (Map<S, T>) new DataLoader(
                sqlClient,
                con,
                prop,
                filter
        ).load(sources);
        if (defaultValue == null || resultMap.size() == sources.size()) {
            return resultMap;
        }
        if (!(resultMap instanceof HashMap<?, ?>)) {
            resultMap = new LinkedHashMap<>(resultMap); // toMutableMap
        }
        for (ImmutableSpi source : sources) {
            resultMap.putIfAbsent((S) source, defaultValue);
        }
        return resultMap;
    }
}
