
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

public class MultiThreading_HarmonySearch extends binMeta
{
   // MultiThreading_HarmonySearch constructor
   public MultiThreading_HarmonySearch(Data startPoint,Objective obj,long maxTime)
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
      Random R = new Random();
      long startime = System.currentTimeMillis();
      int HMSIZE = 10; // Harmony memory size
      int PA = 3;      // Pitch adjusting

      //Initialize the harmony memory randomly
      Data [] harmonyMemory = new Data[HMSIZE];   
      for(int i = 0 ; i<harmonyMemory.length ; i++){
         harmonyMemory[i] = new Data(this.solution);
      }

      // main loop
      while (System.currentTimeMillis() - startime < this.maxTime)
      {
         // Improvise a new harmony close to a random harmony in harmony memory
         int number = R.nextInt(HMSIZE);
         Data selectedHarmony = harmonyMemory[number];
         int adjusting = 1 + R.nextInt(PA);
         Data newHarmony = selectedHarmony.randomSelectInNeighbour(adjusting);

         // Looking for the worst harmony
         double worstValue = obj.value(harmonyMemory[0]);
         int worstValuePosition = 0;
         for(int i = 0 ; i<harmonyMemory.length ; i++){
            if(obj.value(harmonyMemory[i]) > worstValue ){
               worstValue = obj.value(harmonyMemory[i]);
               worstValuePosition = i;
            }
         }

         // If newharmony is better than the worst harmony of harmonyMemory then replace worst harmony with newharmony
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
         if (this.objValue > bestValue)
         {
            this.objValue = bestValue;
            this.solution = new Data(harmonyMemory[bestValuePosition]);
         }

      }
   }

   // main
   public static void main(String[] args)
   {
      int ITMAX = 10000;  // number of iterations

      // BitCounter
      int n = 50;
      Objective obj = new BitCounter(n);
      Data D = obj.solutionSample();
      MultiThreading_HarmonySearch hs = new MultiThreading_HarmonySearch(D,obj,ITMAX);
      System.out.println(hs);
      System.out.println("starting point : " + hs.getSolution());
      System.out.println("optimizing ...");
      hs.optimize();
      System.out.println(hs);
      System.out.println("solution : " + hs.getSolution());
      System.out.println();

      // Fermat
      int exp = 2;
      int ndigits = 10;
      obj = new Fermat(exp,ndigits);
      D = obj.solutionSample();
      hs = new MultiThreading_HarmonySearch(D,obj,ITMAX);
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
      hs = new MultiThreading_HarmonySearch(D,cp,ITMAX);
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

