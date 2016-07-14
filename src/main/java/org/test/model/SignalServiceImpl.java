package org.test.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class SignalServiceImpl implements SignalService {

    private static SignalServiceImpl instance;
    private static final Logger LOGGER = Logger.getLogger(MockSignalService.class.getName());

    static {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
    	} catch (Exception e) {
        	LOGGER.severe("Unable to load MySQL JDBC Driver");
        }
    }
    
    private StateService stateService;
    
    private SignalServiceImpl() {
    }

    /** Injected dependency on StateService */
    public void setStateService(StateService service) {
    	stateService = service;
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
        return findAll(LoadData.ALL_ASSETS, GroupBy.DAY);
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

		// If an asset name filter is not provided, return totals.
		if (assetName == null || assetName.isEmpty()) {
	    	assetName = LoadData.ALL_ASSETS;
	    }
		
		// Generate the appropriate SQL to group the results
		String groupSql = null;
		String displaySql = null;
		switch (groupBy) {
			case YEAR:
				displaySql = "MAKEDATE(year(entry_date), 1)"; 
				groupSql = "year(entry_date)"; 
				break;
			case MONTH:
				displaySql = "STR_TO_DATE(concat('01,', month(entry_date), ',', year(entry_date)),'%d,%m,%Y')";
				groupSql = "concat(year(entry_date),month(entry_date))";
				break;
			case DAY:
				displaySql = "date(entry_date)";
				groupSql = displaySql;
				break;
			default: throw new RuntimeException("Unnexpected Grouping param: " + groupBy);
		}
		
		// Get list of states for later use (Assumption: This list is small and does not change often)
		List<State> states = stateService.findAll();
		
		// Build the state row to column pivot into the prepared statement SQL
		// (The list of valid states should change infrequently)
		// There is one parameter - The asset name (AssetUN)
		String getSigCountSql = "select id, " + displaySql + ", count(*) total";
		for (State state : states) {
			getSigCountSql += 
				", count(if(`asset_status` = '" + state.getName() + "', 1, null)) '" + state.getName() + "' "; 
		}
		getSigCountSql += "from signals3 ";
		if ( ! LoadData.ALL_ASSETS.equals(assetName)) {
			getSigCountSql += "where (AssetUN = ?) ";
		}
		getSigCountSql += "group by asset_status, " + groupSql + " order by entry_date;";	
		
	    Connection conn = null;
        try {
    	    conn = DriverManager.getConnection(LoadData.getJdbcConnectionString());
    	    PreparedStatement signalsStmt = conn.prepareStatement(getSigCountSql);
    		if ( ! LoadData.ALL_ASSETS.equals(assetName)) {
    			signalsStmt.setString(1, assetName);
    		}
        	ResultSet result = signalsStmt.executeQuery();
        	while (result.next()) {
        		Signal signal = new Signal();
        		signal.setId(result.getInt(1));
        		signal.setDate(result.getDate(2));
        		signal.setTotal(result.getInt(3));
        		int i = 0;
        		for (State state : states) {
        			signal.setCount(state.getName(), result.getInt(i + 4));
        			i++;
        		}
        		if (LOGGER.isLoggable(Level.FINE)) {
            		// Debug check to ensure separate state counts match the total returned by the query
        			signal.checkTotal();
        		}
        		list.add(signal);
        	}
        } catch (SQLException e) {
        	try { conn.close(); } catch (SQLException e1) { /*Ignore*/ }
    	    LOGGER.info("SQLException: " + e.getMessage());
    	    LOGGER.info("SQLState: " + e.getSQLState());
    	    LOGGER.info("VendorError: " + e.getErrorCode());
        }
        
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