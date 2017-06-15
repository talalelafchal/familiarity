Uri phoneNumber = Uri.parse(“tel:0123456789″);
Intent callIntent = new Intent(Intent.ACTION_CALL,phoneNumber);
startActivity(callIntent);
