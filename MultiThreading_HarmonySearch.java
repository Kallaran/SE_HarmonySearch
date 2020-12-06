
/* MultiThreading_HarmonySearch class
 *
 * binMeta project
 *
 * last update: Nov 25, 2020
 *
 * Jean DERIEUX
 */

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*; 


public class MultiThreading_HarmonySearch extends binMeta
{

	protected int harmonyMemorySize;               // Harmony memory size
    protected int pitchAdjusting;                  // Pitch adjusting
    protected double harmonyMemoryConsideration;    // Harmony memory consideration

   // MultiThreading_HarmonySearch constructor
   public MultiThreading_HarmonySearch(Data startPoint,Objective obj,long maxTime,int harmonyMemorySize,int pitchAdjusting,double harmonyMemoryConsideration)
   {
      try
      {
         String msg = "Impossible to create MultiThreading_HarmonySearch object: ";
         if (maxTime <= 0) throw new Exception(msg + "the maximum execution time is 0 or even negative");
         this.maxTime = maxTime;
         if (startPoint == null) throw new Exception(msg + "the reference to the starting point is null");
         this.solution = startPoint;
         if (obj == null) throw new Exception(msg + "the reference to the objective is null");
         this.obj = obj;
         this.objValue = this.obj.value(this.solution);
         this.metaName = "MultiThreading_HarmonySearch";
	     if (harmonyMemorySize <= 0) throw new Exception(msg + "the harmonyMemorySize is 0 or even negative");
	     this.harmonyMemorySize = harmonyMemorySize;
 	     if (pitchAdjusting <= 0) throw new Exception(msg + "the pitchAdjusting is 0 or even negative");
 	     this.pitchAdjusting = pitchAdjusting;
 	     if (harmonyMemoryConsideration < 0.0 || harmonyMemoryConsideration > 1.0) throw new Exception("Specified probability of harmonyMemoryConsideration should be contained in [0,1]");
 	     this.harmonyMemoryConsideration = harmonyMemoryConsideration;

      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
   }

   @Override
   public void optimize()  // by MultiThreading_HarmonySearch
   {
      long startime = System.currentTimeMillis();
      Semaphore sem = new Semaphore(1);


      ThreadAgent T1 = new ThreadAgent(startime, this, sem);
      ThreadAgent T2 = new ThreadAgent(startime, this, sem);

      T1.start();
      T2.start();



      try 
      {
        T1.join();
        T2.join();
      } 
      catch(InterruptedException e)
      {
	  	e.printStackTrace();
        System.exit(1);

      }


   }

   class ThreadAgent extends Thread {

      private MultiThreading_HarmonySearch mhs;
      private long startime;
      private Semaphore sem; 


      ThreadAgent(long startime,MultiThreading_HarmonySearch mhs,Semaphore sem) {
         this.startime = startime;
         this.mhs = mhs;
         this.sem = sem;
      }  

    

      @Override
      public void run() {

         Random R = new Random();
         Data newHarmony;
         
         //Initialize the harmony memory randomly
         Data [] harmonyMemory = new Data[mhs.harmonyMemorySize];   
         for(int i = 0 ; i<harmonyMemory.length ; i++){
            harmonyMemory[i] = new Data(mhs.solution.numberOfBits(), 0.5);
         }

         // main loop
         while (System.currentTimeMillis() - startime < mhs.maxTime)
         {
         	// Choose to considerate or not the harmony memory
         	if( R.nextDouble() < mhs.harmonyMemoryConsideration){
	         	// Improvise a new harmony close to a random harmony in harmony memory
	            int number = R.nextInt(mhs.harmonyMemorySize);
	            Data selectedHarmony = harmonyMemory[number];
	            int adjusting = 1 + R.nextInt(mhs.pitchAdjusting);
	            newHarmony = selectedHarmony.randomSelectInNeighbour(adjusting);
         	}
         	else{
         		// Choose to not considerate the harmony memory
          		newHarmony = new Data(mhs.solution.numberOfBits(), 0.5);
         	}
                    

            // Looking for the worst harmony
            double worstValue = obj.value(harmonyMemory[0]);
            int worstValuePosition = 0;
            for(int i = 0 ; i<harmonyMemory.length ; i++){
               if(obj.value(harmonyMemory[i]) > worstValue ){
                  worstValue = obj.value(harmonyMemory[i]);
                  worstValuePosition = i;
               }
            }

            // Is new harmony better than the worst harmony of harmonyMemory ?
            double valueNewHarmony = obj.value(newHarmony);
            if (worstValue > valueNewHarmony){
               harmonyMemory[worstValuePosition] = newHarmony;
            }

            // Looking for the best harmony
            double bestValue = obj.value(harmonyMemory[0]);
            int bestValuePosition = 0;
            for(int i = 0 ; i<harmonyMemory.length ; i++){
               if(obj.value(harmonyMemory[i]) < bestValue ){
                  bestValue = obj.value(harmonyMemory[i]);
                  bestValuePosition = i;
               }
            }

            // Update with the best Harmony
            try
            {
            	sem.acquire();
	            if (mhs.objValue > bestValue) 
	            {
	               mhs.objValue = bestValue;
	               mhs.solution = new Data(harmonyMemory[bestValuePosition]);
	            }
            }
            catch(InterruptedException e)
            {
	        	e.printStackTrace();
	        	System.exit(1);

            }            
            sem.release();

         }

      }


   }

   // main
   public static void main(String[] args)
   {
      int ITMAX = 10000;  // Number of iterations
      int HMSIZE = 10;    // Harmony memory size
      int PA = 3;         // Pitch adjusting
      double HMC = 0.9;   // Harmony memory consideration 

      // BitCounter
      int n = 8000;
      Objective obj = new BitCounter(n);
      Data D = obj.solutionSample();
      MultiThreading_HarmonySearch hs = new MultiThreading_HarmonySearch(D,obj,ITMAX,HMSIZE,PA,HMC);
      System.out.println(hs);
      //System.out.println("starting point : " + hs.getSolution());
      System.out.println("optimizing ...");
      hs.optimize();
      System.out.println(hs);
      //System.out.println("solution : " + hs.getSolution());
      System.out.println();

      // Fermat
      int exp = 2;
      int ndigits = 10;
      obj = new Fermat(exp,ndigits);
      D = obj.solutionSample();
      hs = new MultiThreading_HarmonySearch(D,obj,ITMAX,HMSIZE,PA,HMC);
      System.out.println(hs);
      System.out.println("starting point : " + hs.getSolution());
      System.out.println("optimizing ...");
      hs.optimize();
      System.out.println(hs);
      System.out.println("solution : " + hs.getSolution());
      Data x = new Data(hs.solution,0,ndigits-1);
      Data y = new Data(hs.solution,ndigits,2*ndigits-1);
      Data z = new Data(hs.solution,2*ndigits,3*ndigits-1);
      System.out.print("equivalent to the equation : " + x.posLongValue() + "^" + exp + " + " + y.posLongValue() + "^" + exp);
      if (hs.objValue == 0.0)
         System.out.print(" == ");
      else
         System.out.print(" ?= ");
      System.out.println(z.posLongValue() + "^" + exp);
      System.out.println();

      // ColorPartition
      n = 4;  int m = 14;
      ColorPartition cp = new ColorPartition(n,m);
      D = cp.solutionSample();
      hs = new MultiThreading_HarmonySearch(D,cp,ITMAX,HMSIZE,PA,HMC);
      System.out.println(hs);
      System.out.println("starting point : " + hs.getSolution());
      System.out.println("optimizing ...");
      hs.optimize();
      System.out.println(hs);
      System.out.println("solution : " + hs.getSolution());
      cp.value(hs.solution);
      System.out.println("corresponding to the matrix :\n" + cp.show());
   }
}

