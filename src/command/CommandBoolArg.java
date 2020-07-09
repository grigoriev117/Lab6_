package command;

public class CommandBoolArg extends CommandSimple {
    String l;

    public CommandBoolArg(String l) {
        super(CommandsList.FILTER_LESS_THAN_LOYAL);
        this.l = l;
    }

    @Override
    public String toString() {
        return "CommandBoolArg{" +
                "l=" + l +
                '}';
    }

    @Override
    public String returnObj() {
        return l;
    }
}
