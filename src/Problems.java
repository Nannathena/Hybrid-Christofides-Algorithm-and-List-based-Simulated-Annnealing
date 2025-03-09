import java.io.FileReader;
import java.util.Scanner;


public class Problems {
	//to create a problem 
	private Problems(String filename)  {
		FileReader data;//membaca file txt
		Scanner scan;// Membaca dari FileReader
		int problemType=0;//inialisasi masalah 
		try {
			data = new FileReader(filename);
			scan = new Scanner(data);
			//mengambil angka pada file: Apabila 1--- maka akan dianggap sebagai TSP Simetris
			problemType = scan.nextInt();
			if (problemType == Problems.SYMMETRIC || problemType == Problems.SYMMETRIC_GEO) {
				readSymmetricData(scan);//membaca data yang simetris
				isSymmetric = true;
				if (problemType == Problems.SYMMETRIC) {
				    calcuDistance();
				} else {
					calcuGeoDistance();
				}
			} else if (problemType == Problems.ASYMMETRIC){
				readAsymmetricData(scan);
				isSymmetric = false;
			}else if (problemType == Problems.ASYMMETRIC_UPP){
				readAsymmetricUppData(scan);
				isSymmetric = false;
			}
			//nearCityNumber = cityNumber;
			scan.close();
			data.close();
			//Menutup file
		} catch (Exception ex) {
			System.out.println(ex);//apabila error
		}
		this.setupNearCityList();
	}

	private void readSymmetricData(Scanner scan) throws Exception {
		cityNumber = scan.nextInt();//mengambil banyak kota
		bestTourLength = scan.nextInt();//mengambil panjang terbaik
		cityPosition = new double[cityNumber][2];//posisi kota
		try {
			cityDistance = new float[cityNumber][cityNumber];//panjangan kota
			//dCityDistance = new double[cityNumber][cityNumber];
		} catch ( Throwable ex) {
			cityDistance = null;
			System.out.println(ex.getMessage()+", City Number:" + cityNumber);//apabila error
		}
		for (int i=0; i<cityNumber;i++) {
			scan.nextInt();//untuk skip kota
			//the x position
			cityPosition[i][0] = (scan.nextDouble());//scan.nextInt();
			//the y position
			cityPosition[i][1] = (scan.nextDouble());//scan.nextInt();
		}
		
		//membaca tour terbaik
		if (scan.hasNextInt()) {
			bestTour = new int[cityNumber];// Membuat array bestTour dengan ukuran cityNumber
			for (int i = 0; i<cityNumber; i++) {
				if (scan.hasNextInt()) 
					bestTour[i] = scan.nextInt() - 1;// Mengambil nilai city ID dan mengurangi 1 (karena ID kota dimulai dari 1)
				else {
					bestTour = null;// Jika tidak ada angka berikutnya, set bestTour menjadi null
					break;
				}
			}
		} else {
			bestTour = null;// Jika tidak ada integer yang ditemukan pada awalnya, set bestTour menjadi null
		}
	}

	//tidak perlu
	private void readAsymmetricData(Scanner scan) throws Exception {
		cityNumber = scan.nextInt();
		bestTourLength = scan.nextInt();
		cityPosition = new double[cityNumber][2];
		cityDistance = new float[cityNumber][cityNumber];
		for (int i=0; i<cityNumber;i++) {
			//System.out.println("city"+i);
			for (int j=0; j<cityNumber; j++) {
				cityDistance[i][j] = scan.nextInt();
			}
			if (fileName.contains("ftv90") || fileName.contains("ftv1")) {
				int skipLength = 171 - cityNumber;
				while (skipLength-->0) {
					scan.nextInt();
				}
			}
			
		}
	}
	
	//tidak perlu
	private void readAsymmetricUppData(Scanner scan) throws Exception {
		cityNumber = scan.nextInt();
		bestTourLength = scan.nextDouble();
		cityPosition = new double[cityNumber][2];
		cityDistance = new float[cityNumber][cityNumber];
		for (int i=0; i<cityNumber;i++) {
			//System.out.println("city"+i);
			for (int j = 0; j < i; j++){
				cityDistance[i][j] = cityDistance[j][i];
			}
			for (int j=i; j<cityNumber; j++) {
				cityDistance[i][j] = scan.nextInt();
			}
		}
	}

	//to calculate the distance between cities
	public void calcuDistance() {
		for (int i=0; i<cityNumber;i++) {
			for (int j=0; j<cityNumber;j++) {
				if (i==j) {
					if (cityDistance != null) {
						cityDistance[i][j]=Integer.MAX_VALUE;// Jika i == j (sama), set jarak menjadi sangat besar
					}
				} else {
					double distance;
					distance = (cityPosition[i][0]-cityPosition[j][0]);// Menghitung selisih koordinat X
					distance *= distance;
					distance += (cityPosition[i][1]-cityPosition[j][1])*(cityPosition[i][1]-cityPosition[j][1]); // Menghitung selisih koordinat Y
					if (fileName.contains("att") ) {
						distance = Math.ceil(Math.sqrt(distance/10.0));// Jika nama file mengandung "att", normalisasi jarak
					} else {
						distance = Math.sqrt(distance);// Jika tidak, hitung jarak Euclidean biasa
					}
					if (cityDistance != null) {
						if (Problems.USE_INTEGER_EDGE) {
							cityDistance[i][j] = (int)(distance + 0.5);// Jika menggunakan integer, pembulatan ke integer terdekat
						} else {
							cityDistance[i][j] = (float)distance;// Jika menggunakan float, simpan jarak sebagai float
						}
					}
				}
			}
		}
	}
	
	//tidak perlu
	public void calcuGeoDistance() {
		for (int i=0; i<cityNumber;i++) {
			for (int j=0; j<cityNumber;j++) {
				if (i==j) {
					if (cityDistance != null) {
						cityDistance[i][j]=Integer.MAX_VALUE;
					}
				} else {
					long deg;
					double NaLatitude, NaLongitude, NbLatitude, NbLongitude, minu, q1, q2, q3;
					deg = (long)cityPosition[i][0];
					minu = cityPosition[i][0] - deg;
					NaLatitude = Math.PI * (deg + 5.0 * minu / 3.0) / 180.0;
					deg = (long)cityPosition[i][1];
					minu = cityPosition[i][1] - deg;
					NaLongitude = Math.PI * (deg + 5.0 * minu / 3.0) / 180.0;
					deg = (long)cityPosition[j][0];
					minu = cityPosition[j][0] - deg;
					NbLatitude = Math.PI * (deg + 5.0 * minu / 3.0) / 180.0;
					deg = (long)cityPosition[j][1];
					minu = cityPosition[j][1] - deg;
					NbLongitude = Math.PI * (deg + 5.0 * minu / 3.0) / 180.0;
					q1 = Math.cos(NaLongitude - NbLongitude);
					q2 = Math.cos(NaLatitude - NbLatitude);
					q3 = Math.cos(NaLatitude + NbLatitude);
					double distance = 6378.388 * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0;
					if (cityDistance != null) {
						if (Problems.USE_INTEGER_EDGE) {
							cityDistance[i][j] = (int)(distance + 0.5); 
						} else {
							cityDistance[i][j] = (float)distance;
						}
					}
				}
			}
		}
	}


	//untuk output posisi
	public void outputPosition() {
		for (int i=0; i<cityNumber; i++) {
			System.out.print(i+1);
			System.out.print(':');
			System.out.print(cityPosition[i][0]);
			System.out.print('-');
			System.out.print(cityPosition[i][1]);
			System.out.println();
		}
	}
	
	//output jarak antar kota
	public void outputDistance() {
		for (int i=0; i<cityNumber; i++) {
			System.out.print(i+1);
			System.out.print(':');
			for (int j=0; j<cityNumber; j++) {
				System.out.print(getEdge(i,j));
				System.out.print('-');
			}
			System.out.println();
		}
	}
	
	//clear
	private void setupNearCityList( ) {
		nearCityList = new int[cityNumber][];// Membuat array 2D dengan jumlah baris sebanyak cityNumber
		for (int i = 0; i < cityNumber; i++) {
			nearCityList[i] = setupNearCityList(i, nearCityNumber);// Mengisi setiap elemen array dengan daftar kota terdekat
		}
	}
	
	//clear
	//Kota 0 terdekat: [1, 2, 3]
	//Kota 1 terdekat: [0, 2, 3]
	//Kota 2 terdekat: [1, 0, 3]
	//Kota 3 terdekat: [0, 1, 2]
	//Kota 4 terdekat: [1, 2, 3]
	private int[] setupNearCityList( int city, int nearCityNumber) {
		int[] cityList = new int[nearCityNumber];// Menyimpan daftar kota terdekat
		int[] d = new int[cityNumber];// Menyimpan jarak antara kota yang sedang diproses dengan kota lainnya
		//menghitung jarak antar kota i dengan lainnya
		for (int j=0; j< cityNumber; j++) {
			if (j != city) {
				d[j] = (int) getEdge(city,j);// Menghitung jarak dari kota pada sekarang ke kota j
			} else {
				d[j] = Integer.MAX_VALUE;// Menghindari kota itu sendiri (menetapkan jarak ke kota itu sangat jauh)
			}
		}
		// Mencari kota-kota terdekat dari kota sekarang
		for (int j = 0; j < nearCityNumber; j++) {
			int index = 0;
			for (int k = 0; k < cityNumber ; k++) {
				if (d[index] > d[k] ) {// Menemukan kota dengan jarak terkecil
					index = k;
				}
			}
			cityList[j] = index;// Menambahkan kota yang ditemukan ke dalam daftar kota terdekat
			d[index] = Integer.MAX_VALUE;// Menandai kota yang sudah dipilih agar tidak dipilih lagi
		}
		return cityList;
	}
	
	
	public double evaluate(Solution solution) {
		return evaluate(solution.getTour());
		
	}
	
	public double evaluate(Solution2 solution) {
		return evaluate(solution.getTour());
		
	}
	
	public double evaluate(int[] tour) {
		double tourLength = 0;// Inisialisasi panjang perjalanan
		boolean[] visited = new boolean[tour.length];// Array untuk menandai kota yang sudah dikunjungi
		 // Menghitung panjang perjalanan berdasarkan urutan kota dalam tour
		for (int i=0; i<=tour.length-1; i++) {
			if (visited[tour[i]]) {
				System.out.println("Wrong Solution");// Jika kota sudah dikunjungi sebelumnya, output pesan kesalahan (tidak hamilton)
			}
			tourLength += getEdge(i, tour[i]);// Menambahkan jarak dari kota ke kota berikutnya
			visited[tour[i]] = true;// Tandai kota yang telah dikunjungi
		}
		// Memeriksa apakah semua kota sudah dikunjungi
		for (int i=0; i<cityNumber; i++) {
			if (!visited[i]) {
				System.out.println("Wrong Solution"); // Jika ada kota yang belum dikunjungi, output pesan kesalahan (tidak hamilton)
			}
		}
		return tourLength;
	}
	
	
	public static Problems getProblem() {
		if (problem == null) {
			problem = new Problems(fileName);
		}
		return problem;
	}
	
	public static void setFileName(String fileName) {
		Problems.fileName = fileName;
		problem = new Problems(fileName);
	}

	public int getCityNumber() {
		return cityNumber;
	}
	public double[][] getCityPosition() {
		return cityPosition;
	}

	// Fungsi ini digunakan untuk mendapatkan jarak antara dua kota
	public double getEdge(int from, int to) {
		// Cek apakah jarak antar kota sudah dihitung sebelumnya dan disimpan dalam `cityDistance`
		if ( cityDistance != null) {
			return cityDistance[from][to];
		} else {
			double distance;
			// Jika jarak belum dihitung, hitung jarak menggunakan rumus Euclidean
			distance = (cityPosition[from][0]-cityPosition[to][0]);
			distance *= distance;
			distance += (cityPosition[from][1]-cityPosition[to][1])*(cityPosition[from][1]-cityPosition[to][1]);
			if ( Problems.USE_INTEGER_EDGE ) { // Jika `USE_INTEGER_EDGE` diset true, bulatkan jarak ke angka bulat terdekat
				return (int)(Math.round(Math.sqrt(distance))+0.5);
			} else {
				return (Math.sqrt(distance));
			}
		}
	}
	
	public double getBestTourLength() {	return bestTourLength;	}
	public static int getNearCityNumber() { return nearCityNumber;}
	public static String getFileName() { return Problems.fileName;}
	public boolean isSymmetric() {	return isSymmetric;	}

	public int[][] getNearCityList() { 
		if ( nearCityList == null) {
			this.setupNearCityList();
		}
		return nearCityList;	
	}

	public static void setNearCityParameters() {
		setNearCityParameters(nearCityNumber);
	}
	
	public static void setNearCityParameters(int nearCityNumber) {
		Problems.nearCityNumber = nearCityNumber;
		if ( problem != null) {
			problem.nearCityList = null;
		}
	}


	private static Problems problem = null;
	//the private data member of class TravelingSalesmanProblem
	private boolean isSymmetric=true;
	private int cityNumber;
	private static int nearCityNumber = 20;
	private double[][] cityPosition;
	private float[][] cityDistance;

	private int[][] nearCityList = null;
	
	private int[] bestTour;
	private double bestTourLength;

	public static final boolean USE_INTEGER_EDGE = true;
	public static final int SYMMETRIC = 1;
	public static final int ASYMMETRIC = 2;
	public static final int SYMMETRIC_GEO = 3;
	public static final int ASYMMETRIC_UPP = 4;

	private static String fileName = null;
}