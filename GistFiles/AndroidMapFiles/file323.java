final CharSequence[] items = {"Red", "Green", "Blue", "Purple"};

AlertDialog.Builder builder = new AlertDialog.Builder(this);

builder.setTitle("Pick color(s)")
        .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
          
            @Override
            public void onClick(DialogInterface dialog, int which,
                    boolean isChecked) {
                Log.d(tag, items[which] + " " + isChecked);
            }
        })
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          
            @Override
            public void onClick(DialogInterface dialog, int which) {
              
                //reading content of the dialog
                AlertDialog a = ((AlertDialog)dialog);
                Log.d(tag, "" + a.getListView().getCheckedItemCount());
                
                for(long i: a.getListView().getCheckItemIds())
                {
                    Log.d(tag, i + "");
                }
            }
        });
 
        final AlertDialog alert = builder.create();
        
        ((Button) findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
          
                    @Override
                    public void onClick(View v) {
                        alert.show();
                    }
                });