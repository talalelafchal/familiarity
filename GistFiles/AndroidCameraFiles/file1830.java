public class MainActivity extends WearableActivity implements ConnectionCallbacks, OnConnectionFailedListener, NodeListener, ChannelListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient googleApiClient = null;


    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
    }

    /**
     * Build the Google API client.
     */
    private void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Called after onCreate(Bundle) — or after onRestart() when the activity had been stopped, but
     * is now again being displayed to the user. It will be followed by onResume().
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        googleApiClient.connect();
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to
     * start interacting with the user. This is a good place to begin animations, open
     * exclusive-access devices (such as the camera), etc.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // If the client is already connected or connecting, this method does nothing.
        googleApiClient.connect();
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either onRestart(),
     * onDestroy(), or nothing, depending on later user activity.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        stopListeners();
    }

    /**
     * Perform any final cleanup before an activity is destroyed. This can happen either because the
     * activity is finishing (someone called finish() on it, or because the system is temporarily
     * destroying this instance of the activity to save space. You can distinguish between these two
     * scenarios with the isFinishing() method.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        stopListeners();
    }

    /**
     * Stops all listeners and disconnects the {@link GoogleApiClient}.
     */
    private void stopListeners() {
        Log.d(TAG, "stopListeners");

        Wearable.ChannelApi.removeListener(googleApiClient, this);
        Wearable.NodeApi.removeListener(googleApiClient, this);
        googleApiClient.disconnect();
    }

    /**
     * Method of {@link ConnectionCallbacks} interface.
     * After calling connect(), this method will be invoked asynchronously when the connect request
     * has successfully completed.
     *
     * @param connectionHint Bundle of data provided to clients by Google Play services. May be null
     *                       if no content is provided by the service.
     */
    @Override
    public void onConnected(final Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        Wearable.NodeApi.addListener(googleApiClient, this);
        Wearable.ChannelApi.addListener(googleApiClient, this);
    }

    /**
     * Called when a new channel is opened by a remote node.
     * Method of {@link com.google.android.gms.wearable.ChannelApi.ChannelListener} interface.
     *
     * @param channel A channel created through openChannel(GoogleApiClient, String, String) by a
     *                remote node.
     */
    @Override
    public void onChannelOpened(final Channel channel) {
        Log.d(TAG, "onChannelOpened: A new channel to this device was just opened.\n" +
                "From Node ID" + channel.getNodeId() + "\n" +
                "Path: " + channel.getPath());

        channel.getInputStream(googleApiClient).setResultCallback(new ResultCallback<Channel.GetInputStreamResult>() {
            @Override
            public void onResult(Channel.GetInputStreamResult getInputStreamResult) {
                Log.d(TAG, "onChannelOpened: onResult");

                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                try {
                    inputStream = getInputStreamResult.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    // You may need to read more lines, depending on what you send.
                    final String message = bufferedReader.readLine();
                    Log.d(TAG, "onChannelOpened: onResult: Received the following message: " + message);
                }
                catch (final IOException ioexception) {
                    Log.w(TAG, "Could not read message from smartwatch to given node.\n" +
                            "Node ID: " + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath() + "\n" +
                            "Error message: " + ioexception.getMessage() + "\n" +
                            "Error cause: " + ioexception.getCause());
                }
                finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    }
                    catch (final IOException ioexception) {
                        Log.d(TAG, "onChannelOpened: onResult: Could not close buffered reader.\n" +
                                "Node ID: " + channel.getNodeId() + "\n" +
                                "Path: " + channel.getPath() + "\n" +
                                "Error message: " + ioexception.getMessage() + "\n" +
                                "Error cause: " + ioexception.getCause());
                    }
                }
            }
        });

        // Perform operations that require permissions
//        final long token = Binder.clearCallingIdentity();
//        try {
//            // performOperationRequiringPermissions();
//        }
//        finally {
//            Binder.restoreCallingIdentity(token);
//        }
    }

    /**
     *  Called when a channel is closed. This can happen through an explicit call to
     *  close(GoogleApiClient) or close(GoogleApiClient, int) on either side of the connection,
     *  or due to disconnecting from the remote node.
     *  Method of {@link com.google.android.gms.wearable.ChannelApi.ChannelListener} interface.
     *
     * @param channel A channel created through openChannel(GoogleApiClient, String, String).
     * @param closeReason the reason for the channel closing. One of CLOSE_REASON_DISCONNECTED, CLOSE_REASON_REMOTE_CLOSE, or CLOSE_REASON_LOCAL_CLOSE.
     * @param appSpecificErrorCode the error code specified on close(GoogleApiClient), or 0 if closeReason is CLOSE_REASON_DISCONNECTED.
     */
    @Override
    public void onChannelClosed(final Channel channel, final int closeReason, final int appSpecificErrorCode) {
        switch (closeReason) {
            case CLOSE_REASON_NORMAL:
                Log.d(TAG, "onChannelClosed: Channel closed. Reason: normal close (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_DISCONNECTED:
                Log.d(TAG, "onChannelClosed: Channel closed. Reason: disconnected (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_REMOTE_CLOSE:
                Log.d(TAG, "onChannelClosed: Channel closed. Reason: closed by remote (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_LOCAL_CLOSE:
                Log.d(TAG, "onChannelClosed: Channel closed. Reason: closed locally (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
        }

        // Perform operations that require permissions
//        final long token = Binder.clearCallingIdentity();
//        try {
//            // performOperationRequiringPermissions();
//        }
//        finally {
//            Binder.restoreCallingIdentity(token);
//        }
    }

    /**
     * Called when the input side of a channel is closed.
     * Method of {@link com.google.android.gms.wearable.ChannelApi.ChannelListener} interface.
     *
     * @param channel A channel created through openChannel(GoogleApiClient, String, String).
     * @param closeReason the reason for the input closing. One of CLOSE_REASON_DISCONNECTED,
     *                    CLOSE_REASON_REMOTE_CLOSE, CLOSE_REASON_LOCAL_CLOSE, or
     *                    CLOSE_REASON_NORMAL
     * @param appSpecificErrorCode the error code specified on close(GoogleApiClient), or 0 if
     *                             closeReason is CLOSE_REASON_DISCONNECTED or CLOSE_REASON_NORMAL.
     */
    @Override
    public void onInputClosed(final Channel channel, final int closeReason, final int appSpecificErrorCode) {
        switch (closeReason) {
            case CLOSE_REASON_NORMAL:
                Log.d(TAG, "onInputClosed: Channel input side closed. Reason: normal close (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_DISCONNECTED:
                Log.d(TAG, "onInputClosed: Channel input side closed. Reason: disconnected (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_REMOTE_CLOSE:
                Log.d(TAG, "onInputClosed: Channel input side closed. Reason: closed by remote (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_LOCAL_CLOSE:
                Log.d(TAG, "onInputClosed: Channel input side closed. Reason: closed locally (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
        }

        // Perform operations that require permissions
//        final long token = Binder.clearCallingIdentity();
//        try {
//            // performOperationRequiringPermissions();
//        }
//        finally {
//            Binder.restoreCallingIdentity(token);
//        }
    }

    /**
     * Called when the output side of a channel is closed.
     * Method of {@link com.google.android.gms.wearable.ChannelApi.ChannelListener} interface.
     *
     * @param channel A channel created through openChannel(GoogleApiClient, String, String).
     * @param closeReason the reason for the output closing. One of CLOSE_REASON_DISCONNECTED,
     *                    CLOSE_REASON_REMOTE_CLOSE, CLOSE_REASON_LOCAL_CLOSE, or
     *                    CLOSE_REASON_NORMAL
     * @param appSpecificErrorCode the error code specified on close(GoogleApiClient), or 0 if
     *                             closeReason is CLOSE_REASON_DISCONNECTED or CLOSE_REASON_NORMAL.
     */
    @Override
    public void onOutputClosed(final Channel channel, final int closeReason, final int appSpecificErrorCode) {
        switch (closeReason) {
            case CLOSE_REASON_NORMAL:
                Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: normal close (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_DISCONNECTED:
                Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: disconnected (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_REMOTE_CLOSE:
                Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: closed by remote (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
            case CLOSE_REASON_LOCAL_CLOSE:
                Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: closed locally (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                        "From Node ID" + channel.getNodeId() + "\n" +
                        "Path: " + channel.getPath());
                break;
        }

        // Perform operations that require permissions
//        final long token = Binder.clearCallingIdentity();
//        try {
//            // performOperationRequiringPermissions();
//        }
//        finally {
//            Binder.restoreCallingIdentity(token);
//        }
    }

    /**
     * Send a message to a connected and nearby device.
     *
     * @param message The text to send to the connected device.
     * @param path The path to identify the message on the receivers side.
     */
    private void sendMessageToDevice(final String message, final String path) {
        Log.d(TAG, "sendMessageToDevice");

        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(final GetConnectedNodesResult getConnectedNodesResult) {
                Log.d(TAG, "sendMessageToDevice: onResult");

                final List<Node> nodes = getConnectedNodesResult.getNodes();
                for (final Node node : nodes) {
                    if (node.isNearby()) {
                        Wearable.ChannelApi.openChannel(googleApiClient, node.getId(), path).setResultCallback(new ResultCallback<ChannelApi.OpenChannelResult>() {
                            @Override
                            public void onResult(ChannelApi.OpenChannelResult openChannelResult) {
                                Log.d(TAG, "sendMessageToDevice: onResult: onResult");
                                final Channel channel = openChannelResult.getChannel();
                                channel.getOutputStream(googleApiClient).setResultCallback(new ResultCallback<Channel.GetOutputStreamResult>() {
                                    @Override
                                    public void onResult(final Channel.GetOutputStreamResult getOutputStreamResult) {
                                        Log.d(TAG, "sendMessageToDevice: onResult: onResult: onResult");

                                        OutputStream outputStream = null;
                                        try {
                                            outputStream = getOutputStreamResult.getOutputStream();
                                            outputStream.write(message.getBytes());
                                            Log.d(TAG, "sendMessageToDevice: onResult: onResult: onResult: Message sent: " + message);
                                        }
                                        catch (final IOException ioexception) {
                                            Log.w(TAG, "sendMessageToDevice: onResult: onResult: onResult: Could not send message from smartwatch to given node.\n" +
                                                    "Node ID: " + channel.getNodeId() + "\n" +
                                                    "Path: " + channel.getPath() + "\n" +
                                                    "Error message: " + ioexception.getMessage() + "\n" +
                                                    "Error cause: " + ioexception.getCause());
                                        }
                                        finally {
                                            try {
                                                if (outputStream != null) {
                                                    outputStream.close();
                                                }
                                            }
                                            catch (final IOException ioexception) {
                                                Log.w(TAG, "sendMessageToDevice: onResult: onResult: onResult: Could not close Output Stream from smartwatch to given node.\n" +
                                                        "Node ID: " + channel.getNodeId() + "\n" +
                                                        "Path: " + channel.getPath() + "\n" +
                                                        "Error message: " + ioexception.getMessage() + "\n" +
                                                        "Error cause: " + ioexception.getCause());
                                            }
                                            finally {
                                                // Will call onChannelClosed
                                                channel.close(googleApiClient);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    }
                }
            }
        });
    }
}
