package cz.tefek.util;

public class ColonTimeParser
{
    public static String fromMillis(long millis)
    {
        var le = millis / 1000L;
        var h = (le / 3600);

        if (h > 0)
        {
            return String.format("%d:%02d:%02d", h, le / 60 % 60, le % 60);
        }
        else
        {
            return String.format("%02d:%02d", le / 60 % 60, le % 60);
        }
    }
}
