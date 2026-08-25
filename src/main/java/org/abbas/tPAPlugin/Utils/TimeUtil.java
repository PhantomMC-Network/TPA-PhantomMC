package org.abbas.tPAPlugin.Utils;

public class TimeUtil {
    private TimeUtil() {
        //utility class
    }
    /*
    Convert Second to Milliseconds
     */
    public static long secondsToMilliseconds(long seconds) {
        return seconds * 1000;
    }
    /*
    Convert Milliseconds to Seconds
     */
    public static long millisecondsToSeconds(long milliseconds) {
        return milliseconds / 1000L;
    }
    /*
    Expiration timestamp
     */
    public static long ExpirationTime(long seconds) {
        return System.currentTimeMillis() + secondsToMilliseconds(seconds);
    }
    /*
    get Remaining Seconds until expiration
     */
    public static long getRemainingSeconds(long expirationTime) {
        long remainingSeconds = expirationTime - System.currentTimeMillis();
        if (remainingSeconds < 0) {
            return 0;
        }
        return (long) Math.ceil(remainingSeconds / 1000.0);
    }
}
