import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;

public class BaseAppCompatActivity extends AppCompatActivity
        implements ViewInterface {

    ProgressDialog loadingDialog;

    View parentLayout;

    protected android.support.v7.app.ActionBar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initProgressDialog();
    }

    private void initProgressDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setMessage(getText(R.string.loading));
        loadingDialog.setCancelable(false);
    }

    /**
     * Setup the toolbar
     * @param title the title of the view.
     */
    protected void initToolbar(String title) {
        toolbar = getSupportActionBar();
        if (toolbar != null) {
            setToolbarTitle(title);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Remove the back icon on the toolbar. <br>
     * To disable back button, override onBackPressed function to do nothing.
     */
    protected void removeBackIconOnToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initView(int id, int parentLayoutId) {
        setContentView(id);
        this.parentLayout = findViewById(parentLayoutId);
        ButterKnife.bind(this);
    }

    // --- View interface

    @Override
    public void displayError(String message) {
        if (parentLayout != null) {
            ViewTools.displayError(parentLayout, message);
        }
    }

    @Override
    public void displayToast(String title) {
        ViewTools.displayToast(this, title);
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.hide();
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

}