Tanzim Khan
Tokenizer Programming Assignment

README: Source files

    Program.java - Contains the main class that passes the input file path t the parser and prints the parse tree

    Tokenizer.java - Creates valid tokens from the given input file and has methods for returning
                     numeric value of these tokens.
                     ** These are the changes made to the tokenizer:
                     **     1. The EOF token value changed to 33 from 32
                     **     2. Tokenizer now throws InvalidTokenException

    Parser.java - Parses the passed in file, using the tokenizer class and parse tree implementation.
                  Contains method for "pretty printing" the program which prints using the Printer class.

    Printer.java - Prints the formatted program from a passed in Parse Tree.

    ParseTree.java - The Parse Tree object class. Contains methods for navigating through the tree

    TreeNode.java - Node object for the parse tree.

    Exceptions - 5 custom exceptions. All the exceptions inherit from the InterpreterException class. (More details on the PA2 Documentation file)


Instructions:
    1. Use the make file to compile the program (Simply type in the command make)
    2. Use the following command to run the program:
            java Program [file name arg]

    * Note: Due to a fair number of files, the compiled program is going to look messy. All the source files are under the src folder.