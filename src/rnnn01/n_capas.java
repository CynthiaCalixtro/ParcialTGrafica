package rnnn01;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.IntStream;

public class n_capas {
	static final Random rand = new Random();
	int ci;
	int co;
	int cs;

	int capas;

	double xin[][];// ={{0,1,0},{0,1,1},{1,0,0},{1,0,1}};
	// double xin[][]={{0,1,0},{0,1,1},{1,0,0},{1,0,1}};
	double xout[][];// ={{1},{0},{1},{0}};

	double y[];
	int resul_prueba[];

//   double w[]={2,-2,0,1,3,-1,3,-2};
//   double s[]={0,0,0};
	double s[];
	double g[];
//   double g[]={0,0,0};
	double w[];

//   int c[] = {3,2,1};//capas de datos
	int c[];// =new int[3];//capas de datos

	public n_capas(int ci_, int[] co_, int cs_, boolean pesos, double pesos_preconf[]) {
		int ci = ci_; // cantidad de neuronas de entrada
		int co[] = co_; // cantidad de neuronas de oculta x capa [3,5,6,3,6,7]
		int cs = cs_;

		int neuronas = IntStream.of(co).sum() + cs + ci; // cantidad de neuronas
		y = new double[neuronas - ci];
		s = new double[neuronas - ci];
		g = new double[neuronas - ci];

		capas = co.length + 2;
		c = new int[capas];

		// calculando cantidad de pesos
		int acum = ci * co[0];
		for (int i = 0; i < co.length - 1; i++) {
			acum += co[i] * co[i + 1];
		}
		acum += co[co.length - 1] * cs;

		w = new double[acum];

		c[0] = ci; // primera capa
		for (int i = 0; i < co.length; i++) {
			c[i + 1] = co[i];
		}
		c[capas - 1] = cs; // ultima capa

		for (int i = 0; i < y.length; i++) {
			y[i] = 0;
			s[i] = 0;
			g[i] = 0;
		}
		
		if (pesos) {
			setPesos(pesos_preconf);
		}else {
			for (int i = 0; i < w.length; i++) {
				w[i] = getRandom();
			}
		}
	}
	
	public void setPesos(double w_[]) {
		for (int i=0; i<w.length; i++) {
			w[i] = w_[i];
		}
	}

	public double fun(double d) {
		return 1 / (1 + Math.exp(-d));
	}

	public void printxingreso() {
		// visualizar x ingreso
		for (int i = 0; i < xin.length; i++)
			for (int j = 0; j < xin[i].length; j++)
				System.out.println("xingreso[" + i + "," + j + "]=" + xin[i][j]);
		System.out.println("                ");
	}

	public void printxysalida() {
		// visalizar x de salida
		for (int i = 0; i < xout.length; i++)
			for (int j = 0; j < xout[i].length; j++)
				System.out.println("xsalida[" + i + "," + j + "]=" + xout[i][j]);
	}

	public void printy() {
		for (int i = 0; i < y.length; i++)
			System.out.println("y[" + i + "]=" + y[i]);
	}

	public void printw() {
		for (int i = 0; i < w.length; i++)
			System.out.println("w[" + i + "]=" + w[i]);
	}

	public void prints() {
		for (int i = 0; i < s.length; i++)
			System.out.println("s[" + i + "]=" + s[i]);
	}

	public void printg() {
		for (int i = 0; i < g.length; i++)
			System.out.println("g[" + i + "]=" + g[i]);
	}

	double getRandom() {
		return (rand.nextDouble() * 2 - 1); // [-1;1[
	}

	public void entrenamiento(double[][] in, double[][] sal, int veces) {
		xin = in;
		xout = sal;
		for (int v = 0; v < veces; v++)
			for (int i = 0; i < xin.length; i++) {
				entreno(i); // barrida del i-esimo example
			}
	}

	public void entreno(int cii) {
		int ii;
		double pls;
		int ci;

		// entrenamiento
		//////////////////////////////////
		////// ******** Ida**********//////
		// +++++++capa1
		/// ci=0;//entrenamiento primero /////HOPE
		ci = cii;
		ii = 0;// capa0*capa1
		pls = 0;
		for (int i = 0; i < c[1]; i++) {
			for (int j = 0; j < c[0]; j++) {
				System.out.println("psl=" + pls + " , W[ii]=" + w[ii] + " , xin[ci][j]=" + xin[ci][j]);
				pls = pls + w[ii] * xin[ci][j];
				ii++;
			}
			s[i] = pls; // i = i+ capa0
			y[i] = fun(s[i]); // i = i+ capa0
			pls = 0;
		}
		// ++++++capa2
		int n = c.length; // cantidad de capas
		for (int k = 1; k < n - 1; k++) {
			pls = 0;
			ii = c[k - 1] * c[k];// capa(k-1)*capa(k+2) <-- ii = c[0]*c[1];//capa1*capa2
			for (int i = 0; i < c[k + 1]; i++) {
				for (int j = 0; j < c[k]; j++) {
					pls = pls + w[ii] * y[j];
					ii++;
				}
				s[i + c[k]] = pls; // i = i + capa1
				y[i + c[k]] = fun(s[i + c[k]]); // i = i + capa1
				pls = 0;
			}
		}

		for (int i = 0; i < c[capas - 1]; i++) {
			g[i + c[capas - 2]] = (xout[ci][i] - y[i + c[capas - 2]]) * y[i + c[capas - 2]] * (1 - y[i + c[capas - 2]]);
		}

		// ++++capa1 g
		for (int k = n - 1; k >= 2; k--) {
			pls = 0;
			for (int i = 0; i < c[k - 1]; i++) {
				for (int j = 0; j < c[k]; j++) {
					pls = pls + w[c[k - 2] * c[k - 1] + j * c[k - 1] + i] * g[c[k - 1] + j];
				}
				g[i] = y[i] * (1 - y[i]) * pls;
				pls = 0;
			}
		}

		// *** ACTUALIZACION ***
		for (int k = n - 1; k >= 2; k--) {
			ii = c[k - 2] * c[k - 1];// capa1*capa2
			for (int i = 0; i < c[k]; i++) {
				for (int j = 0; j < c[k - 1]; j++) {
					w[ii] = w[ii] + g[i + c[k - 1]] * y[j];
					ii++;
				}
			}
		}

		// ++++capa1 w
		ii = 0;// capa0*capa1
		for (int i = 0; i < c[1]; i++) {
			for (int j = 0; j < c[0]; j++) {
				w[ii] = w[ii] + g[i] * xin[ci][j];
				ii++;
			}
		}
		////// ----------Fin Vuelta--------/////
		// printg();
		// printy();
		// prints();
		// printw();
		// printxingreso();
		// printxysalida();
		// System.out.println("----------------------****Fin****------------------------");
	}

	public void prueba(double[][] pruebas) {
		double prubs[] = new double[c[0]];
		resul_prueba = new int[pruebas.length];

		for (int i = 0; i < pruebas.length; i++) {
			for (int j = 0; j < pruebas[i].length; j++) {
				prubs[j] = pruebas[i][j];
				// System.out.println("["+i+","+j+"]"+pruebas[i][j]);
			}
			usored(prubs,i);
		}

	}

	void GuardarPeso() throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter("myfile.csv"));
		StringBuilder sb = new StringBuilder();

// Append strings from array
		for (double element : this.w) {
			sb.append(String.valueOf(element));
			sb.append(",");
		}

		br.write(sb.toString());
		br.close();
	}

	public void usored(double[] datatest, int iresprueba) {
		System.out.println("-----------****Inicio Test****----------");
		int ii;
		double pls;
		// int ci;

		// entrenamiento
		//////////////////////////////////
		////// ******** Ida**********//////
		// +++++++capa1
		/// ci=0;//entrenamiento primero /////HOPE
		// ci=cii;
		ii = 0;// capa0*capa1
		pls = 0;
		for (int i = 0; i < c[1]; i++) {
			for (int j = 0; j < c[0]; j++) {
				// pls=pls+w[ii]*xin[ci][j];
				pls = pls + w[ii] * datatest[j];
				ii++;
			}
			s[i] = pls; // i = i+ capa0
			y[i] = fun(s[i]); // i = i+ capa0
			pls = 0;
		}
		// ++++++capa2
		/*
		 * pls=0; ii = c[0]*c[1];//capa1*capa2 for(int i=0;i<c[2];i++){ for(int
		 * j=0;j<c[1];j++){ pls=pls+w[ii]*y[j]; ii++; } s[i+c[1]]=pls; //i = i + capa1
		 * y[i+c[1]]=fun(s[i+c[1]]); //i = i + capa1 pls=0; }
		 */
		int n = c.length; // cantidad de capas
		for (int k = 1; k < n - 1; k++) {
			pls = 0;
			ii = c[k - 1] * c[k];// capa(k-1)*capa(k+2) <-- ii = c[0]*c[1];//capa1*capa2
			for (int i = 0; i < c[k + 1]; i++) {
				for (int j = 0; j < c[k]; j++) {
					pls = pls + w[ii] * y[j];
					ii++;
				}
				s[i + c[k]] = pls; // i = i + capa1
				y[i + c[k]] = fun(s[i + c[k]]); // i = i + capa1
				pls = 0;
			}
		}

		// printy();
		// printy();
		System.out.print("prueba");
		for (int i = 0; i < datatest.length; i++) {
			System.out.print("[" + datatest[i] + "] ");
		}
		System.out.println();
		System.out.print("salida:  ");
		// for(int i=(co-1);i<(co+cs);i++){
		// for(int i=2;i<3;i++){
		for (int i = c[capas - 2]; i < (c[capas - 2] + c[capas - 1]); i++) {// for(int i=c[1];i<(c[1]+c[2]);i++){
			if (y[i] > 0.50) {
				System.out.printf("y[i]" + y[i]);
				System.out.printf(" , GATO >.<");
				resul_prueba[iresprueba] = 1;
			} else {
				System.out.printf("y[i]" + y[i]);
				System.out.printf(" , PERRO ().()");
				resul_prueba[iresprueba] = 0;
			}
			iresprueba++;
		}
		System.out.println();

		// System.out.println("-----------****Fin Test****----------");

	}

	void entrenamiento(Leerjpg ingresoImg, double[][] salida, int i) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

}
