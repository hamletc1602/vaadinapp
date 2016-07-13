package org.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * An in memory dummy service with mock data.
 */
public class MockSignalService implements SignalService {

    private static MockSignalService instance;
    private static final Logger LOGGER = Logger.getLogger(MockSignalService.class.getName());

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private LoadData data;
    
    private MockSignalService() {
    }

    /**
     * @return a reference to an example facade for Signal objects.
     */
    public static MockSignalService getInstance() {
        if (instance == null) {
            instance = new MockSignalService();
        }
        return instance;
    }
    
    private Signal createSignal(
    	int id, String dateStr, int engaged, int active, int override, int load, int unplug)
    	throws Exception
    {
    	Signal signal = new Signal();
    	signal.setId(id);
    	signal.setDate(dateFormat.parse(dateStr));
    	signal.setCount("Engaged", engaged);
    	signal.setCount("Active", active);
    	signal.setCount("Override", override);
    	signal.setCount("Load", load);
    	signal.setCount("Unplug", unplug);
    	signal.setTotal(engaged + active + override + load + unplug);
    	return signal;
    }
    
    /**
   		
    */
    public void setData(LoadData data) {
    	this.data = data;
    }

    /* (non-Javadoc)
	 * @see org.test.SignalService#findAll()
	 */
    public synchronized List<Signal> findAll() {
        return findAll("_TOTAL_", GroupBy.DAY);
    }

    /* (non-Javadoc)
	 * @see org.test.SignalService#findAll(java.lang.String)
	 */
    public synchronized List<Signal> findAll(String assetName, GroupBy groupBy) {
		List<Signal> list = new ArrayList<>();
    	try {
		    // If an asset name filter is not provided, return totals.
			if (assetName == null || assetName.isEmpty()) {
		    	assetName = "_TOTAL_";
		    }
		    
		    if (GroupBy.YEAR == groupBy) {
		    	if (assetName.length() > 4) {
			    	list.add(createSignal(1, "2014-01-01", 9234, 345, 136, 4, 5));
			    	list.add(createSignal(2, "2015-01-01", 8654, 195, 246, 14, 1));
		    	} else {
			    	list.add(createSignal(1, "2014-01-01", 7234, 245, 236, 14, 1));
			    	list.add(createSignal(2, "2015-01-01", 4654, 95, 146, 4, 5));
		    	}
		    } else if (GroupBy.MONTH == groupBy) {
		    	if (assetName.length() > 4) {
			    	list.add(createSignal(1, "2014-02-01", 1234, 345, 256, 14, 5));
			    	list.add(createSignal(2, "2014-03-01", 1234, 345, 256, 14, 9));
			    	list.add(createSignal(3, "2014-04-01", 345, 345, 256, 14, 2));
			    	list.add(createSignal(4, "2014-05-01", 1234, 345, 256, 14, 0));
			    	list.add(createSignal(5, "2014-06-01", 345, 345, 256, 14, 1));
			    	list.add(createSignal(6, "2014-08-01", 1234, 345, 256, 64, 4));
			    	list.add(createSignal(7, "2014-09-01", 1234, 345, 256, 14, 9));
			    	list.add(createSignal(8, "2014-10-01", 345, 345, 256, 34, 9));
			    	list.add(createSignal(9, "2014-11-01", 1234, 345, 256, 14, 5));
			    	list.add(createSignal(10, "2014-12-01", 1234, 345, 256, 24, 5));
			    	list.add(createSignal(11, "2015-01-01", 1234, 345, 256, 14, 0));
			    	list.add(createSignal(12, "2015-02-01", 345, 345, 356, 44, 0));
			    	list.add(createSignal(13, "2015-03-01", 1234, 345, 246, 14, 1));
			    	list.add(createSignal(14, "2015-05-01", 1234, 345, 256, 34, 12));
			    	list.add(createSignal(15, "2015-06-01", 345, 345, 196, 14, 5));
			    	list.add(createSignal(16, "2015-07-01", 1234, 345, 256, 24, 4));
			    	list.add(createSignal(17, "2015-08-01", 345, 345, 256, 14, 6));
			    	list.add(createSignal(18, "2015-09-01", 1034, 345, 136, 34, 5));
			    	list.add(createSignal(19, "2015-010-01", 1234, 345, 256, 14, 2));
		    	} else {
			    	list.add(createSignal(1, "2014-01-01", 1234, 345, 256, 14, 0));
			    	list.add(createSignal(2, "2014-02-01", 345, 345, 356, 44, 0));
			    	list.add(createSignal(3, "2014-03-01", 1234, 345, 246, 14, 1));
			    	list.add(createSignal(4, "2014-05-01", 1234, 345, 256, 34, 12));
			    	list.add(createSignal(5, "2014-06-01", 345, 345, 196, 14, 5));
			    	list.add(createSignal(6, "2014-07-01", 1234, 345, 256, 24, 4));
			    	list.add(createSignal(7, "2014-08-01", 345, 345, 256, 14, 6));
			    	list.add(createSignal(8, "2014-09-01", 1034, 345, 136, 34, 5));
			    	list.add(createSignal(9, "2014-010-01", 1234, 345, 256, 14, 2));
			    	list.add(createSignal(10, "2015-02-01", 1234, 345, 256, 14, 5));
			    	list.add(createSignal(11, "2015-03-01", 1234, 345, 256, 14, 9));
			    	list.add(createSignal(12, "2015-04-01", 345, 345, 256, 14, 2));
			    	list.add(createSignal(13, "2015-05-01", 1234, 345, 256, 14, 0));
			    	list.add(createSignal(14, "2015-06-01", 345, 345, 256, 14, 1));
			    	list.add(createSignal(15, "2015-08-01", 1234, 345, 256, 64, 4));
			    	list.add(createSignal(16, "2015-09-01", 1234, 345, 256, 14, 9));
			    	list.add(createSignal(17, "2015-10-01", 345, 345, 256, 34, 9));
			    	list.add(createSignal(18, "2015-11-01", 1234, 345, 256, 14, 5));
			    	list.add(createSignal(19, "2015-12-01", 1234, 345, 256, 24, 5));
		    	}
		    } else if (GroupBy.DAY == groupBy) {
		    	try {
		        	for (Signal signal : data.getSignals(assetName).values()) {
		        		list.add(signal.clone());
		        	}
		    	} catch (Exception e) {
		    		/* Ignore */
		    	}
		    } else {
		    	throw(new RuntimeException("Unexpected grouping: " + groupBy));
		    }
		    
		    // Sort signals by date
		    Collections.sort(list, new Comparator<Signal>() {
		        @Override
		        public int compare(Signal o1, Signal o2) {
		            return (int) (o1.getDate().getTime() - o2.getDate().getTime());
		        }
		    });        
    	} catch (Exception e) {
    		LOGGER.severe(e.getMessage());
    	}
	    return list;
    }

    /* (non-Javadoc)
	 * @see org.test.SignalService#findAll(java.lang.String, int, int)
	 */
    public synchronized List<Signal> findAll(String stringFilter, GroupBy groupBy, int start, int maxresults) {
        List<Signal> arrayList = findAll(stringFilter, groupBy);
        int end = start + maxresults;
        if (end > arrayList.size()) {
            end = arrayList.size();
        }
        return arrayList.subList(start, end);
    }

    /* (non-Javadoc)
	 * @see org.test.SignalService#count()
	 */
    public synchronized long count() {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.SignalService#delete(org.test.Signal)
	 */
    public synchronized void delete(Signal value) {
        throw new RuntimeException("Not Implemented");
    }

    /* (non-Javadoc)
	 * @see org.test.SignalService#save(org.test.Signal)
	 */
    public synchronized void save(Signal entry) {
        throw new RuntimeException("Not Implemented");
    }
 
}