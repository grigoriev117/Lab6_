package command;


public class CommandMagic extends CommandSimple {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String smtr;

    public CommandMagic(String smtr) {
        super(CommandsList.MAGIC);
        this.smtr = smtr;
    }

    @Override
    public String toString() {
        return "CommandMagic{" +
                "smtr=" + smtr +
                '}';
    }

    @Override
    public String returnObj() {
        return smtr;
    }
}