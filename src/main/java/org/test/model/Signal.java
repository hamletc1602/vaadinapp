package org.test.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
	One aggregate asset state. Contains the number of signals received for each state 
	(for an assumed asset and time period) when aggregated.
*/
@SuppressWarnings("serial")
public class Signal implements Serializable, Cloneable {

    private Integer id;

    private Date date;
    
    private HashMap<String, Integer> counts = new HashMap<>();
    
    private int total;

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }   

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCount(String state) {
    	if (counts.containsKey(state)) {
    		return counts.get(state);
    	} else {
    		return 0;
    	}
    }

    public void setCount(String state, int count) {
        this.counts.put(state, count);
    }
    
    public void addToCount(String state, int count) {
		counts.put(state, getCount(state) + count);
    }

    /*
    public void addToCount(Signal other) {
    	for (String state : counts.keySet()) {
    		addToCount(state, other.getCount(state));
    	}
    	total = total + other.total;
    }
    */

    public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.id == null) {
            return false;
        }

        if (obj instanceof Signal && obj.getClass().equals(getClass())) {
            return this.id.equals(((Signal) obj).id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (id == null ? 0 : id.hashCode());
        return hash;
    }

    @Override
    public Signal clone() throws CloneNotSupportedException {
        return (Signal) super.clone();
    }

    @Override
    public String toString() {
    	String ret = "";
    	for (String state : counts.keySet()) {
    		ret += state + ": " + counts.get(state);
    	}
        return date.getTime() + ": " + ret;
    }
}