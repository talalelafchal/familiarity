 //Programatically setting font. This is tedious i condiser , instead used UsingInLayoutFille.xml. 
 Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/10TitilliumWeb-SemiBold.ttf");
    TextView myTextView = (TextView)findViewById(R.id.myTextView);
    myTextView.setTypeface(myTypeface);