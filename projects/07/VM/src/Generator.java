import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

class Generator implements AutoCloseable {

    // segment name to symbol mapping
    private static final HashMap<String, String> segmentTable;
    static {
        segmentTable = new HashMap<>();
        segmentTable.put(Constants.Segment.LOCAL, "LCL");
        segmentTable.put(Constants.Segment.ARGUMENT, "ARG");
        segmentTable.put(Constants.Segment.THIS, "THIS");
        segmentTable.put(Constants.Segment.THAT, "THAT");
    }

    private final File file;
    private final BufferedWriter writer;

    private int labelCount = 0;
    private String currentTranslationUnitName;

    Generator(final File file) throws IOException {
        this.file = file;
        this.writer = new BufferedWriter(new FileWriter(file));
    }

    /**
     * New translation unit start.
     */
    public void setFileName(String fileName) throws IOException {
        currentTranslationUnitName = fileName;
        writer.append("// ").append(fileName);
        writer.newLine();
    }

    public void writeArithmetic(String command) throws IOException {
        writer.append("// ").append(command);
        writer.newLine();
        switch (command) {
            case Constants.Operation.ADD:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("M=M+D");
                writer.newLine();
                break;
            case Constants.Operation.SUB:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("M=M-D");
                writer.newLine();
                break;
            case Constants.Operation.NEG:
                writer.append("@SP");
                writer.newLine();
                writer.append("A=M-1");
                writer.newLine();
                writer.append("M=-M");
                writer.newLine();
                break;
            case Constants.Operation.AND:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("M=M&D");
                writer.newLine();
                break;
            case Constants.Operation.OR:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("M=M|D");
                writer.newLine();
                break;
            case Constants.Operation.NOT:
                writer.append("@SP");
                writer.newLine();
                writer.append("A=M-1");
                writer.newLine();
                writer.append("M=!M");
                writer.newLine();
                break;
            case Constants.Operation.EQ:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("D=M-D");
                writer.newLine();

                handleConditionalAssignment("JEQ");
                break;                
            case Constants.Operation.GT:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("D=M-D");
                writer.newLine();

                handleConditionalAssignment("JGT");
                break;                                
            case Constants.Operation.LT:
                writer.append("@SP");
                writer.newLine();
                writer.append("AM=M-1");
                writer.newLine();
                writer.append("D=M");
                writer.newLine();
                writer.append("A=A-1");
                writer.newLine();
                writer.append("D=M-D");
                writer.newLine();

                handleConditionalAssignment("JLT");
                break;                
        }

        if (Constants.Operation.EQ.equals(command) 
            || Constants.Operation.GT.equals(command) 
            || Constants.Operation.LT.equals(command)) {
            labelCount += 1;
        }
    }

    private void handleConditionalAssignment(String condition) throws IOException {
        writer.append("M=0").append(" // true");
        writer.newLine();

        selectBooleanLabel("IF");
        writer.append("D;").append(condition);
        writer.newLine();
        selectBooleanLabel("ELSE");
        writer.append("0;JMP");
        writer.newLine();

        writeBooleanLabel("IF");
        writer.append("@SP");
        writer.newLine();
        writer.append("A=M-1");
        writer.newLine();
        writer.append("M=-1").append(" // false");
        writer.newLine();

        writeBooleanLabel("ELSE");
    }

    private void writeBooleanLabel(String label) throws IOException {
        writer.append(String.format("(%s_%d)", label, labelCount));
        writer.newLine();
    }

    private void selectBooleanLabel(String label) throws IOException {
        writer.append(String.format("@%s_%d", label, labelCount));
        writer.newLine();
    }

    public void writePushPop(String command, String segment, int index) throws IOException {
        writer.append("// ")
              .append(command + " ")
              .append(segment + " ")
              .append(Integer.toString(index));
        writer.newLine();
        switch (segment) {
            case Constants.Segment.LOCAL:
                handlePushPop(command,"@LCL", index);
                break;
            case Constants.Segment.ARGUMENT:
                handlePushPop(command,"@ARG", index);
                break;
            case Constants.Segment.THIS:
                handlePushPop(command,"@THIS", index);
                break;
            case Constants.Segment.THAT:
                handlePushPop(command,"@THAT", index);
                break;
            case Constants.Segment.TEMP:
                handlePushPop(command,"@5", index);
                break;                
            case Constants.Segment.CONSTANT:
                if (command.equals(Constants.Operation.PUSH)) {
                    writer.append("@" + index);
                    writer.newLine();
                    writer.append("D=A");
                    writer.newLine();
                    derefStackPtr();
                    writer.append("M=D");
                    writer.newLine();
                    incStackPtr();
                } else {
                    throw new UnsupportedOperationException();
                }
                break;                                
            case Constants.Segment.STATIC:
                if (command.equals(Constants.Operation.PUSH)) {
                    writeStatic(index);
                    writer.append("D=M");
                    writer.newLine();

                    writer.append("@SP");
                    writer.newLine();
                    writer.append("A=M");
                    writer.newLine();
                    writer.append("M=D");
                    writer.newLine();
                    incStackPtr();
                } else {
                    decStackPtr();
                    writer.append("A=M");
                    writer.newLine();
                    writer.append("D=M");
                    writer.newLine();

                    writeStatic(index);
                    writer.append("M=D");
                    writer.newLine();
                }
                break; 
            case Constants.Segment.POINTER:
            if (command.equals(Constants.Operation.PUSH)) {
                if (index == 1) {
                    writer.append("@THAT");
                    writer.newLine();
                    writer.append("D=A");
                    writer.newLine();

                    derefStackPtr();
                    writer.append("M=D");
                    writer.newLine();
                    incStackPtr();
                } else if (index == 0) {
                    writer.append("@THIS");
                    writer.newLine();
                    writer.append("D=A");
                    writer.newLine();

                    derefStackPtr();
                    writer.append("M=D");
                    writer.newLine();
                    incStackPtr();
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                if (index == 1) {
                    decStackPtr();
                    derefStackPtr();
                    writer.append("D=M");
                    writer.newLine();

                    writer.append("@THAT");
                    writer.newLine();
                    writer.append("M=D");
                    writer.newLine();
                } else if (index == 0) {
                    decStackPtr();
                    derefStackPtr();
                    writer.append("D=M");
                    writer.newLine();

                    writer.append("@THIS");
                    writer.newLine();
                    writer.append("M=D");
                    writer.newLine();
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }

    private void writeStatic(int index) throws IOException {
        writer.append("@" + currentTranslationUnitName + "." + index);
        writer.newLine();
    }

    private void handlePushPop(String command, String segPtr, int index) throws IOException {
        if (segPtr == null || segPtr.isEmpty()) {
            throw new IllegalArgumentException("Empty segment pointer.");
        }
        if (index < 0 || index > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid index value.");
        }

        // push -> addr = segPtr + i; *SP = *addr; SP++;        
        if (command.equals(Constants.Operation.PUSH)) {
            derefSegmentOffset(segPtr, index);
            writer.append("D=M");
            writer.newLine();
            
            derefStackPtr();
            writer.append("M=D");
            writer.newLine();
            incStackPtr();
        } 
        // pop -> addr = segPtr + i; SP--; *addr = *SP;
        else {
            derefSegmentOffset(segPtr, index);
            writer.append("D=A");
            writer.newLine();
            // use non-colliding register to hold target addr
            writer.append("@13");
            writer.newLine();
            writer.append("M=D");
            writer.newLine();
            
            decStackPtr();
            derefStackPtr();
            writer.append("D=M");
            writer.newLine();
            
            // write popped value
            writer.append("@13");
            writer.newLine();
            writer.append("A=M");
            writer.newLine();
            writer.append("M=D");
            writer.newLine();
        }
    }

    /**
     * Dereferenced value can be taken from register "M".
     */
    private void derefSegmentOffset(String segPtr, int offset) throws IOException {
        writer.append(segPtr);
        writer.newLine();
        writer.append("D=M");
        writer.newLine();
        writer.append("@" + offset);
        writer.newLine();
        writer.append("D=D+A");
        writer.newLine();
        writer.append("A=D");
        writer.newLine();
    }

    private void derefStackPtr() throws IOException {
        writer.append("@SP");
        writer.newLine();
        writer.append("A=M");
        writer.newLine();
    }

    private void incStackPtr() throws IOException {
        writer.append("@SP");
        writer.newLine();
        writer.append("M=M+1");
        writer.newLine();
    }

    private void decStackPtr() throws IOException {
        writer.append("@SP");
        writer.newLine();
        writer.append("M=M-1");
        writer.newLine();
    }

    public void writeInit() {

    }

    public void writeLabel(String label) throws IOException {
        writer.append("// ").append("label declaration");     
        writer.newLine();              
        writer.append("(" + label + ")");
        writer.newLine();        
    }

    public void writeGoto(String label) throws IOException {
        writer.append("// ").append("goto " + label);     
        writer.newLine();              
        writer.append("@"+ label);
        writer.newLine();
        writer.append("0;JMP");
        writer.newLine();   
    }

    public void writeIf(String label) throws IOException {
        writer.append("// ").append("if-goto " + label);    
        writer.newLine();
        decStackPtr();
        derefStackPtr();    
        writer.append("D=M");
        writer.newLine();      
        writer.append("@"+ label);
        writer.newLine();
        writer.append("D;JGT");
        writer.newLine();   
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            Logger logger = Logger.getAnonymousLogger();
            logger.severe(e.getMessage());
        }
    }
}