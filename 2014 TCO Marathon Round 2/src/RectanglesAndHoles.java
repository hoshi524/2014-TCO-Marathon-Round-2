class RectanglesAndHoles {
	int N;

	class rectangle {
		final int id, A, B;
		int X = 0, Y = 0, K = 0;

		public rectangle(int id, int A, int B) {
			this.id = id;
			this.A = A;
			this.B = B;
		}
	}

	rectangle rect[];

	public int[] place(int A[], int B[]) {
		N = A.length;
		rect = new rectangle[N];
		for (int i = 0; i < N; i++) {
			rect[i] = new rectangle(i, A[i], B[i]);
		}
		return getAns(rect);
	}

	int[] getAns(rectangle rect[]) {
		int ans[] = new int[N * 3];
		for (rectangle r : rect) {
			ans[r.id * 3] = r.X;
			ans[r.id * 3 + 1] = r.Y;
			ans[r.id * 3 + 2] = r.K;
		}
		return ans;
	}
}