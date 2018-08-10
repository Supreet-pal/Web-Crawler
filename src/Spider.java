import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.rmi.CORBA.Util;

public class Spider
{
  private static final int MAX_PAGES_TO_SEARCH = 200;
  private HashMap<String,Integer> pagesVisited = new HashMap<String,Integer>();
  private List<String> pagesToVisit = new LinkedList<String>();
  ValueComparator bvc =  new ValueComparator(pagesVisited);
  TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
  Long total_crawl_time=(long) 0;
  Long total_search_time=(long) 0;


  /**
   * Our main launching point for the Spider's functionality. Internally it creates spider legs
   * that make an HTTP request and parse the response (the web page).
   * 
   * @param url
   *            - The starting point of the spider
   * @param searchWord
   *            - The word or string that you are searching for
   */
  public  void search(String url, String searchWord)
  {
	  try 
	  {
		FileWriter writer = new FileWriter("CNN_100.txt");
		while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH)
		{
          String currentUrl;
          SpiderLeg leg = new SpiderLeg();
          if(this.pagesToVisit.isEmpty())
          {
              currentUrl = url;
              this.pagesVisited.put(url,0);
          }
          else
          {
              currentUrl = this.nextUrl();
          }
          Long start = System.nanoTime();
          leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
                                 // SpiderLeg
          Long end = System.nanoTime();
          total_crawl_time = total_crawl_time+(end-start);
          writer.write(currentUrl);
          writer.write(System.getProperty("line.separator"));
          
          Long start1 = System.nanoTime();
          int count = leg.searchForWord(searchWord);
          Long end1 = System.nanoTime();
          total_search_time = total_search_time+(end1-start1);
          
          if(count>0)
          {
        	  pagesVisited.put(currentUrl, pagesVisited.get(currentUrl)+count);
              //System.out.println(String.format("**Success** Word %s found at %s %d number of times", searchWord, currentUrl, count));
              //break;
          }
          this.pagesToVisit.addAll(leg.getLinks());
      }
      //System.out.println("\n**Done** Visited " + this.pagesVisited.size() + " web page(s)");
      
      sorted_map.putAll(pagesVisited);
      ArrayList<String> list = new ArrayList<String>() ;
      for (Entry<String, Integer> entry : sorted_map.entrySet()) 
      {
    	  String key = entry.getKey();
    	  list.add(key); 
    	  if(list.size()==10)
    		  break;
    	    // Do things with the list
      }
      System.out.println(list);
      //System.out.println("\n\n\n\n\nNow will print sorted map\n");

      //System.out.println("results: "+sorted_map);
      
      SortedMap<String, Integer> firstTen = putFirstEntries(10, sorted_map);
      
      writer.close();
      
      //System.out.println("Average crawling time "+ total_crawl_time/MAX_PAGES_TO_SEARCH);
      //System.out.println("Average search time "+ total_search_time/MAX_PAGES_TO_SEARCH);
      System.out.println("Searched for: "+searchWord);
      System.out.println("\nTop 10 results:\n");

      System.out.println("results: "+firstTen);
	  } 
	  catch (IOException e) 
	  {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	  catch (NullPointerException e) 
	  {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
  }

  
  
  public static SortedMap<String,Integer> putFirstEntries(int max, SortedMap<String,Integer> source) 
  {
	  int count = 0;
	  TreeMap<String,Integer> target = new TreeMap<String,Integer>();
	  for (Map.Entry<String,Integer> entry:source.entrySet()) 
	  {
	     if (count >= max) break;

	     target.put(entry.getKey(), entry.getValue());
	     count++;
	  }
	  return target;
	}

  /**
   * Returns the next URL to visit (in the order that they were found). We also do a check to make
   * sure this method doesn't return a URL that has already been visited.
   * 
   * @return
   */
  private String nextUrl()
  {
      String nextUrl;
      do
      {
          nextUrl = this.pagesToVisit.remove(0);
      } while(this.pagesVisited.containsKey(nextUrl));
      this.pagesVisited.put(nextUrl,0);
      return nextUrl;
  }
  
  public static void main(String[] args )
  {
	  String url1 = "http://windsorstar.com";
	  String url2 = "http://www.bbc.com/news";
	  String url3 = "https://www.cnn.com/";
	  Spider spid = new Spider();
	  spid.search(url1, "Tim Hortons");
	  
  }
}



class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(HashMap<String, Integer> pagesVisited) {
        this.base = pagesVisited;
    }
  
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}