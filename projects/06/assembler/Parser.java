import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.lang.Iterable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser implements AutoCloseable {

    private final File file;
    private BufferedReader reader;
    private Command command;
    private int lineNumber = 0;

    // named after their corresponding instruction
    private final Pattern aPattern = Pattern.compile("^\\s*\\@([\\w.$:]+)");
    private final Pattern cPattern = Pattern.compile("^\\s*([AMD]{0,3})?\\=?([AMD]?[-+!&|]?(?:[AMD]|[0-1]))\\;?(\\w{3})?");
    private final Pattern lPattern = Pattern.compile("^\\s*\\((.+)\\)");
    // whitespace | comments
    private final Pattern wPattern = Pattern.compile("^(\\/\\/.*|\\s*)$");

    private String symbol;
    private String dest;
    private String comp;
    private String jump;
    
    Parser(final File file) throws IOException {
        this.file = file;
        this.reader = new BufferedReader(new FileReader(file));
    }

    public boolean hasNext() throws IOException {
        return reader.ready();
    }

    public void next() throws IOException {
        String line = reader.readLine();
        if (line != null) {
            final Matcher am = aPattern.matcher(line);
            final Matcher wm = wPattern.matcher(line);
            final Matcher lm = lPattern.matcher(line);
            final Matcher cm = cPattern.matcher(line);

            // A-instruction
            if (am.find()) {
                command = Command.A_COMMAND;
                symbol = am.group(1);
                lineNumber += 1;
            } 
            // whitespace
            else if (wm.find()) {
                // ignore
                command = Command.WHITESPACE;
            }
            // label-instruction 
            else if (lm.find()) {
                command = Command.L_COMMAND;
                symbol = lm.group(1);
            }
            // C-instruction
            else if (cm.find()) {
                command = Command.C_COMMAND;
                dest = cm.group(1);
                comp = cm.group(2);
                jump = cm.group(3);
                lineNumber += 1;
            }
            // invalid line
            else {
                throw new IllegalArgumentException(
                    String.format("Syntax error at line: %d.\nLine: \"%s\"", lineNumber, line));
            }
        }
    }

    Command getCommand() {
        return command;
    }

    String getSymbol() {
        return symbol;
    }

    String getDest() {
        return dest;
    }
    
    String getComp() {
        return comp;
    }
    
    String getJump() {
        return jump;
    }

    int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                Logger logger = Logger.getAnonymousLogger();
                logger.severe(e.getMessage());
            }
        }
    }

    static enum Command {
        A_COMMAND,
        C_COMMAND,
        L_COMMAND,
        WHITESPACE
    }
}