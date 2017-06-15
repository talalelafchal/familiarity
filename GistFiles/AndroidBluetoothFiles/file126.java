private String getDistance(accuracy) {
  if (accuracy == -1.0) {
    return "Unknown";
  } else if (accuracy < 1) {
    return "Immediate";
  } else if (accuracy < 3) {
    return "Near";
  } else {
    return "Far";
  }
}