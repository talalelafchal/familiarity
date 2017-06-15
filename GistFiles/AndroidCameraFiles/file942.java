        // get our folding cell
        final FoldingCell foldingCell = (FoldingCell) findViewById(R.id.folding_cell);

        // set custom parameters
       foldingCell.initialize(1000, Color.DKGRAY, 2);

        // or with camera height parameter
       foldingCell.initialize(cameraHeight, animationDuration, backSideColor, additionalFlipCounts);
       foldingCell.initialize(1000, Color.DKGRAY, 0);

        foldingCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foldingCell.toggle(false);
            }
        });