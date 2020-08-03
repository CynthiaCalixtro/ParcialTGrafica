package rnnn01;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class prueba_n_capas {
	static int hilos = 3;
	static int epocas = 20;
	public double accuracy[] = new double[hilos]; // exactitud por hilo
	ArrayList<ArrayList<Double>> pesos = new ArrayList<ArrayList<Double>>(hilos); // conjunto de pesos por hilo

	public class mini_red extends Thread {
		double w_hilo[];
		double ingreso[][];
		int ocultas[];
		double salida[][];
		double error;
		int salida_prueba[];
		double acc = 0.0;
		int id;
		boolean flag;
		double pesos_preconf[];

		// constructor
		mini_red(double ingreso_[][], int ocultas_[], double salida_[][], int id_, boolean flag_,
				double pesos_preconf_[]) {
			ingreso = ingreso_;
			ocultas = ocultas_;
			salida = salida_;
			id = id_;
			flag = flag_;
			pesos_preconf = pesos_preconf_;
		}

		// ejecucion del hilo
		public void run() {
			n_capas rn = new n_capas(ingreso[0].length, ocultas, 1, flag, pesos_preconf);
			rn.entrenamiento(ingreso, salida, epocas);
			rn.prueba(ingreso);
			w_hilo = rn.w;
			salida_prueba = rn.resul_prueba;

			int success = 0;
			int k = 0;
			for (double[] sasa : salida) {
				if (sasa[0] == salida_prueba[k]) {
					success += 1;
				}
				k++;
			}
			acc = success / salida_prueba.length;

			// variables que usan todos los hilos
			accuracy[id] = acc;

			System.out.println("Entraaaaaaaaaaaaaaaaaaa");
			ArrayList<Double> tmp = new ArrayList<Double>(); 
			for (double pes : w_hilo) {
				tmp.add(pes);
			}
			pesos.add(id,tmp);
			
			System.out.println("Termina ejecucion del hilo "+id+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	public static void main(String[] args) throws IOException {
		new prueba_n_capas().inicio();
	}
	
	void GuardarPeso(double w[]) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter("pesosFinal.csv"));
		StringBuilder sb = new StringBuilder();

		// Append strings from array
		for (double element : w) {
			sb.append(String.valueOf(element));
			sb.append(",");
		}

		br.write(sb.toString());
		br.close();
	}

	void inicio() {
		String root = "cat_entreno/cat.";
		String root_dog = "dog_entreno/dog.";
		int imagesn = 100;
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
		double salida[][] = new double[imagesn*2][1];
		for (int k = 0; k < imagesn; k++) {
			Arrays.fill(salida[k], 1.0);

		}
		for (int k = imagesn; k < imagesn*2; k++) {
			Arrays.fill(salida[k], 0.0);
		}

		int ocultas[] = { 15, 13, 14 };

		// ****************** parelelizacion

		System.out.println("Genera hilossssssssssssssssssssssssssssssssssssssss");
		// ===== Barrera 1
		Thread grupo1[] = new Thread[hilos];
		for (int i = 0; i < hilos; i++) {
			grupo1[i] = new mini_red(ingreso, ocultas, salida, i, false, new double[hilos]); // pesos basurita
			grupo1[i].start();
		}

		// join barrera 1
		for (int i = 0; i < hilos; i++) {
			try {
				grupo1[i].join();
			} catch (InterruptedException ex) {
				System.out.println("error" + ex);
			}
		}

		// hallando el hilo del grupo1 con pesos optimo
		int id_max = 0;
		for (int i = 0; i < accuracy.length; i++) {
			if (accuracy[id_max] < accuracy[i])
				id_max = i;
		}
		
		double[] elpeso = new double[pesos.get(id_max).size()];

		for (int i = 0; i < pesos.get(id_max).size(); i++) {
			elpeso[i] = pesos.get(id_max).get(i);
		}

		// ===== Barrera 2
		Thread grupo2[] = new Thread[hilos];
		grupo2[0] = new mini_red(ingreso, ocultas, salida, 0, true, elpeso); // pesos optimo obtenido de la primera
		grupo2[0].start();

		for (int i = 1; i < hilos; i++) {
			grupo2[i] = new mini_red(ingreso, ocultas, salida, i, false, new double[hilos]); // pesos basurita
			grupo2[i].start();
		}

		// join barrera 2
		for (int i = 0; i < hilos; i++) {
			try {
				grupo2[i].join();
			} catch (InterruptedException ex) {
				System.out.println("error" + ex);
			}
		}
		
		// hallando el hilo del grupo2 con pesos optimo
		id_max = 0;
		for (int i = 0; i < accuracy.length; i++) {
			if (accuracy[id_max] < accuracy[i])
				id_max = i;
		}
		
		for (int i = 0; i < pesos.get(id_max).size(); i++) {
			elpeso[i] = pesos.get(id_max).get(i);
		}
		
		try {
			GuardarPeso(elpeso);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
