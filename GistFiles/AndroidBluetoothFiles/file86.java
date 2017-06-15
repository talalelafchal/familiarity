public class JournalEntryActivity extends ActionBarActivity {
  
  private final String[] INTENT_FILTER = new String[] {
          "com.twitter.android",
          "com.facebook.katana"
  };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.journal_entry_menu, menu);

      // Set up ShareActionProvider's default share intent
      MenuItem shareItem = menu.findItem(R.id.action_share);

      if (shareItem instanceof SupportMenuItem) {
          mShareActionProvider = new ShareActionProvider(this);
          mShareActionProvider.setShareIntent(ShareUtils.share(mJournalEntry));
          mShareActionProvider.setIntentFilter(Arrays.asList(INTENT_FILTER));
          mShareActionProvider.setShowHistory(false);
          ((SupportMenuItem) shareItem).setSupportActionProvider(mShareActionProvider);
      }

      return super.onCreateOptionsMenu(menu);
  }
}