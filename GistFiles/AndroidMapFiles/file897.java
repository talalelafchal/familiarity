/**
 * Date Util class holds static utility methods specific to Date and Time manipulation
 * that can be used across multiple projects.
 *
 * @author Damon
 */
@SuppressWarnings("unused")
public class DLDateUtil {

    public static final String SECONDS_AGO = " seconds ago";
    public static final String MINUTES_AGO = " minutes ago";
    public static final String HOURS_AGO = " hours ago";
    public static final String DAYS_AGO = " days ago";

    /**
     * Change the date format.
     *
     * @param date   Date object
     * @param format Date format
     * @return The changed date as string
     */
    public static String formatDate(Date date, String format) {
        String dateStr = "";

        if (date != null) {
            try {
                DateFormat df = new DateFormat();
                dateStr = df.format(format, date).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dateStr;
    }

    /**
     * Get the current date/time in a specific format.
     *
     * @param format Date format
     * @return Current date and time in specified format as string
     */
    public static String getCurrentDateAndTime(String format) {
        String currentDateAndTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            currentDateAndTime = sdf.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentDateAndTime;
    }

    /**
     * Returns relative time difference in string format.
     *
     * @param date Date object
     * @return String expressing relative time difference.
     *
     * @see #getTimeDifferenceAsString(java.util.Date, String, String, String, String)
     */
    public static String getTimeDifferenceAsString(Date date) {
        return getTimeDifferenceAsString(date, SECONDS_AGO, MINUTES_AGO, HOURS_AGO, DAYS_AGO);
    }

    /**
     * Returns relative time difference in string format.
     *
     * <p>Expected output:</p>
     * <ol>
     *     <li>x seconds ago</li>
     *     <li>x minutes ago</li>
     *     <li>x hours ago</li>
     *     <li>x days ago</li>
     * </ol>
     *
     * @param date Date object
     * @param secsAgo Override default {@link #SECONDS_AGO} string.
     * @param minsAgo Override default {@link #MINUTES_AGO} string.
     * @param hoursAgo Override default {@link #HOURS_AGO} string.
     * @param daysAgo Override default {@link #DAYS_AGO} string.
     * @return String expressing relative time difference.
     */
    public static String getTimeDifferenceAsString(Date date, String secsAgo, String minsAgo,
                                                   String hoursAgo, String daysAgo) {
        Date currentDate = new Date();

        //in milliseconds
        long diff = currentDate.getTime() - date.getTime();

        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffSeconds < 0)
            return "0" + secsAgo;

        if (diffSeconds > 60) {
            if (diffMinutes > 60) {
                if (diffHours > 24) {
                    return diffDays + daysAgo;
                } else {
                    return diffHours + hoursAgo;
                }
            } else {
                return diffMinutes + minsAgo;
            }
        } else {
            return diffSeconds + secsAgo;
        }
    }
}
