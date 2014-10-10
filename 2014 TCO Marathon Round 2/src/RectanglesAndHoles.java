import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;

class RectanglesAndHoles {
	int A[], B[];
	int N;

	class rectangle {
		final int id, A, B;
		int X = 0, Y = 0, K = 0;
		ArrayList<rectangle> connect = new ArrayList<>();

		rectangle(int id, int A, int B) {
			this.id = id;
			this.A = A;
			this.B = B;
		}

		int getX(int K) {
			return K == 0 ? A : B;
		}

		int getX() {
			return getX(K);
		}

		int getY(int K) {
			return K == 0 ? B : A;
		}

		int getY() {
			return getY(K);
		}

		void swapK() {
			K = (K + 1) & 1;
		}

		@Override
		public String toString() {
			return String.format("id:%4d A:%4d B:%4d X:%6d Y:%6d K:%d\n", id, A, B, X, Y, K);
		}
	}


	rectangle rect[];

	void bigRect(rectangle rect[]) {

		Arrays.sort(rect, new Comparator<rectangle>() {
			@Override
			public int compare(rectangle o1, rectangle o2) {
				return calc(o1) - calc(o2);
			}

			int calc(rectangle r) {
				return Math.min(r.A, r.B);
			}
		});
		rectangle top = rect[0];
		if (top.getX() < top.getY())
			top.swapK();
		rectangle bot = rect[1];
		if (bot.getX() < bot.getY())
			bot.swapK();

		ArrayList<rectangle> left = new ArrayList<>();
		ArrayList<rectangle> right = new ArrayList<>();
		int leftSum = 0, rightSum = 0;
		for (int i = 2; i < rect.length; i++) {
			if (leftSum < rightSum) {
				left.add(rect[i]);
				leftSum += rect[i].getY();
			} else {
				right.add(rect[i]);
				rightSum += rect[i].getY();
			}
		}

		while (true) {
			boolean isLeft = false;
			rectangle swap = null;
			int diff = rightSum - leftSum, tmp = Math.abs(diff);
			for (rectangle r : left) {
				if (tmp > Math.abs(diff - r.getX() + r.getY())) {
					isLeft = true;
					swap = r;
					tmp = Math.abs(diff - r.getX() + r.getY());
				}
			}
			for (rectangle r : right) {
				if (tmp > Math.abs(diff + r.getX() - r.getY())) {
					isLeft = false;
					swap = r;
					tmp = Math.abs(diff + r.getX() - r.getY());
				}
			}
			if (swap == null)
				break;
			if (isLeft)
				leftSum += swap.getX() - swap.getY();
			else
				rightSum += swap.getX() - swap.getY();
			swap.swapK();
		}
		{
			bot.X = bot.Y = top.X = 0;
			top.Y = Math.min(leftSum, rightSum) + bot.getY();
		}

		class setY {
			ArrayList<rectangle> run(ArrayList<rectangle> list) {
				Collections.sort(list, new Comparator<rectangle>() {
					@Override
					public int compare(rectangle o1, rectangle o2) {
						return calc(o1) - calc(o2);
					}

					int calc(rectangle r) {
						return r.getX() - r.getY();
					}
				});
				Deque<rectangle> deq = new ArrayDeque<>();
				for (int i = 0; i < list.size(); i++) {
					if (i % 2 == 0)
						deq.addFirst(list.get(i));
					else
						deq.addLast(list.get(i));
				}
				int y = bot.getY();
				for (rectangle r : deq) {
					r.Y = y;
					y += r.getY();
				}
				return new ArrayList<>(deq);
			}
		}
		right = new setY().run(right);
		int ti = right.size() - 1, bi = 0;
		while (ti>=bi) {
			rectangle b = bi == 0 ? bot : right.get(bi - 1);
			rectangle t = ti == right.size() - 1 ? top : right.get(ti + 1);
			int botMove = b.getX() - (right.get(bi).X - b.X);
			int topMove = t.getX() - (right.get(ti).X - t.X);
			int move = Math.min(botMove, topMove);
			for (int i = bi; i <= ti; i++) {
				right.get(i).X += move;
			}
			if (move == topMove)
				ti--;
			if (move == botMove)
				bi++;
		}

		left = new setY().run(left);
		ti = left.size() - 1;
		bi = 0;
		while (ti >= bi) {
			rectangle b = bi == 0 ? bot : left.get(bi - 1);
			rectangle t = ti == left.size() - 1 ? top : left.get(ti + 1);
			int botMove = left.get(bi).getX() - (b.X - left.get(bi).X);
			int topMove = left.get(ti).getX() - (t.X - left.get(ti).X);
			int move = Math.min(botMove, topMove);
			for (int i = bi; i <= ti; i++) {
				left.get(i).X -= move;
			}
			if (move == topMove)
				ti--;
			if (move == botMove)
				bi++;
		}

		for (int i = 0; i < rect.length; i++) {
			for (int j = i + 1; j < rect.length; j++) {
				if (isConnect(rect[i], rect[j])) {
					rect[i].connect.add(rect[j]);
					rect[j].connect.add(rect[i]);
					connect[rect[i].id][rect[j].id] = connect[rect[j].id][rect[i].id] = true;
				}
			}
		}
	}

	private static final boolean isRange(int a, int b, int c, int d) {
		return (a <= c && c <= b) || (a <= d && d <= b);
	}

	boolean isConnect(rectangle r1, rectangle r2) {
		return (isRange(r1.X, r1.X + r1.getX(), r2.X, r2.X + r2.getX()))
				&& (isRange(r1.Y, r1.Y + r1.getY(), r2.Y, r2.Y + r2.getY()));
	}

	void countRect() {
	}

	boolean connect[][];
	void init(){
		N = A.length;
		rect = new rectangle[N];
		connect = new boolean[N][N];
		for (int i = 0; i < N; i++) {
			rect[i] = new rectangle(i, A[i], B[i]);
		}
	}

	public int[] place(int A[], int B[]) {
		this.A = A;
		this.B = B;
		init();

		Arrays.sort(rect, new Comparator<rectangle>() {
			@Override
			public int compare(rectangle o1, rectangle o2) {
				return calc(o2) - calc(o1);
			}

			int calc(rectangle r) {
				return r.A * r.A + r.B * r.B;
			}
		});
		rectangle big[] = new rectangle[(N + 1) / 2];
		rectangle small[] = new rectangle[N / 2];
		{
			int bi = 0, si = 0;
			for (int i = 0; i < N; i++) {
				if (i < big.length) {
					big[bi++] = rect[i];
				} else {
					small[si++] = rect[i];
				}
			}
		}
		bigRect(big);

		int x = 30000, y = 0;
		for (rectangle r : small) {
			r.X = x;
			r.Y = y;
			x += r.getX();
			y += r.getY();
		}

		return getAns(rect);
	}

	int[] getAns(rectangle rect[]) {
		int ans[] = new int[N * 3];
		for (rectangle r : rect) {
			int i = r.id * 3;
			ans[i] = r.X;
			ans[i + 1] = r.Y;
			ans[i + 2] = r.K;
		}
		return ans;
	}
}