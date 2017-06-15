Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + sonSemt +
                            "&daddr=" + mNavigationAdresSemt));
            startActivity(intent);