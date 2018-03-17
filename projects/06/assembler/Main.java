import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Deque;

class Main {

    private static final int WORD_WIDTH = 16;
    private static final int UNALLOC_ADDRESS_START = 16;

    public static void main(String[] args) throws IOException {
        for (String path : args) {
            final String fileName = path.substring(path.lastIndexOf(File.separator) + 1, path.indexOf(".asm"));
            final File file = new File(path);
            Parser parser = new Parser(file);

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName + ".hack"))) {
                // resolve symbolic labels
                while (parser.hasNext()) {
                    parser.next();
                    if (parser.getCommand() == Parser.Command.L_COMMAND && !isConstant(parser.getSymbol())) {
                        SymbolTable.addSymbol(parser.getSymbol(), parser.getLineNumber());
                    }
                }
                
                parser = new Parser(file);
                int a = UNALLOC_ADDRESS_START;
                while (parser.hasNext()) {
                    parser.next();
                    StringBuilder sb;
                    switch (parser.getCommand()) {
                        case A_COMMAND:
                            sb = new StringBuilder();

                            String symbol = parser.getSymbol();
                            int address;
                            if (isConstant(symbol)) {
                                address = Integer.parseInt(symbol);
                            } else {
                                if (SymbolTable.contains(symbol)) {
                                    address = SymbolTable.resolveSymbol(symbol);
                                } else {
                                    SymbolTable.addSymbol(symbol, a);
                                    address = a;
                                    a += 1;
                                }
                            }
                            String binaryAddress = toBinaryString(address);

                            int padding = WORD_WIDTH - binaryAddress.length();
                            for (int i = 0; i < padding; ++i) {
                                sb.append('0');
                            }
                            sb.append(binaryAddress);

                            writer.println(sb.toString());
                            break;
                        case C_COMMAND:
                            sb = new StringBuilder("111");

                            String comp = Code.compCode(parser.getComp());
                            if (null != comp) {
                                sb.append(comp);
                            }
                            String dest = Code.destCode(parser.getDest());
                            if (null != dest) {
                                sb.append(dest);
                            }
                            String jump = Code.jumpCode(parser.getJump());
                            if (null != jump) {
                                sb.append(jump);
                            }

                            writer.println(sb.toString());
                            break;
                    }
                }
            }
        }
    }

    // conversion algorithm
    static String toBinaryString(int number) {
        StringBuilder sb = new StringBuilder();
        while (number != 0) {
            sb.append(number % 2);
            number /= 2;
        }
        return sb.reverse().toString();
    }

    static boolean isConstant(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}