package org.test.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;


/**
 * An in memory dummy "database" for the example purposes. In a typical Java app
 * this class would be replaced by e.g. EJB or a Spring based service class.
 * <p>
 * In demos/tutorials/examples, get a reference to this service class with
 * {@link MockSignalService#getInstance()}.
 */
public class LoadData {
    private static final Logger LOGGER = Logger.getLogger(LoadData.class.getName());
    private static String dbhost;
    private static String dbport;
    private static String dbuser;
    private static String dbpw;

    static {
    	// Load databas settings from Amazon Env. vars:
    	dbhost = System.getenv("RDS_HOSTNAME");
    	dbport = System.getenv("RDS_PORT");
    	dbuser = System.getenv("RDS_USERNAME");
    	dbpw = System.getenv("RDS_PASSWORD");
    	
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
    	} catch (Exception e) {
        	LOGGER.severe("Unable to load MySQL JDBC Driver");
        }
    }
    
    private final HashMap<String, HashMap<Integer, Signal> > signalData = new HashMap<>();
    private final HashMap<Integer, Signal> signalsTotal = new HashMap<>();
    private final ArrayList<String> stateNames = new ArrayList<>();
    private final ArrayList<State> states = new ArrayList<>();
    private final ArrayList<Asset> assets = new ArrayList<>();
    
    public LoadData() {
    	LOGGER.info("Database URL: " + dbhost + ":" + dbport);
    }
    
    public static String getJdbcConnectionString() {
    	return "jdbc:mysql://" + dbhost + ":" + dbport + "/aimsio?user=" + 
    		dbuser + "&password=" + dbpw;
    }
    
    /** */
    public List<Asset> getAssets() {
    	return assets;
    }
    
    /** */
    public List<State> getStates() {
    	return states;
    }
    
    /** */
    public Map<Integer, Signal> getSignals(String assetName) {
    	if (assetName.equals("_TOTAL_")) {
    		return signalsTotal;
    	} else {
    		return signalData.get(assetName);
    	}
    }
    
    /**
    	Load test data embedded in the application.
    */
    public void loadTestData(String filename) {
        try {
            InputStream is = this.getClass().getResourceAsStream(filename);          
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int rowCount = 0;
            int nextStateId = 0;
            int nextAssetId = 0;
            int nextSignalId = 0;

            LOGGER.log(Level.INFO, "Start loading mock data.");

            for (CSVRecord record : CSVFormat.DEFAULT.withHeader().parse(br)) {
                //
                String assetName = record.get("AssetUN");
                String stateName = record.get("status");
                String dateStr = record.get("entry_date");
                
                // Collect all unique state names
                if ( ! stateNames.contains(stateName)) {
                	stateNames.add(stateName);
                	State state = new State();
                	state.setId(nextStateId);
                	state.setName(stateName);
                	states.add(state);
                	nextStateId++;
                }

                // Get the right signals dataset for this asset (or create one)
                HashMap<Integer, Signal> signals = signalData.get(assetName);
                if (null == signals) {
                    signals = new HashMap<>();
                    signalData.put(assetName, signals);
                    Asset asset = new Asset();
                    asset.setId(nextAssetId);
                    asset.setName(assetName);
                    assets.add(asset);
                    nextAssetId++;
                }

                // Aggregate all records to map using the time in _hours_
                // as the key - Given the UI requirements of aggregation by 
                // day/month/year, seconds level data does not seem 
                // worth the processing load at this time.
                try {
                    Date date = dateFormat.parse(dateStr);
            		Instant ts = date.toInstant();
            		long timeagg = ts.truncatedTo(ChronoUnit.DAYS).toEpochMilli();
            		// Cast should be safe enough, since we're truncating to days...
            		assert((timeagg / 1000) < Integer.MAX_VALUE);
            		int id = (int)(timeagg / 1000); 

                    // Update the signal for this Asset + time period
                    if (signals.containsKey(id)) {
                        Signal signal = signals.get(id);
                        signal.addToCount(stateName, 1);
                        signal.setTotal(signal.getTotal() + 1);
                    } else {
                        Signal signal = new Signal();
                        signal.setId(nextSignalId);
                        signal.setDate(new Date(timeagg));
                        signal.setCount(stateName, 1);
                        signal.setTotal(1);
                        signals.put(id, signal);
                        nextSignalId++;
                    }
                    
                    // Update the signals total for this time period
                    if (signalsTotal.containsKey(id)) {
                        Signal signal = signalsTotal.get(id);
                        signal.addToCount(stateName, 1);
                        signal.setTotal(signal.getTotal() + 1);
                    } else {
                        Signal signal = new Signal();
                        signal.setId(nextSignalId);
                        signal.setDate(new Date(timeagg));
                        signal.setCount(stateName, 1);
                        signal.setTotal(1);
                        signalsTotal.put(id, signal);
                        nextSignalId++;
                    }
                } catch (ParseException e) {
                    LOGGER.log(Level.SEVERE, "Unable to parse date for asset: " + assetName + ": " + dateStr + ". " + e.getMessage());
                }

                rowCount++;
            } // For CSV Records

            // Count total signals
            long totalSignals = 0;
            for (HashMap<Integer, Signal> signalMap : signalData.values()) {
            	totalSignals += signalMap.size();
            }
            
            LOGGER.info("Loaded " + rowCount + " rows of mock data.");
            LOGGER.info("Loaded " + states.size() + " States, " + assets.size() + " Assets and " + totalSignals + " aggregate signals");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load demo data: " + e.getMessage());                
        }
    }

    /**
     * Write the loaded test data to the database.
     */
    public void writeTestDataToDatabase() {
    	Connection conn = null;
        try {
    	    conn = DriverManager.getConnection(getJdbcConnectionString());
    	    conn.setAutoCommit(false);
    	    
    	    PreparedStatement insertState = 
        	    	conn.prepareStatement("insert into state (id, name) values (?,?)");
    	    PreparedStatement insertAsset = 
    	    	conn.prepareStatement("insert into asset (id, name) values (?,?)");
    	    PreparedStatement insertSignal = 
    	    	conn.prepareStatement(
    	    		"insert into asset_signal (asset_id, asset_state, signal_count, entry_date) values (?,?,?,?)");
    	    int nextAssetId = 0;
    	    
    	    for (State state : states) {
    	    	insertState.setInt(1, state.getId());
    	    	insertState.setString(2, state.getName());
    	    	insertState.executeUpdate();
    	    }
	    	conn.commit();
	    	
	    	int signalCount = 0;
    	    for (String assetName : signalData.keySet()) {
    	    	LOGGER.info("Inserting data for asset " + assetName);
    	    	
    	    	// Insert each unique asset into the asset table
    	    	insertAsset.setInt(1, nextAssetId);
    	    	insertAsset.setString(2,  assetName);
    	    	insertAsset.executeUpdate();
    	    	
    	    	// Insert signal rows
    	    	for (Signal signal : signalData.get(assetName).values()) {
    	    		// Insert each signal update into the signals table
    	    		// (One row per state, so we can easily add new states in future)
    	    		for (State state : states) {
        	    		//insertSignal.setInt(1, signal.getId()); // id col is auto-inc.
        	    		insertSignal.setInt(1, nextAssetId);
        	    		insertSignal.setInt(2, state.getId());
        	    		insertSignal.setInt(3, signal.getCount(state.getName()));
        	    		insertSignal.setTimestamp(4, new java.sql.Timestamp(signal.getDate().getTime()));
        	    		insertSignal.executeUpdate();
    	    		}
    	    		signalCount++;
    	    	}

    	    	// Commit after each asset (no partial data for assets)
    	    	conn.commit();
    	    	
    	    	LOGGER.info("Inserted " + signalCount + " signals for asset: " + assetName);
    	    	
    	    	// Pause for a second between assets
    	    	try {
    	    		Thread.sleep(1000);
    	    	} catch (InterruptedException e) {
    	    		/* Ignore */
    	    	}
    	    	
    	    	nextAssetId++;
    	    }
    	} catch (SQLException ex) {
    	    LOGGER.info("SQLException: " + ex.getMessage());
    	    LOGGER.info("SQLState: " + ex.getSQLState());
    	    LOGGER.info("VendorError: " + ex.getErrorCode());
    		try {
    			conn.rollback();
    		} catch (SQLException e) {
    			LOGGER.severe("Error on rollback: " + e.getMessage());
    		}
    	}            	
    }

}