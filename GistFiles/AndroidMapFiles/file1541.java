AlertDialog alertDialog = new AlertDialog.Builder(
                        AlertDialogActivity.this).create();
 
        // Setting Dialog Title
        alertDialog.setTitle("Alert Dialog");
 
        // Setting Dialog Message
        alertDialog.setMessage("Welcome to AndroidHive.info");
 
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.tick);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
        });
        
        //OR 2 BUTTONS
        
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
 
            // Write your code here to invoke YES event
            Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
            }
        });
 
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // Write your code here to invoke NO event
            Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
            dialog.cancel();
            }
        });
        
        //OR ADD A 3D ONE
        
        // Setting Netural "Cancel" Button
        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // User pressed Cancel button. Write Logic Here
            Toast.makeText(getApplicationContext(), "You clicked on Cancel",
                                Toast.LENGTH_SHORT).show();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();