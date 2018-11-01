
// Created to be used as a static class
public class Parser
{
    private static Tokenizer tokenizer;

    // Parses a file and returns a parse tree
    public static ParseTree parse(String filePath) throws InterpreterException
    {
        tokenizer = new Tokenizer();
        tokenizer.tokenize(filePath);

        ParseTree parseTree = new ParseTree();
        parseProg(parseTree);
        matchAndConsume("~EOF~");
        return parseTree;
    }

    // Prints the provided parse tree
    public static void print(ParseTree parseTree)
    {
        Printer.printProgram(parseTree);
    }

    // Verifies that the current token in the tokenizer is valid and moves to the next token
    private static void matchAndConsume(String tokenString) throws InterpreterException
    {
        String currentToken = tokenizer.currentToken();
        if (((tokenString.equals("INT")) && (tokenizer.getTokenValue(currentToken) != 31)) ||
                ((tokenString.equals("ID")) && (tokenizer.getTokenValue(currentToken) != 32)) ||
                ((tokenString.equals("~EOF~")) && (tokenizer.getTokenValue(currentToken) != 33)) || (
                (!tokenString.equals("INT")) && (!tokenString.equals("ID")) && (!tokenString.equals("~EOF~")) &&
                        (!tokenString.equals(currentToken))))
        {
            throw new UnexpectedTokenException("Syntax Error: [Line " + tokenizer.currentTokenLine() + "] Expected: " + tokenString + ", found: " + currentToken);
        }
        tokenizer.nextToken();
    }

    /*
     *  The following methods parses each of the specific nodes, i.e adds the current token to the parse tree.
     *  The method names specify which node it's parsing. The parsing implementation is done using the parse tree approach and the CORE grammar
     *  Appropriate exceptions are thrown based on the error.
     *
     *  NOTE: The ID declaration and assignments are stored on a symbol table in the ParseTree class.
     */

    private static void parseProg(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.PROG);
        matchAndConsume("program");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseDeclSeq(parseTree);
        parseTree.moveToParent();

        matchAndConsume("begin");
        parseTree.addChild();
        parseTree.moveToChild(1);
        parseStmtSeq(parseTree);
        parseTree.moveToParent();

        matchAndConsume("end");
    }

    private static void parseDeclSeq(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.DECLSEQ);

        parseTree.addChild();
        parseTree.moveToChild(0);
        parseDecl(parseTree);
        parseTree.moveToParent();

        if (tokenizer.currentToken().equals("int"))
        {
            parseTree.setAlt(2);
            parseTree.addChild();
            parseTree.moveToChild(1);
            parseDeclSeq(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void parseStmtSeq(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.STMTSEQ);

        parseTree.addChild();
        parseTree.moveToChild(0);
        parseStmt(parseTree);
        parseTree.moveToParent();

        String currentToken = tokenizer.currentToken();
        if ((currentToken.equals("end")) || (currentToken.equals("else")))
        {
            parseTree.setAlt(1);
        }
        else
        {
            parseTree.setAlt(2);
            parseTree.addChild();
            parseTree.moveToChild(1);
            parseStmtSeq(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void parseDecl(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.DECL);
        matchAndConsume("int");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseIdList(parseTree, true);
        parseTree.moveToParent();

        matchAndConsume(";");
    }

    private static void parseIdList(ParseTree parseTree, boolean notAssigned) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.IDLIST);
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseId(parseTree, notAssigned);
        parseTree.moveToParent();

        String currentToken = tokenizer.currentToken();
        if (currentToken.equals(","))
        {
            matchAndConsume(",");

            parseTree.setAlt(2);
            parseTree.addChild();
            parseTree.moveToChild(1);
            parseIdList(parseTree, notAssigned);
            parseTree.moveToParent();
        }
        else
        {
            parseTree.setAlt(1);
        }
    }

    private static void parseStmt(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.STMT);
        String currentToken = tokenizer.currentToken();

        parseTree.addChild();
        parseTree.moveToChild(0);
        if (tokenizer.getTokenValue(currentToken) == 32)
        {
            parseAssign(parseTree);
            parseTree.moveToParent();
            parseTree.setAlt(1);
        }
        else if (currentToken.equals("if"))
        {
            parseIf(parseTree);
            parseTree.moveToParent();
            parseTree.setAlt(2);
        }
        else if (currentToken.equals("while"))
        {
            parseLoop(parseTree);
            parseTree.moveToParent();
            parseTree.setAlt(3);
        }
        else if (currentToken.equals("read"))
        {
            parseIn(parseTree);
            parseTree.moveToParent();
            parseTree.setAlt(4);
        }
        else if (currentToken.equals("write"))
        {
            parseOut(parseTree);
            parseTree.moveToParent();
            parseTree.setAlt(5);
        }
        else
        {
            throw new UnexpectedTokenException("[Line " + tokenizer.currentTokenLine() + "] Unexpected token " + currentToken + " while parsing <stmt>");
        }
    }

    private static void parseAssign(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.ASSIGN);

        parseTree.addChild();
        parseTree.moveToChild(0);
        parseId(parseTree, false);
        parseTree.moveToParent();

        matchAndConsume("=");

        parseTree.addChild();
        parseTree.moveToChild(1);
        parseExp(parseTree);
        parseTree.moveToParent();

        matchAndConsume(";");
    }

    private static void parseIf(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.IF);
        parseTree.setAlt(1);

        matchAndConsume("if");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseCond(parseTree);
        parseTree.moveToParent();

        matchAndConsume("then");
        parseTree.addChild();
        parseTree.moveToChild(1);
        parseStmtSeq(parseTree);
        parseTree.moveToParent();

        if (tokenizer.currentToken().equals("else"))
        {
            matchAndConsume("else");
            parseTree.addChild();
            parseTree.moveToChild(2);
            parseStmtSeq(parseTree);
            parseTree.moveToParent();
            parseTree.setAlt(2);
        }

        matchAndConsume("end");
        matchAndConsume(";");
    }

    private static void parseLoop(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.LOOP);
        matchAndConsume("while");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseCond(parseTree);
        parseTree.moveToParent();

        matchAndConsume("loop");
        parseTree.addChild();
        parseTree.moveToChild(1);
        parseStmtSeq(parseTree);
        parseTree.moveToParent();

        matchAndConsume("end");
        matchAndConsume(";");
    }

    private static void parseIn(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.IN);
        matchAndConsume("read");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseIdList(parseTree, false);
        parseTree.moveToParent();

        matchAndConsume(";");
    }

    private static void parseOut(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.OUT);
        matchAndConsume("write");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseIdList(parseTree, false);
        parseTree.moveToParent();

        matchAndConsume(";");
    }

    private static void parseCond(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.COND);

        String currentToken = tokenizer.currentToken();
        switch (currentToken) {
            case "!":
                matchAndConsume("!");
                parseTree.setAlt(2);
                parseTree.addChild();
                parseTree.moveToChild(0);
                parseCond(parseTree);
                parseTree.moveToParent();
                break;

            case "[":
                matchAndConsume("[");

                parseTree.addChild();
                parseTree.moveToChild(0);
                parseCond(parseTree);
                parseTree.moveToParent();

                currentToken = tokenizer.currentToken();
                if (currentToken.equals("and")) {
                    parseTree.setAlt(3);
                    matchAndConsume("and");
                } else if (currentToken.equals("or")) {
                    parseTree.setAlt(4);
                    matchAndConsume("or");
                }

                parseTree.addChild();
                parseTree.moveToChild(1);
                parseCond(parseTree);
                parseTree.moveToParent();

                matchAndConsume("]");
                break;

            default:
                parseTree.setAlt(1);
                parseTree.addChild();
                parseTree.moveToChild(0);
                parseComp(parseTree);
                parseTree.moveToParent();
                break;
        }
    }

    private static void parseComp(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.COMP);
        matchAndConsume("(");
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseFac(parseTree);
        parseTree.moveToParent();

        parseTree.addChild();
        parseTree.moveToChild(1);
        parseCompOp(parseTree);
        parseTree.moveToParent();

        parseTree.addChild();
        parseTree.moveToChild(2);
        parseFac(parseTree);
        parseTree.moveToParent();

        matchAndConsume(")");
    }

    private static void parseExp(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.EXP);
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseTerm(parseTree);
        parseTree.moveToParent();

        String currentToken = tokenizer.currentToken();
        switch (currentToken) {
            case "+":
                matchAndConsume("+");
                parseTree.setAlt(2);
                parseTree.addChild();
                parseTree.moveToChild(1);
                parseExp(parseTree);
                parseTree.moveToParent();
                break;

            case "-":
                matchAndConsume("-");
                parseTree.setAlt(3);
                parseTree.addChild();
                parseTree.moveToChild(1);
                parseExp(parseTree);
                parseTree.moveToParent();
                break;

            default:
                parseTree.setAlt(1);
                break;
        }
    }

    private static void parseTerm(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.TERM);
        parseTree.addChild();
        parseTree.moveToChild(0);
        parseFac(parseTree);
        parseTree.moveToParent();

        if (tokenizer.currentToken().equals("*"))
        {
            matchAndConsume("*");
            parseTree.setAlt(2);
            parseTree.addChild();
            parseTree.moveToChild(1);
            parseTerm(parseTree);
            parseTree.moveToParent();
        }
        else
            parseTree.setAlt(1);
    }

    private static void parseFac(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.FAC);

        String currentToken = tokenizer.currentToken();
        if (tokenizer.getTokenValue(currentToken) == 31)
        {
            parseTree.setAlt(1);
            parseTree.addChild();
            parseTree.moveToChild(0);
            parseInt(parseTree);
            parseTree.moveToParent();
        }
        else if (tokenizer.getTokenValue(currentToken) == 32)
        {
            parseTree.setAlt(2);
            parseTree.addChild();
            parseTree.moveToChild(0);
            parseId(parseTree, false);
            parseTree.moveToParent();
        }
        else if (currentToken.equals("("))
        {
            matchAndConsume("(");
            parseTree.setAlt(3);
            parseTree.addChild();
            parseTree.moveToChild(0);
            parseExp(parseTree);
            parseTree.moveToParent();
            matchAndConsume(")");
        }
        else
            throw new UnexpectedTokenException("[Line " + tokenizer.currentTokenLine() + "] Unexpected token " + currentToken + " while parsing <fac>.");
    }

    private static void parseCompOp(ParseTree parseTree)
    {
        parseTree.setNodeType(TreeNode.NodeType.COMPOP);

        String currentToken = tokenizer.currentToken();
        switch (currentToken) {
            case "!=":
                parseTree.setAlt(1);
                break;
            case "==":
                parseTree.setAlt(2);
                break;
            case "<":
                parseTree.setAlt(3);
                break;
            case ">":
                parseTree.setAlt(4);
                break;
            case "<=":
                parseTree.setAlt(5);
                break;
            case ">=":
                parseTree.setAlt(6);
                break;
        }
        tokenizer.nextToken();
    }

    private static void parseId(ParseTree parseTree, boolean declared) throws InterpreterException
    {
        parseTree.setNodeType(TreeNode.NodeType.ID);
        String currentToken = tokenizer.currentToken();
        tokenizer.nextToken();
        if ((declared) && (parseTree.isDeclared(currentToken)))
            throw new IdRedeclaredException("[Line " + tokenizer.currentTokenLine() + "] " + currentToken + " already declared");

        if ((!declared) && (!parseTree.isDeclared(currentToken)))
            throw new IdUndeclaredException("[Line " + tokenizer.currentTokenLine() + "] Using undeclared variable " + currentToken);

        parseTree.setId(currentToken);
    }

    private static void parseInt(ParseTree parseTree) throws InterpreterException
    {
        parseTree.setAlt(1);
        parseTree.setNodeType(TreeNode.NodeType.INT);
        int value = Integer.parseInt(tokenizer.currentToken());
        parseTree.setValue(value);
        matchAndConsume("INT");
    }
}
