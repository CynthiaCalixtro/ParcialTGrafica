package rnnn01;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.IntStream;

public class n_capas {
	static final Random rand = new Random();
	int ci;
	int co;
	int cs;
	double xin[][];
	double xout[][];
	double y[];
	double s[];
	double g[];
	double w[];

	int c[];// =new int[3];//capas de datos
	int resul_prueba[];
	double predicts_resultado[][];

	int capas;

	public n_capas(int ci_, int[] co_, int cs_, boolean pesos, double pesos_preconf[]) {
		int ci = ci_; // cantidad de neuronas de entrada
		int co[] = co_; // cantidad de neuronas de oculta x capa [3,5,6,3,6,7]
		int cs = cs_;

		int neuronas = 0;
		for (int i=0;i<co.length;i++){
			neuronas += co[i];
		}
		y = new double[neuronas + cs];
		s = new double[neuronas + cs];
		g = new double[neuronas + cs];

		// calculando cantidad de pesos
		int acum = ci * co[0];
		for (int i = 0; i < co.length - 1; i++) {
			acum += co[i] * co[i + 1];
		}
		acum += co[co.length - 1] * cs;

		capas = co.length + 2;
		c = new int[capas];
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
		System.out.println("Oe queeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
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
			for (int j = 0; j < xin[i].length; j++) {
				System.out.println("xingreso[" + i + "," + j + "]=" + xin[i][j]);
				System.out.println("                ");
			}
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
		int yy = 0;
		ci = cii;
		ii = 0;// capa0*capa

		for (int i = 0; i < c[1]; i++) {
			pls = 0;
			for (int j = 0; j < c[0]; j++) {
				//System.out.println("psl=" + pls + " , W[ii]=" + w[ii] + " , xin[ci][j]=" + xin[ci][j]);
				pls = pls + w[ii] * xin[ci][j];
				ii++;
			}
			s[i] = pls; // i = i+ capa0
			y[i] = fun(s[i]); // i = i+ capa0
			yy++;
		}
		// CAPAS INTERMEDIAS
		int ypp =0;
		int n = c.length; // cantidad de capas
		for (int k = 1; k < n - 1; k++) {
			for (int i = 0; i < c[k + 1]; i++) {
				pls = 0;
				for (int j = 0; j < c[k]; j++) {
					pls = pls + w[ii] * y[j + ypp];
					ii++;
				}
				s[yy] = pls; // i = i + capa1
				y[yy] = fun(s[yy]); // i = i + capa1
				yy++;
			}
			ypp = ypp + c[k];
		}
		yy--;
		for (int i =c[n -1] -1; i>=0; i--){
			g[yy] = (xout[ci][i] - y[yy])* y[yy] * (1-y[yy]);
			yy--;
		}
		// cantidad de ws
		int total_w = 0;
		for(int i=1;i<c.length;i++){
			total_w += c[i-1]*c[i];
		}
		ii--;

		// capas intermedias g
		for (int k = n - 2; k >= 1; k--) {
			pls = 0;
			total_w -= c[k]*c[k+1];
			for (int i = 0; i < c[k]; i++) {
				for (int j = 0; j < c[k+1]; j++) {
					pls = pls + w[total_w + j*c[k]] * g[c[k] + j];
				}
				g[yy] = y[yy] * (1 - y[yy]) * pls;
				pls = 0;
				yy--;
			}
		}

		int gpp = 0;
		ii = 0;// capa0*capa1
		for (int i = 0; i < c[1]; i++) {
			for (int j = 0; j < c[0]; j++) {
				w[ii] = w[ii] + g[i] * xin[ci][j];
				ii++;
			}
		}
		gpp += c[1];
		ypp = 0;
		for (int k = 2; k < n ; k++) {
			for (int i = 0; i < c[k]; i++) {
				for (int j = 0; j < c[k - 1]; j++) {
					w[ii] = w[ii] + g[gpp] * y[ypp + j];
					ii++;
				}
				gpp++;
			}
			ypp += c[k-1];
		}

	}

	public void prueba(double[][] pruebas) {
		double prubs[] = new double[c[0]];
		resul_prueba = new int[pruebas.length];
		predicts_resultado = new double[pruebas.length][];

		for (int i = 0; i < pruebas.length; i++) {
			for (int j = 0; j < pruebas[i].length; j++) {
				prubs[j] = pruebas[i][j];
			}
			usored(prubs,i);
		}
		System.out.println("Array c:" + Arrays.toString(c));

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
		int ii;
		double pls;
		int yy = 0;

		ii = 0;// capa0*capa1
		for (int i = 0; i < c[1]; i++) {
			pls = 0;
			for (int j = 0; j < c[0]; j++) {
				pls = pls + w[ii] * datatest[j];
				ii++;
			}
			s[yy] = pls; // i = i+ capa0
			y[yy] = fun(s[yy]); // i = i+ capa0
			yy++;
		}
		// ++++++capa2
		/*
		 * pls=0; ii = c[0]*c[1];//capa1*capa2 for(int i=0;i<c[2];i++){ for(int
		 * j=0;j<c[1];j++){ pls=pls+w[ii]*y[j]; ii++; } s[i+c[1]]=pls; //i = i + capa1
		 * y[i+c[1]]=fun(s[i+c[1]]); //i = i + capa1 pls=0; }
		 */
		int ypp = 0;
		int n = c.length; // cantidad de capas
		for (int k = 1; k < n - 1; k++) {
			for (int i = 0; i < c[k + 1]; i++) {
				pls = 0;
				for (int j = 0; j < c[k]; j++) {
					pls = pls + w[ii] * y[j + ypp];
					ii++;
				}
				s[yy] = pls; // i = i + capa1
				y[yy] = fun(s[yy]); // i = i + capa1
				yy++;
			}
			ypp += c[k];
		}
		yy--;
		/*
		System.out.println("entrada_dataset: ");
		int indexito =0;
		for(double x: datatest){
			System.out.println("("+indexito+"):"+Math.round(x*100)/100);
			indexito++;
		}
		System.out.println("entrada_dataset_fin: ");
		*/
		System.out.print("salida:");
		// for(int i=(co-1);i<(co+cs);i++){
		// for(int i=2;i<3;i++){
		int ip = 0;
		double[] predict = new double[c[n-1]];
		for(int i = c[n-1] -1; i>=0; i--){
			System.out.println("yy:"+yy+ " i: "+i+" , y["+(yy-i)+"]");
			predict[ip] = y[yy-i];
			ip++;
		}
		predicts_resultado[iresprueba] = predict;
		if(predicts_resultado[iresprueba][0] <= 0.5){
			resul_prueba[iresprueba] = 0;
			System.out.println("pre: " + predicts_resultado[iresprueba][0]+ " -> GATO");
		}
		else {
			resul_prueba[iresprueba] = 1;
			System.out.println("pre: " + predicts_resultado[iresprueba][0]+ " -> PERRO");
		}

	}

	void entrenamiento(Leerjpg ingresoImg, double[][] salida, int i) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

}
