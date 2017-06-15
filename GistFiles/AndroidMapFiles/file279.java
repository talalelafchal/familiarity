import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import annotations.Bind;
import annotations.BindModule;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.activity.RoboActivity;

import static com.pivotallabs.robolectricgem.expect.Expect.expect;

/**
 * Tests for RobolectricTestRunnerWithInjection.
 *
 * @author Christopher J. Perry {github.com/christopherperry}
 */
@RunWith(RobolectricTestRunnerWithInjection.class)
@BindModule(RobolectricTestRunnerWithInjectionTest.InnerTestModule.class)
public class RobolectricTestRunnerWithInjectionTest {
    @Inject private Context context;
    @Inject private LayoutInflater inflater;
    private TestClassFive testClassFive = new TestClassFive();

    public class InnerTestModule extends ModuleWrapper {
        @Override
        public void configure() {
            bind(TestClassOne.class, TestClassTwo.class);
            bind(TestClassFour.class, testClassFive);
        }
    }

    @Test
    public void bindModuleAnnotation_onClassDeclaration_shouldBeUsedCorrectly() {
        TestActivityWithInjection myActivity = new TestActivityWithInjection();
        myActivity.onCreate(null);
        TestClassOne classOne = myActivity.getClassOne();
        expect(classOne.value()).toEqual("two");
    }

    @Test
    public void shouldInjectContext() {
        expect(context).not.toBeNull();
    }

    @Test
    public void shouldInjectLayoutInflater() {
        expect(inflater).not.toBeNull();
    }

    @Test
    public void shouldInjectContextIntoClass() {
        TestActivityWithInjection myActivity = new TestActivityWithInjection();
        myActivity.onCreate(null);
        expect(myActivity.getContext()).not.toBeNull();
    }

    @Test
    public void shouldInjectInflaterIntoClass() {
        TestActivityWithInjection myActivity = new TestActivityWithInjection();
        myActivity.onCreate(null);
        expect(myActivity.getInflater()).not.toBeNull();
    }

    @Test
    public void shouldUseBindAnnotationCorrectlyForApplicationClasses() {
        TestActivityWithInjection myActivity = new TestActivityWithInjection();
        myActivity.onCreate(null);
        TestClassOne classOne = myActivity.getClassOne();
        expect(classOne.value()).toEqual("two");
    }

    @Test
    @Bind(from = TestClassOne.class, to = TestClassThree.class)
    public void bindAnnotation_onMethodDeclaration_shouldOverrideModuleBinding_onClassDeclaration() {
        TestActivityWithInjection myActivity = new TestActivityWithInjection();
        myActivity.onCreate(null);
        TestClassOne classOne = myActivity.getClassOne();
        expect(classOne.value()).toEqual("three");
    }

    @Test
    @Bind(from = TestClassFour.class, to = TestClassSix.class)
    public void bindAnnotation_onMethodDeclaration_shouldOverrideModuleBindingWithInstance_onClassDeclaration() {
        TestActivityWithInjection myActivity = new TestActivityWithInjection();
        myActivity.onCreate(null);
        TestClassFour classFour = myActivity.getClassFour();
        expect(classFour.value()).toEqual("six");
    }

    private static class TestActivityWithInjection extends RoboActivity {
        @Inject private Context context;
        @Inject private LayoutInflater inflater;
        @Inject private TestClassOne classOne;
        @Inject private TestClassFour classFour;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public TestClassOne getClassOne() {
            return classOne;
        }

        public TestClassFour getClassFour() {
            return classFour;
        }

        public LayoutInflater getInflater() {
            return inflater;
        }

        public Context getContext() {
            return context;
        }
    }

    private static class TestClassOne {
        public String value() {
            return "one";
        }
    }

    private static class TestClassTwo extends TestClassOne {
        @Override
        public String value() {
            return "two";
        }
    }

    private static class TestClassThree extends TestClassOne {
        @Override
        public String value() {
            return "three";
        }
    }

    private static class TestClassFour {
        public String value() {
            return "four";
        }
    }

    private static class TestClassFive extends TestClassFour {
        @Override
        public String value() {
            return "five";
        }
    }

    private static class TestClassSix extends TestClassFour {
        @Override
        public String value() {
            return "six";
        }
    }
}
