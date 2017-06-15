MediaConstraints videoConstraints = new MediaConstraints();
videoConstraints.mandatory.add(
    new MediaConstraints.KeyValuePair("maxWidth", "1280"));
...
PnSignalingParams params = new PnSignalingParams(pcConstraints, videoConstraints, audioConstraints);
pnRTCClient.setSignalParams(params);