package command;

import spacemarine.*;

public class CommandArgs extends CommandSimple {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SpaceMarine sm;

    public CommandArgs(CommandsList com, SpaceMarine sm) {
        super(com);
        this.sm = sm;
    }

    @Override
    public String toString() {
        return "CommandArgs{" +
                "sm=" + sm +
                '}';
    }

    @Override
    public SpaceMarine returnObj() {
        return sm;
    }
}
