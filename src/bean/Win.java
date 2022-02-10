package bean;

import java.io.*;

public class Win {
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = BOARD_SIZE + 2;
    private TreeNode[] nodes;
    private TreeNode currentNode;

    public Win() {
        String path = "src/resources/1.txt";
        File file = new File(path);
        try {
            var fileInputStream = new FileInputStream(file);
            FileReader fileReader = new FileReader(file);
            var bufferedReader = new BufferedReader(fileReader);
            try {
                String s;
                int lineNum = 0;
                nodes = new TreeNode[1000];
                while ((s = bufferedReader.readLine()) != null) {
                    var strs1 = s.split(" ");
                    if (strs1[0].equals("-1")) {
                        nodes[lineNum] = new TreeNode(null, 0, -1, -1);
                    } else {
                        var posstr = strs1[1];
                        int x = (posstr.charAt(0) - 'A' + 1);
                        int y = Integer.parseInt(posstr.substring(1));
                        var father = nodes[Integer.parseInt(strs1[0])];
                        nodes[lineNum] = new TreeNode(father, 1 - father.color, x, y);
                        father.children.add(nodes[lineNum]);
                    }


                    lineNum++;
                }
                for (int i = 1; i < lineNum; i++) {
                    System.out.print(nodes[i].father.color + "#");
                    System.out.print(nodes[i].x + "," + nodes[i].y + "#");
                    System.out.println(nodes[i].color);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                int ch = fileReader.read();
                System.out.println(ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        currentNode = nodes[0];
    }

    public Chess AIGo() {
        var child = currentNode.children;

        if (child.size() != 0) {
            currentNode = child.stream().findFirst().get();
            return new Chess(currentNode.x, currentNode.y);
        } else {
            return ScanBoard(GobangPanel.boardData);
        }

    }

    public void PlayerGo(int x, int y) {
        var child = currentNode.children;
        if (child.size() != 0) {
            for (int i = 0; i < child.size(); i++) {
                if (child.get(i).x == x && child.get(i).y == y) {

                    currentNode = child.get(i);

                }
                //如果找不到 当前节点不更新
            }
        }
    }

    public void goback() {
        currentNode = currentNode.father;
    }

    public Chess ScanBoard(int[][] data) {

        for (int i = 1; i < BT - 1; i++) {
            for (int j = 1; j < BT - 1; j++) {//对棋盘的所有点循环
                if (data[i][j] == 0) {  //如果是空位

                    for (int m = 1; m <= 4; m++) {
                        int chessCount = 1;  // 和当前位置里连续同色的棋子数 ###
                        int spaceCount1 = 0;
                        int spaceCount2 = 0;
                        data[i][j] = 1;//如果下黑子
                        switch (m) {
                            case 1: //水平方向
                                //向右查找相同颜色连续的棋子
                                for (int k = i + 1; k < BT - 1; k++) {
                                    if (data[k][j] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][j] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }

                                //向相反方向查找相同颜色连续的棋子
                                for (int k = i - 1; k >= 1; k--) {
                                    if (data[k][j] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][j] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 2:  //  垂直方向
                                //向增加的方向查找相同颜色连续的棋子
                                for (int k = j + 1; k < BT - 1; k++) {
                                    if (data[i][k] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[i][k] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }
                                //向相反方向查找相同颜色连续的棋子
                                for (int k = j - 1; k >= 1; k--) {
                                    if (data[i][k] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[i][k] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 3:  //  左上到右下
                                //向增加的方向查找相同颜色连续的棋子
                                for (int k = i + 1, n = j + 1; (k < BT - 1) && (n < BT - 1); k++, n++) {
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }

                                //向相反方向查找相同颜色连续的棋子
                                for (int k = i - 1, n = j - 1; (k >= 1) && (m >= 1); k--, n--) {
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 4:  //  右上到左下
                                for (int k = i + 1, n = j - 1; k < BT - 1 && n >= 1; k++, n--) {  //查找连续的同色棋子
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }
                                for (int k = i - 1, n = j + 1; k >= 1 && n < BT - 1; k--, n++) {  //查找连续的同色棋子
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                        }
                        data[i][j] = 0;
                        System.out.println(chessCount);
                        switch (chessCount) {
                            case 5:  //如果已经可以连成5子，则赢棋  11111
                                System.out.println("hahah");
                                return new Chess(i, 16-j);

                            case 4://连续四子  1111
                                if ((spaceCount1 > 0) && (spaceCount2 > 0)) { //活四
                                    return new Chess(i, 16-j);
                                }
                            default:
                                System.out.println("无冲四活三");


                        }
                    }
                }
            }
        }

        return null;
    }


}
