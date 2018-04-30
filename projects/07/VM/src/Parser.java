import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.lang.Iterable;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser implements AutoCloseable {

    // virtual memory segments
    private static final String SEGMENT = Constants.Segment.ARGUMENT 
        + '|' + Constants.Segment.LOCAL
        + '|' + Constants.Segment.STATIC
        + '|' + Constants.Segment.CONSTANT
        + '|' + Constants.Segment.THIS
        + '|' + Constants.Segment.THAT
        + '|' + Constants.Segment.POINTER
        + '|' + Constants.Segment.TEMP;

    // vm commands
    private static final String COMMAND = Constants.Operation.ADD
    + '|' + Constants.Operation.SUB
    + '|' + Constants.Operation.EQ
    + '|' + Constants.Operation.GT
    + '|' + Constants.Operation.LT
    + '|' + Constants.Operation.NEG
    + '|' + Constants.Operation.NOT
    + '|' + Constants.Operation.AND
    + '|' + Constants.Operation.OR;
            
    // whitespace | comments
    private static final Pattern WHITESPACE_PATTERN =  Pattern.compile("^(\\/\\/.*|\\s*)$");
    // named after their corresponding commands
    private static final Pattern ARITHMETIC_PATTERN = Pattern.compile("^\\s*(" + COMMAND + ")");
    private static final Pattern PUSH_PATTERN = Pattern.compile("^\\s*push (" + SEGMENT + ") (\\d+)");
    private static final Pattern POP_PATTERN = Pattern.compile("^\\s*pop (" + SEGMENT + ") (\\d+)");
    private static final Pattern LABEL_PATTERN = Pattern.compile("^\\s*label (.+?)\\b");
    private static final Pattern GOTO_PATTERN = Pattern.compile("^\\s*goto (.+?)\\b");
    private static final Pattern IF_GOTO_PATTERN = Pattern.compile("^\\s*if-goto (.+?)\\b");

    private final File file;

    private BufferedReader reader;
    private Command command;
    private int lineNumber = 0;
    private String arg1;
    private String arg2;
    
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
            final Matcher am = ARITHMETIC_PATTERN.matcher(line);
            final Matcher pum = PUSH_PATTERN.matcher(line);
            final Matcher pom = POP_PATTERN.matcher(line);
            final Matcher wm = WHITESPACE_PATTERN.matcher(line);
            final Matcher lm = LABEL_PATTERN.matcher(line);
            final Matcher gm = GOTO_PATTERN.matcher(line);
            final Matcher igm = IF_GOTO_PATTERN.matcher(line);

            // whitespace
            if (wm.find()) {
                command = Command.WHITESPACE;
            }
            else if (am.find()) {
                command = Command.ARITHMETIC;
                arg1 = am.group(1);
            } 
            else if (pum.find()) {
                command = Command.PUSH;
                arg1 = pum.group(1);
                arg2 = pum.group(2);
            }
            else if (pom.find()) {
                command = Command.POP;
                arg1 = pom.group(1);
                arg2 = pom.group(2);
            }
            else if (lm.find()) {
                command = Command.LABEL;
                arg1 = lm.group(1);
            }
            else if (gm.find()) {
                command = Command.GOTO;
                arg1 = gm.group(1);
            }
            else if (igm.find()) {
                command = Command.IF;
                arg1 = igm.group(1);
            }
            // invalid line
            else {
                throw new IllegalArgumentException(
                    String.format("Syntax error at line: %d.\nLine: \"%s\"", lineNumber, line));
            }

            if (!wm.find()) {
                lineNumber += 1;
            }
        }
    }

    Command getCommand() {
        return command;
    }

    String getArg1() {
        return arg1;
    }

    int getArg2() {
        return Integer.parseInt(arg2);
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
        ARITHMETIC,
        PUSH, 
        POP,
        LABEL,
        GOTO,
        IF,
        FUNCTION,
        RETURN,
        CALL,
        WHITESPACE;
    }
}