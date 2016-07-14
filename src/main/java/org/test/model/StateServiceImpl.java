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
public class StateServiceImpl implements StateService {

    private static StateServiceImpl instance;
    private static final Logger LOGGER = Logger.getLogger(StateServiceImpl.class.getName());

    private StateServiceImpl() {
    }

    /**
     * @return a reference to an example facade for State objects.
     */
    public static StateServiceImpl getInstance() {
        if (instance == null) {
            instance = new StateServiceImpl();
        }
        return instance;
    }
    
    /* (non-Javadoc)
	 * @see org.test.StateService#findAll()
	 */
    public synchronized List<State> findAll() {
        return findAll(null);
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#findAll(java.lang.String)
	 */
    public synchronized List<State> findAll(String stateName) {
        ArrayList<State> list = new ArrayList<>();
	    Connection conn = null;
        try {
    	    conn = DriverManager.getConnection(LoadData.getJdbcConnectionString());
    	    PreparedStatement states = conn.prepareStatement("select id, name from state order by name");
        	ResultSet result = states.executeQuery();
        	while (result.next()) {
        		State state = new State();
        		state.setId(result.getInt(1));
        		state.setName(result.getString(2));
        		list.add(state);
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
	 * @see org.test.StateService#findAll(java.lang.String, int, int)
	 */
    public synchronized List<State> findAll(String stringFilter, int start, int maxresults) {
        List<State> arrayList = findAll(stringFilter);
        int end = start + maxresults;
        if (end > arrayList.size()) {
            end = arrayList.size();
        }
        return arrayList.subList(start, end);
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#count()
	 */
    public synchronized long count() {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#delete(org.test.State)
	 */
    public synchronized void delete(State value) {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#save(org.test.State)
	 */
    public synchronized void save(State entry) {
        throw new RuntimeException("Not Implemented");
    }
 
}