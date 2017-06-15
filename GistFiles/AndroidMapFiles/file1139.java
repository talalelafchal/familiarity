new ServerHelper(getActivity()).serverPostRequest("http://www.your-url.com", map, "Loading Products...", new ServerHelper.ServerCallback() {
            @Override
            public void onSuccess(String response) {
                
            }

            @Override
            public void onError(String error) {
               
            }
        });