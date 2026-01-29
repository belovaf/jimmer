package org.babyfish.jimmer.spring.cfg;

import org.babyfish.jimmer.jackson.v2.ImmutableModuleV2;
import org.babyfish.jimmer.spring.repository.config.JimmerRepositoriesConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration(afterName = {
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration"
})
@EnableConfigurationProperties(JimmerProperties.class)
@Import({
        SqlClientConfig.class,
        JimmerRepositoriesConfig.class,
        ErrorTranslatorConfig.class
})
public class JimmerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ImmutableModuleV2.class)
    public ImmutableModuleV2 immutableModuleV2() {
        return new ImmutableModuleV2();
    }
}

