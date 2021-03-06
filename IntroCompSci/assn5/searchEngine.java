import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

	public HashMap<String, LinkedList<String> > wordIndex;                  					// this will contain a set of pairs (String, LinkedList of Strings)	
	public directedGraph internet;             													// this is our internet graph

	// Constructor initializes everything to empty data structures
	// It also sets the location of the internet files
	searchEngine() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String> > ();		
		internet = new directedGraph();				
	} // end of constructor2014

	// Returns a String description of a searchEngine
	public String toString () {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	}

	// This does a graph traversal of the internet, starting at the given url.
	// For each new vertex seen, it updates the wordIndex, the internet graph,
	// and the set of visited vertices.
	void traverseInternet(String url) throws Exception {
		internet.addVertex(url);																// we create the graph for the internet (sub)set
		LinkedList<String> content = htmlParsing.getContent(url);								// we get the word content for within the page used to call the method
		ListIterator<String> itr = content.listIterator(0);										// and create a structure to iterate through the word content

		//internet.addVertex(url);																
		//internet.setVisited(url,true);														
		//LinkedList<String> neighbors = htmlParsing.getLinks(url);								
		//LinkedList<String> content = htmlParsing.getContent(url);								
		//Iterator<String> itr = content.iterator();											

		/* We keep a single index in the search engine of which web sites contain which words by mapping each word of the site's content from linked list to a dictionary
		 * (hash map). Each word is a key, with list of web sites containing it as its data.
		 */
		while (itr.hasNext()) {																	// we iterate through the entire word content in the given page
			String word = itr.next();
			word = word.toLowerCase();															// set all words to lower case so that later search is not case-sensitive
			LinkedList<String> list;
			if (wordIndex.containsKey(word)) {													// for any word already contained within the dictionary hash map structure,
				list = wordIndex.get(word);														// we access the list referenced (mapped) by the key word
				if (!list.contains(url)) {
					list.addLast(url);															// and add the current page's url to the list if the
				}																				// url is not already mapped to (indicated as containing) the word
			}
			else {																				// for any word NOT already contained within the hash map,
				list = new LinkedList<String>();												// we create a new list,
				list.add(url);																	// add the current page's url to the list of pages containing the word,
				wordIndex.put(word,list);														// and associate (map) the url to the key word
			}
		}
		internet.setVisited(url, true);															// we set url to visited once have indexed it within hash map
		
		LinkedList<String> links = htmlParsing.getLinks(url);									// we get the links
		itr = links.listIterator(0);
		while (itr.hasNext()) {																	// we iterate through all the link contained in the given page
			String outLink = itr.next();
			internet.addEdge(url, outLink);														// and add to the internet graph an edge from the current page to the outer linked page
			if (!internet.getVisited(outLink)) {												// and for any links not visited (and thus not in our internet graph)
				traverseInternet(outLink);														// we recursive call our current method to traverse this subset
			}
		}
		// NOTE: the above loop implements a breadth-first traversal of the links, but due to the recursive call within the loop, the ultimate traversal is depth-first
	} // end of traverseInternet

	// This computes the pageRanks for every vertex in the internet graph.
	// It will only be called after the internet graph has been constructed using
	// traverseInternet.
	void computePageRanks() {
		LinkedList<String> pages = internet.getVertices();										// we create a list of all the pages in the internet (sub)set
		ListIterator<String> itr = pages.listIterator(0);										// and create a structure with which to iterate through the list
		while (itr.hasNext()) {
			internet.setPageRank(itr.next(), 1);												// and set the initial page rank of each site to 1 as a base for our
		}																						// iterative calculation below
		/* We want to compute the page ranks of each site, but as they are all dependent on each other, we must do multiple iterations, until the values all converge;
		 * 100 iterations should do
		 */
		for (int i = 0; i < 100; i++) {
			itr = pages.listIterator();															// we recreate a structure with which to iterate through the pages in the internet
			while (itr.hasNext()) {																// subset, as iterators are single use only
				String url = itr.next();														// current page
				double pageRank = 0;											
				LinkedList<String> linksIntoList = internet.getEdgesInto(url);					// we create a list of all the pages in the internet (subset) that link
				ListIterator<String> linksInto = linksIntoList.listIterator(0);					// to our current page, and create a structure with which to iterate through the list
				while (linksInto.hasNext()) {
					String link = linksInto.next();
					pageRank += (internet.getPageRank(link)) / (internet.getOutDegree(link));	// we compute the ratio of page rank to out-degree for each link to the current page
				}																				// and add it to our current page's rank
				pageRank = 0.5*(1 + pageRank);													// and finalize the formula for the current page's rank
				internet.setPageRank(url, pageRank);											// and reset the current page's rank
			}
		}
	} // end of computePageRanks

	/* Returns the URL of the page with the high page-rank containing the query word, returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
	 */
	String getBestURL(String query) {
		String answer = "";
		LinkedList<String> pages = new LinkedList<String>();									// create a new List to hold all the URLs containing the query word
		query = query.toLowerCase();															// set the query to lower case to ensure search is not case-sensitive
		if (wordIndex.containsKey(query)) {														// if our dictionary (hash map) for our internet (sub)set contains the query word,
			pages = wordIndex.get(query);														// return a list of all the pages (URLs) containing the query word
		}
		else {																					// if our hash map for our internet sub(set) does NOT contain the query word,
			return answer;																		// simply return an empty string
		}
		ListIterator<String> itr = pages.listIterator();										// if our query word does exist in our internet (sub)set, create a structure with
		double topRank = 0;																		// which to iterate through the pages in our internet (sub)set
		while (itr.hasNext()) {																	// for each page in our list of pages containing the query word,	
			String url = itr.next();
			double thisRank = internet.getPageRank(url);										// get the rank of the page
			if (thisRank > topRank ) {															// and if it is higher than the rank of previously highest ranked page,
				answer = url;																	// set the query answer to the current page
				topRank = thisRank;																// and reset the top rank
			}
		}
		System.out.println("Top result: " +answer + " (" + topRank + ")");
		return answer;																			// return the query answer with the top page rank
	} // end of getBestURL

	public static void main(String args[]) throws Exception{		
		searchEngine mySearchEngine = new searchEngine();
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
		mySearchEngine.computePageRanks();

		BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
		String query;
		do {
			System.out.print("Enter query: ");
			query = stndin.readLine();
			if ( query != null && query.length() > 0 ) {
				System.out.println("Best site = " + mySearchEngine.getBestURL(query));
			}
		} while (query!=null && query.length()>0);				
	} // end of main
}