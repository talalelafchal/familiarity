public class MyFragment extends Fragment {
  
  ...
  
    public void showPermissionDetails(OnClickListener retryBehavior) {
        showViewState(true, false, false);
        retryPermissionView.setOnClickListener(retryBehavior);
        retryPermissionView.setVisibility(retryBehavior == null ? View.GONE : View.VISIBLE);
    }

    public void showProgressView() {
        showViewState(false, true, false);
    }

    public void showFailedView() {
        showViewState(false, false, true);
    }

    private void showViewState(boolean permissionVisible, boolean progressVisible, boolean failedVisible) {
        permissionView.setVisibility(permissionVisible ? View.VISIBLE : View.GONE);
        progressView.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
        failedView.setVisibility(failedVisible ? View.VISIBLE : View.GONE);
    }
}