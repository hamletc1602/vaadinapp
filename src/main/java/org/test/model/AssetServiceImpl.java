package org.test.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 */
public class AssetServiceImpl implements AssetService {

    private static AssetServiceImpl instance;
    private static final Logger LOGGER = Logger.getLogger(AssetServiceImpl.class.getName());

    private AssetServiceImpl() {
    }

    /**
     * @return a reference to an example facade for Asset objects.
     */
    public static AssetServiceImpl getInstance() {
        if (instance == null) {
            instance = new AssetServiceImpl();
        }
        return instance;
    }
    
    /* (non-Javadoc)
	 * @see org.test.AssetService#findAll()
	 */
    public synchronized List<Asset> findAll() {
        return findAll(null);
    }

    /* (non-Javadoc)
	 * @see org.test.AssetService#findAll(java.lang.String)
	 */
    public synchronized List<Asset> findAll(String assetName) {
        ArrayList<Asset> list = new ArrayList<>();
	    Connection conn = null;
        try {
    	    conn = DriverManager.getConnection(LoadData.getJdbcConnectionString());
    	    PreparedStatement assets = conn.prepareStatement("select id, name from asset order by name");
        	ResultSet result = assets.executeQuery();
        	while (result.next()) {
        		Asset asset = new Asset();
        		asset.setId(result.getInt(1));
        		asset.setName(result.getString(2));
        		list.add(asset);
        	}
        	conn.close();
        } catch (SQLException e) {
    	    LOGGER.info("SQLException: " + e.getMessage());
    	    LOGGER.info("SQLState: " + e.getSQLState());
    	    LOGGER.info("VendorError: " + e.getErrorCode());
        	try { if (conn != null) { conn.close(); } } catch (SQLException e1) { /*Ignore*/ }
        }
        return list;
    }

    /* (non-Javadoc)
	 * @see org.test.AssetService#findAll(java.lang.String, int, int)
	 */
    public synchronized List<Asset> findAll(String stringFilter, int start, int maxresults) {
        List<Asset> arrayList = findAll(stringFilter);
        int end = start + maxresults;
        if (end > arrayList.size()) {
            end = arrayList.size();
        }
        return arrayList.subList(start, end);
    }

    /* (non-Javadoc)
	 * @see org.test.AssetService#count()
	 */
    public synchronized long count() {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.AssetService#delete(org.test.Asset)
	 */
    public synchronized void delete(Asset value) {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.AssetService#save(org.test.Asset)
	 */
    public synchronized void save(Asset entry) {
        throw new RuntimeException("Not Implemented");
    }
 
}