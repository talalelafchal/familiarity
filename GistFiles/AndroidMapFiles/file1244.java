/*
 * Let take the simplest example possible... our beloved `ArrayAdapter`.
 */
adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayOrList);
listView.setAdapter(adapter);

// becomes
Adapt.with(context, android.R.layout.simple_list_item_1)
        .load(arrayOrList)
        .into(listView); // or `.build()` when we need to store the adapter for later use

/*
 * We could set an explicit `TextView` target, as expected.
 */
adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1, arrayOrList);
listView.setAdapter(adapter);

// becomes...
Adapt.with(context, android.R.layout.simple_list_item_1)
        .load(arrayOrList)
        .view(android.R.id.text1)
        .into(listView);

/*
 * The `ArrayAdapter` doesn't change so much, and become a lot more verbose than just
 * instantiate the adapter directly. But when we talk about `SimpleCursorAdapter`,
 * IMO the * `Adapt` builder really shines.
 */
adapter = new SimpleCursorAdapter(
        context, android.R.layout.simple_list_item_1,
        cursor,
        new String[] { ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER },
        new int[] { android.R.id.text1, android.R.id.text2 },
        0
);
listView.setAdapter(adapter);

// becomes...
Adapt.with(context, android.R.layout.simple_list_item_2)
        .load(cursor)
        .columnView(ContactsContract.Contacts.DISPLAY_NAME, android.R.id.text1)
        .columnView(ContactsContract.PhoneLookup.NUMBER, android.R.id.text2)
        .into(listView);
        
/*
 * At last, `SimpleAdapter`. Unfortunately, because of Java's *runtime type erasure*, I was not able to
 * make this work using the same `load` signature. So just changed to `maps`. The rest works the same as
 * the cursor example.
 */
adapter = new SimpleAdapter(
        context, data, android.R.layout.simple_list_item_2,
        new String[] { "name", "email" },
        new int[] { android.R.id.text1, android.R.id.text2 }
);
listView.setAdapter(adapter);

// becomes...
Adapt.with(context, android.R.layout.simple_list_item_2)
        .maps(data)
        .bind("name", android.R.id.text1)
        .bind("email", android.R.id.text2)
        .into(listView);

/**
 * When the life (or javac/jvm) give us lemons, let's do a lemonade. What if I could map object getters
 * using reflection into the views?
 */
Adapt.with(context, android.R.layout.simple_list_item_2)
        .maps(objects, Contact.class, "name", "email")
        .bind("name", android.R.id.text1)
        .bind("email", android.R.id.text2)
        .into(listView);