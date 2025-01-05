package com.computiotion.sfrp.bot;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassTools {
    public static <B> Set<Class<? extends B>> getSubclassesOf(String packageName, @NotNull Class<B> baseClass) throws IOException, ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(baseClass));

        return provider.findCandidateComponents(packageName.replace(".", "/"))
                .stream().map(bean -> {
                    try {
                        //noinspection unchecked
                        return (Class<? extends B>) Class.forName(bean.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }
}
