package reflection.di.usage;

import reflection.di.framework.Bootstrapper;
import reflection.di.framework.Bootstrapper.ObjectFactory;

class Application {

    public static void main(String[] args) {
        ObjectFactory objectFactory = Bootstrapper.startup("reflection.di.usage");
        var someService = objectFactory.getInstance(SomeService.class);

        System.out.println(someService.calculateSomething());

        // int NPE = new SomeService().calculateSomething();
    }
}
