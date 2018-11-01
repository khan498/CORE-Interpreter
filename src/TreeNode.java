import java.util.ArrayList;
import java.util.List;

public class TreeNode
{
    String id;
    int alt;
    NodeType type;
    int parent;
    List<Integer> children;
    int value;

    public TreeNode()
    {
        this.alt = 1;
        this.children = new ArrayList<>();
    }

    public enum NodeType
    {
        PROG, DECLSEQ, STMTSEQ,  DECL,  IDLIST,  STMT,  ASSIGN,  IF,  LOOP,  IN,  OUT,  COND,  COMP,  EXP,  TERM,  FAC,  COMPOP,  ID,  INT;
    }
}