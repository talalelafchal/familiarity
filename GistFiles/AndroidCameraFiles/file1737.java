package $PACKAGE_NAME$;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

public class DoubleBackToExit extends Activity
{
    private static final int DOUBLE_PRESS_BACK_TO_EXIT_RESET_DELAY_MSEC = 2000;  // length of Toast.LENGTH_SHORT
    private static final String DOUBLE_PRESS_BACK_TO_EXIT_TOAST_TEXT = "Press again to exit";
    private boolean mDoublePressBackToExit = false;
    private boolean mIsBackPressedByTheUser = false;

    @Override
    public void onBackPressed()
    {
        if (mDoublePressBackToExit)
        {
            // process the back button as per normal
            mIsBackPressedByTheUser = true;
            super.onBackPressed();
            return;
        }
        
        mDoublePressBackToExit = true;  // set flag
        Toast.makeText(this, DOUBLE_PRESS_BACK_TO_EXIT_TOAST_TEXT, Toast.LENGTH_SHORT).show();

        // reset after time delay
        new Handler().postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    mDoublePressBackToExit = false;
                }
            }, DOUBLE_PRESS_BACK_TO_EXIT_RESET_DELAY_MSEC
        );
    }
}
