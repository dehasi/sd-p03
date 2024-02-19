package reflection.di.usage;

import reflection.di.framework.Component;

@Component
public class InternalService {

    public int getMultiplier() {
        return 42;
    }
}
