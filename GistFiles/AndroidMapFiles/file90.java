// Source: http://www.anddev.org/resize_and_rotate_image_-_example-t621.html

// load the origial BitMap (500 x 500 px)
Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
       R.drawable.android);

int width = bitmapOrg.width();
int height = bitmapOrg.height();
int newWidth = 720;

float scaleWidth = ((float) newWidth) / width;
float ratio = ((float) scaled.getWidth()) / newWidth;
int newHeight = (int) (height / ratio);
float scaleHeight = ((float) newHeight) / height;

// createa matrix for the manipulation
Matrix matrix = new Matrix();
// resize the bit map
matrix.postScale(scaleWidth, scaleHeight);
// rotate the Bitmap
matrix.postRotate(45);

// recreate the new Bitmap
Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                  width, height, matrix, true);