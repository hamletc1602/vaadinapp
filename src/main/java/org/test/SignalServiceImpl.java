package org.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class SignalServiceImpl implements SignalService {

    private static SignalServiceImpl instance;
    private static final Logger LOGGER = Logger.getLogger(MockSignalService.class.getName());

    private final HashMap<String, HashMap<Long, Signal> > assets = new HashMap<>();

    static {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
    	} catch (Exception e) {
        	LOGGER.severe("Unable to load MySQL JDBC Driver");
        }
    }
    
    private SignalServiceImpl() {
    }

    /**
     * @return a reference to an example facade for Signal objects.
     */
    public static SignalServiceImpl getInstance() {
        if (instance == null) {
            instance = new SignalServiceImpl();
        }
        return instance;
    }

    /**
     * @return all available Signal objects.
     */
    public synchronized List<Signal> findAll() {
        return findAll("_TOTAL_", GroupBy.DAY);
    }

    /**
     * Finds all Signal's that match given filter.
     *
     * @param assetName Return all signals for this asset. Required.
     *
     * @return list of Signal objects
     */
    public synchronized List<Signal> findAll(String assetName, GroupBy groupBy) {
        ArrayList<Signal> list = new ArrayList<>();

        try {
            if (assetName != null && !assetName.isEmpty()) {
            	Connection conn;
            	try {
            	    conn =
            	       DriverManager.getConnection("jdbc:mysql://general.cw84wdvj2qvy.us-east-1.rds.amazonaws.com?" +
            	                                   "user=adriaan&password=w1ndr0se");

            	    // Do something with the Connection

            	    
            	} catch (SQLException ex) {
            	    // handle any errors
            	    LOGGER.info("SQLException: " + ex.getMessage());
            	    LOGGER.info("SQLState: " + ex.getSQLState());
            	    LOGGER.info("VendorError: " + ex.getErrorCode());
            	}            	
            	
            	
                HashMap<Long, Signal> signals = assets.get(assetName);
                if (signals != null) {
                    for (Signal signal : signals.values()) {
                        list.add(signal.clone());
                    }
                } else {
                   LOGGER.log(Level.WARNING, "No signals data found for asset: " + assetName);
                }
            } else {
                //throw new RuntimeException("An asset name must be provided.");
                // Returns an empty list if there's no filter. 
                // TODO: Think more on how we should handle the 'All assets' case...
                return list; 
            }
        } catch (CloneNotSupportedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        Collections.sort(list, new Comparator<Signal>() {
            @Override
            public int compare(Signal o1, Signal o2) {
                return (int) (o1.getId() - o2.getId());
            }
        });
        return list;
    }

    /**
     * Finds all Signal's that match given filter and limits the resultset.
     *
     * @param stringFilter
     *            filter that returned objects should match or null/empty string
     *            if all objects should be returned.
     * @param start
     *            the index of first result
     * @param maxresults
     *            maximum result count
     * @return list a Signal objects
     */
    public synchronized List<Signal> findAll(String stringFilter, GroupBy groupBy, int start, int maxresults) {
        List<Signal> arrayList = findAll(stringFilter, groupBy);
        int end = start + maxresults;
        if (end > arrayList.size()) {
            end = arrayList.size();
        }
        return arrayList.subList(start, end);
    }

    /**
     * @return the amount of all Signals in the system
     */
    public synchronized long count() {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * Deletes a Signal from a system
     *
     * @param value
     *            the Signal to be deleted
     */
    public synchronized void delete(Signal value) {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * Persists or updates Signal in the system. Also assigns an identifier
     * for new Signal instances.
     *
     * @param entry
     */
    public synchronized void save(Signal entry) {
        throw new RuntimeException("Not Implemented");
    }
}