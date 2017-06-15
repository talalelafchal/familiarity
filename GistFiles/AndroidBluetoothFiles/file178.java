if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
                // Let's log all extras from the intent received
                String allExtras = intent.getExtras().toString();
                Log.d("MainActivity", "META INTENT | " + allExtras);

                String trackId = intent.getStringExtra("id");
                String artistName = intent.getStringExtra("artist");
                String albumName = intent.getStringExtra("album");
                String trackName = intent.getStringExtra("track");
                int trackLengthInSec = intent.getIntExtra("length", 0);
                int trackLengthInMS = trackLengthInSec * 1000;
                boolean playing = intent.getBooleanExtra("playing", true);
                int positionInMs = intent.getIntExtra("playbackPosition", 0);

                Log.i("MainActivity", "Meta Changed | ID: " + trackId);
                Log.i("MainActivity", "Meta Changed | Artist: " + artistName);
                Log.i("MainActivity", "Meta Changed | Album: " + albumName);
                Log.i("MainActivity", "Meta Changed | Length: " + trackLengthInSec);
                Log.i("MainActivity", "Meta Changed | Playing: " + playing);
                Log.i("MainActivity", "Meta Changed | Position (MS):" + positionInMs);

                // Also SendMetadata
                Intent avrcp = new Intent(BroadcastTypes.ANDROID_METADATA_CHANGED);
                //avrcp.putExtra("id", trackId);
                avrcp.putExtra("track", trackName);
                avrcp.putExtra("artist", artistName);
                avrcp.putExtra("album", albumName);
                avrcp.putExtra("duration", trackLengthInMS);
                avrcp.putExtra("playing", true);
                avrcp.putExtra("position", 13029);

                // Insert Fake Info (Matching Android Intent)
                avrcp.putExtra("id", 1648);
                avrcp.putExtra("currentContainerName", "");
                avrcp.putExtra("albumId", 766585914);
                avrcp.putExtra("domain", 0);
                avrcp.putExtra("currentSongLoaded", true);
                avrcp.putExtra("preparing", false);
                avrcp.putExtra("rating", 0);
                avrcp.putExtra("supportsRating", true);
                avrcp.putExtra("currentContainerTypeValue", 11);
                avrcp.putExtra("currentContainerId", 4);
                avrcp.putExtra("streaming", false);
                avrcp.putExtra("inErrorState", false);
                avrcp.putExtra("albumArtFromService", false);
                avrcp.putExtra("local", true);
                avrcp.putExtra("ListSize", 75);
                avrcp.putExtra("previewPlayType", -1);
                avrcp.putExtra("ListPosition", 50);

                // Send AVRCP Over Bluetooth
                sendBroadcast(avrcp);