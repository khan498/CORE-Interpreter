public class Program {

    // Parses the input file (using the parese tree implementation) and prints the program
    public static void main(String[] args) {

        if (args.length != 1)
        {
            System.err.println("Invalid arguments");
            System.exit(1);
        }

        ParseTree parseTree;
        try
        {
            parseTree = Parser.parse(args[0]);
            Parser.print(parseTree);
        }
        catch (InterpreterException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
