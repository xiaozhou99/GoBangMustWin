package bean;

import java.util.ArrayList;

public class TreeNode {
    TreeNode father;
    ArrayList<TreeNode> children;
    int color;
    int x;
    int y;

    public TreeNode(TreeNode father, int color, int x, int y) {
        this.father = father;
        this.color = color;
        this.x = x;
        this.y = y;
        this.children=new ArrayList<TreeNode>();
    }


}
