package reflection.di.framework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

public class Bootstrapper {

    public static ObjectFactory startup(String basePackage) {
        Map<Class<?>, Object> instances = loadAnnotatedClasses(basePackage);

        populateProperties(instances);

        return new ObjectFactory(instances);
    }

    private static Map<Class<?>, Object> loadAnnotatedClasses(String basePackage) {
        Map<Class<?>, Object> instances = new HashMap<>();

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = basePackage.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);

            for (Iterator<URL> iterator = resources.asIterator(); iterator.hasNext(); ) {
                URL url = iterator.next();
                File file = new File(url.toURI());

                stream(requireNonNull(file.listFiles()))
                        .map(File::getName)
                        .filter(name -> name.endsWith(".class"))
                        .map(name -> name.substring(0, name.lastIndexOf('.')))
                        .map(name -> loadClass(basePackage, name))
                        .filter(clazz -> clazz.isAnnotationPresent(Component.class))
                        .map(Bootstrapper::createInstance)
                        .forEach(instance ->
                                instances.put(instance.getClass(), instance)
                        );
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return instances;
    }

    private static void populateProperties(Map<Class<?>, Object> instances) {
        instances.values().forEach(instance -> {
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object dependency = instances.get(field.getType());
                    try {
                        field.setAccessible(true);
                        field.set(instance, dependency);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private static Object createInstance(Class<?> classObject) {
        try {
            return classObject.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> loadClass(String basePackage, String className) {
        try {
            return Class.forName(basePackage + "." + className);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static class ObjectFactory {
        private final Map<Class<?>, Object> instances;

        private ObjectFactory(Map<Class<?>, Object> instances) {
            this.instances = instances;
        }

        public <T> T getInstance(Class<T> clazz) {
            return (T) instances.get(clazz);
        }
    }
}
