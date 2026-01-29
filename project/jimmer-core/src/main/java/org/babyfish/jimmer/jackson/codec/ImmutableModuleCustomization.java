package org.babyfish.jimmer.jackson.codec;

import org.babyfish.jimmer.jackson.v2.ModulesRegistrar;
import org.babyfish.jimmer.jackson.v3.ModulesRegistrar3;

public class ImmutableModuleCustomization implements JsonCodecCustomization {
    @Override
    public void customizeV2(com.fasterxml.jackson.databind.json.JsonMapper.Builder builder) {
        ModulesRegistrar.registerImmutableModule(builder);
    }

    @Override
    public void customizeV3(tools.jackson.databind.json.JsonMapper.Builder builder) {
        ModulesRegistrar3.registerImmutableModule(builder);
    }
}
