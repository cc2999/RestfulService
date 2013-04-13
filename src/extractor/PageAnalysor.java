package extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageAnalysor {
	
	private HashSet<String> hs;
	private HashMap<String, Integer> hm;
	private HashSet<String> nameSet;
	private HashMap<String, Competitor> candidateMap;
	private Vector<Competitor> candidates;
	
	public HashSet<String> stopSets = new HashSet<String>();
	
	private String entity;
	private int pcount = 0;
	private final static String currentFolder= "C:\\cheng\\workspace\\CompetitorUI";
	
	public PageAnalysor(String target) {
		hs = new HashSet<String>();
		hm = new HashMap<String, Integer>();
		nameSet = new HashSet<String>();
		candidateMap = new HashMap<String, Competitor>();
		candidates = new Vector<Competitor>();
		pcount = -1;
		entity = target;
		
		String fileName = currentFolder + "\\stop words";
		String temp;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			
			while ( (temp = reader.readLine()) != null )		
				stopSets.add(temp);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String extractCN(String match, int pcount) {		
		String cn = "";
		String[] ss;
		
		if ( pcount == 0 ) {
			ss = match.split(" [v|V][s|S](.)* ");
			cn = ss[1];
		}
		else if ( pcount == 1){
			ss = match.split(" [v|V][s|S](.)* ");
			cn = ss[0];
		}
		else if ( pcount == 2 ){
			ss = match.split(" [o|O][r|R] ");
			cn = ss[1];
		}
		else if ( pcount == 3 ){
			ss = match.split(" [o|O][r|R] ");
			cn = ss[0];		
		}
		else if ( pcount > 3 && pcount < 7 ){
			ss = match.split(" [a|A]nd ");
			cn = ss[1];
		}		
		return cn;
	}
	
	public void patternMatch(String input, String patternString) {
		
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(input);
	
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String match = input.substring(start, end);
			hs.add(match);
			if ( !hm.containsKey(match)) {
				hm.put(match, 1);
			}
			else {
				hm.put(match, hm.get(match).intValue()+1);
			}			
		}

	}
	
	public void generateCNs(int pcount) {
				
		Iterator<String> iter = hs.iterator();
		String match, cn;
		while ( iter.hasNext() ) {
			match = iter.next();
			cn = extractCN(match, pcount);
			nameSet.add(cn);
			if ( !candidateMap.containsKey(cn) ) {
				Competitor candidate = new Competitor(cn);
				candidate.updatepatternCounter(pcount, hm.get(match).intValue());	
				candidateMap.put(cn, candidate);
			}
			else {				
				candidateMap.get(cn).updatepatternCounter(pcount, hm.get(match).intValue());			
			}
		}		
	}
	
	public void parsePage(String patternString) throws Exception {		
		
		pcount ++;
		hs.clear();
		hm.clear();
		
		File f = new File(currentFolder + "\\source\\" + entity);		
		File[] files = f.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			StringBuffer sb = new StringBuffer();
			
			BufferedReader br = new BufferedReader(new FileReader(files[i]));
			
			String line;
			while ( (line = br.readLine()) != null ) {
				sb.append(line + "\n");
			}
			patternMatch(sb.toString(), patternString);
		}	
		
		Iterator<String> iter = hs.iterator();
		String temp;
		System.out.println("\nRelExp match results for pattern " + pcount + ":\n");
		int count = 0;
		while ( iter.hasNext() ) {
			count++;
			temp = iter.next();
			System.out.println(count + ":" + temp + "\t" + hm.get(temp).intValue());
		}
		
		generateCNs(pcount);
	}	
	
	public void displayCandidateMap() {
		Iterator<String> iter = nameSet.iterator();
		String name;
		int count = 0;
		
		System.out.println("\nCandidate Lists:");
		
		while ( iter.hasNext() ) {
			count++;
			name = iter.next();
			System.out.print(count + ":" + name + " ");
			int[] counters = candidateMap.get(name).getpatternCounter();
			for (int i=0; i < Competitor.patternnNumber; i++) {
				System.out.print(" " + counters[i]);
			}
			System.out.println();
		}
	}
	
	public String getPrefix(String s1, String s2) {
		String res = null;
		String ss1[], ss2[];
		int pos = -1;
		
		ss1 = s1.split(" ");
		ss2 = s2.split(" ");
		if ( ss1.length > 1 && ss2.length > 1 ) {
			int k = 0;
			while ( ss1[k].equalsIgnoreCase(ss2[k]) ) {
				pos = k;
				k++;
				if ( k >= ss1.length || k >= ss2.length ) break;
			}
			if ( pos == -1 ) return null;
			res = ss1[0];
			for ( k = 1; k <= pos; k++ )
				res = res + " " + ss1[k];
		}		
		return res;
	}
	
	public void candidateFilter() {
		HashSet<String> brandList = new HashSet<String>();
		
		Iterator<String> iter = nameSet.iterator();
		while ( iter.hasNext() ) {
			candidates.add(candidateMap.get(iter.next()));
		}					
		
		Vector<Competitor> temp = new Vector<Competitor>();
		//Filte candidates with total counter <= 1
		for (int i = 0; i < candidates.size(); i++)
			if ( candidates.get(i).getTotalCounter() > 1 )
				temp.add(candidates.get(i));
		candidates.clear();
		candidates.addAll(temp);
		
		displayCandidates();
		
		//Merge candidates according to same prefix
		temp.clear();
		String s;
		for (int i = 0; i < candidates.size()-1; i++) 
			for (int j = i+1; j < candidates.size(); j++) {
				if ( candidates.get(i).getName().equalsIgnoreCase(candidates.get(j).getName())) {
					candidates.get(i).mergepatternCounter(candidates.get(j).getpatternCounter());
					candidates.get(j).clearpatternCounter();
				}
				
				s = getPrefix(candidates.get(i).getName(), candidates.get(j).getName());
				if ( s == null ) continue;
				for ( int k = 0; k < candidates.size(); k++ ) {
					if ( candidates.get(k).getName().equalsIgnoreCase(s)) {
						if ( candidates.get(i).getPatternNumber() >= candidates.get(k).getPatternNumber() 
						    || candidates.get(j).getPatternNumber() >= candidates.get(k).getPatternNumber() )
							continue;
						brandList.add(s);
						candidates.get(k).mergepatternCounter(candidates.get(i).getpatternCounter());
						candidates.get(k).mergepatternCounter((candidates.get(j).getpatternCounter()));
						candidates.get(i).clearpatternCounter();
						candidates.get(j).clearpatternCounter();
					}
				}
			}
		for (int i = 0; i < candidates.size(); i++)
			if ( candidates.get(i).getTotalCounter() > 0 )
				temp.add(candidates.get(i));
		candidates.clear();
 		candidates.addAll(temp);
		
		displayCandidates();
		
		//Filter candidates appear in only one pattern
		temp.clear();
		for (int i = 0; i < candidates.size(); i++)
			if ( candidates.get(i).getPatternNumber() > 1 && !candidates.get(i).getName().equalsIgnoreCase(entity))
				temp.add(candidates.get(i));
		candidates.clear();
		candidates.addAll(temp);
		displayCandidates();
		
		//Filter candidates which contains stopwords
		temp.clear();
		for (int i = 0; i < candidates.size(); i++) {
			String st = candidates.get(i).getName();
			String[] ss = st.split(" ");
			boolean bool = true;
			for (int j=0; j<ss.length; j++)
				if ( stopSets.contains(ss[j].toLowerCase())) {
					bool = false;
					break;
				}
			if ( bool ) 
				temp.add(candidates.get(i));
		}
		candidates.clear();
		candidates.addAll(temp);
		displayCandidates();
		
		// Merge candidates who is contained by another 
		temp.clear();
		for (int i = 0; i < candidates.size()-1; i++) 
			for (int j = i+1; j < candidates.size(); j++) {
				if ( candidates.get(i).getName().contains(candidates.get(j).getName()) ) {
					candidates.get(i).mergepatternCounter(candidates.get(j).getpatternCounter());
					candidates.get(j).clearpatternCounter();
				}
				else if ( candidates.get(j).getName().contains(candidates.get(i).getName()) ) {
					candidates.get(j).mergepatternCounter(candidates.get(i).getpatternCounter());
					candidates.get(i).clearpatternCounter();
				}
			}
		for (int i = 0; i < candidates.size(); i++)
			if ( candidates.get(i).getTotalCounter() > 0 )
				temp.add(candidates.get(i));
		candidates.clear();
		candidates.addAll(temp);
		
		displayCandidates();
	
		//Eliminate the candidates which contain the target
		//For example: entity=Xbox eliminate the Xbox 360
		temp.clear();
		int length = entity.length();
		for(int i=0;i<candidates.size();i++){
			String candidateName = candidates.get(i).getName();
			if(candidateName.length()>= length){
				String prefix = candidateName.substring(0, length);
				if(entity.compareToIgnoreCase(prefix)==0){
					temp.add(candidates.get(i));
				}
			}
		}
		candidates.removeAll(temp);
		displayCandidates();
		
		
//		System.out.println("\n Brand Candidates:");
//		iter = brandList.iterator();
//		while (iter.hasNext()) {
//			System.out.println(iter.next());
//		}
	}
	
	public Vector<Competitor> getCandidates(){
		return this.candidates;
	}
	
	public void displayCandidates() {
		
		System.out.println("\nCandidate Lists:");
		
		for (int i = 0; i < candidates.size(); i++) {
			System.out.print(i + ":" + candidates.get(i).getName() + " ");
			int[] counters = candidates.get(i).getpatternCounter();
			for (int j=0; j < Competitor.patternnNumber; j++) {
				System.out.print(" " + counters[j]);
			}
			System.out.println();
		}
	}
	
	public void testPattern() throws Exception {
		String patternString1 = entity+" [v|V][s|S][.]* [A-Z]\\w*( [A-Z|0-9]\\w+){0,2}";
		parsePage(patternString1);
	
		String patternString2 = "[A-Z]\\w*( [A-Z|0-9]\\w+){0,2} [v|V][s|S][.]* "+entity;
		parsePage(patternString2);
		
		String patternString3 = entity+" [o|O][r|R] [A-Z]\\w*( [A-Z|0-9]\\w+){0,2}";
		parsePage(patternString3);
		
		String patternString4 = "[A-Z]\\w*( [A-Z|0-9]\\w+){0,2} [o|O][r|R] "+entity;
		parsePage(patternString4);
		
		String patternString5 = "[s|S]uch as "+entity+"(\\s*,\\s*\\w+)*(,)* [a|A]nd [A-Z]\\w*( [A-Z|0-9]\\w+){0,2}";		
		parsePage(patternString5);
		
		String patternString6 = "[e|E]specially "+entity+"(\\s*,\\s*\\w+)*(,)* [a|A]nd [A-Z]\\w*( [A-Z|0-9]\\w+){0,2}";
		parsePage(patternString6);
		
		String patternString7 = "[i|I]ncluding "+entity+"(\\s*,\\s*\\w+)*(,)* [a|A]nd [A-Z]\\w*( [A-Z|0-9]\\w+){0,2}";
		parsePage(patternString7);
		
		displayCandidateMap();
		candidateFilter();
	}
	
	
}
