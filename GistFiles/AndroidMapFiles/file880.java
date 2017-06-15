// Get a Spinner and bind it to an ArrayAdapter that 
// references a String array.
Spinner s1 = (Spinner) findViewById(R.id.spinner1);
ArrayAdapter adapter = ArrayAdapter.createFromResource(
    this, R.array.colors, android.R.layout.simple_spinner_item);
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
s1.setAdapter(adapter);

// Load a Spinner and bind it to a data query.
private static String[] PROJECTION = new String[] {
        People._ID, People.NAME
    };

Spinner s2 = (Spinner) findViewById(R.id.spinner2);
Cursor cur = managedQuery(People.CONTENT_URI, PROJECTION, null, null);
     
SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,
    android.R.layout.simple_spinner_item, // Use a template
                                          // that displays a
                                          // text view
    cur, // Give the cursor to the list adapter
    new String[] {People.NAME}, // Map the NAME column in the
                                         // people database to...
    new int[] {android.R.id.text1}); // The "text1" view defined in
                                     // the XML template
                                         
adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
s2.setAdapter(adapter2);