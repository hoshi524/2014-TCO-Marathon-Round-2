import java.util.Arrays;
import java.util.Comparator;

class RectanglesAndHoles {
	int N;

	class rectangle {
		final int id, A, B;
		int X = 0, Y = 0, K = 0;

		rectangle(int id, int A, int B) {
			this.id = id;
			this.A = A;
			this.B = B;
		}

		int getX(int K) {
			return K == 0 ? B : A;
		}

		int getX() {
			return getX(K);
		}

		int getY(int K) {
			return K == 0 ? A : B;
		}

		int getY() {
			return getY(K);
		}

		void swap() {
			K = (K + 1) & 1;
		}

		@Override
		public String toString() {
			return String.format("[ %d %d %d %d %d]", A, B, X, Y, K);
		}
	}

	class rectSort implements Comparator<rectangle> {
		@Override
		public int compare(rectangle o1, rectangle o2) {
			return calc(o2) - calc(o1);
		}

		int calc(rectangle r) {
			return r.A * r.A + r.B * r.B;
		}
	}

	rectangle rect[];

	void bigRect(rectangle rect[]) {
		// TODO
	}

	public int[] place(int A[], int B[]) {
		N = A.length;
		rect = new rectangle[N];
		for (int i = 0; i < N; i++) {
			rect[i] = new rectangle(i, A[i], B[i]);
		}
		Arrays.sort(rect, new rectSort());
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

		int x = 50000, y = 0;
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