    // load the list of contacts with name, email and display photo of contacts
    // who have either phone numebr or an email address stored on the device's
    // contact book
    public void loadContacts() {
    
        // map to store and update the data as we loop through all type of data
        HashMap<Integer, Friend> tempContacts = new LinkedHashMap<>();
        
        // Loading All Contacts
        final String[] PROJECTION = new String[]{
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.DATA1,
                ContactsContract.Data.PHOTO_URI,
                ContactsContract.Data.MIMETYPE
        };
        
        long start = System.currentTimeMillis();
        Log.d(TAG, "Contacts query cursor initialized. Querying..");
        
        ContentResolver cr = getContentResolver();
        
        // We need the record from the ContactsContract.Data table if 
        // the mime type is Email or Phone
        // And the sort order should be by name
        Cursor cursor = cr.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                ContactsContract.Data.MIMETYPE + " = ?" +
                        " OR " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                },
                "lower(" + ContactsContract.Data.DISPLAY_NAME + ")"
        );
        
        Log.d(TAG, "Total Rows :" + cursor.getCount());
        
        try {
            final int idPos = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            final int namePos = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
            final int photoPos = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
            final int emailNoPos = cursor.getColumnIndex(ContactsContract.Data.DATA1);
            final int mimePos = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            
            while (cursor.moveToNext()) {
                int contactId = cursor.getInt(idPos);
                String emailNo = cursor.getString(emailNoPos);
                String photo = cursor.getString(photoPos);
                String name = cursor.getString(namePos);
                String mime = cursor.getString(mimePos);
                
                // If contact is not yet created
                if (tempContacts.get(contactId) == null) {
                    // If type email, add all detail, else add name and photo (we don't need number)
                    if (mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE))
                        tempContacts.put(contactId, new Friend(name, emailNo, photo));
                    else
                        tempContacts.put(contactId, new Friend(name, null, photo));
                } else {
                    // Contact is already present
                    // Add email if type email
                    if (mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE))
                        tempContacts.get(contactId).setEmail(emailNo);
                }
            }
        } finally {
            cursor.close();
            Log.d(TAG, "Cursor closed..");
        }
        long end = System.currentTimeMillis();
        float diffSeconds = (float) ((end - start) / 1000.0);
        Log.d(TAG, tempContacts.size() + " contacts loaded in: " + diffSeconds + "s || " +
                (end - start) + " ms");
        
        // Convert to ArrayList if you need an arraylist
        ArrayList<Friend> mContacts = new ArrayList<>();
        for (Map.Entry<Integer, Friend> friend : tempContacts.entrySet()) {
            mContacts.add(friend.getValue());
        }
        
        Log.d(TAG, "ArrayList created from contacts");
        
        // Do whatever you want to do with the loaded contacts
        
    }