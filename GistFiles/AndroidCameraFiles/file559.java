private void replaceFragment(Fragment newFragment) {
    
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    ft.replace(R.id.frame_container, newFragment);
    ft.addToBackStack(newFragment.getClass().getName());
    ft.commit();
  
}