import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;

/**
 * Turn this into a class with builders, for the lazy!
 * https://developer.android.com/guide/components/intents-common.html
 * @author John
 *
 */
public class CommonIntents {

	/**
	 * Create a new alarm
	 * 
	 * In order to invoke the ACTION_SET_ALARM intent, your app must have the 
	 * SET_ALARM permission
	 * @author John
	 *
	 */
	public static class AlarmIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(AlarmClock.ACTION_SET_ALARM);
			}
			
			/**
			 * Hour between 0 and 23
			 * @param hour
			 * @return
			 */
			public Builder setHour(int hour) {
				intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
				return this;
			}
			
			/**
			 * Minute between 0 and 59
			 * @param minute
			 * @return
			 */
			public Builder setMinute(int minute) {
				intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
				return this;
			}
			
			/**
			 * A custom message to identify the alarm.
			 * @param message
			 * @return
			 */
			public Builder setMinute(String message) {
				intent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
				return this;
			}
			
			/**
			 * An ArrayList including each week day on which this 
			 * alarm should be repeated. Each day must be declared 
			 * with an integer from the Calendar class such as MONDAY. 
			 * For a one-time alarm, do not specify this extra.
			 * @param days
			 * @return
			 */
			public Builder setDays(ArrayList<Integer> days) {
				intent.putExtra(AlarmClock.EXTRA_DAYS, days);
				return this;
			}
			
			/**
			 * A content: URI specifying a ringtone to use with the alarm,
			 * or VALUE_RINGTONE_SILENT for no ringtone. 
			 * To use the default ringtone, do not specify this extra.
			 * @param ringtone
			 * @return
			 */
			public Builder setRingtone(Uri ringtone) {
				intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtone);
				return this;
			}
			
			/**
			 * A boolean specifying whether to vibrate for this alarm.
			 * @param ringtone
			 * @return
			 */
			public Builder setVibrate(boolean vibrate) {
				intent.putExtra(AlarmClock.EXTRA_VIBRATE, vibrate);
				return this;
			}
			
			/**
			 * A boolean specifying whether the responding app should 
			 * skip its UI when setting the alarm. If true, the app 
			 * should bypass any confirmation UI and simply set the 
			 * specified alarm.
			 * @return
			 */
			public Builder setSkipUi(boolean value) {
				intent.putExtra(AlarmClock.EXTRA_SKIP_UI, value);
				return this;
			}
			
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Open a webpage
	 * @author John
	 *
	 */
	public static class BrowserIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_VIEW);
			}
			
			public Builder setUrl(String url) {
				intent.setData(Uri.parse(url));
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Intent for adding a calendar event
	 * @author John
	 *
	 */
	public static class CalendarIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
				intent.setData(Events.CONTENT_URI);
			}
			
			public Builder setBeginTime(long endTime) {
				intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, endTime);
				return this;
			}
			
			public Builder setEndTime(long endTime) {
				intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
				return this;
			}
			
			public Builder setAllDay(boolean allDay) {
				intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
				return this;
			}
			
			public Builder setTitle(String title) {
				intent.putExtra(Events.TITLE, title);
				return this;
			}
			
			public Builder setDescription(String description) {
				intent.putExtra(Events.DESCRIPTION, description);
				return this;
			}
			
			public Builder setLocation(String location) {
				intent.putExtra(Events.EVENT_LOCATION, location);
				return this;
			}
			
			/**
			 * Comma separated list of attendees
			 * @param attendee
			 * @return
			 */
			public Builder setAttendees(String attendees) {
				intent.putExtra(Intent.EXTRA_EMAIL, attendees);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Capture a picture or video and return it. Defaults to 
	 * image capture
	 * @author John
	 *
	 */
	public static class CameraIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
			    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			}
			
			public Builder restrictToImageCapture() {
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				return this;
			}
			
			public Builder restrictToVideoCapture() {
				intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
				return this;
			}
			
			public Builder setOutput(Uri location, String fileName) {
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
			            Uri.withAppendedPath(location, fileName));
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Use static parse method to get the data in onActivityResult from 
	 * a CameraIntent
	 * @author John
	 *
	 */
	public static class CameraResponse {
		Bitmap thumbnail;
		
		public static CameraResponse parse(Intent data) {
			Bitmap image = data.getParcelableExtra("data");
			CameraResponse response = new CameraResponse(image);
			return response;
		}
		
		public CameraResponse(Bitmap image) {
			thumbnail = image;
		}
		
		public Bitmap getThumbnail() {
			return thumbnail;
		}
	}
	
	/**
	 * Initiate a phone call or predial a number
	 * @author John
	 *
	 */
	public static class DialIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_DIAL);
			}
			
			/**
			 * Valid telephone numbers are those defined in the IETF RFC 3966. Valid examples include the following:
			 * 2125551212, 
			 * (212) 555 1212
			 * @param number
			 * @return
			 */
			public Builder setNumber(String number) {
				intent.setData(Uri.parse("tel:" + number));
				return this;
			}
			
			/**
			 * Launch straight into call instead of dialer
			 * Requires CALL_PHONE permission in manifest
			 * @param email
			 * @return
			 */
			public Builder forceCall() {
				intent.setAction(Intent.ACTION_CALL);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	public static class DirectionsIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_VIEW);
			}
			
			public Builder setDestination(String destination) {
				intent.setData(
						Uri.parse("http://maps.google.com/maps")
						.buildUpon()
						.appendQueryParameter("daddr", destination)
						.build());
				return this;
			}
			
			public Builder forceGoogleMaps() {
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	public static class EmailIntent {
		
		public static class Builder {
			private Intent intent;
			public Builder() {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.fromParts("mailto", "", null));
			}
			
			public Builder setRecipient(String email) {
				return setRecipients(new String[] { email });
			}
			
			public Builder setRecipients(String[] emails) {
				intent.putExtra(Intent.EXTRA_EMAIL,
			            emails);
				return this;
			}
			
			public Builder setCCRecipient(String email) {
				return setCCRecipients(new String[] { email });
			}
			
			public Builder setCCRecipients (String[] emails) {
				intent.putExtra(Intent.EXTRA_CC,
			            emails);
				return this;
			}
			
			public Builder setBCCRecipient(String email) {
				return setBCCRecipients(new String[] { email });
			}
			
			public Builder setBCCRecipients (String[] emails) {
				intent.putExtra(Intent.EXTRA_CC,
			            emails);
				return this;
			}
			
			public Builder setSubject(String subject) {
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				return this;
			}
			
			public Builder setBody(String body) {
				intent.putExtra(Intent.EXTRA_TEXT, body);
				return this;
			}
			
			public Builder setAttachment(Uri uri) {
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				return this;
			}
			
			public Builder setAttachments(ArrayList<Uri> uris) {
				intent.putExtra(Intent.EXTRA_STREAM, uris);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Show location on a map
	 * @author John
	 *
	 */
	public static class MapIntent {
		
		public static class Builder {
			private Intent intent;
			private Uri uri;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_VIEW);
			}
			
			/**
			 * Show the map at the given longitude and latitude.
			 * @param lat
			 * @param lng
			 * @return
			 */
			public Builder setLatLng(String lat, String lng) {
				uri = Uri.parse("geo:" + lat + "," + lng);
				return this;
			}
			
			/**
			 * Show the map at the given longitude and latitude 
			 * at a certain zoom level. A zoom level of 1 shows 
			 * the whole Earth, centered at the given lat,lng. 
			 * The highest (closest) zoom level is 23.
			 * @param lat
			 * @param lng
			 * @param zoom
			 * @return
			 */
			public Builder setLatLngZoom(String lat, String lng, String zoom) {
				uri = Uri.parse("geo:" + lat + "," + lng);
				return this;
			}
			
			/**
			 * Show the map at the given longitude and latitude with a string label.
			 * @param lat
			 * @param lng
			 * @param label
			 * @return
			 */
			public Builder setLatLngLabel(String lat, String lng, String label) {
				uri = Uri.parse("geo:0,0?q=" + lat + "," + lng + "(" + label + ")");
				return this;
			}
			
			/**
			 * Show the location for "my street address" 
			 * (may be a specific address or location query).
			 * @param query
			 * @return
			 */
			public Builder setLocationQuery(String query) {
				uri = Uri.parse("geo:0,0?q=" + query);
				return this;
			}
			
			public Intent build() {
				intent.setData(uri);
				return intent;
			}
		}
	}
	
	/**
	 * Play a media file
	 * @author John
	 *
	 */
	public static class MediaIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_VIEW);
			}
			
			public Builder setMedia(Uri media) {
				intent.setData(media);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Retrieve a file
	 * The file reference returned to your app is transient 
	 * to your activity's current lifecycle, so if you want 
	 * to access it later you must import a copy that you can 
	 * read later. This intent also allows the user to create 
	 * a new file in the process (for example, instead of 
	 * selecting an existing photo, the user can capture a 
	 * new photo with the camera).
	 * @author John
	 *
	 */
	public static class RetrieveFileIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_GET_CONTENT);
			}
			
			public Builder setMimeType(String mimeType) {
				intent.setType(mimeType);
				return this;
			}
			
			/**
			 * Restrict to local files
			 * @param mimeType
			 * @return
			 */
			public Builder restrictToLocal() {
				intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
				return this;
			}
			
			/**
			 * To return only "openable" files that can be represented
			 * as a file stream with openFileDescriptor().
			 * @return
			 */
			public Builder restrictToOpenable() {
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				return this;
			}
			
			/**
			 * Allow user to select multiple files. 4.3+ only
			 * @return
			 */
			public Builder allowMultiple() {
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Initiates a web search
	 * @author John
	 *
	 */
	public static class SearchIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_SEARCH);
			}
			
			public Builder setQuery(String query) {
				intent.putExtra(SearchManager.QUERY, query);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Open the settings screen respective to the action name.
	 * @author John
	 *
	 */
	public static class SettingsIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Settings.ACTION_SETTINGS);
			}
			
			public Builder setActionWireless() {
				intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
				return this;
			}
			
			public Builder setActionAirplaneMode() {
				intent.setAction(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
				return this;
			}
			
			public Builder setActionWifi() {
				intent.setAction(Settings.ACTION_WIFI_SETTINGS);
				return this;
			}
			
			public Builder setActionAPN() {
				intent.setAction(Settings.ACTION_APN_SETTINGS);
				return this;
			}
			
			public Builder setActionBluetooth() {
				intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
				return this;
			}
			
			public Builder setActionDate() {
				intent.setAction(Settings.ACTION_DATE_SETTINGS);
				return this;
			}
			
			public Builder setActionLocale() {
				intent.setAction(Settings.ACTION_LOCALE_SETTINGS);
				return this;
			}
			
			public Builder setActionInputMethod() {
				intent.setAction(Settings.ACTION_INPUT_METHOD_SETTINGS);
				return this;
			}
			
			public Builder setActionDisplay() {
				intent.setAction(Settings.ACTION_DISPLAY_SETTINGS);
				return this;
			}
			
			public Builder setActionSecurity() {
				intent.setAction(Settings.ACTION_SECURITY_SETTINGS);
				return this;
			}
			
			public Builder setActionLocationSource() {
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				return this;
			}
			
			public Builder setActionInternalStorage() {
				intent.setAction(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
				return this;
			}
			
			public Builder setActionMemoryCard() {
				intent.setAction(Settings.ACTION_MEMORY_CARD_SETTINGS);
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
	/**
	 * Send SMS message with optional attachment and subject
	 * @author John
	 *
	 */
	public static class TextMessageIntent {
		
		public static class Builder {
			private Intent intent;
			
			public Builder() {
				intent = new Intent(Intent.ACTION_SEND);
			}
			
			/**
			 * A string for the text message.
			 * @param message
			 * @return
			 */
			public Builder setMessage(String message) {
				intent.putExtra("sms_body", message);
				return this;
			}
			
			/**
			 * A string for the message subject (usually for MMS only).
			 * @param subject
			 * @return
			 */
			public Builder setSubject(String subject) {
				intent.putExtra("subject", subject);
				return this;
			}
			
			/**
			 * A Uri pointing to the image or video to attach.
			 * @param uri
			 * @return
			 */
			public Builder setAttachment(Uri uri) {
				intent = new Intent(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				return this;
			}
			
			/**
			 * A Uri pointing to the image or video to attach. 
			 * If using the ACTION_SEND_MULTIPLE action, this extra 
			 * Should be an ArrayList of Uris pointing to the 
			 * images/videos to attach.
			 * @param uris
			 * @return
			 */
			public Builder setAttachments(ArrayList<Uri> uris) {
				intent.setAction(Intent.ACTION_SEND_MULTIPLE);
				intent.putExtra(Intent.EXTRA_STREAM, uris);
				return this;
			}
			
			/**
			 * Ensure that your intent is handled only by a text messaging app
			 * @param message
			 * @return
			 */
			public Builder forceSmsOnly(String message) {
				intent.setData(Uri.parse("smsto:"));
				return this;
			}
			
			public Intent build() {
				return intent;
			}
		}
	}
	
}
