public interface MediaSelector {
    Intent getIntentChooser();

    Uri getMediaUriFromActivityResult(int requestCode, Intent data);
}
