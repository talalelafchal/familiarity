view.findViewById(R.id.program_keychain).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        switchModule.routeData().fromSensor().monitor(new DataSignal.ActivityHandler() {
            @Override
            public void onSignalActive(Map<String, DataProcessor> map, DataSignal.DataToken dataToken) {
                ledModule.configureColorChannel(Led.ColorChannel.BLUE)
                        .setHighIntensity((byte) 31)
                        .setHighTime((short) 50)
                        .setPulseDuration((short) 500)
                        .setRepeatCount((byte) 3)
                        .commit();
                ledModule.play(false);
            }
        }).commit();
    }
});