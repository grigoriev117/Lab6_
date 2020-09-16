package command;

import java.io.Serializable;

public class CommandSimple implements Serializable {
    CommandsList current;

    public CommandSimple(CommandsList com) {
        current = com;
    }

    public CommandsList getCurrent() {
        return current;
    }

    public Object returnObj() {
        return null;
    }

    @Override
    public String toString() {
        return "Command{" +
                "current=" + current +
                '}';
    }
}