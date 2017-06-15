// Comparison of data management

// Given a simple object
class MyObject {
  String name;
  int value;
}

// Create an instance with some values
MyObject myObjectInstance = new MyObject();
myObjectInstance.name = "One";
myObjectInstance.value = 1;





Couchbase:
//////////////////////////////
// Create Document
client.set("myObjectInstance", myObjectInstance).get();

// Retrieve Document
MyObject myObjectInstance = (MyObject) client.getDocument("myObjectInstance");

// Update Document
MyObject myObjectInstance = (MyObject) client.getDocument("myObjectInstance");
myObjectInstance.name = "newName";
client.set("myObjectInstance", myObjectInstance);

//////////////////////////////






Couchbase Mobile:
//////////////////////////////
// Define Document
Map<String, Object> map = new HashMap<String, Object>();
map.put("name", myObjectInstance.name);
map.put("value", myObjectInstance.value);

// Create Document
Document document = database.getDocument("myObjectInstance");
UnsavedRevision rev = document.createRevision();
rev.setUserProperties(map);
rev.save();

// Retrieve Document
Document document = database.getDocument("myObjectInstance");
MyObject obj = new MyObject();
obj.name = document.getProperty("name");
obj.value = document.getProperty("value");

// Update Document
Document document = database.getDocument("myObjectInstance");
UnsavedRevision rev = document.createRevision();
rev.getUserProperties().put("name", "newName");
rev.save();

//////////////////////////////
