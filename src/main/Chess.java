package main;
public class Chess implements Comparable<Chess> {
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int BORDER = -1;
	public static final int EMPTY = 0;
	public int x;
	public int y;
	public int color;//����


	public Chess(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public Chess(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}



	//���
	public void reset() {
		color = EMPTY;
	}


	// ��ӡchess����
	@Override
	public String toString() {
		return "Chess{" +
				"x=" + x +
				", y=" + y +
				", color=" + color +
				'}';
	}

	public boolean isEmpty() {
		return color == EMPTY ? true : false;
	}


	@Override
	public int compareTo(Chess o) {
		return 0;
	}
}
