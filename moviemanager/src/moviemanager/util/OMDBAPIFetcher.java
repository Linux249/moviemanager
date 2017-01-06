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
 
    public NamedNodeMap FetchMovieDetailsByID (String IMDB_ID) {
    	//perform an omdb search for a given imdb id
    	// returns movie details as named node map
    	
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
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (MovieManagerException e) {
            e.printStackTrace();
        }
        // if the response contains data print out the raw data to the console
        // TODO: Update / Create movie and Performer instances right here with the received data
        if (movieDetails != null) {
            for (int i = 0; i < movieDetails.getLength(); i++) {
                System.out.println(movieDetails.item(i));
            }
        }
    	return movieDetails;
    }
    
    public static void main(String[] args) {
        // perform a omdb search for the title 'Star Wars'
        String title = "Star Wars";
        String year_ = "";
        // response holds the response of the API
        NamedNodeMap response = null;
        try {
            String title_ = title.replaceAll(" ", "+");
            // create a URL which is used to perform the API request
            URL omdb = new URL(omdbTitel + title_ + "&y=" + year_ + omdbParams);
            // DocumentBuilder instance handles the XML data returned by the API
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // perform the request to the API
            Document responseDoc = builder.parse(omdb.openStream());
            // check if the response contains movie data
            if (responseDoc.getElementsByTagName("movie") != null
                    && responseDoc.getElementsByTagName("movie").getLength() > 0) {
                // store the movie data as NamedNodeMap in the response
                response = responseDoc.getElementsByTagName("movie").item(0).getAttributes();
            } else {
                throw new MovieManagerException("Could not find the movie in the OMDb.");
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (MovieManagerException e) {
            e.printStackTrace();
        }
        // if the response contains data print out the raw data to the console
        // TODO: Update / Create movie and Performer instances right here with the received data
        if (response != null) {
            for (int i = 0; i < response.getLength(); i++) {
                System.out.println(response.item(i));
            }
        }
    }
}