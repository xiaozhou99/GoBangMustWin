package bean;

// 棋子类：落子棋子以Chess形式存储，主要包含x，y，color三个属性，即坐标和棋子颜色
public class Chess{
	public static final int BLACK = 1; // 三个表示棋子颜色的常量，1表示黑子，2表示白子，0表示该处为空
	public static final int WHITE = 2;
	public static final int EMPTY = 0;
	protected int x; //x坐标，这里用int型存储，便于记录及界面画棋子，棋盘界面上显示的是A-O英文字母，
					//x+64即得到对应字母的ASCⅡ码，可转换成对应字母显示
	protected int y; //对应棋盘上的纵坐标
	protected int color; //棋子颜色

	// 构造函数
	public Chess(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	// 只有坐标两个参数的构造函数
	public Chess(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// 获取x坐标
	public int getX() {
		return x;
	}

	// 设置x坐标
	public void setX(int x) {
		this.x = x;
	}

	// 获取y坐标
	public int getY() {
		return y;
	}

	// 设置y坐标
	public void setY(int y) {
		this.y = y;
	}

	// 获取棋子颜色
	public int getColor() {
		return color;
	}

	// 设置棋子颜色
	public void setColor(int color) {
		this.color = color;
	}

	// 清空：设置棋子颜色为空，即该处无棋
	public void reset() {
		color = EMPTY;
	}

	// 判断该处是否为空，是则返回true，反之返回false
	public boolean isEmpty() {
		return color == EMPTY ? true : false;
	}

	// 打印chess对象
	public String toString() {
		return "Chess{" +
				"x=" + x +
				", y=" + y +
				", color=" + color +
				'}';
	}
}
