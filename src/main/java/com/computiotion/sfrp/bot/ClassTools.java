package com.computiotion.sfrp.bot;

import com.google.common.reflect.ClassPath;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassTools {
    /**
     * Finds all classes in the specified package using Google Guice
     *
     * @param packageName The package name in which to search for classes
     * @return A set of classes found in the specified package
     * @throws IOException If an I/O error occurs
     * @see <a href="https://www.baeldung.com/java-find-all-classes-in-package">Baeldung</a>
     */
    public static Set<Class<?>> getAllClasses(String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .equalsIgnoreCase(packageName))
                .map(clazz -> clazz.load())
                .collect(Collectors.toSet());
    }

    public static <B> Set<Class<? extends B>> getSubclassesOf(String packageName, @NotNull Class<B> baseClass) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getTopLevelClasses(packageName)
                .stream()
                .map(ClassPath.ClassInfo::load)
                .filter(baseClass::isAssignableFrom)
                .filter(clazz -> !clazz.equals(baseClass))
                .map(clazz -> (Class<? extends B>) clazz)
                .collect(Collectors.toSet());
    }
}
