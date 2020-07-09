package command;

public class CommandWArg extends CommandSimple {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String l;

    public CommandWArg(String l) {
        super(CommandsList.REMOVE_ALL_BY_WEAPON_TYPE);
        this.l = l;
    }

    @Override
    public String toString() {
        return "CommandWArg{" +
                "l=" + l +
                '}';
    }

    @Override
    public String returnObj() {
        return l;
    }
}
