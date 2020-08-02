package rnnn01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class csv_n_capas {
	static double w[];
	public static final String delimiter = ",";

	public static void read(String csvFile) {
		List<Double> tokens = new ArrayList<Double>();
		try {
			File file = new File(csvFile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String[] tempArr;
			while ((line = br.readLine()) != null) {
				tempArr = line.split(delimiter);
				for (String tempStr : tempArr) {
					tokens.add(Double.parseDouble(tempStr));
				}
			}
			br.close();
			Double[] tokenArray = tokens.toArray(new Double[0]);
			w = new double[tokenArray.length];

			for (int i = 0; i < tokenArray.length; i++) {
				w[i] = tokenArray[i];
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// leemos los pesos desde un archivo
		String csvFile = "pesosFinal.csv";
		read(csvFile);

		String root = "cat_entreno/cat.";
		String root_dog = "dog_entreno/dog.";
		int imagesn = 10;
		double ingreso[][] = new double[imagesn * 2][];
		for (int ind = 1; ind <= imagesn; ind++) {
			Leerjpg ingresoImg = new Leerjpg(root + ind + ".jpg");
			System.out.println(" Verificacion, paso el primer jpg");
			double matriz_lec[][] = ingresoImg.getfinal_matriz();
			int row = matriz_lec.length;
			int col = matriz_lec[0].length;
			double matriz_lec1d[] = new double[row * col];

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					matriz_lec1d[i * col + j] = matriz_lec[i][j];
				}
			}
			ingreso[ind - 1] = matriz_lec1d;
		}

		for (int ind = 1; ind <= imagesn; ind++) {
			Leerjpg ingresoImg = new Leerjpg(root_dog + ind + ".jpg");
			double matriz_lec[][] = ingresoImg.getfinal_matriz();
			int row = matriz_lec.length;
			int col = matriz_lec[0].length;
			double matriz_lec1d[] = new double[row * col];

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					matriz_lec1d[i * col + j] = matriz_lec[i][j];
				}
			}
			ingreso[ind + imagesn - 1] = matriz_lec1d;
		}

		double evaluar[][] = new double[10][];

		String root_test = "cat_test/gato_";
		String root_dog_test = "dog_test/perro_";
		for (int ind = 1; ind <= 5; ind++) {
			Leerjpg ingresoImg = new Leerjpg(root_test + ind + ".jpg");
			double matriz_lec[][] = ingresoImg.getfinal_matriz();
			int row = matriz_lec.length;
			int col = matriz_lec[0].length;
			double matriz_lec1d[] = new double[row * col];

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					matriz_lec1d[i * col + j] = matriz_lec[i][j];
				}
			}
			evaluar[ind - 1] = matriz_lec1d;
		}

		for (int ind = 1; ind <= 5; ind++) {
			Leerjpg ingresoImg = new Leerjpg(root_dog_test + ind + ".jpg");
			double matriz_lec[][] = ingresoImg.getfinal_matriz();
			int row = matriz_lec.length;
			int col = matriz_lec[0].length;
			double matriz_lec1d[] = new double[row * col];

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					matriz_lec1d[i * col + j] = matriz_lec[i][j];
				}
			}
			evaluar[ind + 4] = matriz_lec1d;
		}
		double salida[][] = new double[20][1];
		for (int k = 0; k < 10; k++) {
			Arrays.fill(salida[k], 1.0);

		}
		for (int k = 10; k < 20; k++) {
			Arrays.fill(salida[k], 0.0);
		}

		int ocultas[] = { 15, 13, 14 };
		
		n_capas rn = new n_capas(ingreso[0].length, ocultas, 1, true, w);
		rn.prueba(evaluar);

	}

}
