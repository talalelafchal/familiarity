Uri phoneNumber = Uri.parse(“tel:0123456789″);
Intent callIntent = new Intent(Intent.ACTION_DIAL,phoneNumber);
startActivity(callIntent);
