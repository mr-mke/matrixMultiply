import java.util.*;
import java.math.*;
class matrixMultiply{
	public static void main(String[] args){
        int n;
		int i = 0;
        int j = 0;
        int k = 0;
        int r = 0;
		int a[][];
		int b[][];
		int c[][];
		int minimum = 1;
		int maximum = 50;
        int startSize=16;
        int itrations=8;
        double stimes[] = new double[itrations];
        double ptimes[] = new double[itrations];
        double stime;
        double ptime;
        double sstart;
        double send;
        double stotal=0;
        double pstart;
        double pend;
        double ptotal=0;
        
        n = startSize;
        for(k=0;k<itrations;k++){
            System.out.println("Itration "+k+" matrix size = "+n+" elements");
		    a = new int[n][n];
		    b = new int[n][n];
		    c = new int[n][n];
       
            for(r=0;r<10;r++){
                //randomly fill the matrices
		        for(i=0;i<n;i++){
			        for(j=0;j<n;j++){
			            a[i][j] = minimum + (int)(Math.random() * maximum);
			            b[i][j] = minimum + (int)(Math.random() * maximum);
			        }
		        }

		        sstart = System.nanoTime();
                c = multiply(a,b,n);
                send = System.nanoTime();
                stime=(send-sstart)/1000000000;

                pstart = System.nanoTime();
                c = strassen(a,b);
                pend = System.nanoTime();
                ptime=(pend-pstart)/1000000000;

		        System.out.println("Times taken for matrix of size n = "+n+" is : Serial= " + stime + " Parallel= "+ ptime);
                stotal = stotal + stime;
                ptotal = ptotal + ptime;
            }
            stimes[k]=stotal/10;
            ptimes[k]=ptotal/10;
	        System.out.println("Average times for matrix of size n = "+n+" is : Serial= "+ stimes[k] + " Parallel= "+ ptimes[k]);
            n=n*2;
            stotal=0;
            ptotal=0;
        }
	}

    public static int[][] multiply(int a[][], int b[][],int n){
        int c[][] = new int[n][n];
	    for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
			    for (int k = 0; k < n; k++){
				    c[i][j] = c[i][j] + a[i][k] * b[k][j];
				}
   			}
   		}
  		return c;
    }

    public static int[][] strassen(int[][]a, int[][]b){
		int n = a.length;
		int[][] R = new int[n][n];
		if(n==1)
			R[0][0] = a[0][0]*b[0][0];
		else if(n<=64){
			int[][] a11 = new int[n/2][n/2];
			int[][] a12 = new int[n/2][n/2];
			int[][] a21 = new int[n/2][n/2];
			int[][] a22 = new int[n/2][n/2];
			int[][] b11 = new int[n/2][n/2];
			int[][] b12 = new int[n/2][n/2];
			int[][] b21 = new int[n/2][n/2];
			int[][] b22 = new int[n/2][n/2];

			split(a,a11,0,0);
			split(a,a12,0,n/2);
			split(a,a21,n/2,0);
			split(a,a22,n/2,n/2);
			split(b,b11,0,0);
			split(b,b12,0,n/2);
			split(b,b21,n/2,0);
			split(b,b22,n/2,n/2);

			int [][] M1 = strassen(add(a11, a22), add(b11, b22));
       		int [][] M2 = strassen(add(a21, a22), b11);
    		int [][] M3 = strassen(a11, sub(b12, b22));
    		int [][] M4 = strassen(a22, sub(b21, b11));
    		int [][] M5 = strassen(add(a11, a12), b22);
    		int [][] M6 = strassen(sub(a21, a11), add(b11, b12));
    		int [][] M7 = strassen(sub(a12, a22), add(b21, b22));

			int [][] C11 = add(sub(add(M1, M4), M5), M7);
    		int [][] C12 = add(M3, M5);
    		int [][] C21 = add(M2, M4);
    		int [][] C22 = add(sub(add(M1, M3), M2), M6);

			join(C11, R, 0 , 0);
    		join(C12, R, 0 , n/2);
    		join(C21, R, n/2, 0);
    		join(C22, R, n/2, n/2);
		}else{
			int[][] a11 = new int[n/2][n/2];
			int[][] a12 = new int[n/2][n/2];
			int[][] a21 = new int[n/2][n/2];
			int[][] a22 = new int[n/2][n/2];
			int[][] b11 = new int[n/2][n/2];
			int[][] b12 = new int[n/2][n/2];
			int[][] b21 = new int[n/2][n/2];
			int[][] b22 = new int[n/2][n/2];

			class Mul implements Runnable{
				private Thread t;
				private String threadName;
				Mul(String name){
					threadName=name;
				}
				public void run(){
					if(threadName.equals("thread1")){
						split(a,a11,0,0);
					}else if(threadName.equals("thread2")){
						 split(a,a12,0,n/2);
					}else if(threadName.equals("thread3")){
						  split(a,a21,n/2,0);
					}else if(threadName.equals("thread4")){
						split(a,a22,n/2,n/2);
					}else if(threadName.equals("thread5")){
						split(b,b11,0,0);
					}else if(threadName.equals("thread6")){
						 split(b,b12,0,n/2);
					}else if(threadName.equals("thread7")){
						split(b,b21,n/2,0);
					}else if(threadName.equals("thread8")){
						split(b,b22,n/2,n/2);
					}
				}

				public void start(){
					if(t==null){
						t=new Thread(this,"Thread1");
						t.start();
					}
				}
			}
			Mul m1=new Mul("thread1");
			m1.start();
			Mul m2=new Mul("thread2");
			m2.start();
			Mul m3=new Mul("thread3");
			m3.start();
			Mul m4=new Mul("thread4");
			m4.start();
			Mul m5=new Mul("thread5");
			m5.start();
			Mul m6=new Mul("thread6");
			m6.start();
			Mul m7=new Mul("thread7");
			m7.start();
			Mul m8=new Mul("thread8");
			m8.start();
			for (Thread t : new Thread[] { m1.t,m2.t,m3.t,m4.t,m5.t,m6.t,m7.t,m8.t }){
				try{
					t.join();
				}catch(Exception e){
					System.out.println(e);
				}
			}


			int [][] M1 = strassen(add(a11, a22), add(b11, b22));
       		int [][] M2 = strassen(add(a21, a22), b11);
    		int [][] M3 = strassen(a11, sub(b12, b22));
    		int [][] M4 = strassen(a22, sub(b21, b11));
    		int [][] M5 = strassen(add(a11, a12), b22);
    		int [][] M6 = strassen(sub(a21, a11), add(b11, b12));
    		int [][] M7 = strassen(sub(a12, a22), add(b21, b22));

			int [][] C11 = add(sub(add(M1, M4), M5), M7);
    		int [][] C12 = add(M3, M5);
    		int [][] C21 = add(M2, M4);
    		int [][] C22 = add(sub(add(M1, M3), M2), M6);

			class Mul2 implements Runnable{
				private Thread t;
				private String threadName;
				Mul2(String name){
					threadName=name;
				}

				public void run(){
					if(threadName.equals("thread1")){
						join(C11, R, 0 , 0);
					}else if(threadName.equals("thread2")){
						 join(C12, R, 0 , n/2);
					}else if(threadName.equals("thread3")){
						 join(C21, R, n/2, 0);
					}else if(threadName.equals("thread4")){
						 join(C22, R, n/2, n/2);
					}

				}

				public void start(){
					if(t==null){
						t=new Thread(this,"Thread1");
						t.start();
					}
				}
			}
			Mul2 m11=new Mul2("thread1");
			m11.start();
			Mul2 m22=new Mul2("thread2");
			m22.start();
			Mul2 m33=new Mul2("thread3");
			m33.start();
			Mul2 m44=new Mul2("thread4");
			m44.start();
			for (Thread t : new Thread[] { m11.t,m22.t,m33.t,m44.t }){
				try{
					t.join();
				}catch(Exception e){
					System.out.println(e);
				}
			}
		}
		return R;
	}

    public static void split(int[][]P,int[][]C,int iB,int jB){
		int i2 = iB;
		for(int i1=0;i1< C.length;i1++){
			int j2 = jB;
			for(int j1=0; j1<C.length;j1++){
				C[i1][j1] = P[i2][j2];
				j2++;
			}
			i2++;
		}
	}

    public static int[][] add(int[][] a,int[][] b){
		int n = a.length;
		int[][] c = new int[n][n];
		for(int i = 0;i<n;i++){
			for(int j=0;j<n;j++)
				c[i][j] = a[i][j] + b[i][j];
		}
	return c;
	}

    public static int[][] sub(int[][] a,int[][] b){
		int n = a.length;
		int[][] c = new int[n][n];
		for(int i = 0;i<n;i++){
			for(int j=0;j<n;j++)
				c[i][j] = a[i][j] - b[i][j];
		}
	return c;
	}
    public static void join(int[][]P,int[][]C,int iB,int jB){
		int i2 = iB;
		for(int i1=0;i1< P.length;i1++){
			int j2 = jB;
			for(int j1=0; j1< P.length;j1++){
				C[i2][j2] = P[i1][j1];
				j2++;
			}
			i2++;
		}
    }
}
