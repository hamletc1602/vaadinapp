package org.test.model;

import java.io.Serializable;

/**
	One of the states an asset can be in when an update occurs (Asset status)
 */
@SuppressWarnings("serial")
public class State implements Serializable, Cloneable {

    private Integer id;

    private String name;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }   

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
            return this.id.equals(((State) obj).id);
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
    public State clone() throws CloneNotSupportedException {
        return (State) super.clone();
    }

    @Override
    public String toString() {
        return name;
    }
}