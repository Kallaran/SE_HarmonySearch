
# binMeta project

all: basic objectives methods

basic: Data.class Objective.class

objectives: BitCounter.class ColorPartition.class Fermat.class

methods: binMeta.class RandomWalk.class Sequential_HarmonySearch.class MultiThreading_HarmonySearch.class

binMeta.class: binMeta.java Data.class Objective.class
	javac -cp . binMeta.java

BitCounter.class: BitCounter.java Data.class Objective.class
	javac -cp . BitCounter.java

ColorPartition.class: ColorPartition.java Data.class Objective.class
	javac -cp . ColorPartition.java

Data.class: Data.java
	javac -cp . Data.java

Fermat.class: Fermat.java Data.class Objective.class
	javac -cp . Fermat.java

Objective.class: Objective.java
	javac -cp . Objective.java

RandomWalk.class: RandomWalk.java binMeta.class BitCounter.class ColorPartition.class Fermat.class
	javac -cp . RandomWalk.java

Sequential_HarmonySearch.class: Sequential_HarmonySearch.java binMeta.class BitCounter.class ColorPartition.class Fermat.class
	javac -cp . Sequential_HarmonySearch.java

MultiThreading_HarmonySearch.class: MultiThreading_HarmonySearch.java binMeta.class BitCounter.class ColorPartition.class Fermat.class
	javac -cp . MultiThreading_HarmonySearch.java

clean:
	rm -rf *.class

