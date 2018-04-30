import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

class Main {

    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            final File node = new File(arg);
                
            File[] translationUnits = {};
            if (node.isDirectory()) {
                translationUnits = Stream.of(node.list())
                    .map(s -> {
                        try {
                            return node.getCanonicalPath() + File.separator + s;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(File::new)
                    .filter(file -> {
                        String extension = getFileExtension(file);
                        return (".vm").equals(extension) ;
                    }).toArray(File[]::new);
            } else {
                if (getFileExtension(node).equals(".vm")) {
                    translationUnits = new File[] { node }; 
                } else {
                    throw new IllegalArgumentException("Not a VM file.");
                }
            }

            String basePath = node.getCanonicalPath().substring(0,
                    node.getCanonicalPath().lastIndexOf(File.separator));
            File outFile = new File(basePath + File.separator + node.getName() + ".asm");
            System.out.println("Creating out file: " + outFile.getCanonicalPath());

            try (Generator generator = new Generator(outFile)) {
                for (File unit : translationUnits) {
                    generator.setFileName(getFileNameNoExtension(unit));
                    try (Parser parser = new Parser(unit)) {
                        while (parser.hasNext()) {
                            parser.next();
                            switch (parser.getCommand()) {
                                case ARITHMETIC:
                                    generator.writeArithmetic(parser.getArg1());
                                    break;
                                case PUSH:
                                    generator.writePushPop("push", parser.getArg1(), parser.getArg2());
                                    break;
                                case POP:
                                    generator.writePushPop("pop", parser.getArg1(), parser.getArg2());
                                    break;
                                case LABEL:
                                    generator.writeLabel(parser.getArg1());
                                    break;
                                case GOTO:
                                    generator.writeGoto(parser.getArg1());
                                    break; 
                                case IF:
                                    generator.writeIf(parser.getArg1());
                                    break;                                    
                                case WHITESPACE:
                                    // ignore
                                    break;
                                default:
                                    throw new UnsupportedOperationException(
                                        String.format("Command \"%s\" is not implemented.",
                                        parser.getCommand()));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }

    static String getFileExtension(File file) {
        if (file.getName().lastIndexOf('.') != -1) {
            return file.getName().substring(file.getName().lastIndexOf('.'));
        } else {
            return "";
        }
    }

    static String getFileNameNoExtension(File file) {
        if (file.getName().lastIndexOf('.') != -1) {
            return file.getName().substring(0, file.getName().lastIndexOf('.'));
        } else {
            return file.getName();
        }
    }
}