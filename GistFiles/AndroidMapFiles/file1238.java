import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxySample {

    public static void main(String[] args) {

        final Bar a = mapTo("It's a String!", Bar.class, true);
        a.doSomething(); // prints [InvocationHandler] doSomething()

        final Bar b = mapTo(new Foo(), Bar.class, false);
        b.doSomething(); // prints [Instance] doSomething()

        final Bar c = mapTo("Another String!", Bar.class, false);
        // c.doSomething(); // NullPointerException
    }

    /**
     * Tries to map anObject to the specified type. Creates a proxy instance 
     * if anObject is NOT an instance of type and forceCreate is set to true.
     *
     * @param anObject
     * @param type
     * @param forceCreate
     * @param <T>
     * @return
     */
    public static <T> T mapTo(final Object anObject,
                              final Class<T> type,
                              final boolean forceCreate) {
        if ((null == anObject) || (null == type)) {
            throw new IllegalArgumentException("Invalid parameters!");
        }

        T proxy = null;

        if (type.isInstance(anObject)) {
            proxy = (T) anObject;
        } else if (forceCreate) {
            try {
                // Create a proxy instance
                proxy = (T) Proxy.newProxyInstance(type.getClassLoader(),
                        new Class[] { type },
                        // Invocation Handler
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy,
                                                 Method method,
                                                 Object[] args) throws Throwable {
                                System.out.println("[InvocationHandler] " + method.getName() + "()");
                                return null;
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                proxy = null;
            }
        }

        return proxy;
    }


    static interface Bar {
        public void doSomething();
    }

    static class Foo implements Bar {
        @Override
        public void doSomething() {
            System.out.println("[Instance] doSomething()");
        }
    }
}