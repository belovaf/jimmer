package org.babyfish.jimmer.sql.ast.impl.mutation;

import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.KeyMatcher;
import org.babyfish.jimmer.sql.DissociateAction;
import org.babyfish.jimmer.sql.TargetTransferMode;
import org.babyfish.jimmer.sql.ast.mutation.*;
import org.babyfish.jimmer.sql.event.TriggerType;
import org.babyfish.jimmer.sql.event.Triggers;
import org.babyfish.jimmer.sql.runtime.ExceptionTranslator;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.Set;

public class SaveOptionsImpl implements SaveOptions {

    JSqlClientImplementor sqlClient;

    SaveMode mode = SaveMode.UPSERT;

    AssociatedSaveMode associatedMode = AssociatedSaveMode.REPLACE;

    UserOptimisticLock<?, ?> userOptimisticLock;

    public SaveOptionsImpl(JSqlClientImplementor sqlClient) {
        this.sqlClient = sqlClient;
    }

    @Override
    public JSqlClientImplementor getSqlClient() {
        return sqlClient;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public SaveMode getMode() {
        return mode;
    }

    @Override
    public AssociatedSaveMode getAssociatedMode(ImmutableProp prop) {
        return associatedMode;
    }

    @Override
    public Triggers getTriggers() {
        return sqlClient.getTriggerType() == TriggerType.BINLOG_ONLY ?
                null :
                sqlClient.getTriggers(true);
    }

    @Override
    public KeyMatcher getKeyMatcher(ImmutableType type) {
        return type.getKeyMatcher();
    }

    @Override
    @Nullable
    public UpsertMask<?> getUpsertMask(ImmutableType type) {
        return null;
    }

    @Override
    public boolean isTargetTransferable(ImmutableProp prop) {
        return prop.getTargetTransferMode() == TargetTransferMode.ALLOWED;
    }

    @Override
    public DeleteMode getDeleteMode() {
        return DeleteMode.PHYSICAL;
    }

    @Override
    public int getMaxCommandJoinCount() {
        return sqlClient.getMaxCommandJoinCount();
    }

    @Override
    public DissociateAction getDissociateAction(ImmutableProp prop) {
        return DissociateAction.NONE;
    }

    @Override
    public boolean isPessimisticLocked(ImmutableType type) {
        return false;
    }

    @Override
    public UnloadedVersionBehavior getUnloadedVersionBehavior(ImmutableType type) {
        return UnloadedVersionBehavior.IGNORE;
    }

    @Override
    public UserOptimisticLock<?, ?> getUserOptimisticLock(ImmutableType type) {
        return userOptimisticLock;
    }

    @Override
    public boolean isAutoCheckingProp(ImmutableProp prop) {
        return false;
    }

    @Override
    public boolean isIdOnlyAsReference(ImmutableProp prop) {
        return true;
    }

    @Override
    public boolean isKeyOnlyAsReference(ImmutableProp prop) {
        return false;
    }

    @Override
    public boolean isBatchForbidden() {
        return sqlClient.isBatchForbidden(false);
    }

    @Override
    public boolean isConstraintViolationTranslatable() {
        return sqlClient.isConstraintViolationTranslatable();
    }

    @Override
    public @Nullable ExceptionTranslator<Exception> getExceptionTranslator() {
        return null;
    }

    @Override
    public boolean isTransactionRequired() {
        return sqlClient.isMutationTransactionRequired();
    }
}
