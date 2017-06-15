//Edited file to get desired result on success 

public void onEventMainThread(final SdkCobbocEvent event) {
        if (event != null) {

            if (event.getType() == SdkCobbocEvent.CREATE_WALLET) {

                if (event.getStatus()) {
                    String status = "-1";
                    //Success - OTP verified
                    JSONObject eventObject = (JSONObject) event.getValue();
                    try {
                        status = eventObject.getString(SdkConstants.STATUS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = "-1";
                    }

                    if (status.equals("0")) {
                        if (eventObject != null && eventObject.has(SdkConstants.RESULT) && !eventObject.isNull(SdkConstants.RESULT)) {
                            try {
                                JSONObject resultObject = eventObject.getJSONObject(SdkConstants.RESULT);

                                if (resultObject.has(SdkConstants.AVAILABLE_AMOUNT) &&
                                        !resultObject.isNull(SdkConstants.AVAILABLE_AMOUNT)) {
                                    mWalletRecentlyVerified = true;
                                    loadWalletMinLimit = resultObject.optDouble(SdkConstants.MIN_LOAD_LIMIT, 10.00);
                                    loadWalletMaxLimit = resultObject.optDouble(SdkConstants.MAX_LOAD_LIMIT, 10000.00);
                                    calculateOffersAndCashback();
                                    if (OTPVerificationdialog != null && OTPVerificationdialog.isShowing()) {
                                        OTPVerificationdialog.dismiss();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        progressBarWaitOTP.setVisibility(View.GONE);
                        OTPEditText.setEnabled(true);
                        proceed.setEnabled(true);

                        if (event.getValue() != null) {
                            try {
                                Toast.makeText(SdkHomeActivityNew.this, ((JSONObject) event.getValue())
                                        .getString(SdkConstants.MESSAGE), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                            }
                        } else {

                            Toast.makeText(SdkHomeActivityNew.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    }
                } else { // end if(event.getstatus())
                    OTPEditText.setEnabled(true);
                    proceed.setEnabled(true);
                    if (event.getValue() != null) {

                        try {

                            Toast.makeText(SdkHomeActivityNew.this, ((JSONObject) event.getValue())
                                    .getString(SdkConstants.MESSAGE), Toast.LENGTH_LONG).show();

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    } else {

                        Toast.makeText(SdkHomeActivityNew.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                    progressBarWaitOTP.setVisibility(View.GONE);
                }
            } else if (event.getType() == SdkCobbocEvent.LOAD_WALLET) {
                dismissProgress();
                if (event.getStatus()) {
                    JSONObject paymentDetailsObject = (JSONObject) event.getValue();
                    Intent intent = new Intent(this, SdkHomeActivityNew.class);
                    map.put(SdkConstants.PAYMENT_TYPE, SdkConstants.LOAD_WALLET);
                    map.put(SdkConstants.INTERNAL_LOAD_WALLET_CALL, paymentDetailsObject.toString());
                    if (!fromPayUMoneyApp) {
                        map.put(SdkConstants.NEED_TO_SHOW_ONE_TAP_CHECK_BOX, true + "");
                    }
                    intent.putExtra(SdkConstants.PARAMS, map);

                    // unRegister Event for HomeActivity current Instance to prevent the posting of multiple
                    // events in LoadWallet Instance of HomeActivity
                    if (EventBus.getDefault() != null && EventBus.getDefault().isRegistered(this)) {
                        EventBus.getDefault().unregister(this);
                    }

                    startActivityForResult(intent, LOAD_AND_PAY_USING_WALLET); //Start the HomeActivity for loadWallet
                } else {
                    SdkHelper.showToastMessage(this, this.getString(R.string.something_went_wrong), true);
                }
            } else if (event.getType() == SdkCobbocEvent.ONE_TAP_OPTION_ALTERED) {
                if (event.getStatus()) {
                    JSONObject result = (JSONObject) event.getValue();
                    handleOneClickAndOneTapFeature(result);
                    /*SdkDebit.mCardStore.setText("");
                    SdkDebit.sdkTnc.setVisibility(View.VISIBLE);*/
                    //SdkHelper.showToastMessage(this,"Can't opt for this feature now",true);// STOPSHIP: 12/21/15  ;
                    // invalidateOptionsMenu();
                } else {
                    /*call failed stick with the older choice*/
                    SharedPreferences mPref = getSharedPreferences(SdkConstants.SP_SP_NAME, Activity.MODE_PRIVATE);
                    Boolean oneClickPayment = false, oneTapFeature = false;
                    if (mPref.contains(SdkConstants.CONFIG_DTO))
                        try {
                            JSONObject userConfigDto = new JSONObject(mPref.getString(SdkConstants.CONFIG_DTO,
                                    SdkConstants.XYZ_STRING));
                            if (userConfigDto != null) {
                                if (userConfigDto.has(SdkConstants.ONE_CLICK_PAYMENT) && !userConfigDto.isNull
                                        (SdkConstants.ONE_CLICK_PAYMENT)) {
                                    oneClickPayment = userConfigDto.optBoolean(SdkConstants.ONE_CLICK_PAYMENT);
                                    /*if (oneClickPayment && userConfigDto.has(SdkConstants.ONE_TAP_FEATURE) &&
                                    !userConfigDto.isNull(SdkConstants.ONE_TAP_FEATURE)) {
                                        oneTapFeature = userConfigDto.optBoolean(SdkConstants.ONE_TAP_FEATURE);
                                    }*/
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    if (!oneClickPayment) {
                        /*SdkDebit.mCardStore.setText("Save this card");
                        SdkDebit.sdkTnc.setVisibility(View.GONE);*/
                        mOneTap.setChecked(false);
                    } else {
                        /*SdkDebit.mCardStore.setText("");
                        SdkDebit.sdkTnc.setVisibility(View.VISIBLE);*/
                        mOneTap.setChecked(true);
                    }
                    if (!firstTimeFetchingOneClickFlag)
                        SdkHelper.showToastMessage(this, this.getString(R.string.something_went_wrong), true);

                    firstTimeFetchingOneClickFlag = false;

                }
            } else if (event.getType() == SdkCobbocEvent.VERIFY_MANUAL_COUPON) {
                verifyCouponProgress.setVisibility(View.INVISIBLE);
                if (event.getStatus()) {
                    try {
                        JSONObject manualCoupon = (JSONObject) event.getValue();
                        if (manualCoupon.has("couponStringForUser") && !manualCoupon.isNull("couponStringForUser")) {
                            manualCouopnNameString = manualCoupon.getString("couponStringForUser");
                            mannualCouponEditText.setText(manualCouopnNameString + " Added");
                        } else
                            mannualCouponEditText.setText("Coupon Added");

                        if (manualCoupon.has("couponString") && !manualCoupon.isNull("couponString")) {
                            choosedCoupan = manualCoupon.getString("couponString");
                        }
                        if (manualCoupon.has("amount") && !manualCoupon.isNull("amount")) {
                            coupan_amt = manualCoupon.getDouble("amount");
                        } else
                            coupan_amt = 0.0;

                        manualCouponEntered = true;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        manualCouponEntered = false;
                        mannualCouponEditText.setText("Invalid Coupon");
                        SdkLogger.d(SdkConstants.TAG, "Invalid Coupon Entered");
                    }

                } else {
                    manualCouponEntered = false;
                    mannualCouponEditText.setText("Invalid Coupon");
                    SdkLogger.d(SdkConstants.TAG, "Invalid Coupon Entered");
                }

            }
            if (event.getType() == SdkCobbocEvent.FETCH_USER_PARAMS) {
                if (event.getStatus()) {
                    userParamsFetchedExplicitely = true;
                    user = (JSONObject) event.getValue();
                    initLayout();
                } else {
                    SdkLogger.d(SdkConstants.TAG, "Error fetching User Params");
                }

            }
            if (event.getType() == SdkCobbocEvent.FETCH_MERCHANT_PARAMS) {
                if (event.getStatus()) {
                    try {
                        JSONObject jsonObject = (JSONObject) event.getValue();
                        if (jsonObject.has(SdkConstants.RESULT) && !jsonObject.isNull(SdkConstants.RESULT)) {
                            JSONArray result = jsonObject.getJSONArray(SdkConstants.RESULT);
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject object = result.getJSONObject(i);

                                String paramKey = object.optString(SdkConstants.PARAM_KEY, "");
                                String paramValue = object.optString(SdkConstants.PARAM_VALUE, "");

                                if (paramKey.equals(SdkConstants.OTP_LOGIN)) {
                                    quickLogin = paramValue;
                                } else if (paramKey.equals(SdkConstants.MERCHANT_PARAM_ALLOW_GUEST_CHECKOUT_VALUE)) {
                                    if ((SdkConstants.MERCHANT_PARAM_ALLOW_QUICK_GUEST_CHECKOUT).equals(paramValue)) {
                                        paramValue = SdkConstants.MERCHANT_PARAM_ALLOW_GUEST_CHECKOUT_ONLY;
                                    }
                                    allowGuestCheckout = paramValue;
                                }
                            }
                        } else {
                            SdkLogger.d(SdkConstants.TAG, "Error fetching Merchant Login Params");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismissProgress();
                check_login();
            } else if (event.getType() == SdkCobbocEvent.LOGOUT) {
                if (event.getValue() != null) {
                    if (event.getValue().equals(SdkConstants.LOGOUT_FORCE)) {
                        Toast.makeText(this, R.string.inactivity, Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor edit = getSharedPreferences(SdkConstants.SP_SP_NAME, Activity.MODE_PRIVATE).edit();
                        edit.clear();
                        edit.commit();
                        sdkSession.reset();
                        if (!mIsLoginInitiated) {
                            sdkSession.fetchMechantParams(map.get(SdkConstants.MERCHANT_ID));
                        }
                    } else if (SdkConstants.WALLET_SDK) {

                        SdkHelper.dismissProgressDialog();
                        if (event.getStatus()) {
                            SdkHelper.showToastMessage(this, getString(R.string.logout_success), false);
                            close(PAYMNET_LOGOUT);
                        } else {

                            if (!SdkHelper.checkNetwork(this)) {
                                SdkHelper.showToastMessage(this, getString(R.string.disconnected_from_internet), true);
                            } else {
                                SdkHelper.showToastMessage(this, getString(R.string.something_went_wrong), true);
                            }
                        }
                    }
                }
                // clear the token stored in SharedPreferences
            } else if (event.getType() == SdkCobbocEvent.USER_POINTS) { //Add wallet points

                SdkLogger.d(SdkConstants.TAG, "Entered in User Points");
                /*if (event.getStatus()) {
                    try {
                        walletJason = (JSONObject) event.getValue();

                        if ((walletJason.has(SdkConstants.WALLET)) && !walletJason.isNull(SdkConstants.WALLET))
                            walletAmount = walletJason.getJSONObject(SdkConstants.WALLET).optDouble(SdkConstants.
                            AVAILABLE_AMOUNT, 0.0);

                        if (walletAmount > 0.0) {
                            //   wallettext.setVisibility(View.VISIBLE);
                            walletCheck.setVisibility(View.VISIBLE);
                            walletBoxLayout.setVisibility(View.VISIBLE);
                            walletBalance.setText("Wallet balance: " + round(walletBal, 2));
                            //walletText.setText("Initial bal: " + walletAmount);
                            SdkLogger.d(SdkConstants.TAG, "Exited from  User Points");
                        }
                    } catch (Exception e) {
                        SdkLogger.d(SdkConstants.TAG, e.toString());
                    }
                } else {
                    dismissProgress();
                    Toast.makeText(this, "Some error occurred! Try again", Toast.LENGTH_LONG).show();
                }*/
            } else if (event.getType() == SdkCobbocEvent.CREATE_PAYMENT) {  //New Payment
                SdkLogger.d(SdkConstants.TAG, "Entered in Create Payment");
                if (event.getStatus()) {

                    try {
                        details = (JSONObject) event.getValue();
                        if (details != null && details.has(SdkConstants.PAYMENT_ID) && !details.isNull(SdkConstants.PAYMENT_ID))
                            paymentId = details.getString(SdkConstants.PAYMENT_ID);
                        if (guestCheckOut && !fromPayUMoneyApp && !fromPayUBizzApp && !mInternalLoadWalletCall) {
                            String guestEmail = sdkSession.getGuestEmail();
                            sdkSession.updateTransactionDetails(paymentId, guestEmail);
                        }
                        startPayment(null);
                        // sdkSession.getPaymentDetails(details.getJSONObject("payment").getString(Constants.PAYMENT_ID));//merge
                        //sdkSession.getPaymentDetails(result.getString(Constants.PAYMENT_ID)); //Fire getpaymentdetails
                        // of sdkSession
                    } catch (Exception e) {
                        dismissProgress();
                        e.printStackTrace();
                    }
                    SdkLogger.d(SdkConstants.TAG, "exited from Create Payment");
                } else {
                    dismissProgress();
                    try {
                        String responseString = (String) (event.getValue());
                        if (responseString != null && !responseString.isEmpty() && !responseString.equals(SdkConstants.NULL_STRING)) {
                            JSONObject responseObject = new JSONObject(responseString);
                            if (responseObject != null && responseObject.has(SdkConstants.MESSAGE) &&
                                    !responseObject.isNull(SdkConstants.MESSAGE)) {
                                String messageString = responseObject.getString(SdkConstants.MESSAGE);
                                if (messageString.contains(SdkConstants.PAYMENT_NOT_VALID)) {
                                    Toast.makeText(this, messageString, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "Some error occurred! Try again", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(this, "Some error occurred! Try again", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Some error occurred! Try again", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (event.getType() == SdkCobbocEvent.PAYMENT_POINTS) {

                //sending paymentDTO to merchant
                /*Intent intent = new Intent();
                try {
                    JSONObject jsonObject = new JSONObject(event.getValue().toString()).getJSONObject(SdkConstants.RESULT);
                    intent.putExtra(SdkConstants.RESULT, jsonObject.toString());
                    setResult(RESULT_OK, intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    intent.putExtra(SdkConstants.RESULT, SdkConstants.NULL_STRING);
                    setResult(PayUmoneySdkInitilizer.RESULT_FAILED, intent);
                }
                finish();*/

                if (event.getStatus()) {
                    Intent intent = new Intent();
                    Log.d("SDK_HOME_ACTIVITY", "" + event.getValue().toString());

                    String status = null;
                    String paymentId = null;
                    String paymentMode = null;
                    String exactAmount = null;
                    String additionalCharges = null;
                    String emailId = null;
                    String phoneNo = null;
                    String hash = null;
                    String errorMessage = null;

                    try {
                        JSONObject jsonObject = new JSONObject(event.getValue().toString()).getJSONObject(SdkConstants.RESULT);
                        if (jsonObject != null && jsonObject.has(SdkConstants.PAYMENT_ID) &&
                                !jsonObject.isNull(SdkConstants.PAYMENT_ID)) {
                            paymentId = jsonObject.getString(SdkConstants.PAYMENT_ID);
                        }
                        if (jsonObject != null && jsonObject.has(SdkConstants.STATUS) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            status = jsonObject.getString(SdkConstants.STATUS);
                        }

                        if (jsonObject != null && jsonObject.has(SdkConstants.PAYMENT_MODE) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            paymentMode = jsonObject.getString(SdkConstants.PAYMENT_MODE);
                        }

                        if (jsonObject != null && jsonObject.has(SdkConstants.AMOUNT) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            exactAmount = jsonObject.getString(SdkConstants.AMOUNT);
                        }

                        if (jsonObject != null && jsonObject.has(SdkConstants.ADDITIONAL_CHARGES) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            additionalCharges = jsonObject.getString(SdkConstants.ADDITIONAL_CHARGES);
                        }
                        if (jsonObject != null && jsonObject.has(SdkConstants.EMAIL) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            emailId = jsonObject.getString(SdkConstants.EMAIL);
                        }
                        if (jsonObject != null && jsonObject.has(SdkConstants.PHONE) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            phoneNo = jsonObject.getString(SdkConstants.PHONE);
                        }
                        if (jsonObject != null && jsonObject.has(SdkConstants.HASH) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            hash = jsonObject.getString(SdkConstants.HASH);
                        }

                        if (jsonObject != null && jsonObject.has(SdkConstants.ERROR_MESSAGE) && !jsonObject.isNull(SdkConstants.STATUS)) {
                            errorMessage = jsonObject.getString(SdkConstants.ERROR_MESSAGE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (intent != null) {
                        intent.putExtra(SdkConstants.PAYMENT_ID, paymentId);
                        intent.putExtra(SdkConstants.RESULT, status);
                        intent.putExtra(SdkConstants.PAYMENT_MODE, paymentMode);
                        intent.putExtra(SdkConstants.AMOUNT, exactAmount);
                        intent.putExtra(SdkConstants.ADDITIONAL_CHARGES, additionalCharges);
                        intent.putExtra(SdkConstants.PHONE, phoneNo);
                        intent.putExtra(SdkConstants.HASH, hash);
                        intent.putExtra(SdkConstants.ERROR_MESSAGE, errorMessage);
                        intent.putExtra(SdkConstants.EMAIL, emailId);
                    }

                    if (status != null && status.equalsIgnoreCase(SdkConstants.SUCCESS_STRING)) {
                        setResult(RESULT_OK, intent);
                    } else {
                        setResult(PayUmoneySdkInitilizer.RESULT_FAILED, intent);
                    }

                    finish();

                } else if (event.getValue() != null && event.getValue().toString().equals(SdkConstants.INVALID_APP_VERSION)) {
                    //showAlertDialog();
                } else {

                    Intent intent = new Intent();
                    intent.putExtra(SdkConstants.RESULT, event.getValue().toString());
                    setResult(PayUmoneySdkInitilizer.RESULT_FAILED, intent);
                    finish();
                    //onActivityResult(PAYMENT_SUCCESS, RESULT_FAILED, intent);
                }


            } else if (event.getType() == SdkCobbocEvent.PAYMENT) {
                dismissProgress();
                if (event.getStatus()) {
                    SdkLogger.i("reached", "cred" +
                            "it");
                    Intent intent = new Intent(this, SdkWebViewActivityNew.class);
                    intent.putExtra(SdkConstants.RESULT, event.getValue().toString());
                    intent.putExtra(SdkConstants.PAYMENT_MODE, mode);
                    if (mode.isEmpty()) {
                        if (cardHashForOneClickTxn != null)
                            intent.putExtra(SdkConstants.CARD_HASH_FOR_ONE_CLICK_TXN, cardHashForOneClickTxn);
                        else
                            intent.putExtra(SdkConstants.CARD_HASH_FOR_ONE_CLICK_TXN, "0");
                    }

                    // Adding merchant Permission for OTP Auto Read
                    if (mOTPAutoRead) {
                        intent.putExtra(SdkConstants.OTP_AUTO_READ, true);
                    }

                    intent.putExtra(SdkConstants.MERCHANT_KEY, getIntent().getExtras().getString("key"));
                    intent.putExtra(SdkConstants.PAYMENT_ID, paymentId);
                    this.startActivityForResult(intent, this.WEB_VIEW);
                } else if (event.getValue().toString().equals(SdkConstants.INVALID_APP_VERSION)) {
                    //showAlertDialog();
                } else {
                    SdkLogger.i("reached", "failed");
                    //If not status do nothing
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
                }
            } else if (event.getType() == SdkCobbocEvent.SEND_OTP_TO_USER) {
                if (event.getStatus()) {
                    JSONObject response = (JSONObject) event.getValue();
                    try {
                        if (response.getString("status").equals("0")) {

                            //register receiver for otp reading
                            autoFillOTPForWalletCreation();

                            proceed.setEnabled(false);
                            proceed.setText("Activate");
                            resend.setVisibility(View.VISIBLE);
                            OTPEditText.setVisibility(View.VISIBLE);
                            humble.setVisibility(View.VISIBLE);
                            progressBarWaitOTP.setVisibility(View.VISIBLE);
                            info.setText("Waiting for OTP..");
                            Toast.makeText(SdkHomeActivityNew.this, ((JSONObject) event.getValue()).getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (event.getValue() != null) {
                                try {
                                    Toast.makeText(SdkHomeActivityNew.this, ((JSONObject) event.getValue()).getString("message"),
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                }
                            } else {
                                Toast.makeText(SdkHomeActivityNew.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        if (event.getValue() != null) {
                            try {
                                Toast.makeText(SdkHomeActivityNew.this, ((JSONObject) event.getValue()).getString("message"),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e1) {
                            }
                        } else {
                            Toast.makeText(SdkHomeActivityNew.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    } //end catch

                } // end if(event.getstatus())
                else {
                    if (event.getValue() != null) {
                        try {
                            Toast.makeText(SdkHomeActivityNew.this, ((JSONObject) event.getValue()).getString("message"),
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                        }
                    } else {
                        Toast.makeText(SdkHomeActivityNew.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (event.getType() == SdkCobbocEvent.NET_BANKING_STATUS) {
                if (event.getStatus()) {
                    mNetBankingStatusObject = (JSONObject) event.getValue();
                }
            }
        }
    }