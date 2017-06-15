 /* Android side code (not the full example of course) */
 private static final int KEY_ACCELEROMETER_DATA = 200;
 
 mAppMsgReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {

                    @Override
                    public void receiveData(Context context, int transactionId, final PebbleDictionary data) {
                        try {
                           //Send ack as soon as possible to avoid pebble AppMessage timeout
          		    LOG.info("Received ack from pebble transactionId:" + transactionId);
                            PebbleKit.sendAckToPebble(context, transactionId);
                            
                            if (data.contains(KEY_ACCELEROMETER_DATA)) {
                                //Pebble will start to send accelerometer data via AppMessage
                                LOG.info("Pebble app message with accelerometer data arrived, samples count: " + data.size());
							
                                    int startKey = APP_MESSAGE_ACCELEROMETER_DATA;
                                    while (data.contains(startKey++)) {
                                    	//We can optimize the below for sure (this is only a prototype).
                                        byte[] bytes = data.getBytes(startKey);
                                        ByteBuffer buf = ByteBuffer.allocate(bytes.length);
					buf.order(ByteOrder.LITTLE_ENDIAN);
					buf.put(bytes);

					short x = buf.getShort(0);
					short y = buf.getShort(2);
					short z = buf.getShort(4);
					long timestamp = buf.getLong(6);
                                    }
   

                            }


                        } catch (Exception e) {
                            LOG.error("Failed to get app message from pebble", e);
                        }
                    }
};