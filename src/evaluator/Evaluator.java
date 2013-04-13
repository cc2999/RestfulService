package evaluator;

import java.util.Vector;
import java.util.TreeSet;
import java.util.Iterator;
import extractor.Competitor;
import searchEngine.*;
import extractor.Pair;

public class Evaluator {
	private String targetEntity;
	private Vector<Competitor> competitorList;
	private final static int urlNumber = 10;

	//we must know the target entity name first
	public Evaluator(String target, Vector<Competitor> vec){
		this.targetEntity = target;
		this.competitorList = vec;
	}
	
	
	public void computeScore() throws Exception{
		int targetHits = 0;
		int combinationHits = 0;
		targetHits = YahooBossSearch.countHits(this.targetEntity);
		for(Competitor c : competitorList)
		{
			combinationHits = YahooBossSearch.countHits(this.targetEntity+"%20"+c.getName());
			c.setScore(1.0*combinationHits/targetHits);
			//associate corresponding urls with competitor
			Vector <Pair> vector = YahooBossSearch.getTargetURL(c.getName(), urlNumber);
			for(Pair urlPair : vector){
				c.addUrlPair(urlPair);
			}
		}		
	}
	//get top n competitors according to the score
	public TreeSet<Competitor> getCompetitors(){
		TreeSet<Competitor> set = new TreeSet<Competitor>(competitorList);
		return set;
	}
	
	public void displayList(TreeSet<Competitor> set){
		Iterator<Competitor> iter = set.descendingIterator();
		while(iter.hasNext()){
			Competitor c = iter.next();
			System.out.println("Name : "+c.getName());
			System.out.println("Score : "+c.getScore());
			System.out.println("URLs : ");
			for(Pair urlPair : c.getAttachedUrlPairs()){
				System.out.println("   "+urlPair.title+":");
				System.out.println("   "+urlPair.url);
			}
			System.out.println("------------------------");
		}
	}
}
