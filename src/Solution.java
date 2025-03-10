import java.io.File;
import java.util.Random;
import java.util.Vector;


public class Solution implements Comparable<Solution> {
	/**
	 * To create an empty solution
	 */
	public Solution() {
		nTour = new int[Problems.getProblem().getCityNumber()];
		pTour = new int[nTour.length];
	}
	/**
	 * 
	 */
	public Solution(boolean isGreedyRandom) {
		Problems p = Problems.getProblem();
		//System.out.println("Ini getProblem :"+p);
		if ( isGreedyRandom ) {
			nTour = greedyRandomTour(Solution.greedyListLength);
		} else {
			nTour = randomTour();
		}
		pTour = new int[nTour.length];
		for (int i = 0; i< nTour.length; i++ ) {
			pTour[nTour[i]] = i;
			// System.out.println("pTOur "+pTour[nTour[i]]);
		}
		tourLength = p.evaluate(nTour);
	}

	public Solution(Solution s) {
		this.nTour = s.nTour.clone();
		this.pTour = s.pTour.clone();
		tourLength = s.tourLength;
	}

	public int[] randomTour() {
		int cityNumber = Problems.getProblem().getCityNumber();
		int[] tour = new int[cityNumber];

		// System.out.print("Tour: ");
		// for (int i = 0; i < tour.length; i++) {
		// 	System.out.print(tour[i]);
		// 	if (i < tour.length - 1) {
		// 		System.out.print(" -> "); // Tambahkan pemisah antara elemen
		// 	}
		// }
		// System.out.println(); // Tambahkan baris baru setelah selesai


		Vector<Integer> rdyCity = new Vector<Integer>();
		for (int i=0; i<cityNumber; i++) {
			rdyCity.add(i);
			//System.out.println(rdyCity);
		}

		for (int i=0; i<cityNumber; i++) {
			int idx = Solution.rand.nextInt(rdyCity.size());
			tour[i] = rdyCity.remove(idx).intValue();
		}

		// System.out.print("Tour: ");
		// for (int i = 0; i < tour.length; i++) {
		// 	System.out.print(tour[i]);
		// 	if (i < tour.length - 1) {
		// 		System.out.print(" -> "); // Tambahkan pemisah antara elemen
		// 	}
		// }
		// System.out.println(); // Tambahkan baris baru setelah selesai

		int[] linkedTour = new int[cityNumber];
		for (int i=0; i<cityNumber-1; i++) {
			linkedTour[tour[i]] = tour[i+1];
		}
		linkedTour[tour[cityNumber-1]] = tour[0];

		return linkedTour;
	}

	public int[] greedyRandomTour(int len) 	{
		int cityNumber = Problems.getProblem().getCityNumber();
		//System.out.println("Citynumber"+cityNumber);
		int[][] nearCityList = Problems.getProblem().getNearCityList();
		// System.out.println("Isi dari nearCityList:");
		// for (int i = 0; i < nearCityList.length; i++) { // Iterasi melalui baris
		// 	for (int j = 0; j < nearCityList[i].length; j++) { // Iterasi melalui kolom
		// 		System.out.print(nearCityList[i][j] + " "); // Cetak elemen dalam baris yang sama
		// 	}
		// 	System.out.println(); // Pindah ke baris berikutnya
		// }
		int[] tour = new int[cityNumber]; 
		// System.out.println("Isi dari tour:");
		// for (int i = 0; i < tour.length; i++) {
		// 	System.out.print(tour[i] + " "); // Cetak elemen di baris yang sama
		// }
		// System.out.println(); // Pindah ke baris baru setelah mencetak semua elemen

		boolean[] visited = new boolean[cityNumber];
		for (int i=0; i<cityNumber; i++) 	{
			tour[i]=-1;
			// System.out.print(tour[i] + " ");
		}
		int cityIndex=0;
		int city;
		city = Solution.rand.nextInt(cityNumber);
		//System.out.print("ini city:"+city);
		tour[cityIndex] = city;
		//System.out.print("ini tour:"+tour[cityIndex]);
		visited[city] = true;
		// System.out.print("ini visited:"+visited[city]);
		int[] candidateCities = new int[len];
		while (cityIndex<cityNumber-1) {
			int idx;
			int nextCity;
			cityIndex++;
			int candidateLength=0;
			for (int i=0 ; i<nearCityList[city].length; i++) {
				if ( !visited[nearCityList[city][i]]) {
					candidateCities[candidateLength] = nearCityList[city][i];
					candidateLength++;
					if ( candidateLength == len ) {
						break;
					}
				}
			}
			if ( candidateLength > 0) {
				idx = Solution.rand.nextInt(candidateLength);
				nextCity = candidateCities[idx];
			} else {
				int i = Solution.rand.nextInt(cityNumber) ;
				while (visited[i]) {
					i = (i+1)%cityNumber;
				}
				nextCity = i;
			}

			tour[cityIndex] = nextCity;
			visited[nextCity] = true;
			city = nextCity;
		}

		int[] linkedTour = new int[cityNumber];
		for (int i=0; i<cityNumber-1; i++) {
			linkedTour[tour[i]] = tour[i+1];
			
		}
		linkedTour[tour[cityNumber-1]] = tour[0];
		
		return linkedTour;
	}


	public void mutation(int times) {
		if (Problems.getProblem().isSymmetric()) {
			int[] tour = new int[nTour.length];
			int city = 0;
			tour[0] = city;
			for (int i=1; i<nTour.length; i++) {
				city = nTour[city];
				tour[i] = city;
			}
			while (times-- > 0) {
				int from = Solution.rand.nextInt(tour.length);
				int to = Solution.rand.nextInt(tour.length);
				int temp = tour[from];
				tour[from] = tour[to];
				tour[to] = temp;
			}
			for (int i=0; i<tour.length-1; i++) {
				nTour[tour[i]] = tour[i+1];
			}
			nTour[tour[tour.length-1]] = tour[0];
			for (int i = 0; i< nTour.length; i++ ) {
				pTour[nTour[i]] = i;
			}
			tourLength = Problems.getProblem().evaluate(nTour);
		} 
	}


	public int next(int city) {
		return nTour[city];
	}


	public int previous(int city) {
		return pTour[city];
	}

	public void evaluate() {
		Problems p = Problems.getProblem();
		tourLength = p.evaluate(this);
	}

	public void update(Solution s) {
		System.arraycopy(s.nTour, 0, nTour, 0, nTour.length);
		System.arraycopy(s.pTour, 0, pTour, 0, pTour.length);
		tourLength = s.tourLength;
	}

	public void update(Neighbor m) {
		if ( m != null ) {
			if ( m.type == Neighbor.INVERSE) {
				reverse(m.x1, m.y1, m.delta);
			} else if ( m.type == Neighbor.INSERT) {
				insert(m.x1, m.y1, m.y2, m.delta);
			} else if ( m.type == Neighbor.SWAP){
				swap(m.x1, m.x2, m.y1, m.y2, m.delta);
			}
		}

		if (!Problems.getProblem().isSymmetric()) {
			evaluate();
		}
		//evaluate();
	}

	public Neighbor[] findNeighbors() {
		int ci = Solution.rand.nextInt(nTour.length);
		int nci = next(ci);
		int pci = previous(ci);
		int cj = pci;
		int[] list = Problems.getProblem().getNearCityList()[ci];
		while (cj == pci || cj == nci) {
			cj = list[Solution.rand.nextInt(list.length)];
		}
		Neighbor invNeighbor = findInverse(ci,cj);
		Neighbor insNeighbor = findInsert(ci, cj);
		Neighbor swaNeighbor = findSwap(ci, cj);
		return new Neighbor[] {invNeighbor, insNeighbor, swaNeighbor};
	}

	public Neighbor findNeighbor(int ci, int cj) {
		Neighbor bestMove = null;
		bestMove = findInverse(ci, cj);
		Neighbor move;
		move = findInsert(ci, cj);
		if (move.delta < bestMove.delta) {
			bestMove = move;
		}
		move = findSwap(ci, cj);
		if (move!=null && move.delta < bestMove.delta) {
			bestMove = move;
		}
		return bestMove;
	}

	private Neighbor findInverse(final int ci, final int cj) {
		Problems problem = Problems.getProblem();
		int nci = next(ci);
		int ncj = next(cj);

		double dci_cj = problem.getEdge(ci, cj);
		double dnci_ncj = problem.getEdge(nci, ncj);
		double dci_nci = problem.getEdge(ci, nci);
		double dcj_ncj = problem.getEdge(cj, ncj);

		double ivsDelta = dci_cj + dnci_ncj - (dci_nci + dcj_ncj);
		if ( ! problem.isSymmetric() ) {
			ivsDelta = this.findInverseDelta(ci, cj);
		}

		return new Neighbor(ci, cj, ivsDelta);
	}
	
	private double findInverseDelta(int ci, int cj) {//for ASTSP
		Problems problem = Problems.getProblem();
		double delta = 0;
		int from = ci;
		int to = next(from);
		int ncj = next(cj);
		while (to != ncj) {
			delta += -problem.getEdge(from, to);
			from = to;
			to = next(from);
		}
		delta += -problem.getEdge(from, to);
		
		int nci = next(ci);
		delta += problem.getEdge(ci, cj);
		delta += problem.getEdge(nci, ncj);
		from = nci;
		to = next(nci);
		while ( to != ncj) {
			delta += problem.getEdge(to,from);
			from = to;
			to = next(from);
		}
		
		return delta;
	}



	private Neighbor findInsert(final int ci, final int cj) {
		Problems problem = Problems.getProblem();

		int x1 = ci;
		int px1 = previous(x1);
		int nx1 = next(x1);

		int y1 = cj;
		int y2 = y1;

		//find a single block move 
		int len = Solution.rand.nextInt( Simulations.getMaxInsertBlockSize() ) + 1;
		int ny = next(y2);
		while (len > 1 && ny != px1) {
			y2 = ny;
			ny = next(y2);
			len--;
		}

		int py1 = previous(y1);
		int ny2 = next(y2);

		double x1_y1 = problem.getEdge(x1, y1);
		double y2_nx1 = problem.getEdge(y2, nx1);
		double py1_ny2 = problem.getEdge(py1, ny2);
		double movDelta = x1_y1 + y2_nx1 + py1_ny2;

		double x1_nx1 = problem.getEdge(x1, nx1);
		double py1_y1 = problem.getEdge(py1, y1);
		double y2_ny2 = problem.getEdge(y2, ny2);
		movDelta -= x1_nx1 + py1_y1 + y2_ny2;

		return new Neighbor(x1, y1, y2, movDelta);
	}

	private Neighbor findSwap(final int ci, final int cj) {
		Problems problem = Problems.getProblem();

		int x1 = next(ci);
		int px1 = ci;
		int nx1 = next(x1);
		if (nx1 == cj) return findInverse(ci, cj); //inverse two cities
		
		int y1 = cj;
		int py1 = previous(y1);
		int ny1 = next(y1);
		
		double px1_y1 = problem.getEdge(px1, y1);
		double y1_nx1 = problem.getEdge(y1, nx1);
		double py1_x1 = problem.getEdge(py1, x1);
		double x1_ny1 = problem.getEdge(x1, ny1);
		double swaDelta = px1_y1 + y1_nx1 + py1_x1 + x1_ny1;

		double px1_x1 = problem.getEdge(px1, x1);
		double x1_nx1 = problem.getEdge(x1, nx1);
		double py1_y1 = problem.getEdge(py1, y1);
		double y1_ny1 = problem.getEdge(y1, ny1);
		swaDelta -= px1_x1 + x1_nx1 + py1_y1 + y1_ny1;

		return new Neighbor(x1, x1, y1, y1, swaDelta);
	}


	/**
	 * reverse the block of cities specified by from (excluded) and to (included)
	 * @param from
	 * @param to
	 * @param delta
	 */
	private void reverse(int from, int to, double delta) {
		int city_from = from;
		int city_from_next = nTour[from];
		int city_to = to;
		int city_to_next = nTour[to];
		while (  city_to != from) { //city_to != from
			nTour[city_from] = city_to;
			int temp = pTour[city_to];
			pTour[city_to] = city_from;
			city_from  = city_to;	    	          	  
			city_to = temp;
		}
		nTour[city_from_next] = city_to_next;
		pTour[city_to_next] = city_from_next;

		tourLength += delta;
		ivsTimes++;
	}

	/**
	 * Insert a block of city specified by cityFrom (included) and cityEnd (included) after 
	 *        city cityTo
	 *        
	 * @param cityTo
	 * @param cityFrom
	 * @param cityEnd
	 * @param delta
	 */
	private void insert(int cityTo, int cityFrom, int cityEnd, double delta) {
		int ci = cityTo;
		int cj = cityFrom;
		int ck = cityEnd;
		int nci = nTour[ci];
		int nck = nTour[ck];
		int pcj = pTour[cj];
		nTour[ci] = cj; 
		nTour[ck] = nci; 
		nTour[pcj] = nck;
		pTour[cj] = ci; 
		pTour[nci] = ck; 
		pTour[nck] = pcj;

		tourLength += delta;
		insTimes++;
	}

	/**
	 * swap two block of cities (included)
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param delta
	 */
	private void swap(int x1, int x2, int y1, int y2, double delta) {//include x1
		int px1 = pTour[x1];
		int nx2 = nTour[x2];
		int py1 = pTour[y1];
		int ny2 = nTour[y2];
		nTour[px1] = y1; nTour[y2] = nx2; nTour[py1] = x1; nTour[x2] = ny2;
		pTour[y1] = px1; pTour[nx2] = y2; pTour[x1] = py1; pTour[ny2] = x2;

		tourLength += delta;
		swpTimes++;
	}


	@Override
	public int compareTo(Solution s) {
		if ( tourLength < s.tourLength) {
			return 1;
		} else if ( tourLength == s.tourLength) {
			return 0;
		} else {
			return -1;
		}
	}

	public String toString() {
		String str = "";
		int city = 0;
		do {
			str += city + "-";
			city = next(city);
		} while (city != 0);
		return str;
	}

	public void setLastImproving(int n) { this.lastImproving = n;}
	public int getLastImproving() { return this.lastImproving; }
	public int getCityNumber() { return nTour.length; }

	//public void setTourLength(long tourLength) { this.tourLength = tourLength; }
	public double getTourLength() { return tourLength; }
	public int[] getTour() { return nTour; }

	public double tourLength;
	private int lastImproving = 0;

	public static long ivsTimes;
	public static long insTimes;
	public static long swpTimes;

	public static int greedyListLength = 10;


	protected int[] nTour;
	protected int[] pTour;

	private static Random rand = new Random();


	public static void main(String[] args) {
		final int TIMES = 1;
		String fileName = (new File("D:\\DOWNLOAD\\ELBSA\\ELBSA4TSP-master\\data")).getAbsolutePath() + "\\TSPLarge4\\02d2103.txt";
		Problems.setFileName(fileName);
		Solution s;
		double tourLength = 0;
		for (int i = 0; i < TIMES; i++) {
			s = new Solution(true);
			System.out.println(i + "-:" + s.getTourLength());
			tourLength += s.getTourLength();
		}
		tourLength /= TIMES;
		System.out.println("Average: " + tourLength);

	}
}
