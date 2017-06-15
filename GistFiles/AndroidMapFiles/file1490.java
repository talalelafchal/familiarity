/**
 * Base UI View to show a basic information about a domain model.
 * This class has basic components like {@link CoordinatorLayout} and
 * {@link AppBarLayout}.
 * Created by luisburgos on 8/31/16.
 */
public abstract class InformationActivity extends AppCompatActivity {

    protected CoordinatorLayout coordinatorLayout;
    protected AppBarLayout appBarLayout;
    protected CollapsingToolbarLayout collapsingToolbarLayout;
    protected Toolbar toolbar;

    /**
     * This method has sequential cohesion in order to achieve a
     * correct configuration for the activity. This method NEEDS to be called from
     * concrete implementations.
     *
     * This method depends on {@link InformationActivity#initViews()},
     * {@link InformationActivity#setInformationData()}, {@link InformationActivity#setupToolbar()},
     * {@link InformationActivity#setupAppBar()}
     */
    protected void baseSetup(){
        this.initViews();
        this.setInformationData();
        this.setupToolbar();
        this.setupAppBar();
    }

    /**
     * BINDS xml components to class variables.
     */
    protected void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.information_coordinator_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
    }

    /**
     * Displays home back button and enables back pressed behavior.
     */
    protected void setupToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Sets a {@link AppBarLayout.OnOffsetChangedListener} to the AppBar and notifies when
     * offset change and is less than 25.
     */
    protected void setupAppBar() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset <= 25) {
                    onToolbarCollapsed(true);
                    isShow = true;
                } else if(isShow) {
                    onToolbarCollapsed(false);
                    isShow = false;
                }
            }
        });
    }

    /**
     * Shows a {@link Snackbar} with the message to show.
     * @param message text to show.
     */
    protected void showMessage(String message) {
        if(coordinatorLayout == null){
            return;
        }
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * This method is NOT used by this class.
     * @param isCollapsed state of toolbar.
     */
    protected abstract void onToolbarCollapsed(boolean isCollapsed);

    /**
     * Retrieves a custom activity title for display on toolbar.
     * @return activity title.
     */
    protected abstract String getActivityTitle();

    /**
     * This method is needed to retrieve information a set the current activity information.
     * It could be necessary to implement a extra method to update the UI.
     */
    protected abstract void setInformationData();
}
