
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DataStatisticalUtils {
    /**
     * 
     * @param arr
     * @return
     */
    public static double getSum(double[] arr) {//penjumlahan
        double sum = 0;
        for (double num : arr) {
            sum += num;
        }
        return sum;
    }

    /**
     *
     * @param arr
     * @return
     */
    public static double getMean(double[] arr) {//rata-rata
        return getSum(arr) / arr.length;
    }

    /**
     * Mode
     *
     * @param arr
     * @return
     */
    public static double getMode(double[] arr) {//mencari modus
        Map<Double, Integer> map = new HashMap<Double, Integer>();
        for (int i = 0; i < arr.length; i++) {
            if (map.containsKey(arr[i])) {
                map.put(arr[i], map.get(arr[i]) + 1);// Jika elemen sudah ada di HashMap, tambahkan 1 ke jumlah kemunculannya.
            } else {
                map.put(arr[i], 1);// Jika elemen belum ada, tambahkan elemen ke HashMap dengan nilai awal 1.
            }
        }
        int maxCount = 0;
        double mode = -1;
        Iterator<Double> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            double num = iter.next();
            int count = map.get(num);
            if (count > maxCount) {// Jika jumlah kemunculan elemen lebih besar dari jumlah maksimum yang ditemukan sejauh ini:
                maxCount = count;
                mode = num;// Tetapkan elemen saat ini sebagai modus.
            }
        }
        return mode;
    }

    /**
     * Median
     *
     * @param arr
     * @return
     */
    public static double getMedian(double[] arr) {//mencari median
        double[] tempArr = Arrays.copyOf(arr, arr.length);// Membuat salinan array asli agar tidak memengaruhi urutan elemen dalam array aslinya
        Arrays.sort(tempArr);//urutkan
        if (tempArr.length % 2 == 0) {// Jika panjang array adalah genap:
            // Ambil dua elemen tengah, hitung rata-rata mereka, dan kembalikan hasilnya.
            return (tempArr[tempArr.length >> 1] + tempArr[(tempArr.length >> 1) - 1]) / 2;
        } else {
            return tempArr[(tempArr.length >> 1)];//jika ganjil
            // Kembalikan elemen tengah sebagai median.
        }
    }


    /**
     * Middle range
     *
     * @param arr
     * @return
     */
    public static double getMidrange(double[] arr) {//menghitung midrange 
        double max = arr[0], min = arr[0];// Inisialisasi nilai maksimum dan minimum dengan elemen pertama array.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];// Jika elemen saat ini lebih besar dari `max`, perbarui nilai `max`.
            }
            if (arr[i] < min) {
                min = arr[i];// Jika elemen saat ini lebih kecil dari `min`, perbarui nilai `min`.
            }
        }
        return (min + max) / 2;// Hitung midrange sebagai rata-rata dari nilai maksimum dan minimum.
    }

    /**
     * ���ķ�λ��
     *
     * @param arr
     * @return ��������ķ�λ��������
     */
    public static double[] getQuartiles(double[] arr) {//mencari quartils
        double[] tempArr = Arrays.copyOf(arr, arr.length);// Salin array `arr` ke array baru `tempArr` agar tidak mengubah data asli.
        Arrays.sort(tempArr);//urutkan
        double[] quartiles = new double[3]; // Inisialisasi array `quartiles` untuk menyimpan Q1, Q2 (median), dan Q3.
        quartiles[1] = getMedian(tempArr);//median

        if (tempArr.length % 2 == 0) {//jika genap
            quartiles[0] = getMedian(Arrays.copyOfRange(tempArr, 0, tempArr.length / 2));
            // Q1 adalah median dari setengah pertama array.
            quartiles[2] = getMedian(Arrays.copyOfRange(tempArr, tempArr.length / 2, tempArr.length));
             // Q3 adalah median dari setengah kedua array.
        } else {//jika ganjil
            quartiles[0] = getMedian(Arrays.copyOfRange(tempArr, 0, tempArr.length / 2));
            quartiles[2] = getMedian(Arrays.copyOfRange(tempArr, tempArr.length / 2 + 1, tempArr.length));
        }
        return quartiles;
    }

    /**
     * �󼫲�
     *
     * @param arr
     * @return
     */
    public static double getRange(double[] arr) {// menghitung rentang (range)
        double max = arr[0], min = arr[0]; // Inisialisasi nilai maksimum dan minimum dengan elemen pertama array
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {// Jika elemen saat ini lebih besar dari `max`, perbarui nilai `max`
                max = arr[i];
            }
            if (arr[i] < min) {// Jika elemen saat ini lebih kecil dari `min`, perbarui nilai `min`
                min = arr[i];
            }
        }
        return max - min;// Rentang dihitung dengan mengurangi nilai minimum dari nilai maksimum
    }

    /**
     * ���ķ�λ������
     *
     * @param arr
     * @return
     */
    public static double getQuartilesRange(double[] arr) {//menghitung rentang antara kuartil dari sebuah array
        return getRange(getQuartiles(arr));
    }

    /**
     * ��ضϾ�ֵ
     *
     * @param arr ��ֵ����
     * @param p   �ض���p������p��ֵΪ20����ض�20%����10%����10%��
     * @return
     */
    public static double getTrimmedMean(double[] arr, int p) {//menghitung rata-rata trim (trimmed mean)
        int tmp = arr.length * p / 100; // tmp: jumlah elemen yang akan dipangkas.
        double[] tempArr = Arrays.copyOfRange(arr, tmp, arr.length + 1 - tmp);
        // Salin elemen array yang berada di tengah setelah memangkas elemen dari kedua ujungnya.
        // tempArr: array baru berisi elemen yang tersisa.
        return getMean(tempArr); // Hitung dan kembalikan rata-rata dari elemen array yang tersisa.
    }

    /**
     * �󷽲�
     *
     * @param arr
     * @return
     */
    public static double getVariance(double[] arr) {//menghitung varians
        double variance = 0;
        double sum = 0, sum2 = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i]; // Menambahkan elemen ke total jumlah
            sum2 += arr[i] * arr[i];// Menambahkan kuadrat elemen ke total kuadrat
        }
        variance = sum2 / arr.length - (sum / arr.length) * (sum / arr.length); // Rumus variansi: (Σx² / n) - (Σx / n)²
        return variance;
    }

    /**
     * �����ƽ��ƫ��(AAD)
     *
     * @param arr
     * @return
     */
    public static double getAbsoluteAverageDeviation(double[] arr) {//menghitung rata-rata deviasi absolut (AAD)
        double sum = 0;
        double mean = getMean(arr);// Hitung nilai rata-rata dari array
        for (int i = 0; i < arr.length; i++) {
            // Hitung nilai absolut selisih antara elemen dan rata-rata, tambahkan ke sum
            sum += Math.abs(arr[i] - mean);
        }
        // Hitung rata-rata deviasi absolut: jumlah deviasi absolut dibagi dengan jumlah elemen
        return sum / arr.length;
    }

    /**
     * ����λ������ƫ��(MAD)
     *
     * @param arr
     * @return
     */
    public static double getMedianAbsoluteDeviation(double[] arr) {//menghitung Median Absolute Deviation (MAD)
        double[] tempArr = new double[arr.length];
        double median = getMedian(arr);// Hitung median dari array asli
        for (int i = 0; i < arr.length; i++) {
            tempArr[i] = Math.abs(arr[i] - median);// Deviasi absolut: |elemen - median|
        }
        return getMedian(tempArr); // Hitung median dari deviasi absolut
    }

    /**
     * ���׼��
     * @param arr
     * @return
     */
    public static double getStandardDevition(double[] arr) {//mencari standar deviasi
        double sum = 0;
        double mean = getMean(arr);//Hitung rata-rata
        for (int i = 0; i < arr.length; i++) {//Hitung jumlah kuadrat dari selisih setiap elemen terhadap rata-rata
            sum += Math.sqrt((arr[i] - mean) * (arr[i] - mean));//// Kuadrat selisih
        }
        return (sum / (arr.length - 1));
    }


}