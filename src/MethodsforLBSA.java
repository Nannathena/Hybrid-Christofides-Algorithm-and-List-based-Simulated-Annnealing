import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class MethodsforLBSA {
    /**
	 * Parallel List-based Simulated Annealing Algorithm for TSP.
	 * Each SA uses its temperature list independently.
	 * 
	 * @param MAX_G maximum generation
	 * @param POP_SIZE population size
	 * @return best solution found
	 */
    public static Solution2 LBSA(final int MAX_G, final int POP_SIZE) {
		Solution2[] solutions = new Solution2[POP_SIZE]; //clear
		int bestIdx = 0;//clear
		for (int i = 0; i < POP_SIZE; i++) {//clear
			solutions[i] = new Solution2(Simulations.USE_GREEDY_RANDOM_STRATEGY);//clear
			//System.out.println("Solusi ke-i: " + solutions[i].getTourLength());
			if (solutions[i].getTourLength() < solutions[bestIdx].getTourLength()) {//clear
				bestIdx = i;//clear
				//System.out.println("\n Solusi ke-i (Jika i = best): " + Solutions[i].getTourLength());
				//System.out.println("\n i (Jika i = best): " + i);
			}
		}
		Solution2 best = new Solution2(solutions[bestIdx]);//clear
		//System.out.println("\nbest: " + best.getTourLength());

		final int cityNumber = Problems.getProblem().getCityNumber();//clear
		//System.out.println("\nCity Number dan Markov Chain Length: " + cityNumber);
		final int MARKOV_CHAIN_LENGTH = cityNumber;//clear

        PriorityQueue<Double>[] tempLists = MethodsforLBSA.produceTemperatureLists_LBSA(solutions, Simulations.getListLength());

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
		int[] seq;
		if (Simulations.getSequenceType() == ESequenceType.CONSTANT) {
			seq = MethodsforLBSA.produceConstantSequence(MARKOV_CHAIN_LENGTH, MAX_G);
			//System.out.println("\nSequence Constant: " + Arrays.toString(seq));

		} else {
			seq = MethodsforLBSA.produceArithmeticSequence((int)(MARKOV_CHAIN_LENGTH * Simulations.getListArithmeticStrength()), 
					MARKOV_CHAIN_LENGTH, 
					(int)(MAX_G * Simulations.getListArithmeticPosition()), MAX_G);
		
                    
		}
		
		double[] temperatures = new double[MAX_G];
		double[] makespans = new double[MAX_G];
		double[] bestMakespans = new double[MAX_G];
		int[] cis = new int[POP_SIZE];
		// int outterloop=0;
		// int innerloop = 0;
		// int looploop=0;
		for (int q = 0; q < MAX_G; q++) {
			// outterloop++;
			temperatures[q] = MethodsforLBSA.averageTemperatur(tempLists);
			//cuma diambil prioritasnya saja
			//System.out.println("\nTemperatures: " + temperatures[q] + " Iterasi ke-"+q);
			makespans[q] = MethodsforLBSA.averageTourLength(solutions);
			//System.out.println("\nRata-rata panjang: " + makespans[q]);
			bestMakespans[q] = best.getTourLength();
			//System.out.println("\nbest spans: " + bestMakespans[q]);
			for (int id = 0; id < POP_SIZE; id++) {
				// innerloop++;
				double t = -tempLists[id].peek();
				//System.out.println("\nsuhu: " + t);
				double totalTemp = 0;
				int counter = 0;
				Solution2 current = solutions[id];
				for (int k = 0; k < seq[q]; k++) {
					// looploop++;
					Neighbor move = MethodsforLBSA.produceMove(id, cis[id], current, solutions);
					double p = MethodsforLBSA.rand.nextDouble();
					if (move.getDelta() < 0 || p < 1.0 / Math.exp(move.getDelta()/t)) {
						current.update(move);//sama
						if (current.getTourLength() < best.getTourLength()) {
							best.update(current);
							best.setLastImproving(q);
						}
						if ( move.getDelta() > 0) {
							totalTemp += move.getDelta() / Math.log(1.0/p);
							counter++;
						}
					}
					if (Simulations.getSamplingType() == ESelectionType.RANDOM) {
						cis[id] = MethodsforLBSA.rand.nextInt(cityNumber);
					} else if (Simulations.getSamplingType() == ESelectionType.SYSTEMATIC_SEQUENCE){
						cis[id] = (cis[id] + 1) % cityNumber;
					} else {
						cis[id] = current.next(cis[id]);
					}
					
				}
				//update temperature list
				if ( counter != 0) {
					tempLists[id].remove();
					tempLists[id].offer( - totalTemp/counter);
				} 
			}
			
		}
		// System.out.println("count innerloop: "+outterloop);
		// System.out.println("count innerloop: "+innerloop);
		// System.out.println("count looploop: "+looploop);

		if (Simulations.SAVING_PROCESS_DATA) MethodsforLBSA.saveConvergenceData(temperatures, makespans, bestMakespans);


		return best;
	}

    	/**
	 * Parameter solutions will not be changed in the method. 
	 * To improve the stability, part of the biggest and smallest temperatures will be discarded.
	 * 
	 * @param solutions
	 * @param LIST_LENGTH
	 * @return
	 */
	private static PriorityQueue<Double>[] produceTemperatureLists_LBSA(final Solution2[] solutions, final int LIST_LENGTH) {
		//create initial temperature list for each SA
		//final int TOP_BOTTOM = LIST_LENGTH;//tidak butuh
		final int listLength = LIST_LENGTH; //+ TOP_BOTTOM;//tidak butuh
		@SuppressWarnings("unchecked")
		PriorityQueue<Double>[] tempLists= new PriorityQueue[solutions.length];
		// int count=0;
		for (int i = 0; i < tempLists.length; i++) {
			Solution2 s = new Solution2(solutions[i]);
			PriorityQueue<Double> list = new PriorityQueue<Double>();
			//System.out.println("count outterloop: "+i);
			while (list.size() < listLength) {
				Neighbor[] moves = s.findNeighbors();
                double p = rand.nextDouble();
				Neighbor bestMove = moves[0];
				//System.out.println("count inner_loop: "+list.size());
                //System.out.println("bestmove.getdelta1: "+bestMove.getDelta());
				
				for (Neighbor move : moves) {
                    // System.out.println("move.getdelta: "+move.getDelta());
					//count++;
					//System.out.println("count MCL: "+count);
					if (move != null && list.size() < listLength) {
						//double t = move.delta;
                        double t = Math.abs(move.getDelta()) / Math.log(1.0/p);
                        //System.out.println("t: "+t);
						// count++;
						list.offer(-t);
					}
					if ( move != null && move.getDelta() < bestMove.getDelta()) {
						bestMove = move;
                        //System.out.println("bestmove.getdelta2: "+bestMove.getDelta());
                        //System.out.println("\n");
					}
				}
                
				if (bestMove.getDelta() < 0) {
                    //System.out.println("bestmove.getdelta: "+bestMove.getDelta());
					s.update(bestMove);
				} 
			}
			//remove the top TOP_BOTTOM/2 elements
			//for (int idx = 0; idx < TOP_BOTTOM / 2; idx++) {
			//	list.poll();
			//}
			//move the remained first LIST_LENGTH elements into tempLists[i]
			tempLists[i] = new PriorityQueue<Double>();
			while ( tempLists[i].size() < LIST_LENGTH && !list.isEmpty()) {
				tempLists[i].offer(list.poll());
			}
		}
		// System.out.println("moves: "+count);//15
        //  System.out.println("templist.length: "+tempLists.length);//15
        //  System.out.println("list length: "+LIST_LENGTH);//150
		return tempLists;
	}

	
	private static Neighbor produceMove(int ID, int ci, Solution2 current, Solution2[] s ) {
		int[][] nearCityList = Problems.getProblem().getNearCityList();
		int cityNumber = current.getCityNumber();
		Neighbor move = null;

		int nci = current.next(ci);
		int pci = current.previous(ci);	
		int cj = ci;
		if ((Simulations.getKnowledgeType() == EKnowledgeType.SEARCH || Simulations.getKnowledgeType() == EKnowledgeType.PROBLEM_SEARCH)) {
			int anotherPos = ID;	   
			while (anotherPos == ID && s.length > 1) {
				anotherPos = MethodsforLBSA.rand.nextInt(s.length);
			}
			Solution2 another = s[anotherPos];

			if (another != null) {
				cj = another.next(ci);
				if ( nci != cj && pci != cj && ci != cj ) {
					MethodsforLBSA.histCount++;
				} else {
					cj = another.previous(ci);
					if ( nci != cj && pci != cj && ci != cj ) {
						MethodsforLBSA.histCount++;
					}
				}
			}
		}

		if ( Simulations.getKnowledgeType() == EKnowledgeType.PROBLEM || Simulations.getKnowledgeType() == EKnowledgeType.PROBLEM_SEARCH) {
			//using near city list to select a city randomly
			if ( nci == cj || pci == cj || ci == cj ) {
				int m = MethodsforLBSA.rand.nextInt(nearCityList[ci].length);
				while ( nearCityList[ci][m] == nci || nearCityList[ci][m] == pci) {
					m = MethodsforLBSA.rand.nextInt(nearCityList[ci].length);
				}
				cj = nearCityList[ci][m];
				MethodsforLBSA.probCount++;
			}
		} 

		if ( Simulations.getKnowledgeType() == EKnowledgeType.NONE || Simulations.getKnowledgeType() == EKnowledgeType.SEARCH) {
			if ( nci == cj || pci == cj || ci == cj ) {
				while ( nci == cj || pci == cj || ci == cj ) {
					cj  = MethodsforLBSA.rand.nextInt(cityNumber);
				} 
				MethodsforLBSA.randCount++;
			}
		}
		move =current.findNeighbor(ci, cj);
		return move;
	}


	private static double averageTourLength(Solution2[] ss) {
		double tl = 0;
		for (Solution2 s : ss) {
			tl += s.getTourLength();
		}
		return tl / ss.length;
	}
	
	private static double averageTemperatur(Queue<Double>[] qs) {
		double t = 0;
		for (Queue<Double> q : qs) {
			t += q.peek();
		}
		return -t / qs.length;
	}

	private static void saveConvergenceData( double[] ts, double[] vs, double[] bs) {
		try {
			String f = Problems.getFileName();
			File file = new File(f);
			f = (new File("")).getAbsolutePath() + "\\results\\Convergence\\" + file.getName();
			f += " " + Simulations.getParaSetting() + " convergence process by list-based SA for TSP results.csv";

			System.out.println(f);
			PrintWriter printWriter = new PrintWriter(new FileWriter(f));
			for (int idx=0; idx<ts.length; idx++) {
				printWriter.println(ts[idx] + "," + vs[idx] + "," + bs[idx]);
			}
			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
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
		//produce sequence
		int[] seq = new int[z];
		double value = x;
		double step;
		if (m > 0) {
		    step = (2*y - 2*x)*1.0/m;
		    for (int i=0; i<m; i++) {
			    seq[i] = (int)(value+0.5);
			    value += step;
		    }
		} else {
			value = y + x;
		}
		step = (2*y - 2*x)*1.0/(z-m);
		for (int i=m; i < seq.length; i++) {
			seq[i] = (int)(value+0.5);
			value -= step;
		}
		return seq;
	}

	private static Random rand = new Random();

	public static int histCount;
	public static int probCount;
	public static int randCount;


	public static void main(String[] args) {
		final int TIMES = 1;
		String fileName = (new File("D:\\DOWNLOAD\\ELBSA\\ELBSA4TSP-master\\data")).getAbsolutePath() + "\\TSPLarge33\\05pcb1173.txt";
		Problems.setFileName(fileName);
		Solution2 s;
		double tourLength = 0;
        long startTime = System.nanoTime();
		for (int i = 0; i < TIMES; i++) {
			s = MethodsforLBSA.LBSA(1000, 30);
			System.out.println(i + "-:" + s.getTourLength());
			tourLength += s.getTourLength();
		}
		tourLength /= TIMES;
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.println("Waktu eksekusi: " + durationInSeconds + " detik");
		System.out.println("Average: " + tourLength);
		//int[] aMCL = MethodsforLBSA.produceArithmeticSequence(100/2, 100, 0, 1000);
		//for (int m : aMCL) {
		//	System.out.println(m);
		//}
	}
}
