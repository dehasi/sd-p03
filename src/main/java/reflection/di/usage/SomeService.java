package reflection.di.usage;

import reflection.di.framework.Component;
import reflection.di.framework.Inject;

@Component
public class SomeService {

    @Inject
    private InternalService service;

    public int calculateSomething() {
        return 42 * service.getMultiplier();
    }
}
