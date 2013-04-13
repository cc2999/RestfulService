/*
 * This class is for Competitor
 * patternCounter field indicate the number of occurences for each of 7 patterns
 * 
 * 
 * 
 */

package extractor;
import java.util.Vector;

public class Competitor implements Comparable<Object>{
	
	public static final int patternnNumber = 7;
	private String name;
	private double score;
	private Vector<Pair> attachedUrls;
	private int[] patternCounter;
	
	public Competitor(){
		this.attachedUrls = new Vector<Pair>();
		this.patternCounter = new int[patternnNumber];
	}
	
	public Competitor(String name){
		this.name = name;
		this.score = 0;
		this.attachedUrls = new Vector<Pair>();
		this.patternCounter = new int[patternnNumber];
	}
	
	//@Override
	public int compareTo(Object o){
		Competitor other = (Competitor) o;
		double temp = this.score - other.score;
		if(temp > 0.0){
			return 1;
		}else if(temp == 0.0){
			return 0;
		}else{
			return -1;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getpatternCounter() {
		return patternCounter;
	}

	public void setpatternCounter(int[] patternCounter) {
		this.patternCounter = patternCounter;
	}
	
	public void updatepatternCounter(int pos, int add) {
		this.patternCounter[pos] += add;
	}
	
	public void mergepatternCounter(int add[]) {
		for (int i=0; i < this.patternnNumber; i++) 
			this.patternCounter[i] += add[i];
	}
	
	public void clearpatternCounter() {
		for (int i=0; i < this.patternnNumber; i++) 
			this.patternCounter[i] = 0;
	}
	
	public int getTotalCounter() {
		int total = 0;
		for (int i=0; i < this.patternnNumber; i++) 
			total += this.patternCounter[i];
		return total;
	}
	
	public int getPatternNumber() {
		int res = 0;
		for (int i=0; i < this.patternnNumber; i++) 
			if ( this.patternCounter[i] > 0 ) res++;
		return res;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Vector<Pair> getAttachedUrlPairs() {
		return attachedUrls;
	}
	
	public void addUrlPair(Pair url){
		this.attachedUrls.add(url);
	}
	
		
}
