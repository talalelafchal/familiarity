ActivityManagerNativeRef
						.bindService(new IApplicationThread() {
							             @Override
							             public IBinder asBinder() {
								             return caller;
							             }
						             }, new Intent("android.bluetooth.IBluetoothHeadset"),
						             new IServiceConnection.Stub() {
							             @Override
							             public void connected(ComponentName name, IBinder service)
									             throws RemoteException {
								             L.d(TAG, "connected(): %s", service);
								             callBluetoothPhoneStateChanged(this, service, numActive, numHeld,
								                                            callState, number, type);
							             }
						             }
						            );