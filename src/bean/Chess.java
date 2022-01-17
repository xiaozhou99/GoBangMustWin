package bean;


public class Chess implements Comparable<Chess> {

	protected int x;
	protected int y;
	protected int color;//落子


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


	// 打印chess对象


	@Override
	public String toString() {
		return "Chess{" +
				"x=" + x +
				", y=" + y +
				", color=" + color +
				'}';
	}


	@Override
	public int compareTo(Chess o) {
		return 0;
	}
}
