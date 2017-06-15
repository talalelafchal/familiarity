    private void openFragment(Fragment newFragment) {

        String newFragmentName = newFragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();
        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if (containerFragment == null) {
            addFragment(newFragment);
        } else {
            
            if (!containerFragment.getClass().getName().equalsIgnoreCase(newFragmentName)) {

                if (newFragmentName.equals(navDefaultNameFragment)) {
                    Log.w(TAG, "Reset backstack fragments: ");
                    fm.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
/*                    boolean fragmentPopped = fm.popBackStackImmediate(newFragmentName, 0);
                    if (!fragmentPopped) */
                    replaceFragment(newFragment);
                }

            }

        }
        
    }
