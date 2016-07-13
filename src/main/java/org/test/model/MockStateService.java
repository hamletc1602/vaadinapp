package org.test.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An in memory dummy service with mock data.
 */
public class MockStateService implements StateService {

    private static MockStateService instance;
    private static final Logger LOGGER = Logger.getLogger(MockStateService.class.getName());

    private LoadData data;
    
    private MockStateService() {
    }

    /**
     * @return a reference to an example facade for State objects.
     */
    public static MockStateService getInstance() {
        if (instance == null) {
            instance = new MockStateService();
        }
        return instance;
    }
    
    /**
   		
    */
    public void setData(LoadData data) {
    	this.data = data;
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#findAll()
	 */
    /* (non-Javadoc)
	 * @see org.test.StateService#findAll()
	 */
    public synchronized List<State> findAll() {
        return findAll(null);
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#findAll(java.lang.String)
	 */
    /* (non-Javadoc)
	 * @see org.test.StateService#findAll(java.lang.String)
	 */
    public synchronized List<State> findAll(String stateName) {
        ArrayList<State> list = new ArrayList<>();

        try {
            List<State> states = data.getStates();
            if (states != null) {
                for (State state : states) {
                    list.add(state.clone());
                }
            } else {
               LOGGER.log(Level.WARNING, "No signals data found for state: " + stateName);
            }
        } catch (CloneNotSupportedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        Collections.sort(list, new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                return (int) (o1.getId() - o2.getId());
            }
        });
        return list;
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#findAll(java.lang.String, int, int)
	 */
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
    /* (non-Javadoc)
	 * @see org.test.StateService#count()
	 */
    public synchronized long count() {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#delete(org.test.State)
	 */
    /* (non-Javadoc)
	 * @see org.test.StateService#delete(org.test.State)
	 */
    public synchronized void delete(State value) {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.StateService#save(org.test.State)
	 */
    /* (non-Javadoc)
	 * @see org.test.StateService#save(org.test.State)
	 */
    public synchronized void save(State entry) {
        throw new RuntimeException("Not Implemented");
    }
 
}