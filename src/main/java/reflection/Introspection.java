package reflection;

import java.lang.annotation.Retention;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static java.util.Arrays.stream;

class Introspection {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
         Class<MyClass> clazz = MyClass.class;
//        Class<MyClass> clazz = (Class<MyClass>) Class.forName("reflection.Introspection$MyClass");



        System.out.println("Class:");
        System.out.println(clazz.getCanonicalName());

        System.out.println("Annotations:");
        stream(clazz.getDeclaredAnnotations()).forEach(annotation -> {
            System.out.println(annotation.toString());
        });

        System.out.println("Fields:");
        stream(clazz.getDeclaredFields()).forEach(field -> {
            System.out.println(field.getName());
        });

        System.out.println("Methods:");
        stream(clazz.getDeclaredMethods()).forEach(method -> {
            System.out.println(method.getName());
        });

        System.out.println("Create an instance");
        MyClass myInstance = clazz.getDeclaredConstructor().newInstance();
        System.out.println(myInstance.getValue());

        Method method = clazz.getMethod("setValue", int.class);
        method.invoke(myInstance, 142);
        System.out.println(myInstance.getValue());
    }

    @MyRuntimeAnnotation
    @MySourceAnnotation
    public static class MyClass {

        int value = 42;

        public int getValue() {
            return value;
        }

        public MyClass setValue(int value) {
            this.value = value;
            return this;
        }
    }

    @Retention(RUNTIME)
    public @interface MyRuntimeAnnotation {}

    @Retention(SOURCE)
    public @interface MySourceAnnotation {}
}
