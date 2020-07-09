package command;

public class CommandScript extends CommandSimple {
    String script;

    public CommandScript(String script) {
        super(CommandsList.EXECUTE_SCRIPT);
        this.script = script;
    }

    @Override
    public String toString() {
        return "CommandScript{" +
                "script='" + script + '\'' +
                '}';
    }

    @Override
    public String returnObj() {
        return script;
    }
}