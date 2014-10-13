import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

class RectanglesAndHoles {
	int A[], B[];
	int N;
	static final int MAX_SIZE = 1000;

	class rectangle {
		final int id, A, B;

		rectangle(int id, int A, int B) {
			this.id = id;
			this.A = A;
			this.B = B;
		}
	}

	class put {
		int X = 0, Y = 0, K = 0;
		final rectangle r;

		put(rectangle r) {
			this.r = r;
		}

		int getX(int K) {
			return K == 0 ? r.A : r.B;
		}

		int getX() {
			return getX(K);
		}

		int getY(int K) {
			return K == 0 ? r.B : r.A;
		}

		int getY() {
			return getY(K);
		}

		void swapK() {
			K = (K + 1) & 1;
		}
	}

	rectangle rect[];

	void bigRect(put big[]) {

		Arrays.sort(big, new Comparator<put>() {
			@Override
			public int compare(put o1, put o2) {
				return calc(o1) - calc(o2);
			}

			int calc(put p) {
				return Math.min(p.r.A, p.r.B);
			}
		});
		put top = big[0];
		if (top.getX() < top.getY())
			top.swapK();
		put bot = big[1];
		if (bot.getX() < bot.getY())
			bot.swapK();

		ArrayList<put> left = new ArrayList<>();
		ArrayList<put> right = new ArrayList<>();
		int leftSum = 0, rightSum = 0;
		for (int i = 2; i < big.length; i++) {
			if (leftSum < rightSum) {
				left.add(big[i]);
				leftSum += big[i].getY();
			} else {
				right.add(big[i]);
				rightSum += big[i].getY();
			}
		}

		while (true) {
			boolean isLeft = false;
			put swap = null;
			int diff = rightSum - leftSum, tmp = Math.abs(diff);
			for (put p : left) {
				if (tmp > Math.abs(diff - p.getX() + p.getY())) {
					isLeft = true;
					swap = p;
					tmp = Math.abs(diff - p.getX() + p.getY());
				}
			}
			for (put p : right) {
				if (tmp > Math.abs(diff + p.getX() - p.getY())) {
					isLeft = false;
					swap = p;
					tmp = Math.abs(diff + p.getX() - p.getY());
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
			ArrayList<put> run(ArrayList<put> list) {
				Collections.sort(list, new Comparator<put>() {
					@Override
					public int compare(put o1, put o2) {
						return calc(o1) - calc(o2);
					}

					int calc(put p) {
						return p.getX() - p.getY();
					}
				});
				Deque<put> deq = new ArrayDeque<>();
				for (int i = 0; i < list.size(); i++) {
					if (i % 2 == 0)
						deq.addFirst(list.get(i));
					else
						deq.addLast(list.get(i));
				}
				int y = bot.getY();
				for (put p : deq) {
					p.Y = y;
					y += p.getY();
				}
				return new ArrayList<>(deq);
			}
		}
		right = new setY().run(right);
		int ti = right.size() - 1, bi = 0;
		while (ti >= bi) {
			put b = bi == 0 ? bot : right.get(bi - 1);
			put t = ti == right.size() - 1 ? top : right.get(ti + 1);
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
			put b = bi == 0 ? bot : left.get(bi - 1);
			put t = ti == left.size() - 1 ? top : left.get(ti + 1);
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
	}

	private static final boolean isRange(int a, int b, int c, int d) {
		return (a <= c && c <= b) || (c <= a && a <= d);
	}

	private static final boolean isConnect(put p1, put p2) {
		return (isRange(p1.X, p1.X + p1.getX(), p2.X, p2.X + p2.getX()))
				&& (isRange(p1.Y, p1.Y + p1.getY(), p2.Y, p2.Y + p2.getY()));
	}

	private static final boolean isOver(put p1, put p2) {
		return (isRange(p1.X + 1, p1.X + p1.getX() - 1, p2.X, p2.X + p2.getX()))
				&& (isRange(p1.Y + 1, p1.Y + p1.getY() - 1, p2.Y, p2.Y + p2.getY()));
	}

	//	public static void main(String args[]) {
	//		new RectanglesAndHoles().test();
	//	}

	void test() {
	}

	private static final boolean isPutOK(List<put> used, put p) {
		for (put u : used) {
			if (isOver(p, u))
				return false;
		}
		return true;
	}

	put getMinDist(List<put> use, int d) {
		put res = null;
		int tmp = Integer.MAX_VALUE;
		for (put p : use) {
			int x = Math.max(p.r.A, p.r.B);
			if (x >= d && tmp > x) {
				tmp = x;
				res = p;
			}
		}
		return res;
	}

	private static final void use(ArrayList<put> use, ArrayList<put> used, ArrayList<put> target, put p) {
		use.remove(p);
		used.add(p);
		target.add(p);
	}

	boolean putRect(put i1, put i2, put i3, ArrayList<put> use, ArrayList<put> used, ArrayList<put> target) {
		put p1 = null, p2 = null, p3 = null;
		boolean b1 = isConnect(i1, i2);
		boolean b2 = isConnect(i1, i3);
		boolean b3 = isConnect(i2, i3);
		if (b1 && b2 && b3) {
			return false;
		} else if (b1 && b2) {
			p1 = i1;
			p2 = i2;
			p3 = i3;
		} else if (b1 && b3) {
			p1 = i2;
			p2 = i1;
			p3 = i3;
		} else if (b2 && b3) {
			p1 = i3;
			p2 = i1;
			p3 = i2;
		} else {
			return false;
		}
		if (p1.X > p2.X && p1.X > p3.X) {
			// ■□
			// □■
			// ■□
			put top = p2.Y < p3.Y ? p3 : p2;
			put bot = p2.Y > p3.Y ? p3 : p2;
			int d = top.Y - bot.Y - bot.getY();
			put p = getMinDist(use, d);
			if (p != null) {
				if (d > p.getY() || (p.getX() >= d && p.getX() < p.getY()))
					p.swapK();
				if (d == p.getY()) {
					p.X = p1.X - p.getX() - 1;
					p.Y = bot.Y + bot.getY();
					if (isPutOK(used, p)) {
						use(use, used, target, p);
						return true;
					}
				}
				if (top.X < bot.X) {
					p.X = bot.X - p.getX();
					p.Y = top.Y - p.getY();
				} else {
					p.X = top.X - p.getX();
					p.Y = bot.Y + bot.getY();
				}
				if (isPutOK(used, p)) {
					use(use, used, target, p);
					return true;
				}
			}
		}
		if (p1.Y > p2.Y && p1.Y > p3.Y) {
			// □■□
			// ■□■
			put right = p2.X < p3.X ? p3 : p2;
			put left = p2.X > p3.X ? p3 : p2;
			int d = right.X - left.X - left.getX();
			put p = getMinDist(use, d);
			if (p != null) {
				if (d > p.getX() || (p.getY() >= d && p.getX() > p.getY()))
					p.swapK();
				if (d == p.getX()) {
					p.X = left.X + left.getX();
					p.Y = p1.Y - p.getY() - 1;
					if (isPutOK(used, p)) {
						use(use, used, target, p);
						return true;
					}
				}
				if (right.Y < left.Y) {
					p.Y = left.Y - p.getY();
					p.X = right.X - p.getX();
				} else {
					p.Y = right.Y - p.getY();
					p.X = left.X + left.getX();
				}
				if (isPutOK(used, p)) {
					use(use, used, target, p);
					return true;
				}
			}
		}
		if (p1.X + p1.getX() < p2.X + p2.getX() && p1.X + p1.getX() < p3.X + p3.getX()) {
			// □■
			// ■□
			// □■
			put top = p2.Y < p3.Y ? p3 : p2;
			put bot = p2.Y > p3.Y ? p3 : p2;
			int d = top.Y - bot.Y - bot.getY();
			put p = getMinDist(use, d);
			if (p != null) {
				if (d > p.getY() || (p.getX() >= d && p.getX() < p.getY()))
					p.swapK();
				if (d == p.getY()) {
					p.X = p1.X + p1.getX() + 1;
					p.Y = bot.Y + bot.getY();
					if (isPutOK(used, p)) {
						use(use, used, target, p);
						return true;
					}
				}
				if (top.X + top.getX() < bot.X + bot.getX()) {
					p.X = top.X + top.getX();
					p.Y = bot.Y + bot.getY();
				} else {
					p.X = bot.X + bot.getX();
					p.Y = top.Y - p.getY();
				}
				if (isPutOK(used, p)) {
					use(use, used, target, p);
					return true;
				}
			}
		}
		if (p1.Y + p1.getY() < p2.Y + p2.getY() && p1.Y + p1.getY() < p3.Y + p3.getY()) {
			// ■□■
			// □■□
			put right = p2.X < p3.X ? p3 : p2;
			put left = p2.X > p3.X ? p3 : p2;
			int d = right.X - left.X - left.getX();
			put p = getMinDist(use, d);
			if (p != null) {
				if (d > p.getX() || (p.getY() >= d && p.getX() > p.getY()))
					p.swapK();
				if (d == p.getX()) {
					p.X = left.X + left.getX();
					p.Y = p1.Y + p1.getY() + 1;
					if (isPutOK(used, p)) {
						use(use, used, target, p);
						return true;
					}
				}
				if (right.Y + right.getY() < left.Y + left.getY()) {
					p.Y = right.Y + right.getY();
					p.X = left.X + left.getX();
				} else {
					p.Y = left.Y + left.getY();
					p.X = right.X - p.getX();
				}
				if (isPutOK(used, p)) {
					use(use, used, target, p);
					return true;
				}
			}
		}
		return false;
	}

	private static final boolean isFar(put p1, put p2) {
		return MAX_SIZE < Math.abs(p1.X - p2.X) || MAX_SIZE < Math.abs(p1.X - p2.X);
	}

	void countRect(put big[], put small[]) {
		ArrayList<put> used = new ArrayList<>();
		ArrayList<put> use = new ArrayList<>();
		ArrayList<put> target = new ArrayList<>();
		{
			put top = null;
			int maxY = Integer.MIN_VALUE;
			for (put p : big) {
				if (p.Y > maxY) {
					maxY = p.Y;
					top = p;
				}
			}
			final int a = 15000;
			for (put p : big) {
				if (top.X - a <= p.X && p.X <= top.X + a && top.Y - a <= p.Y && p.Y <= top.Y + a) {
					target.add(p);
				}
			}
		}
		for (put p : big) {
			used.add(p);
		}
		for (put p : small) {
			use.add(p);
		}
		put: while (!use.isEmpty()) {
			int n = target.size();
			{
				// 3つの四角
				for (int i = 0; i < n; i++) {
					for (int j = i + 1; j < n; j++) {
						if (isFar(target.get(i), target.get(j)))
							continue;
						for (int k = j + 1; k < n; k++) {
							if (!isFar(target.get(i), target.get(k))
									&& putRect(target.get(i), target.get(j), target.get(k), use, used, target)) {
								continue put;
							}
						}
					}
				}
			}
			{
				// 2つの四角
				put minRect = null;
				{
					int tmp = Integer.MAX_VALUE;
					for (put p : use) {
						int x = p.r.A + p.r.B;
						if (tmp > x) {
							minRect = p;
							tmp = x;
						}
					}
				}
				class sort implements Comparable<sort> {
					final int v;
					final put p;

					sort(int v, put p) {
						this.v = v;
						this.p = p;
					}

					@Override
					public int compareTo(sort o) {
						return o.v - v;
					}

					public String toString() {
						return String.format("%d", v);
					}
				}
				List<sort> list = new ArrayList<>();
				for (int i = 0; i < n; i++) {
					int connect = 0;
					put t = target.get(i);
					for (int j = 0; j < n; j++) {
						if (i != j && isConnect(t, target.get(j)))
							++connect;
					}
					if (connect >= 2) {
						list.add(new sort(connect, target.get(i)));
					}
				}
				Collections.sort(list);
				for (sort s : list) {
					minRect.X = s.p.X - minRect.getX();
					minRect.Y = s.p.Y - minRect.getY();
					if (isPutOK(used, minRect)) {
						use(use, used, target, minRect);
						continue put;
					}
					minRect.Y = s.p.Y + s.p.getY();
					if (isPutOK(used, minRect)) {
						use(use, used, target, minRect);
						continue put;
					}
					minRect.X = s.p.X + s.p.getX();
					if (isPutOK(used, minRect)) {
						use(use, used, target, minRect);
						continue put;
					}
					minRect.Y = s.p.Y - minRect.getY();
					if (isPutOK(used, minRect)) {
						use(use, used, target, minRect);
						continue put;
					}
				}
			}
		}
	}

	void init() {
		N = A.length;
		rect = new rectangle[N];
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
		put big[] = new put[(N + 1) / 2];
		put small[] = new put[N / 2];
		{
			int bi = 0, si = 0;
			for (int i = 0; i < N; i++) {
				if (i < big.length) {
					big[bi++] = new put(rect[i]);
				} else {
					small[si++] = new put(rect[i]);
				}
			}
		}
		bigRect(big);
		countRect(big, small);

		return getAns(big, small);
	}

	int[] getAns(put big[], put small[]) {
		int ans[] = new int[N * 3];
		for (put p : big) {
			int i = p.r.id * 3;
			ans[i] = p.X;
			ans[i + 1] = p.Y;
			ans[i + 2] = p.K;
		}
		for (put p : small) {
			int i = p.r.id * 3;
			ans[i] = p.X;
			ans[i + 1] = p.Y;
			ans[i + 2] = p.K;
		}
		return ans;
	}
}