//Remove if exists, the file MUST be created using the lines below
File f = new File(getFilesDir(), "Captured.jpg");
f.delete();
//Create new file
FileOutputStream fos = openFileOutput("Captured.jpg", Context.MODE_WORLD_WRITEABLE);
fos.close();
//Get reference to the file
File f = new File(getFilesDir(), "Captured.jpg");