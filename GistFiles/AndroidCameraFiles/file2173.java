public static float getMaximumTilt(float zoom) {
  	// for tilt values, see:
	// https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/CameraPosition.Builder?hl=fr

	float tilt = 30.0f;

	if (zoom > 15.5f) {
		tilt = 67.5f;
	} else if (zoom >= 14.0f) {
		tilt = (((zoom - 14.0f) / 1.5f) * (67.5f - 45.0f)) + 45.0f;
	} else if (zoom >= 10.0f) {
		tilt = (((zoom - 10.0f) / 4.0f) * (45.0f - 30.0f)) + 30.0f;
	}

	return tilt;
}