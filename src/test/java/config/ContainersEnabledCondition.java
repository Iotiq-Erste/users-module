package config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ContainersEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String testContainersDisabled = context.getEnvironment().getProperty("TEST_CONTAINERS_DISABLED");
        return testContainersDisabled == null || !Boolean.parseBoolean(testContainersDisabled);
    }

}
