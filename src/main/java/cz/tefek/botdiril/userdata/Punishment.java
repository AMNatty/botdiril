package cz.tefek.botdiril.userdata;

public class Punishment
{
    private final long tillWhen;
    private String reason;
    private long issuer;

    public Punishment(long tillWhen, String reason, long issuer)
    {
        this.tillWhen = tillWhen;
        this.reason = reason;
        this.issuer = issuer;
    }

    public String getReason()
    {
        return reason;
    }

    public long getIssuerID()
    {
        return issuer;
    }

    public long getTillWhen()
    {
        return tillWhen;
    }
}
