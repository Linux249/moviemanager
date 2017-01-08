package moviemanager.util;
 
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
 
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import moviemanager.util.MovieManagerUtil.BadConnectionException;
import moviemanager.util.MovieManagerUtil.MovieManagerException;;
 
public class OMDBAPIFetcher {
    // OMDb API Base URL
    private static final String OMDB_BASE = "http://www.omdbapi.com/?";
     
    // OMDb API request URL parameters 
    private static final String omdbTitel = OMDB_BASE + "t=";
    private static final String omdbIMDB_ID = OMDB_BASE + "i=";
    private static final String omdbSearch = OMDB_BASE + "s=";
     
    // OMDb API  xml data format parameter
    private static final String omdbParams = "&plot=full&r=xml";
 
    public NamedNodeMap FetchMovieDetailsByID (String IMDB_ID) throws BadConnectionException, MovieManagerException {
    	//perform an omdb search for a given imdb id
    	// returns movie details as named node map
    	
    	if (IMDB_ID.length() != 9 || !(IMDB_ID.startsWith("tt") || IMDB_ID.startsWith("nm")))
    		throw new MovieManagerException("Not a valid id.");
    	NamedNodeMap movieDetails = null;
    	String query = omdbIMDB_ID + IMDB_ID;
        try {
            // create a URL which is used to perform the API request
            query = query.replaceAll(" ", "+");
            URL omdb = new URL(query + omdbParams);
            // DocumentBuilder instance handles the XML data returned by the API
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // perform the request to the API
            Document responseDoc = builder.parse(omdb.openStream());
            // check if the response contains movie data
            if (responseDoc.getElementsByTagName("movie") != null
                    && responseDoc.getElementsByTagName("movie").getLength() > 0) {
                // store the movie data as NamedNodeMap in the response
            	movieDetails = responseDoc.getElementsByTagName("movie").item(0).getAttributes();
            } else {
                throw new MovieManagerException("Could not find the movie in the OMDb.");
            }
        } catch (SAXException e) {
            //e.printStackTrace();
        	throw new MovieManagerUtil.BadConnectionException("SAX exception");
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            throw new MovieManagerUtil.BadConnectionException("Malformed URL exception");
        } catch (IOException e) {
            //e.printStackTrace();
        	throw new MovieManagerUtil.BadConnectionException("IO exception");
        } catch (ParserConfigurationException e) {
           // e.printStackTrace();
        	throw new MovieManagerUtil.BadConnectionException("Parser configuration exception");
        }
        
    	return movieDetails;
    }
}