import java.util.Map;

public class Test {
    public static void main (String[] args) {
        Map<String, String> env = System.getenv();
        String envName = "ANDROID_HOME";
        System.out.format("%s=%s%n", envName, env.get(envName));
    }
}
