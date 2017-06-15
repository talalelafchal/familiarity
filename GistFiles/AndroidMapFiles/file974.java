
        try {
          URL url = file.toURI().toURL();
          String ext = MimeTypeMap.getFileExtensionFromUrl(url.toString());
          Intent intent = new Intent();
          intent.setAction(android.content.Intent.ACTION_VIEW);
          intent.setDataAndType(Uri.fromFile(file), map.getMimeTypeFromExtension(ext)));
          startActivity(intent);
        } catch (Exception e) {
          Log.e(TAG, "Malformed URL: " + e, e);
        }
