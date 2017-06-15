        // Replace the MoPubViewFactory so we can return instances of MoPubView2 instead.
        MoPubViewFactory.setInstance(new MoPubViewFactory() {
            @Override
            protected MoPubView internalCreate(final Context context) {
                return new MoPubView2(context);
            }
        });