Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
File file = new File(sdcardPath);
String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
myIntent.setDataAndType(Uri.fromFile(file),mimetype);
mContext.startActivity(myIntent);