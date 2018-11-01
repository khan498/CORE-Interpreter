import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseTree
{
    private List<TreeNode> parseTree;
    private int treePointer;
    private Map<String, Integer> symbolTable;

    // Constructs the parsetree with one node. Initializes the symbol table.
    public ParseTree()
    {
        treePointer = 0;
        parseTree = new ArrayList<>();
        parseTree.add(new TreeNode());

        symbolTable = new HashMap<>();
    }

    // Retrieves the alt of the current node
    public int getAlt()
    {
        return parseTree.get(treePointer).alt;
    }

    // Sets the alt of the current node
    public void setAlt(int alt)
    {
        parseTree.get(treePointer).alt = alt;
    }

    // Retrieves the value of the current ID node
    public int getValue()
    {
        if (((getNodeType() != TreeNode.NodeType.valueOf("ID") &&
                (getNodeType() != TreeNode.NodeType.valueOf("INT")))))
            throw new AssertionError("Tree Pointer must be an ID node");

        if (getNodeType() == TreeNode.NodeType.valueOf("INT"))
            return parseTree.get(treePointer).value;
        else
            return symbolTable.get(getId());
    }

    // Sets the value of the current ID node
    public void setValue(int value)
    {
        if (((getNodeType() != TreeNode.NodeType.valueOf("ID") &&
                (getNodeType() != TreeNode.NodeType.valueOf("INT")))))
            throw new AssertionError("Tree Pointer must be an ID node");

        if (getNodeType() == TreeNode.NodeType.valueOf("INT"))
            parseTree.get(treePointer).value = value;
        else
            symbolTable.put(getId(), value);
    }

    // Retrieves the node type of the current node
    public TreeNode.NodeType getNodeType()
    {
        return parseTree.get(treePointer).type;
    }

    // Sets the node type of the current node
    public void setNodeType(TreeNode.NodeType nodeType)
    {
        parseTree.get(treePointer).type = nodeType;
    }

    // Retrieves the name/id of the current ID node
    public String getId()
    {
        if (getNodeType() != TreeNode.NodeType.valueOf("ID"))
            throw new AssertionError("Tree Pointer must be an ID node");

        return parseTree.get(treePointer).id;
    }

    // Sets the name/id of the current ID node
    public void setId(String idString)
    {
        parseTree.get(treePointer).id = idString;
        if (!symbolTable.containsKey(idString)) {
            symbolTable.put(idString, Integer.MIN_VALUE);
        }
    }

    // Adds child node to the current node of the parse tree
    public void addChild()
    {
        TreeNode node = new TreeNode();
        node.parent = treePointer;
        parseTree.get(treePointer).children.add(parseTree.size());

        parseTree.add(node);
    }

    // Moves the pointer/cursor to a child node
    public void moveToChild(int childIndex)
    {
        treePointer = parseTree.get(treePointer).children.get(childIndex);
    }

    // Moves the pointer/cursor to the parent node
    public void moveToParent()
    {
        treePointer = parseTree.get(treePointer).parent;
    }

    // Verifies if an ID is declared or not
    public boolean isDeclared(String symbolTableKey)
    {
        return symbolTable.containsKey(symbolTableKey);
    }
}
