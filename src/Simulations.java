import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Simulations {

	public static void main(String[] args) {
		Problems.setNearCityParameters(Simulations.nearCityNumber);
		String filePath = (new File("D:/DOWNLOAD/ELBSA/ELBSA4TSP-master/")).getAbsolutePath() + "/data/TSPLarge26/"; 
		//Untuk filepath berada
		if (Simulations.TEST_TYPE == ETestType.SINGLE_INSTANCE) {
			filePath = (new File("D:/DOWNLOAD/ELBSA/ELBSA4TSP-master/")).getAbsolutePath() + "/data/TSPLarge26/";
			String fileName = filePath+"01dsj1000.txt";
			System.out.println("\nTEST 1:");
			testSingleInstance(fileName);
			//Untuk test satu buah dataset
		} else if (Simulations.TEST_TYPE == ETestType.MULTIPLE_INSTANCE) {
			System.out.println("\nTEST 2:");
			testPerformance(filePath);
			//Untuk test lebih dari satu buah dataset
		} else if (Simulations.TEST_TYPE == ETestType.PARAMETER_TUNING) {
			System.out.println("\nTEST 5:");
			parametersTunning(filePath);
			//test VMCL dan daftar temperatur yang tepat dalam menentukan hasil
		}
	}
	
	
	private static void parametersTunning(String filePath) {
		java.io.File dir = new java.io.File(filePath);//Membaca filepath yang dimasukkan
		java.io.File[] files = dir.listFiles();//list file pada filepath
		String fileName = (new File("D:/DOWNLOAD/ELBSA/ELBSA4TSP-master/")).getAbsolutePath() + "\\results\\Parameters\\";
		Simulations.selectionType = ESelectionType.RANDOM;//seleksi secara random
		Simulations.selectionType = ESelectionType.SYSTEMATIC_SEQUENCE;//seleksi secara sistematis
		fileName += "list-based SA-" + Simulations.selectionType + " parameter tunning results for 001 instances.csv";
		
		//System.out.println(dir.exists());
		List<double[]> resultsList = new ArrayList<>();
		List<Double> paras = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			//5 list length sebagai percobaan
			double scale = 110 + 10 * i;//(110-150)
			paras.add(scale);
		}
		double[] poses = new double[]{0.25,0.375,0.5};//tiga posisi VMCL
		for (java.io.File file : files) {
		    Problems.setFileName(file.getAbsolutePath());
		    if (Problems.getProblem().getCityNumber() > 10000) {
		    	continue;
		    }
		    //setPopulationSize();
			for (double para : paras) {
				Simulations.listLength= (int)para;// list length yang dibuatpada ke simulasi
				for (double pos :poses) {//posisi VMCL
					Simulations.listArithmeticPosition = pos;
					System.out.println(file.getName() + ", list length--" + Simulations.listLength);
					double[] rs = runSA(Simulations.MAX_GENERATION, Simulations.populationSize, Simulations.TIMES);//Jalankan HCA-LBSA
					for (double r : rs) {
						System.out.print(r + "\t");
					}

					System.out.println();
					resultsList.add(rs);

					Simulations.saveParametersTunningResults(fileName, paras, resultsList);//simpan hasil
				}
			}
		}
	}
	
	private static double[] testSingleInstance(String fileName) {
		Problems.setFileName(fileName);
		//menjalankan HCA-LBSA
		double[] results = runSA(Simulations.MAX_GENERATION, Simulations.populationSize, Simulations.TIMES);
		for (double d : results) {//print hasil
			System.out.print(d + "\t");
		}
		System.out.println();
		return results;
	}

	private static double[] testPerformance(String filePath) {
		java.io.File dir = new java.io.File(filePath);//membaca file pada filepath
		java.io.File[] files = dir.listFiles();//list file yang ada pada folder yang diarahkan oleh filepath
		String pathName = filePath.substring(filePath.lastIndexOf("/", filePath.length()-2)).substring(1);
		pathName = pathName.substring(0, pathName.length()-1);//membaca file satu persatu
		String fileName = (new File("D:/DOWNLOAD/ELBSA/ELBSA4TSP-master/")).getAbsolutePath() + "\\results\\Performance\\";
		fileName += pathName + "-" + Simulations.getParaSetting() + " results 100000-2.csv";//memberi nama file untuk disimpan hasilnya
		System.out.println("pathName: " + pathName);
        System.out.println("fileName2: " + fileName);
		List<double[]> resultList = new ArrayList<>();
		List<String> fileList = new ArrayList<>();
		for (java.io.File file : files) {//menjalankan satu persatu file
			
			Problems.setFileName(file.getAbsolutePath());
			System.out.println(file.getName());

			setPopulationSize();//batasan sesuai dengan penelitian

			double[] rs = runSA(Simulations.MAX_GENERATION, Simulations.populationSize, Simulations.TIMES);//menjalankan SA
			resultList.add(rs);//memasukkan ke dalam list hasil yang didapat
			fileList.add(file.getName());

			for (double d : rs) {
				System.out.print(d+"\t");
			}
			System.out.println();
			
			Simulations.saveFinalResults(fileName, fileList, resultList);//menyimpan hasil
		}

		
		double[] totals = new double[resultList.get(0).length];
		
		for (int i = 0; i < files.length; i++) {
			System.out.println();
			System.out.print(files[i].getName()+"\t");
			double[] datas = resultList.get(i);
			for (int j = 0; j < datas.length; j++) {
				System.out.print(datas[j]+"\t");
				totals[j] += datas[j];
			}
		}
		System.out.println("\t");
		for (int j = 0; j < totals.length; j++) {
			totals[j] = Math.round(totals[j]/files.length*1000)/1000.0;
			System.out.print(totals[j]+"\t");
		}
		return totals; //rata-rata data dari semua file
	}
	
	private static void setPopulationSize() {// akan digunakan jumlah populasi tertentu sesuai dengan banyak kota
		int cityNumber = Problems.getProblem().getCityNumber();// hal ini berguna untuk efisiensi waktu
		if ( cityNumber < 1000) {
			Simulations.populationSize = 50;
		}	else if ( cityNumber < 2000) {
			Simulations.populationSize = 30;
		} else if ( cityNumber < 4000) {
			Simulations.populationSize = 20;
		} else if ( cityNumber < 50000) {
			Simulations.populationSize = 10;
		} else {
			if (Simulations.knowledgeType == EKnowledgeType.SEARCH &&
					Simulations.selectionType == ESelectionType.RANDOM &&
					Simulations.sequenceType == ESequenceType.CONSTANT) {
				Simulations.populationSize = 5;//5 for LBSA
			} else {
			    Simulations.populationSize = 3;//3 for ELBSA
			}
		}
	}
	
	private static void saveFinalResults(String fileName, List<String> fileList, List<double[]> resultList) {
		if ( !Simulations.SAVING_FINAL_RESULTS) {//logika apabila tidak mau disimpan
			return;
		}
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));//menulis file agar tersimpan pada filepath
			for (int i = 0; i < fileList.size(); i++) {
				printWriter.println();
				printWriter.print(fileList.get(i));
				for (double data : resultList.get(i)) {
					printWriter.print("," + data);
				}
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static double[] runSA(final int MAX_GENERATION, final int POPULATION_SIZE, final int TIMES) {
		double duration = (new java.util.Date()).getTime();
		double bMakespan = Problems.getProblem().getBestTourLength();//mengambil solusi optimal dari dataset sesuai dengan TSPLIB
		Solution2 s = null;
		Solution2 bs = null;
		double[] makespans = new double[TIMES];//solusi yang didapat
		int[] iterations = new int[TIMES]; //iterasi 
		System.out.println(Simulations.getParaSetting());//memberitahu apa yang sedang dijalankan
		for (int i = 0; i < TIMES; i++) {
			s = MethodsforHCALBSA.listBasedSA(MAX_GENERATION, POPULATION_SIZE );//menjalankan HCA-LBSA
			makespans[i] = s.getTourLength();
			iterations[i] = s.getLastImproving();
			if (Simulations.OUT_INDIVIDUAL_RUNNING_DATA) {
				System.out.println( i + " -- " + makespans[i] + "," + iterations[i]);//print hasil sementara
			}
			if (bs == null || bs.getTourLength() < s.getTourLength()) {
				bs = s;//hasil menghitung terbaik didapat selama proses
			}
		}
		
		if (Simulations.SAVING_FINAL_TOUR) {
			Simulations.saveTour(bs);//menyimpan hasil terbaik dalam file csv
		}
		
		duration = (new java.util.Date()).getTime()-duration;
		duration /= TIMES;
		duration = Math.round(duration/1000*1000)/1000.0;

		double min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, count = 0;
		double total = 0;
		double totalIterations = 0;
		for (int i = 0; i < makespans.length; i++) {
			double mk = makespans[i];
			total += mk;
			if ( (mk-bMakespan) * (1.0/bMakespan) *100 < 1) {
				count++;
			}
			if ( mk < min) {
				min = mk;
			}
			if (mk > max) {
				max = mk;
			}
			totalIterations += iterations[i];
		}
		double ave = total / TIMES;//rata-rata
		double median = DataStatisticalUtils.getMedian(makespans);//mencari median
		double STD = DataStatisticalUtils.getStandardDevition(makespans);//standar deviasi
	
		double bpd = Math.round((min-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;//PE (percentage error of the best solution)
		double wpd = Math.round((max-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;//PEw (percentage error of the worst solution)
		double apd = Math.round((ave-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;//PEav (Percentage Error of the average solution)
		double mpd = Math.round((median-bMakespan) * (1.0/bMakespan) *100*1000)/1000.0;//PEme (Percentage Error of the median solution)
		double itr = Math.round(totalIterations/iterations.length*10)/10; //Average last improving iteration
		double[] stat =  new double[] {bMakespan, min, max, ave, median, STD, bpd, wpd, apd, mpd, count, itr, duration};
	
		double[] results = new double[stat.length + makespans.length];
		System.arraycopy(stat, 0, results, 0, stat.length);
		System.arraycopy(makespans, 0, results, stat.length, makespans.length);
		return results;
	}
	
	
	private static void saveParametersTunningResults(String fileName, List<Double> paras, List<double[]> resultsList) {
		if (!Simulations.SAVING_PARA_TUNNING) { return;	}//jika tidak mau disave
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));//membuat file secara automatis
			for (int idx = 0; idx < resultsList.size(); idx++) {
				double[] rs = resultsList.get(idx);
				printWriter.println();
				printWriter.print(paras.get(idx % paras.size()));
				for (int j = 0; j < rs.length; j++) {
					printWriter.print(","+rs[j]);
				}
			}
			for (int idx = 0; idx < resultsList.size(); idx++) {
				double[] rs = resultsList.get(idx);
				if (idx % 2 == 0) {
				    printWriter.println();
				} 
				printWriter.print(","+rs[8]);
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void saveTour( Solution2 s) {//menyimpan tour sesuai dengan koordinatnya kedalam file dalam bentuk CSV
		File file = new File(Problems.getFileName());
		//nama file
		String fileName = (new File("D:/DOWNLOAD/ELBSA/ELBSA4TSP-master/")).getAbsolutePath() + "\\results\\" + file.getName();
		System.out.println("filename :"+fileName);
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName + "-tour.csv"));//tour
			int city = 0;
			printWriter.println(city);
			int nextCity = s.next(city);
			while (nextCity != city) {
				printWriter.println(nextCity);
				nextCity = s.next(nextCity);
			}
			printWriter.close();
			
			printWriter = new PrintWriter(new FileWriter(fileName + "-position.csv"));//koordinat/posisi kota
			double[][] position = Problems.getProblem().getCityPosition();
			for (int idx = 0; idx < position.length; idx++) {
				printWriter.println(position[idx][0] + "," + position[idx][1]);
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean isSavingFinalResults() { return Simulations.SAVING_FINAL_RESULTS;}
	public static boolean isSavingProcessData() { return Simulations.SAVING_PROCESS_DATA;}
	public static String getParaSetting() {//settingan nama file
		String str = "list-based SA-" + Simulations.knowledgeType +"-" + Simulations.selectionType + "-";
		str += Simulations.sequenceType;
		if (Simulations.sequenceType == ESequenceType.ARIHMETIC) {
			str += "-" + Simulations.listArithmeticPosition + "-" + Simulations.LIST_ARITHMETIC_STRENGTH;
		}
		str += " LS=" + listLength + " NCN=" + Simulations.nearCityNumber;
		str += " G=" + Simulations.MAX_GENERATION + "-P=" + Simulations.populationSize;
		str += (Problems.USE_INTEGER_EDGE)? "-Int" : "-Float";
		return str;
	}
	
	
	public static EKnowledgeType getKnowledgeType() { return Simulations.knowledgeType; }
	public static ESelectionType getSamplingType() { return Simulations.selectionType; }
	public static ESequenceType getSequenceType() { return Simulations.sequenceType; }
	public static double getListArithmeticPosition() { return Simulations.listArithmeticPosition; }
	public static int getListLength() { return Simulations.listLength; }
	public static int getMaxInsertBlockSize() { return Simulations.maxInsertBlockSize; }
	public static double getListArithmeticStrength() { return Simulations.LIST_ARITHMETIC_STRENGTH; }
	
	private static EKnowledgeType knowledgeType = EKnowledgeType.PROBLEM_SEARCH;
	private static ESelectionType selectionType = ESelectionType.SYSTEMATIC_SEQUENCE;
	private static ESequenceType sequenceType = ESequenceType.ARIHMETIC;//.CONSTANT;
	
	public static final int MAX_GENERATION = 1000;//banyak outter-loop
	public static final int TIMES = 25;//banyak HCA-LBSA dijalankan
	public static int populationSize = 30;//populasi

	public static final boolean OUT_INDIVIDUAL_RUNNING_DATA = true;
	public static final boolean SAVING_PROCESS_DATA = false;
	public static final boolean SAVING_FINAL_RESULTS = true;
	public static final boolean SAVING_PARA_TUNNING = true;
	public static final boolean SAVING_FINAL_TOUR = true;
	public static final boolean USE_GREEDY_RANDOM_STRATEGY = true;
	public static final boolean USE_CHRISTOFIDES = true;
	public static final ETestType TEST_TYPE = ETestType.PARAMETER_TUNING;
	
	//parameters for list-based SA algorithm 
	public static final double LIST_ARITHMETIC_STRENGTH = 0.5;
	private static double listArithmeticPosition = 0.5;//0.5
	private static int listLength = 140; //150 for large TSP instances�� 120 for LBSA
	private static int maxInsertBlockSize = 10;
	private static int nearCityNumber = 20;
}