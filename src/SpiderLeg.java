import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg
{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    



    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public void crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;          
            
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
            	//System.out.println(url);

                //System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                //System.out.println("**Failure** Retrieved something other than HTML");
                return;
            }
            Long start = System.nanoTime();
            Elements linksOnPage = htmlDocument.select("a[href]");
            Long end = System.nanoTime();
            //System.out.println("Time taken to crawl this webpage "+ (end-start)+" ns");
            
            //System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                this.links.add(link.absUrl("href"));
            }
            return;
        }
        catch(IOException ioe)
        {
        	
            // We were not successful in our HTTP request
            return;
        }
        catch(IllegalArgumentException i)
        {
        	//System.out.println("Link cannot be openend\n");
        }
        catch(NullPointerException i)
        {
        	//System.out.println("Link cannot be openend\n");
        }
    }


    /**
     * Performs a search on the body of on the HTML document that is retrieved. This method should
     * only be called after a successful crawl.
     * 
     * @param searchWord
     *            - The word or string to look for
     * @return whether or not the word was found
     */
    public int searchForWord(String searchWord)
    {
    	searchWord= searchWord.toLowerCase();
        // Defensive coding. This method should only be used after a successful crawl.
        if(this.htmlDocument == null)
        {
            //System.out.println("HTML page could not be retrieved\n");
            return 0;
        }
        //System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        //return bodyText.toLowerCase().contains(searchWord.toLowerCase());
        Pattern p = Pattern.compile(searchWord.toLowerCase());
        Matcher matcher = p.matcher(bodyText.toLowerCase());
        int count=0;
        Long start = System.nanoTime();
        while(matcher.find())
        {
        	count++;
        }
        Long end = System.nanoTime();
        //System.out.println("Time taken to search word "+ searchWord+ " is "+ (end-start)+" ns");
        return count;
    }


    public List<String> getLinks()
    {
        return this.links;
    }

}