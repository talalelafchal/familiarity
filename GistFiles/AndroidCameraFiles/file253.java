private void addFragment(Fragment newFragment) {
    
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    ft.add(R.id.frame_container, newFragment);
    ft.commit();
  
}