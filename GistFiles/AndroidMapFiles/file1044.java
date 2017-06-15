Firebase firebase = new Firebase("https://anexample.firebaseIO.com/samplenode");

firebase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Map data = (Map) dataSnapshot.getValue();

            Log.d(TAG, "::onDataChange() -- firebase data: "
                  + dataSnapshot.getValue());
            Log.d(TAG, "::onDataChange() -- sample string data content: "
                  + ((String) data.get("some_string_content")));
            Log.d(TAG, "::onDataChange() -- sample number data content: "
                  + ((Long) data.get("some_number_content")));

            // Awesome stuff!
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            // Cleanup stuff
        }
    });
