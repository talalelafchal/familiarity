package codepath.com.recyclerviewfun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Contact {

    private int mId;

    private String mName;
    private boolean mOnline;

    public Contact(int id, String name, boolean online) {
        mId = id;
        mName = name;
        mOnline = online;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public boolean isOnline() {
        return mOnline;
    }

    private static int lastContactId = 0;

    public static void resetContactId() {
        lastContactId = 0;
    }

    public static List<Contact> createContactsList(int numContacts, int offset) {
        List<Contact> contacts = new ArrayList<Contact>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Contact(i + offset, randomIdentifier(), i <= numContacts / 2));
        }

        return contacts;
    }


    public static String randomIdentifier() {
        final java.util.Random rand = new java.util.Random();
        // class variable
        final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

        // consider using a Map<String,Boolean> to say whether the identifier is being used or not
        final Set<String> identifiers = new HashSet<String>();

        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+5;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
}