package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.FinalResults;
import extractor.Competitor;
import extractor.Pair;

public class CompetitorServlet extends javax.servlet.http.HttpServlet{
	
	private static final long serialVersionUID = (-1L);
	
	public CompetitorServlet() {
	}	

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		if ( req.getParameter("query") != null ) {
			// Get competitor results according to query
			String query = req.getParameter("query");
			PrintWriter writer = resp.getWriter();
			String result = getCompetitorResults(query);
			
			result = new String(result.getBytes(), "UTF-8");
			System.out.println("result = " + result);
			writer.print(result);
			writer.close();
		}
	}
	
	private String getCompetitorResults(String query) {
		try {
			StringBuilder sb = new StringBuilder();
			FinalResults finalResult = new FinalResults(query);
			
			TreeSet<Competitor> competitors = finalResult.getCompetitors();
			Iterator<Competitor> iter = competitors.descendingIterator();
		
			int count = 10;
			int subCount = 3;
				
			sb.append("{");
			sb.append(" competitors: [");
			int i = 0;
			while (iter.hasNext()) {
				Competitor cn = iter.next();
				Vector<Pair> attached = cn.getAttachedUrlPairs();
				sb.append("{ name:\"" + cn.getName() + "\",");
				sb.append(" attached: [");
				for (int j = 0; j < subCount; j++) {
					sb.append("{ url:\"" + attached.get(j).url + "\", title:\"" + attached.get(j).title + "\" }");
					if ( j < subCount-1 ) sb.append(",");
				}
				sb.append("]}");
				if ( i < count-1 ) sb.append(",");
				if ( ++i >= count ) break;
			}
			if(i < count)
			{
				int index = sb.lastIndexOf(",");
				sb.deleteCharAt(index);
			}
			sb.append("]}");						
			return sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}