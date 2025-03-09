import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;


public class Solution2 implements Comparable<Solution2> {
	/**
	 * To create an empty solution
	 */
	public Solution2() {
		nTour = new int[Problems.getProblem().getCityNumber()];//Tour diambil sesuai dengan file
		pTour = new int[nTour.length];//Peta Tour yang akan diubah
	}

	/**
	 * 
	 */
	public Solution2(boolean isChristofides) {//Fungsi Christofides
		Problems p = Problems.getProblem();//mengambil data sesuai dengan file
		if ( isChristofides ) {
			int[] mst = findMST();//Mencari MST
			ArrayList<Integer> oddNodes = findOddDegreeNodes(mst);//Mencari Simpul berderajat Ganjil
			int[] matching = findMinimumWeightPerfectMatching(oddNodes);//Mencari MWPM
			List<int[]> eulerianGraph = combineMSTAndMWPM(mst, matching);//Menggabungkan MST dan MWPM
			List<Integer> eulerianTour = findEulerTour(eulerianGraph);//mencari Euler Tour
			nTour = findHamiltonianCycle(eulerianTour);//mencari siklus hamilton/TSP tour

		} else {
			nTour = randomTour();//Mencari TSP tour secara acak
		}
		pTour = new int[nTour.length];//Peta Tour
		for (int i = 0; i< nTour.length; i++ ) {
			pTour[nTour[i]] = i;// Memetakan kota ke indeks urutannya dalam tour
		}
		//contoh [0,3,2,1], maka kota 0 --> 1, kota 3 -->1 dan seterusnya
		tourLength = p.evaluate(nTour);
	}

	public Solution2(Solution2 s) {//Salinan independen memastikan bahwa perubahan pada array dari objek baru 
		this.pTour = s.pTour.clone();//tidak memengaruhi array dari objek asal s
		this.nTour = s.nTour.clone();
		tourLength = s.tourLength;
	}

	public double[][] distance(){//membuat matriks jarak
		Problems problem = Problems.getProblem();
		int cityNumber = Problems.getProblem().getCityNumber();
		double[][] distanceMatrix  = new double[cityNumber][cityNumber]; 

		for (int i = 0; i < cityNumber; i++) {
            for (int j = 0; j < cityNumber; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0; // Jarak ke titik yang sama adalah 0
                } else {
					distanceMatrix[i][j] = problem.getEdge(i,j);
                }
            }
        }
		return distanceMatrix;
	}

	public int[] randomTour() {
		int cityNumber = Problems.getProblem().getCityNumber();
		int[] tour = new int[cityNumber];

		Vector<Integer> rdyCity = new Vector<Integer>();
		for (int i=0; i<cityNumber; i++) {
			rdyCity.add(i);//inisiasi kota 1, kota 2, kota 3,...
		}

		for (int i=0; i<cityNumber; i++) {
			int idx = Solution2.rand.nextInt(rdyCity.size());
			tour[i] = rdyCity.remove(idx).intValue();//masukkan kota random kedalam tour
		}
		int[] linkedTour = new int[cityNumber];// Array untuk membangun siklus terhubung
		for (int i=0; i<cityNumber-1; i++) {
			linkedTour[tour[i]] = tour[i+1];// Hubungkan setiap simpul ke simpul berikutnya
		}
		linkedTour[tour[cityNumber-1]] = tour[0];// Hubungkan simpul terakhir kembali ke simpul pertama
		return linkedTour;
	}
	
	public int[] findMST() {// Menggunakan Algoritma Prim 
		Problems problem = Problems.getProblem();//data kota
		int cityNumber = Problems.getProblem().getCityNumber();//banyak kota
		Vector<Integer> queue = new Vector<Integer>();//List antrian simpul atau kota
		for (int i=0; i<cityNumber; i++) {
			queue.add(i);//Kota 1, Kota 2, Kota 3, ...
			//System.out.println(rdyCity);
		}
		boolean isInTree[] = new boolean[cityNumber];//menandai apakah telah di list
		double key[]=new double[cityNumber];//berisi jarak minimum dari simpul ke MST
		int p[]=new int[cityNumber];//menyimpan parent dari setiap simpul

		for(int i=0;i<cityNumber;i++){
			key[i]=Integer.MAX_VALUE;//Semua jarak awalnya diatur ke nilai maksimum
		}

		key[0]=0; //kota pertama yang dipilih
		int u=0;
		double temp;
		Integer elem;
		do{
			isInTree[u] = true; // Tandai simpul saat ini sebagai bagian dari MST
			queue.remove((Integer) u);// Hapus simpul dari antrian
			for(int v=0;v<cityNumber;v++){ // Periksa semua tetangga simpul u
					if( !isInTree[v] && problem.getEdge(u,v)<key[v] ){
							p[v]=u;// Tetapkan u sebagai parent dari v
							key[v]=problem.getEdge(u,v);// Perbarui jarak minimum ke MST
					}
			}

			double mint=Double.MAX_VALUE;
			for(int i=0;i<queue.size();i++){
					elem=(Integer)queue.elementAt(i);  // Dapatkan simpul dari antrian
					temp=key[elem.intValue()];
					if(temp<mint){// Temukan simpul dengan jarak minimum ke MST
							u=elem.intValue();
							mint=temp;
					}
			}
		} while(!queue.isEmpty());//Iterasi berlanjut hingga semua simpul ditambahkan ke MST
		// printMST(p, key, problem);
		return p;
    }

	// private void printMST(int[] p, double[] key, Problems problem) {
    //     double totalWeight = 0;
    //     System.out.println("Edge\tWeight");
    //     for (int i = 1; i < p.length; i++) { // Mulai dari 1 karena 0 adalah root
    //         System.out.printf("%d - %d\t%.2f\n", p[i], i, key[i]);
    //         totalWeight += key[i];
    //     }
    //     System.out.printf("Total Weight: %.2f\n", totalWeight);
    // }
	// public void printGraph(int[] mst) {
	// 	System.out.println("Edges in the MST:");
	// 	for (int i = 0; i < mst.length; i++) {
	// 		if (mst[i] != i) { // Abaikan root (simpul pertama)
	// 			System.out.println("Edge: " + (mst[i]+1) + " -> " + (i+1));
	// 		}
	// 	}
	// }
	

	public ArrayList<Integer> findOddDegreeNodes(int[] mst) {
		int cityNumber = Problems.getProblem().getCityNumber();
        // Array untuk menghitung derajat setiap simpul
        int[] degree = new int[cityNumber];

        // Hitung derajat berdasarkan MST (array p[])
        for (int i = 0; i < cityNumber; i++) {
            if (mst[i] != i) { // Abaikan root (simpul pertama)
                degree[i]++;// Tambahkan derajat untuk simpul i
                degree[mst[i]]++;// Tambahkan derajat untuk parent dari i
            }
        }

        // Temukan simpul berderajat ganjil
        ArrayList<Integer> oddNodes = new ArrayList<>();
        for (int i = 0; i < cityNumber; i++) {
            if (degree[i] % 2 != 0) {// Jika derajat ganjil
				// System.out.println((i+1)+" Sebanyak:"+degree[i]);
                oddNodes.add(i);// Tambahkan simpul ke daftar simpul ganjil
            }
        }
		//System.out.println("\n");
        return oddNodes;
    }

	public int[] findMinimumWeightPerfectMatching(ArrayList<Integer> oddNodes) {
        Problems problem = Problems.getProblem();
        int size = oddNodes.size();//banyak simpul dengan derajat ganjil

        // Array untuk menyimpan pasangan (matching)
        int[] matching = new int[problem.getCityNumber()];
        for (int i = 0; i < matching.length; i++) {
            matching[i] = -1; // Inisialisasi semua simpul tidak berpasangan
        }

		// System.out.println("Odd Nodes: " + oddNodes);

        // Iterasi semua pasangan simpul ganjil
        boolean[] visited = new boolean[size];
        for (int i = 0; i < size; i++) {
            if (visited[i]) continue;// Jika simpul sudah dipasangkan, lewati

            int node1 = oddNodes.get(i);// Ambil simpul pertama
            double minWeight = Double.MAX_VALUE;// Bobot minimum
            int matchIndex = -1;

            for (int j = i + 1; j < size; j++) {
                if (!visited[j]) {// Periksa simpul kedua yang belum dipasangkan
                    int node2 = oddNodes.get(j);
                    double weight = problem.getEdge(node1, node2);// Bobot sisi node1 dengan node2

                    // Pilih pasangan dengan bobot minimum
                    if (weight < minWeight) {
                        minWeight = weight;
                        matchIndex = j;// Simpan indeks pasangan
                    }
                }
            }
			
            // Tandai pasangan yang ditemukan
            if (matchIndex != -1) {
                int node2 = oddNodes.get(matchIndex);
                matching[node1] = node2;// Pasangkan node1 dengan node2
                matching[node2] = node1;// Pasangkan node2 dengan node1
                visited[i] = true;// Tandai node1 sebagai dipasangkan
                visited[matchIndex] = true;// Tandai node2 sebagai dipasangkan

				// System.out.println("Matching Pair Found: " + node1 + " <-> " + node2 + " with Weight: " + minWeight);
            }
        }
		int[] mwpw = new int[matching.length];
		// System.out.println("Final Matching: ");
		for (int i = 0; i < matching.length; i++) {
			if (matching[i] != -1) {// Jika ada pasangan
				// System.out.println(i + " -> " + matching[i]);
				mwpw[i]=matching[i];// Salin pasangan ke array hasil
			}
		}
		
        return mwpw;
    }

	public List<int[]> combineMSTAndMWPM(int[] mst, int[] matching) {
		List<int[]> eulerianGraph = new ArrayList<>();//menyimpan pasangan simpul-simpul 
		// System.out.println("MST: ");
		// Tambahkan MST ke graf Eulerian
		for (int i = 0; i < mst.length; i++) {
			if (mst[i] != i) { // Abaikan root (simpul pertama)
				eulerianGraph.add(new int[]{i, mst[i]});//masukkan MST
				// System.out.println(i+"->"+mst[i]);
			}
		}
		// System.out.println("Matching: ");
        for (int i = 0; i < matching.length; i++) {
            if (matching[i] != i&& i < matching[i]) {
                eulerianGraph.add(new int[]{i, matching[i]});//masukkan MWPM
				// System.out.println(i+"->"+matching[i]);
            }
        }

		// System.out.println("Eulerian Graph Edges:");
		// for (int[] edge : eulerianGraph) {
		// 	System.out.println((edge[0]+1) + " -> " + (edge[1]+1));
		// }

		return eulerianGraph;
	}

	public List<Integer> findEulerTour(List<int[]> eulerianGraph) {
		int startNode=0;//Titik awal tour, diasumsikan simpul 0
        // Representasi graf sebagai adjacency list
        Map<Integer, Stack<Integer>> graph = new HashMap<>();
        for (int[] edge : eulerianGraph) {//memanggil sisi pada graf euler
            graph.putIfAbsent(edge[0], new Stack<>());
            graph.putIfAbsent(edge[1], new Stack<>());
            graph.get(edge[0]).push(edge[1]);
            graph.get(edge[1]).push(edge[0]);
        }

        // Stack untuk menyimpan jalur Euler
        Stack<Integer> stack = new Stack<>();//Digunakan untuk melacak jalur saat mencari tur Eulerian.
        List<Integer> eulerTour = new ArrayList<>();//Menyimpan urutan simpul dalam tur Eulerian.
        stack.push(startNode);

        while (!stack.isEmpty()) {//Selama stack tidak kosong, ambil simpul teratas (current).
            int current = stack.peek();

            // Jika simpul memiliki tetangga
            if (graph.containsKey(current) && !graph.get(current).isEmpty()) {
                int neighbor = graph.get(current).pop();//Hapus tetangga v dari daftar u 
                graph.get(neighbor).remove((Integer) current); // Hapus tetanggan u dari daftar v
                stack.push(neighbor); //tambahkan neighbor ke dalam stack
            } else {
                eulerTour.add(stack.pop());//Jika tidak tambahkan simpul tersebut ke eulerTour dan keluarkan dari stack.
            }
        }
		//System.out.println("Euler Tour: " + eulerTour);
        return eulerTour; // Mengembalikan tur Euler
    }

	public int[] findHamiltonianCycle(List<Integer> eulerTour) {
		int cityNumber = Problems.getProblem().getCityNumber();
		boolean[] visited = new boolean[cityNumber];// Array untuk melacak simpul yang sudah dikunjungi
		int[] tour = new int[cityNumber];// Array untuk menyimpan siklus Hamiltonian (sementara)
		int index = 0;//Posisi saat ini 
	
		// Buat siklus Hamiltonian dari tur Euler
		for (int node : eulerTour) {
			if (node < 0 || node >= cityNumber) {
				throw new IllegalArgumentException("Invalid node in Euler Tour: " + node);// Validasi simpul
			}
			if (!visited[node]) {// Jika simpul belum dikunjungi
				if (index >= cityNumber) {
					throw new IllegalStateException("Hamiltonian Cycle exceeds city number!");// Validasi ukuran
				}
				tour[index++] = node;// Masukkan simpul ke dalam siklus Hamiltonian
				visited[node] = true;// Tandai simpul sebagai dikunjungi
			}
		}
	
		// Pastikan jumlah simpul dalam tur Hamilton sama dengan cityNumber
		if (index != cityNumber) {
			throw new IllegalStateException("Incomplete Hamiltonian Cycle. Expected " + cityNumber + " nodes, but found " + index);
		}
	
		// Bangun linkedTour untuk menciptakan siklus
		int[] linkedTour = new int[cityNumber];
		for (int i = 0; i < cityNumber - 1; i++) {
			linkedTour[tour[i]] = tour[i + 1];// Hubungkan setiap simpul ke simpul berikutnya
		}
		linkedTour[tour[cityNumber - 1]] = tour[0]; // Hubungkan simpul terakhir kembali ke simpul pertama
		
		// System.out.println("Hamiltonian Cycle: " + Arrays.toString(tour));
		// System.out.println("Linked Hamiltonian Cycle: " + Arrays.toString(linkedTour));
		return linkedTour;
	}

	public void mutation(int times) {
		if (Problems.getProblem().isSymmetric()) {//apakah dataset simetris
			int[] tour = new int[nTour.length];// Array untuk menyimpan salinan tour
			int city = 0;
			tour[0] = city;// Mulai dari kota 0
			for (int i=1; i<nTour.length; i++) {
				city = nTour[city];// Melakukan iterasi untuk membangun salinan tour
				tour[i] = city;
			}
			while (times-- > 0) {//Menunjukkan berapa kali mutasi akan dilakukan.
				int from = Solution2.rand.nextInt(tour.length);// Pilih indeks asal secara acak
				int to = Solution2.rand.nextInt(tour.length);// Pilih indeks tujuan secara acak
				int temp = tour[from];
				tour[from] = tour[to];// Tukar posisi dua kota dalam tour
				tour[to] = temp;
			}
			for (int i=0; i<tour.length-1; i++) {
				nTour[tour[i]] = tour[i+1];// Perbarui tour berdasarkan tur yang sudah dimutasi
			}
			nTour[tour[tour.length-1]] = tour[0];// Tutup siklus tur
			for (int i = 0; i< nTour.length; i++ ) {
				pTour[nTour[i]] = i;// Menyimpan posisi kota dalam peta tour
			}
			tourLength = Problems.getProblem().evaluate(nTour);//Menghitung panjang tour baru setelah mutasi.
		} 
	}


	public int next(int city) {// Mengembalikan kota berikutnya dalam tur setelah kota yang diberikan
		return nTour[city];
	}


	public int previous(int city) {// Mengembalikan kota sebelumnya dalam tur sebelum kota yang diberikan
		return pTour[city];
	}

	public void evaluate() {//Mengevaluasi panjang tur (tourLength) berdasarkan solusi
		Problems p = Problems.getProblem();// Ambil objek permasalahan (TSP) dari class Problems
		tourLength = p.evaluate(this);// Evaluasi panjang tour (tourLength) berdasarkan solusi saat ini
	}

	public void update(Solution2 s) {
		System.arraycopy(s.nTour, 0, nTour, 0, nTour.length);// Salin tour (nTour) dari solusi s ke objek ini
		System.arraycopy(s.pTour, 0, pTour, 0, pTour.length);// Salin tour (pTour) dari solusi s ke objek ini
		tourLength = s.tourLength;// Salin panjang tour dari solusi s ke objek ini
	}

	public void update(Neighbor m) {
		if ( m != null ) {
			if ( m.type == Neighbor.INVERSE) {// Perubahan adalah Inverse segmen tour
				reverse(m.x1, m.y1, m.delta);
			} else if ( m.type == Neighbor.INSERT) {// Perubahan Insert
				insert(m.x1, m.y1, m.y2, m.delta);
			} else if ( m.type == Neighbor.SWAP){// Perubahan Swap
				swap(m.x1, m.x2, m.y1, m.y2, m.delta);
			}
		}

		if (!Problems.getProblem().isSymmetric()) {// Evaluasi panjang tour ulang jika masalah tidak simetris
			evaluate();// akan gagal jika tidak simetris
		}
		//evaluate();
	}

	public Neighbor[] findNeighbors() {
		int ci = Solution2.rand.nextInt(nTour.length);//Pilih kota secara acak dari tour
		int nci = next(ci);// Kota berikutnya dari ci dalam tour
		int pci = previous(ci);// Kota sebelumnya dari ci dalam tour
		int cj = pci;// Awalnya, tetapkan cj sama dengan kota sebelumnya (pci)
		int[] list = Problems.getProblem().getNearCityList()[ci];// Daftar kota yang berdekatan dengan ci
		while (cj == pci || cj == nci) {// Pilih kota cj yang bukan kota sebelum (pci) atau sesudah (nci) kota ci
			cj = list[Solution2.rand.nextInt(list.length)];// Pilih kota secara acak dari daftar kota dekat
		}
		Neighbor invNeighbor = findInverse(ci,cj);//Mencari dengan inverse
		Neighbor insNeighbor = findInsert(ci, cj);//Mencari dengan insert
		Neighbor swaNeighbor = findSwap(ci, cj);//Mencari dengan swap
		return new Neighbor[] {invNeighbor, insNeighbor, swaNeighbor};// Kembalikan array yang berisi tiga tetangga yang ditemukan
	}

	public Neighbor findNeighbor(int ci, int cj) {
		Neighbor bestMove = null; // Deklarasi variabel untuk menyimpan pergerakan terbaik
		bestMove = findInverse(ci, cj);// Cari pergerakan terbaik dengan inverse
		Neighbor move;
		move = findInsert(ci, cj);// Cari pergerakan terbaik dengan insert dan bandingkan
		if (move.delta < bestMove.delta) {
			bestMove = move;
		}
		move = findSwap(ci, cj);// Cari pergerakan terbaik dengan swap dan bandingkan
		if (move!=null && move.delta < bestMove.delta) {
			bestMove = move;
		}
		 // Kembalikan pergerakan terbaik yang ditemukan
		return bestMove;
	}

	private Neighbor findInverse(final int ci, final int cj) {
		Problems problem = Problems.getProblem();
		// Cari kota berikutnya dari `ci` dan `cj` dalam tour saat ini
		int nci = next(ci);
		int ncj = next(cj);

		// Hitung bobot tepi-tepi yang relevan untuk inverse
		double dci_cj = problem.getEdge(ci, cj);
		double dnci_ncj = problem.getEdge(nci, ncj);
		double dci_nci = problem.getEdge(ci, nci);
		double dcj_ncj = problem.getEdge(cj, ncj);

		// Hitung perubahan (delta) yang dihasilkan dari inverse
		double ivsDelta = dci_cj + dnci_ncj - (dci_nci + dcj_ncj);

		// Jika masalah tidak simetris, gunakan metode khusus untuk menghitung delta
		if ( ! problem.isSymmetric() ) {
			ivsDelta = this.findInverseDelta(ci, cj);
		}
		 // Buat objek Neighbor baru untuk menyimpan informasi tentang langkah inverse
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

		// Variabel yang digunakan untuk mengelola titik awal dan akhir
		int x1 = ci;// Kota yang akan dipindahkan
		int px1 = previous(x1);
		int nx1 = next(x1);

		int y1 = cj;// Kota tujuan awal untuk insert
		int y2 = y1;// Awalnya `y2` sama dengan `y1`

		// Menentukan panjang blok yang akan dipindahkan secara acak
		int len = Solution2.rand.nextInt( Simulations.getMaxInsertBlockSize() ) + 1;// Panjang blok (1 hingga max)
		int ny = next(y2);// Kota setelah y2
		while (len > 1 && ny != px1) {
			y2 = ny;// Perluas blok hingga `len` terpenuhi atau mencapai batas (sebelum x1)
			ny = next(y2);
			len--;// Kurangi panjang blok yang tersisa
		}

		// Kota di sekitar titik tujuan
		int py1 = previous(y1); // Kota sebelum y1
		int ny2 = next(y2);// Kota setelah y2

		double x1_y1 = problem.getEdge(x1, y1);
		double y2_nx1 = problem.getEdge(y2, nx1);
		double py1_ny2 = problem.getEdge(py1, ny2);
		double movDelta = x1_y1 + y2_nx1 + py1_ny2;// Total panjang baru

		double x1_nx1 = problem.getEdge(x1, nx1);
		double py1_y1 = problem.getEdge(py1, y1);
		double y2_ny2 = problem.getEdge(y2, ny2);
		movDelta -= x1_nx1 + py1_y1 + y2_ny2;// Delta total adalah perbedaan panjang baru dan lama

		 // Buat objek Neighbor untuk mewakili gerakan yang ditemukan
		return new Neighbor(x1, y1, y2, movDelta);
	}

	private Neighbor findSwap(final int ci, final int cj) {
		Problems problem = Problems.getProblem();
		// Variabel terkait kota x1 (kota pertama dalam swap)
		int x1 = next(ci);
		int px1 = ci;
		int nx1 = next(x1);
		if (nx1 == cj) return findInverse(ci, cj); // Jika x1 dan cj bertetangga, gunakan inverse saja
		
		// Variabel terkait kota y1 (kota kedua dalam swap)
		int y1 = cj; // Kota tujuan yang akan ditukar
		int py1 = previous(y1); // Kota sebelum y1
		int ny1 = next(y1); // Kota setelah y1
		
		// Hitung panjang baru setelah swap
		double px1_y1 = problem.getEdge(px1, y1);
		double y1_nx1 = problem.getEdge(y1, nx1);
		double py1_x1 = problem.getEdge(py1, x1);
		double x1_ny1 = problem.getEdge(x1, ny1);
		double swaDelta = px1_y1 + y1_nx1 + py1_x1 + x1_ny1;// Total panjang baru
		// Hitung panjang lama sebelum swap
		double px1_x1 = problem.getEdge(px1, x1);
		double x1_nx1 = problem.getEdge(x1, nx1);
		double py1_y1 = problem.getEdge(py1, y1);
		double y1_ny1 = problem.getEdge(y1, ny1);
		swaDelta -= px1_x1 + x1_nx1 + py1_y1 + y1_ny1;// Delta total adalah perbedaan panjang baru dan lama

		// Kembalikan Neighbor baru yang merepresentasikan gerakan swap
		return new Neighbor(x1, x1, y1, y1, swaDelta);
	}


	/**
	 * reverse the block of cities specified by from (excluded) and to (included)
	 * @param from
	 * @param to
	 * @param delta
	 */
	private void reverse(int from, int to, double delta) {
		// Inisialisasi variabel untuk menyimpan informasi kota yang akan diinverse
		int city_from = from;// Titik awal dari segmen yang akan diinverse
		int city_from_next = nTour[from];// Kota setelah titik awal
		int city_to = to;// Titik akhir dari segmen yang akan diinverse
		int city_to_next = nTour[to];// Kota setelah titik akhir
		// Loop untuk inverse arah tur dari `from` hingga `to`
		while (  city_to != from) { //city_to != from
			nTour[city_from] = city_to;// Atur `city_from` agar menunjuk ke `city_to` (membalik arah)
			int temp = pTour[city_to];// Simpan kota sebelumnya dari `city_to`
			pTour[city_to] = city_from;// Atur kota sebelumnya dari `city_to` menjadi `city_from`
			city_from  = city_to;	    // Perbarui `city_from` untuk iterasi berikutnya	          	  
			city_to = temp;// Perbarui `city_to` untuk iterasi berikutnya
		}
		// Sambungkan kembali segmen di luar range `from` dan `to`
		nTour[city_from_next] = city_to_next;// Kota setelah `from` akan terhubung ke kota setelah `to`
		pTour[city_to_next] = city_from_next;// Kota sebelum `to_next` akan menjadi `from_next`
		// Perbarui panjang tur berdasarkan perubahan
		tourLength += delta;// Tambahkan nilai delta ke panjang tour
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
		// Deklarasi variabel untuk mempermudah akses
		int ci = cityTo;
		int cj = cityFrom;
		int ck = cityEnd;
		// Menyimpan referensi kota berikutnya dan sebelumnya untuk koneksi
		int nci = nTour[ci];
		int nck = nTour[ck];
		int pcj = pTour[cj];
		// Memperbarui array `nTour` (menghubungkan tour baru):
		nTour[ci] = cj; 
		nTour[ck] = nci; 
		nTour[pcj] = nck;
		// Memperbarui array `pTour` (menghubungkan arah sebaliknya):
		pTour[cj] = ci; 
		pTour[nci] = ck; 
		pTour[nck] = pcj;
		// Perbarui panjang tur dengan nilai delta
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
		// Variabel untuk referensi kota terkait swap
		int px1 = pTour[x1];
		int nx2 = nTour[x2];
		int py1 = pTour[y1];
		int ny2 = nTour[y2];
		// Memperbarui array `nTour` (menghubungkan tour baru):
		nTour[px1] = y1; nTour[y2] = nx2; nTour[py1] = x1; nTour[x2] = ny2;
		// Memperbarui array `pTour` (menghubungkan arah sebaliknya):
		pTour[y1] = px1; pTour[nx2] = y2; pTour[x1] = py1; pTour[ny2] = x2;
		// Perbarui panjang tur dengan nilai delta
		tourLength += delta;
		swpTimes++;
	}


	@Override
	public int compareTo(Solution2 s) {
		if ( tourLength < s.tourLength) {
			return 1;// Mengembalikan nilai positif jika solusi ini lebih baik (tourLength lebih kecil)
		} else if ( tourLength == s.tourLength) {
			return 0;// Mengembalikan 0 jika kedua solusi memiliki panjang tur yang sama
		} else {
			return -1;// Mengembalikan nilai negatif jika solusi ini lebih buruk (tourLength lebih besar)
		}
	}

	public String toString() {
		String str = "";// Menyimpan representasi string dari tour
		int city = 0;// Mulai dari kota pertama (city 0)
		do {
			str += city + "-";// Tambahkan kota ke string dengan format "city-"
			city = next(city);// Pindah ke kota berikutnya
		} while (city != 0);
		return str;// Mengembalikan string yang berisi urutan kota dalam tur
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
		String fileName = (new File("D:\\DOWNLOAD\\ELBSA\\ELBSA4TSP-master\\data")).getAbsolutePath() + "\\TSPLarge26\\01dsj1000.txt";
		//\\TSP_IDBA\\00oliver30.txt
		Problems.setFileName(fileName);
		Solution2 s;
		double tourLength = 0;
		long startTime = System.nanoTime();
		for (int i = 0; i < TIMES; i++) {
			s = new Solution2(true);
			int optimal = 18659688;
			System.out.println(s);
			System.out.println(i + "-:" + s.getTourLength());
			System.err.println("PE:"+(s.getTourLength()-optimal)*100/optimal);

			tourLength += s.getTourLength();
		}
		long endTime = System.nanoTime();
		tourLength /= TIMES;
		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Waktu eksekusi: " + durationInSeconds + " detik");
		tourLength /= TIMES;
		System.out.println("Average: " + tourLength);
	}
}
