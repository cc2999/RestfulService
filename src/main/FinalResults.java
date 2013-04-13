package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import searchEngine.YahooBossSearch;
import evaluator.Evaluator;
import extractor.Competitor;
import extractor.PageAnalysor;
import extractor.Pair;

public class FinalResults {
	
	private String target;
	private TreeSet<Competitor> competitorSet;
	private static final String cacheDir = "C:\\cheng\\workspace\\CompetitorUI\\sample\\";
	
	public FinalResults(String query){
		File dir = new File(cacheDir);
		if(!dir.exists())
			dir.mkdirs();
		this.target = query;
		this.competitorSet = null;
		Boolean isExist = false;
		
		System.out.println("Starting Searching....");
		File cache = new File(cacheDir+query);
		if(cache.exists())
			isExist = true;
		else
			isExist = false;
		
		if(isExist)
		{
			try{
				Thread.sleep(5000);
				this.competitorSet = new TreeSet<Competitor>();
				BufferedReader breader = new BufferedReader(new FileReader(cache));
				String strline = null;
				while((strline=breader.readLine()) != null){
					if(strline.equals("$"))
					{
						Competitor c = new Competitor();
						strline=breader.readLine();
						c.setName(strline);
						strline=breader.readLine();
						c.setScore(new Double(strline));
						for(int i=0;i<10;i++)
						{
							String title = breader.readLine();
							String url = breader.readLine();
							Pair pair = new Pair(url,title);
							c.addUrlPair(pair);
						}
						this.competitorSet.add(c);
						
					}
				}
			
			}catch(Exception e)
			{
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
			
		}
		else{
			try{
				YahooBossSearch searcher = new YahooBossSearch();
				searcher.setEntity(target);
				searcher.DoSearch();
				PageAnalysor analysor = new PageAnalysor(target);
				analysor.testPattern();
				Vector<Competitor> vec = analysor.getCandidates();
				Evaluator evaluator = new Evaluator(target, vec);
				evaluator.computeScore();
				TreeSet<Competitor> set = evaluator.getCompetitors();
				this.competitorSet = set;
				evaluator.displayList(set);
				//cache the file
				
					cache.createNewFile();
					PrintWriter bwriter = new PrintWriter(cache);
					Iterator<Competitor> iter = set.descendingIterator();
					while(iter.hasNext()){
						Competitor c = iter.next();
						bwriter.println("$");
						bwriter.println(c.getName());
						bwriter.println(c.getScore());
						for(Pair pair : c.getAttachedUrlPairs())
						{

							bwriter.println(pair.title);
							bwriter.println(pair.url);
						}
					}
					bwriter.close();
				
				System.out.println("End!");
			}catch(Exception e){
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	

	public TreeSet<Competitor> getCompetitorSet()
	{
		return this.competitorSet;
	}
	
	public TreeSet<Competitor> getCompetitors(){
		return this.competitorSet;
	}
	
	public static void main(String[] args) {
		String target = "Alfred Aho";
		
		System.out.println("Starting Searching....");
		try{
			YahooBossSearch searcher = new YahooBossSearch();
			searcher.setEntity(target);
			searcher.DoSearch();
			//System.out.println("hehe");
			PageAnalysor analysor = new PageAnalysor(target);
		
			analysor.testPattern();
			Vector<Competitor> vec = analysor.getCandidates();
			Evaluator evaluator = new Evaluator(target, vec);
			evaluator.computeScore();
			TreeSet<Competitor> set = evaluator.getCompetitors();
			evaluator.displayList(set);
			System.out.println("End!");
		}catch(Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
