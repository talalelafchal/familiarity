//the relevant code



    private void showDialog() {
        final View dialogView = View.inflate(MainActivity.this, R.layout.image_selector_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView)
                .setCancelable(false);
        cameraDialog = builder.create();
        cameraDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cameraDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                revealShow(dialogView, true, null);
            }
        });
        dialogView.findViewById(R.id.gallery_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                cameraDialog.dismiss();

            }
        });
        dialogView.findViewById(R.id.camera_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                cameraDialog.dismiss();

            }
        });
        cameraDialog.setCanceledOnTouchOutside(true);
        cameraDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = cameraDialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {

                    revealShow(dialogView, false, cameraDialog);
                }
                return false;
            }
        });
        cameraDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cameraDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "cancelled");
            }
        });
        cameraDialog.setCancelable(true);
        WindowManager.LayoutParams wmlp = cameraDialog.getWindow().getAttributes();
//
        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        wmlp.y = fab.getBaseline();   //y position
        createHandler(dialogView);
        if (imageHandler != null)
            new ImageLoader(imageHandler, this).execute();
        cameraDialog.show();

    }

    private void revealShow(View rootView, boolean reveal, final AlertDialog dialog) {
        final View view = rootView.findViewById(R.id.dialog_layout);
        int w = view.getWidth();
        int h = view.getHeight();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int cx = (int) size.x - (fab.getLeft() + fab.getWidth());
        int cy = (int) size.y - (fab.getTop() + fab.getHeight());
        Log.d(TAG, "cx" + cx + "cy" + cy);
        Log.d(TAG, "datatop" + fab.getX() + "dataright" + fab.getLeft());

        Log.d(TAG, "fabWidth" + fab.getWidth() + "fabHeight" + fab.getHeight());
//        int h = (view.getTop() + view.getBottom());
        float maxRadius = (float) Math.max(w, h);
        float minRadius = (float) fab.getWidth() / 4;
        Log.d(TAG, "radius" + maxRadius);

        if (reveal) {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, minRadius, maxRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(500);
            view.setVisibility(View.VISIBLE);
            animator.start();


        } else {
            SupportAnimator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, maxRadius, minRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(500);
            anim.addListener(new io.codetail.animation.SupportAnimator.AnimatorListener() {


                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {

                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });

            anim.start();
        }

    }