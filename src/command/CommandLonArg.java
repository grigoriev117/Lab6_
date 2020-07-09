package command;


public class CommandLonArg extends CommandSimple {
    Long id;

    public CommandLonArg(Long id) {
        super(CommandsList.REMOVE_BY_ID);
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommandLonArg{" +
                "id=" + id +
                '}';
    }

    @Override
    public Long returnObj() {
        return id;
    }
}