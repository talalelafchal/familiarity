AsyncHttpUtils.getInstance(5).get("http://www.baa", new AsyncHttpResponseHandler(){

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				Logger.initialize(mContext).debug(Constants.SYS_IS_DEBUG).i("--------", "http://www.baa");
			}

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				Logger.initialize(mContext).debug(Constants.SYS_IS_DEBUG).i("--------", response);
			}

		});