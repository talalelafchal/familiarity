public class SomeActivity extends SherlockActivity {

    AQuery aq;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
        ...

        // Show progress
        AlertUtils.getInstance().showProgress(this, "Logging in", "");
        
        // Do some task
        ..
        
        if (taskSuccess) {
            // Closes progress
            AlertUtils.getInstance().hideProgress(this);
        } else {
            // Closes progress and shows error
            AlertUtils.getInstance().showAlert(this, "Error", "Task failed");  
        }
        
    }
    
}