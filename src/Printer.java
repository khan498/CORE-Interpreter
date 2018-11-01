
// Created to be used as a static class
public class Printer {

    private static int indentLevel = 0;     // Global variable that gets modified based on "code-block"

    // Prints indentations based on the indent level
    private static void indent()
    {
        for (int i = 0; i < indentLevel * 2; i++) {
            System.out.print(' ');
        }
    }

    // Prints the program to console using the parse tree
    public static void printProgram(ParseTree parseTree)
    {
        printProg(parseTree);
    }

    /*
     *  The following methods prints each of the specific nodes.
     *  The method names specify which node it's printing. The printing implementation is based on the CORE grammar
     */
    public static void printProg(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.PROG))
            throw new AssertionError("Node must be an PROG");

        System.out.print("program \n");
        indentLevel += 1;
        parseTree.moveToChild(0);
        printDeclSeq(parseTree);
        parseTree.moveToParent();

        indent();
        System.out.print("begin\n");
        indentLevel += 1;

        parseTree.moveToChild(1);
        printStmtSeq(parseTree);
        parseTree.moveToParent();

        indentLevel -= 1;
        indent();
        System.out.print("end\n");
    }

    private static void printDeclSeq(ParseTree parseTree)
    {
        assert (parseTree.getNodeType() == TreeNode.NodeType.DECLSEQ) : "Node must be an DECLSEQ";
        parseTree.moveToChild(0);
        printDecl(parseTree);
        parseTree.moveToParent();

        if (parseTree.getAlt() == 2)
        {
            parseTree.moveToChild(1);
            printDeclSeq(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void printStmtSeq(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.STMTSEQ))
            throw new AssertionError("Node must be an STMTSEQ");

        parseTree.moveToChild(0);
        printStmt(parseTree);
        parseTree.moveToParent();

        if (parseTree.getAlt() == 2)
        {
            parseTree.moveToChild(1);
            printStmtSeq(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void printDecl(ParseTree parseTree)
    {
        indent();
        System.out.print("int ");
        parseTree.moveToChild(0);
        printIdList(parseTree);
        parseTree.moveToParent();
        System.out.print(";\n");
    }

    private static void printIdList(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.IDLIST))
            throw new AssertionError("Node must be an IDLIST");

        parseTree.moveToChild(0);
        printId(parseTree);
        parseTree.moveToParent();

        if (parseTree.getAlt() == 2)
        {
            System.out.print(", ");
            parseTree.moveToChild(1);
            printIdList(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void printStmt(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.STMT))
            throw new AssertionError("Node must be a STMT");

        indent();
        int alt = parseTree.getAlt();
        parseTree.moveToChild(0);

        switch (alt) {
            case 1:
                printAssign(parseTree);
                break;

            case 2:
                printIf(parseTree);
                break;

            case 3:
                printLoop(parseTree);
                break;

            case 4:
                printIn(parseTree);
                break;

            case 5:
                printOut(parseTree);
                break;
        }
        parseTree.moveToParent();
    }

    private static void printAssign(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.ASSIGN))
            throw new AssertionError("Node must be an ASSIGN");

        parseTree.moveToChild(0);
        printId(parseTree);
        parseTree.moveToParent();

        System.out.print(" = ");

        parseTree.moveToChild(1);
        printExp(parseTree);
        parseTree.moveToParent();
        System.out.print(";\n");
    }

    private static void printIf(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.IF))
            throw new AssertionError("Node must be an IF");

        System.out.print("if ");
        parseTree.moveToChild(0);
        printCond(parseTree);
        parseTree.moveToParent();

        System.out.print(" then\n");
        indentLevel += 1;
        parseTree.moveToChild(1);
        printStmtSeq(parseTree);
        parseTree.moveToParent();
        indentLevel -= 1;

        if (parseTree.getAlt() == 2)
        {
            indent();
            System.out.print("else\n");
            indentLevel += 1;
            parseTree.moveToChild(2);
            printStmtSeq(parseTree);
            parseTree.moveToParent();
            indentLevel -= 1;
        }
        indent();
        System.out.print("end;\n");
    }

    private static void printLoop(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.LOOP))
            throw new AssertionError("Node must be a LOOP");

        System.out.print("while ");
        parseTree.moveToChild(0);
        printCond(parseTree);
        parseTree.moveToParent();

        System.out.print(" loop\n");
        indentLevel += 1;
        parseTree.moveToChild(1);
        printStmtSeq(parseTree);
        parseTree.moveToParent();

        indentLevel -= 1;
        indent();
        System.out.print("end;\n");
    }

    private static void printIn(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.IN))
            throw new AssertionError("Node must be an IN");

        System.out.print("read ");
        parseTree.moveToChild(0);
        printIdList(parseTree);
        parseTree.moveToParent();
        System.out.print(";\n");
    }

    private static void printOut(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.OUT))
            throw new AssertionError("Node must be an OUT");

        System.out.print("write ");
        parseTree.moveToChild(0);
        printIdList(parseTree);
        parseTree.moveToParent();
        System.out.print(";\n");
    }

    private static void printCond(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.COND))
            throw new AssertionError("Node must be an COND");
        int alt = parseTree.getAlt();

        switch (alt) {
            case 1:
                parseTree.moveToChild(0);
                printComp(parseTree);
                parseTree.moveToParent();
                break;

            case 2:
                System.out.print("!");
                parseTree.moveToChild(0);
                printCond(parseTree);
                parseTree.moveToParent();
                break;

            default:
                System.out.print("[ ");
                parseTree.moveToChild(0);
                printCond(parseTree);
                parseTree.moveToParent();
                if (alt == 3)
                    System.out.print(" and ");
                else
                    System.out.print(" or ");

                parseTree.moveToChild(1);
                printCond(parseTree);
                parseTree.moveToParent();
                System.out.print(" ]");
                break;
        }
    }

    private static void printComp(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.COMP))
            throw new AssertionError("Node must be a COMP");

        System.out.print("( ");

        parseTree.moveToChild(0);
        printFac(parseTree);
        parseTree.moveToParent();

        parseTree.moveToChild(1);
        printCompOp(parseTree);
        parseTree.moveToParent();

        parseTree.moveToChild(2);
        printFac(parseTree);
        parseTree.moveToParent();

        System.out.print(" )");
    }

    private static void printExp(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.EXP))
            throw new AssertionError("Node must be an EXP");

        parseTree.moveToChild(0);
        printTerm(parseTree);
        parseTree.moveToParent();

        int alt = parseTree.getAlt();
        if (alt > 1)
        {
            if (alt == 2)
                System.out.print(" + ");
            else
                System.out.print(" - ");

            parseTree.moveToChild(1);
            printExp(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void printTerm(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.TERM))
            throw new AssertionError("Node must be an TERM");

        parseTree.moveToChild(0);
        printFac(parseTree);
        parseTree.moveToParent();

        if (parseTree.getAlt() == 2)
        {
            System.out.print(" * ");
            parseTree.moveToChild(1);
            printTerm(parseTree);
            parseTree.moveToParent();
        }
    }

    private static void printFac(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.FAC))
            throw new AssertionError("Node must be a FAC");

        int alt = parseTree.getAlt();
        parseTree.moveToChild(0);

        switch (alt) {
            case 1:
                printInt(parseTree);
                break;

            case 2:
                printId(parseTree);
                break;

            default:
                System.out.print("( ");
                printExp(parseTree);
                System.out.print(" )");
                break;
        }
        parseTree.moveToParent();
    }

    private static void printCompOp(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.COMPOP))
            throw new AssertionError("Node must be a COMPOP");

        String[] compopArray = { "", " != ", " == ", " < ", " > ", " <= ", " >= " };
        System.out.print(compopArray[parseTree.getAlt()]);
    }

    private static void printId(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.ID))
            throw new AssertionError("Node must be an ID");

        System.out.print(parseTree.getId());
    }

    private static void printInt(ParseTree parseTree)
    {
        if ((parseTree.getNodeType() != TreeNode.NodeType.INT))
            throw new AssertionError("Node must be an INT");

        System.out.print(parseTree.getValue());
    }

}
