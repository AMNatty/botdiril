package cz.tefek.botdiril.voice.music;

public class CouldNotEnqueueException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 4845522123941793477L;

    private String human;

    public CouldNotEnqueueException(String human)
    {
        super(human);
        this.human = human;
    }

    public String getHumanReadable()
    {
        return human;
    }
}
