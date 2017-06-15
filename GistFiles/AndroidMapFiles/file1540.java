public class RainbowLightsActivity extends Activity {
    private static final String TAG = RainbowLightsActivity.class.getSimpleName();

    private List<Gpio> buttons = new ArrayList<>();
    private Map<Color, Gpio> leds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService peripheralManagerService = new PeripheralManagerService();
        try {
            for (final Color color : Color.values()) {
                Gpio led = peripheralManagerService.openGpio(color.ledPin);
                led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
                leds.put(color, led);
              
                Gpio button = peripheralManagerService.openGpio(color.buttonPin);
                button.setDirection(Gpio.DIRECTION_IN);
                button.setEdgeTriggerType(Gpio.EDGE_FALLING);
                button.registerGpioCallback(new GpioCallback() {
                    @Override
                    public boolean onGpioEdge(Gpio gpio) {
                        toggleLed(color);
                        return true;
                    }
                });
                buttons.add(button);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error opening Gpio", e);
        }
    }

    private void toggleLed(Color color) {
        try {
            Gpio gpio = leds.get(color);
            gpio.setValue(!gpio.getValue());
        } catch (IOException e) {
            Log.e(TAG, "Error toggling LED status", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            for (Gpio gpio : buttons) {
                gpio.close();
            }
            for (Gpio gpio : leds.values()) {
                gpio.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing Gpio", e);
        }
    }

    private enum Color {
        RED("BCM6", "BCM21"),
        GREEN("BCM19", "BCM20"),
        BLUE("BCM26", "BCM16");

        private final String ledPin;
        private final String buttonPin;

        Color(String ledPin, String buttonPin) {
            this.ledPin = ledPin;
            this.buttonPin = buttonPin;
        }
    }
}