package io.quarkus.runtime.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.IntFunction;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;

/**
 *
 */
public final class ConfigUtils {
    private ConfigUtils() {
    }

    public static <T> IntFunction<List<T>> listFactory() {
        return ArrayList::new;
    }

    public static <T> IntFunction<Set<T>> setFactory() {
        return LinkedHashSet::new;
    }

    public static <T> IntFunction<SortedSet<T>> sortedSetFactory() {
        return size -> new TreeSet<>();
    }

    public static SmallRyeConfigBuilder configBuilder() {
        return emptyConfigBuilder().addDiscoveredCustomizers().addDiscoveredSources();
    }

    public static SmallRyeConfigBuilder emptyConfigBuilder() {
        return new SmallRyeConfigBuilder()
                .forClassLoader(Thread.currentThread().getContextClassLoader())
                .withCustomizers(new QuarkusConfigBuilderCustomizer())
                .addDiscoveredConverters()
                .addDefaultInterceptors()
                .addDiscoveredInterceptors()
                .addDiscoveredSecretKeysHandlers()
                .addDefaultSources();
    }

    public static List<String> getProfiles() {
        return ConfigProvider.getConfig().unwrap(SmallRyeConfig.class).getProfiles();
    }

    public static boolean isProfileActive(final String profile) {
        return getProfiles().contains(profile);
    }

    /**
     * Checks if a property is present in the current Configuration.
     * <p>
     * Because the sources may not expose the property directly in {@link ConfigSource#getPropertyNames()}, we cannot
     * reliably determine if the property is present in the properties list. The property needs to be retrieved to make
     * sure it exists. Also, if the value is an expression, we want to ignore expansion, because this is not relevant
     * for the check and the expansion value may not be available at this point.
     * <p>
     * It may be interesting to expose such API in SmallRyeConfig directly.
     *
     * @param propertyName the property name.
     * @return true if the property is present or false otherwise.
     */
    public static boolean isPropertyPresent(String propertyName) {
        return ConfigProvider.getConfig().unwrap(SmallRyeConfig.class).isPropertyPresent(propertyName);
    }

    /**
     * Checks if a property has non-empty value in the current Configuration.
     * <p>
     * This method is similar to {@link #isPropertyPresent(String)}, but does not ignore expression expansion.
     *
     * @param propertyName the property name.
     * @return true if the property is present or false otherwise.
     */
    public static boolean isPropertyNonEmpty(String propertyName) {
        ConfigValue configValue = ConfigProvider.getConfig().getConfigValue(propertyName);
        return configValue.getValue() != null && !configValue.getValue().isEmpty();
    }

    /**
     * Checks if any of the given properties is present in the current Configuration.
     * <p>
     * Because the sources may not expose the property directly in {@link ConfigSource#getPropertyNames()}, we cannot
     * reliably determine if the property is present in the properties list. The property needs to be retrieved to make
     * sure it exists. Also, if the value is an expression, we want to ignore expansion, because this is not relevant
     * for the check and the expansion value may not be available at this point.
     * <p>
     * It may be interesting to expose such API in SmallRyeConfig directly.
     *
     * @param propertyNames The configuration property names
     * @return true if the property is present or false otherwise.
     */
    public static boolean isAnyPropertyPresent(Collection<String> propertyNames) {
        for (String propertyName : propertyNames) {
            if (isPropertyPresent(propertyName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the value of the first given property present in the current Configuration,
     * or {@link Optional#empty()} if none of the properties is present.
     *
     * @param <T> The property type
     * @param propertyNames The configuration property names
     * @param propertyType The type that the resolved property value should be converted to
     * @return true if the property is present or false otherwise.
     */
    public static <T> Optional<T> getFirstOptionalValue(List<String> propertyNames, Class<T> propertyType) {
        Config config = ConfigProvider.getConfig();
        for (String propertyName : propertyNames) {
            Optional<T> value = config.getOptionalValue(propertyName, propertyType);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }
}
