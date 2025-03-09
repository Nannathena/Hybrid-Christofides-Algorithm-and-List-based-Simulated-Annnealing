import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
//import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;


public class MethodsforHCALBSA {
	/**
	 * Parallel List-based Simulated Annealing Algorithm for TSP.
	 * Each SA uses its temperature list independently.
	 * 
	 * @param MAX_G maximum generation
	 * @param POP_SIZE population size
	 * @return best solution found
	 */
	public static Solution2 listBasedSA( final int MAX_G, final int POP_SIZE) {
		Solution2[] solutions = new Solution2[POP_SIZE]; //solusi sebanyak populasi
		int bestIdx = 0;//indeks solusi terbaik saat ini
		for (int i = 0; i < POP_SIZE; i++) {
			solutions[i] = new Solution2(Simulations.USE_CHRISTOFIDES);//gunakan semua solusi awal dengan christofides
			//System.out.println(solutions[i].getTourLength());
		}
		//System.out.println(solutions[0]);
		Solution2 best = new Solution2(solutions[bestIdx]);//iniliasasi solusi terbaik saat ini
		// System.out.println("\nbest: " + best.getTourLength());

		final int cityNumber = Problems.getProblem().getCityNumber();//banyak kota
		//System.out.println("\nCity Number dan Markov Chain Length: " + cityNumber);
		final int MARKOV_CHAIN_LENGTH = cityNumber;//Panjang Markov Chain (MCL) sebanyak banyak kota yang ada
		
		//Buat inisial daftar temperatur untuk setiap SA
		PriorityQueue<Double>[] tempLists = MethodsforHCALBSA.produceTemperatureLists(solutions, Simulations.getListLength());

		//banyak temperatur 150
		// Cetak isi array tempLists
		// for (int i = 0; i < tempLists.length; i++) {
		// 	System.out.println("PriorityQueue at index " + i + ":");
		// 	PriorityQueue<Double> queue = tempLists[i];
		// 	if (queue != null) {
				
		// 		for (Double value : queue) {
		// 			System.out.print(value + " ");
		// 		}
		// 		System.out.println(); // Baris baru setelah mencetak semua elemen di queue
		// 	} else {
		// 		System.out.println("Queue is null");
    	// 	}
		// }

		

		//create Markov chain length for each temperature
		int[] seq;//deret
		if (Simulations.getSequenceType() == ESequenceType.CONSTANT) {//apabila deret adalah konstan
			seq = MethodsforHCALBSA.produceConstantSequence(MARKOV_CHAIN_LENGTH, MAX_G);
			//System.out.println("\nSequence Constant: " + Arrays.toString(seq));

		} else {// apabila deret dipakai adalah arimatika
			seq = MethodsforHCALBSA.produceArithmeticSequence((int)(MARKOV_CHAIN_LENGTH * Simulations.getListArithmeticStrength()), 
					MARKOV_CHAIN_LENGTH, 
					(int)(MAX_G * Simulations.getListArithmeticPosition()), MAX_G);
			//System.out.println("\nSequence Arithmetic: " + Arrays.toString(seq));
		}
		
		double[] temperatures = new double[MAX_G];//inialisasi temperatur
		double[] makespans = new double[MAX_G];//inialisasi solusi
		double[] bestMakespans = new double[MAX_G];//inialisasi solusi terbaik
		int[] cis = new int[POP_SIZE];
		//outterloop
		for (int q = 0; q < MAX_G; q++) {
			temperatures[q] = MethodsforHCALBSA.averageTemperatur(tempLists);//rata-rata temperatur
			makespans[q] = MethodsforHCALBSA.averageTourLength(solutions);//rata-rata panjang
			bestMakespans[q] = best.getTourLength();//panjang solusi terbaik	
			for (int id = 0; id < POP_SIZE; id++) {
				double t = -tempLists[id].peek();//ambil suhu terbesar pada daftar temperatur satu populasi
				double totalTemp = 0;
				int counter = 0;
				Solution2 current = solutions[id];//solusi untuk populasi tertentu
				//innerloop
				for (int k = 0; k < seq[q]; k++) {//keputusan penerimaan sesuai dengan deret
					Neighbor move = MethodsforHCALBSA.produceMove(id, cis[id], current, solutions);//fungsi tetangga
					double p = MethodsforHCALBSA.rand.nextDouble();//pilih angka random 0-1
					if (move.getDelta() < 0 || p < 1.0 / Math.exp(move.getDelta()/t)) {//Kriteria Metropolis
						current.update(move);//solusi x = solusi y
						if (current.getTourLength() < best.getTourLength()) {// apabila solusi x lebih baik dari solusi terbaik
							best.update(current);//update solusi terbaik
							best.setLastImproving(q);//tandai sebagai improvement
						}
						if ( move.getDelta() > 0) {//apabila solusi lebih buruk
							totalTemp += move.getDelta() / Math.log(1.0/p);//hitung temperatur untuk skema pendinginan
							counter++;//hitung berapa banyak solusi buruk diterima
						}
					}
					if (Simulations.getSamplingType() == ESelectionType.RANDOM) {
						cis[id] = MethodsforHCALBSA.rand.nextInt(cityNumber);
					} else if (Simulations.getSamplingType() == ESelectionType.SYSTEMATIC_SEQUENCE){
						cis[id] = (cis[id] + 1) % cityNumber;// Pemilihan kota secara sistematis
					} else {
						cis[id] = current.next(cis[id]);
					}
					
				}
				//update daftar temperatur
				if ( counter != 0) {
					tempLists[id].remove();//buang temperatur sekarang
					tempLists[id].offer( - totalTemp/counter);//masukkan temperatur baru
				} 
			}
		}
		//menyimpan hasil
		if (Simulations.SAVING_PROCESS_DATA) MethodsforHCALBSA.saveConvergenceData(temperatures, makespans, bestMakespans);
		return best;//solusi terbaik
	}

	/**
	 * Parameter solutions will not be changed in the method. 
	 * To improve the stability, part of the biggest and smallest temperatures will be discarded.
	 * 
	 * @param solutions
	 * @param LIST_LENGTH
	 * @return
	 */
	private static PriorityQueue<Double>[] produceTemperatureLists(final Solution2[] solutions, final int LIST_LENGTH) {
		//create initial temperature list for each SA
		final int TOP_BOTTOM = LIST_LENGTH;
		final int listLength = LIST_LENGTH + TOP_BOTTOM;//inialisasi panjang list = 2 x len
		@SuppressWarnings("unchecked")
		PriorityQueue<Double>[] tempLists= new PriorityQueue[solutions.length];
		for (int i = 0; i < tempLists.length; i++) {
			Solution2 s = new Solution2(solutions[i]);//ambil solusi awal (christofides) 
			PriorityQueue<Double> list = new PriorityQueue<Double>();//list prioritas
			while (list.size() < listLength) {
				Neighbor[] moves = s.findNeighbors();//cari solusi tetangga (y)
				Neighbor bestMove = moves[0];//inialisasi solusi terbaik
				for (Neighbor move : moves) {
					if (move != null && list.size() < listLength) {
						double t = move.delta;
						//System.out.println("t: "+t);
						list.offer( (t > 0)? -t : t);//ubah temperatur menjadi negatif dan masukkan kedalam list
					}
					if ( move != null && move.getDelta() < bestMove.getDelta()) {//Mencari calon kandidat terbaik
						bestMove = move;
					}
				}
				if (bestMove.getDelta() < 0) {//apabila solusi kandidat lebih baik dari solusi awal
					s.update(bestMove);//maka dijadikan solusi x
				}
			}

			//hapus temperatur bagian atas
			for (int idx = 0; idx < TOP_BOTTOM / 2; idx++) {
				list.poll();
			}

			
			//move the remained first LIST_LENGTH elements into tempLists[i]
			tempLists[i] = new PriorityQueue<Double>();
			//menghapus bagian bawah sekalian memasukkannya kedalam tempList
			while ( tempLists[i].size() < LIST_LENGTH && !list.isEmpty()) {
				tempLists[i].offer(list.poll());
			}

		}
		return tempLists;
	}
	
	private static Neighbor produceMove(int ID, int ci, Solution2 current, Solution2[] s ) {
		int[][] nearCityList = Problems.getProblem().getNearCityList();// Mendapatkan daftar kota terdekat
		int cityNumber = current.getCityNumber();//banyak kota
		Neighbor move = null;

		int nci = current.next(ci);//Kota berikutnya dalam tour dari kota `ci`
		int pci = current.previous(ci);	// Kota sebelumnya dalam tur dari kota `ci`
		int cj = ci;// Inisialisasi variabel `cj` dengan nilai kota awal `ci`
		if ((Simulations.getKnowledgeType() == EKnowledgeType.SEARCH || Simulations.getKnowledgeType() == EKnowledgeType.PROBLEM_SEARCH)) {
			int anotherPos = ID;// Inisialisasi posisi solusi lain dengan Populasi saat ini
			while (anotherPos == ID && s.length > 1) {
				// Memastikan `anotherPos` tidak sama dengan populasi solusi saat ini, jika ada lebih dari 1 solusi
				anotherPos = MethodsforHCALBSA.rand.nextInt(s.length);
			}
			Solution2 another = s[anotherPos];// Mendapatkan solusi lain berdasarkan indeks yang dipilih

			if (another != null) {// Pastikan solusi lain tidak null
				cj = another.next(ci);// Pilih kota berikutnya dalam tur solusi lain
				if ( nci != cj && pci != cj && ci != cj ) {
					// Jika kota berikutnya tidak sama dengan kota setelah atau sebelum `ci` dalam tour saat ini, dan tidak sama dengan `ci`
					MethodsforHCALBSA.histCount++;// Tingkatkan penghitung historis
				} else {
					cj = another.previous(ci);// Pilih kota sebelumnya dalam tour solusi lain
					if ( nci != cj && pci != cj && ci != cj ) {
						MethodsforHCALBSA.histCount++;// Tingkatkan penghitung historis
					}
				}
			}
		}

		if ( Simulations.getKnowledgeType() == EKnowledgeType.PROBLEM || Simulations.getKnowledgeType() == EKnowledgeType.PROBLEM_SEARCH) {
			// Menggunakan daftar kota terdekat (nearCityList) untuk memilih kota secara acak
			if ( nci == cj || pci == cj || ci == cj ) {
				// Jika kota yang dipilih saat ini (`cj`) sama dengan kota berikutnya (`nci`), kota sebelumnya (`pci`), atau kota saat ini (`ci`)
				int m = MethodsforHCALBSA.rand.nextInt(nearCityList[ci].length); // Pilih indeks secara acak dari daftar kota terdekat untuk kota `ci`
				while ( nearCityList[ci][m] == nci || nearCityList[ci][m] == pci) {
					// Selama kota yang dipilih (`nearCityList[ci][m]`) sama dengan kota berikutnya (`nci`) atau kota sebelumnya (`pci`)
					m = MethodsforHCALBSA.rand.nextInt(nearCityList[ci].length);
					 // Pilih kembali indeks secara acak
				}
				cj = nearCityList[ci][m];
				// Tetapkan kota terpilih dari daftar kota terdekat sebagai nilai `cj`
				MethodsforHCALBSA.probCount++;
				 // Tingkatkan penghitung probabilitas untuk mencatat jumlah pemilihan berdasarkan daftar kota terdekat
			}
		} 

		if ( Simulations.getKnowledgeType() == EKnowledgeType.NONE || Simulations.getKnowledgeType() == EKnowledgeType.SEARCH) {

			if ( nci == cj || pci == cj || ci == cj ) {
				while ( nci == cj || pci == cj || ci == cj ) {
					cj  = MethodsforHCALBSA.rand.nextInt(cityNumber);
				} 
				MethodsforHCALBSA.randCount++;
			}
		}
		
		//maka solusi didapat akan dijadikan kandidat solusi
		move =current.findNeighbor(ci, cj);
		return move;
	}


	private static double averageTourLength(Solution2[] ss) {//rata-rata panjang tour
		double tl = 0;
		for (Solution2 s : ss) {
			tl += s.getTourLength();
		}
		return tl / ss.length;
	}
	
	private static double averageTemperatur(Queue<Double>[] qs) {//rata-rata temperatur
		double t = 0;
		for (Queue<Double> q : qs) {
			t += q.peek();
		}
		return -t / qs.length;
	}

	private static void saveConvergenceData( double[] ts, double[] vs, double[] bs) {//menyimpan data hasil
		try {
			String f = Problems.getFileName();//nama file
			File file = new File(f);
			//membuat nama file sesuai dengan file path yang akan disimpan
			f = (new File("")).getAbsolutePath() + "\\results\\" + file.getName();
			f += " " + Simulations.getParaSetting() + " convergence process by list-based SA for TSP results.csv";

			System.out.println(f);
			PrintWriter printWriter = new PrintWriter(new FileWriter(f));//print hasil
			for (int idx=0; idx<ts.length; idx++) {
				printWriter.println(ts[idx] + "," + vs[idx] + "," + bs[idx]);
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();//apabila terjadi error
		}
	}

	private static int[] produceConstantSequence(int x, int z) {
		int[] seq = new int[z];
		for (int i=0; i<z; i++) {
			seq[i] = x;
		}
		return seq;
	}

	private static int[] produceArithmeticSequence(int x, int y, int m, int z) {
		//x=nilai awal
		//y=nilai maksimum
		//m= letak elemen menaik
		//z=total panjang elemen
		int[] seq = new int[z];// Inisialisasi array `seq` dengan panjang `z` untuk menyimpan hasil urutan aritmatika.
		double value = x;// Inisialisasi nilai awal dari urutan aritmatika, dimulai dari `x`
		double step; // Variabel untuk menyimpan langkah (beda) antara setiap elemen dalam urutan aritmatika.
		if (m > 0) {
			// Hitung langkah (beda) untuk elemen pertama hingga elemen ke-`m`:
		    step = (2*y - 2*x)*1.0/m;
		    for (int i=0; i<m; i++) {
			    seq[i] = (int)(value+0.5);//pembulatan
			    value += step;// Tambahkan `step` ke nilai saat ini untuk mendapatkan elemen berikutnya.
		    }
		} else {
			value = y + x;
		}
		step = (2*y - 2*x)*1.0/(z-m);// Hitung langkah (beda) untuk elemen sisanya (dari indeks `m` hingga `z`).
		for (int i=m; i < seq.length; i++) {
			seq[i] = (int)(value+0.5);//pembulatan
			value -= step;// Kurangi `step` dari nilai saat ini untuk mendapatkan elemen berikutnya.
		}
		return seq;
	}

	private static Random rand = new Random();

	public static int histCount;
	public static int probCount;
	public static int randCount;


	public static void main(String[] args) {
		final int TIMES = 25;
		String fileName = (new File("D:\\DOWNLOAD\\ELBSA\\ELBSA4TSP-master\\data")).getAbsolutePath() + "\\TSPLarge26\\01dsj1000.txt";
		Problems.setFileName(fileName);
		Solution2 s;
		double tourLength = 0;
		long startTime = System.nanoTime();
		for (int i = 0; i < TIMES; i++) {
			s = MethodsforHCALBSA.listBasedSA(1000, 30);
			System.out.println(i + "-:" + s.getTourLength());
			tourLength += s.getTourLength();
		}
		long endTime = System.nanoTime();
		tourLength /= TIMES;
		double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Waktu eksekusi: " + durationInSeconds + " detik");
		System.out.println("Average: " + tourLength);
		//int[] aMCL = MethodsforHCALBSA.produceArithmeticSequence(100/2, 100, 0, 1000);
		//for (int m : aMCL) {
		//	System.out.println(m);
		//}
	}
}
