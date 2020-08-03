package rnnn01;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Leerjpg extends Component {
    
	/*public static void main(String[] foo) {
		new Leerjpg();
	}
*/
        double final_matriz[][];
        
        public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
        
        public double[][] getfinal_matriz(){
            return this.final_matriz;
        }

	public void printPixelARGB(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		//System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
	}

	public double convertPixelGray(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		double graycito = (0.2126 * red + 0.7152 * green + 0.0722 * blue)/255;
		//System.out.println("gray: " + graycito);
		return graycito;
	}

	public double multiplyMatrices(double[][] mat1, double[][] mat2) {
		double sum = 0.0;
		int dim = mat1.length;

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				sum += mat1[i][j] * mat2[i][j];
			}
		}
		return sum;
	}

	public double[][] getsubMatrix(double[][] mat, int size, int start_row, int start_col) {
		double[][] subMat = new double[size][size];
		for (int i = start_row; i < start_row + size; i++) {
			subMat[i - start_row] = Arrays.copyOfRange(mat[i], start_col, start_col + size);
		}
		return subMat;
	}

	public double[][] convolucion(double[][] graycy,double[][] rasgos) {
		
		int filas_gray = graycy.length;
		int columnas_gray = graycy[0].length;
		
		int filas_rasgos = rasgos.length;
		int columnas_rasgos = rasgos[0].length;

		double mapaCarac[][] = new double[filas_gray - filas_rasgos + 1][columnas_gray - columnas_rasgos + 1];

		for (int i = 0; i < filas_gray - filas_rasgos + 1; i++) {
			for (int j = 0; j < columnas_gray - columnas_rasgos + 1; j++) {
				mapaCarac[i][j] = multiplyMatrices(getsubMatrix(graycy, filas_rasgos, i, j), rasgos);
			}
		}
		/*
		System.out.println("\n\n============ Kernel ============");
		for (int i = 0; i < filas_rasgos; i++) {
			for (int j = 0; j < columnas_rasgos; j++) {
				System.out.printf("%f\t", rasgos[i][j]);
			}
			System.out.println("");
		}
		*/
		return mapaCarac;
	}

	public double getMaxValue(double[][] numbers) {
		double maxValue = numbers[0][0];
		for (int j = 0; j < numbers.length; j++) {
			for (int i = 0; i < numbers[j].length; i++) {
				if (numbers[j][i] > maxValue) {
					maxValue = numbers[j][i];
				}
			}
		}
		return maxValue;
	}

	public double[][] pooling(double[][] mapa, int size) {
		int filas = mapa.length;
		int columnas = mapa[0].length;
		double pooled[][] = new double[filas - size + 1][columnas - size + 1];

		for (int i = 0; i < filas - size + 1; i++) {
			for (int j = 0; j < columnas - size + 1; j++) {
				pooled[i][j] = getMaxValue(getsubMatrix(mapa, size, i, j));
			}
		}

		return pooled;
	}

	public void printMatrix(double[][] mat) {
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				System.out.printf("%.3f\t", mat[i][j]);
			}
			System.out.println("");
		}
	}

	private void marchThroughImage(BufferedImage img) {
                BufferedImage image = resizeImage(img, 200, 200);
		int w = image.getWidth();
		int h = image.getHeight();
		//System.out.println("width, height: " + w + ", " + h+"\n");

		double matriz[][] = new double[h][w];

                for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				//System.out.println("x,y: " + j + ", " + i);
				int pixel = image.getRGB(j, i);
				printPixelARGB(pixel);
				matriz[i][j] = convertPixelGray(pixel);
				//System.out.println("");
				
				Color newColor = new Color((int)matriz[i][j],(int)matriz[i][j],(int)matriz[i][j]);
				image.setRGB(j,i,newColor.getRGB());
			}
		}
	
		//
		// System.out.println("\n\n============ Matriz escala de grises ============");
		//printMatrix(matriz);
            
                double kernel[][][] ={ { { 1,0,1}, { 0,1,0 },{ 1, 0, 1 }},//kernel del profe
                                    { {1 / 9.0, 1 / 9.0, 1 / 9.0},{1 / 9.0, 1 / 9.0, 1 / 9.0},{1 / 9.0, 1 / 9.0, 1 / 9.0}},//kernel box
                                    { {1 / 256.0, 4  / 256.0,  6 / 256.0,  4 / 256.0, 1 / 256.0},{4 / 256.0, 16 / 256.0, 24 / 256.0, 16 / 256.0, 4 / 256.0},{6 / 256.0, 24 / 256.0, 36 / 256.0, 24 / 256.0, 6 / 256.0},{4 / 256.0, 16 / 256.0, 24 / 256.0, 16 / 256.0, 4 / 256.0},{1 / 256.0, 4  / 256.0,  6 / 256.0,  4 / 256.0, 1 / 256.0}},//kernel gaussiano
                                    {{0, 1, 0},{ 1, -4, 1}, {0, 1, 0}}//kernel edge
                }; 
                
                final_matriz=matriz;  
                
                int count = 0;
                while(h*w>100){
                    int rasgo=count%kernel.length;
                    double rasgos[][]=kernel[rasgo];
                    if(h<10 || w<10){
                        break;
                    }
                    double convi[][] = convolucion(final_matriz,rasgos);
                    //System.out.println("Iteración: "+ count+"dimension de convi" );
                    h=convi.length;
                    //System.out.println("h: " + h  );
                    w=convi[0].length;
                    //System.out.println( "w: " + w );
                    if(h<10 || w<10){
                        break;
                    }
                    
                    final_matriz=pooling(convi,2);
                    //System.out.println("Iteración: "+ count+"dimension del pooling" );
                    h=final_matriz.length;
                    //System.out.println("h: " + h  );
                    w=final_matriz[0].length;
                    //System.out.println( "w: " + w );
                    count++;
                }
                
                //System.out.println("\n\n============ Final matriz :) ============");
                //printMatrix(final_matriz);
                this.final_matriz = final_matriz;
                        
               
	}

	public Leerjpg(String archivitoC) {
		try {
			// get the BufferedImage, using the ImageIO class
			BufferedImage image = ImageIO.read(this.getClass().getResource(archivitoC));
			marchThroughImage(image);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}