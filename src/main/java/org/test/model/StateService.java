package org.test.model;

import java.util.List;

public interface StateService {

	/* (non-Javadoc)
	 * @see org.test.StateService#findAll()
	 */
	List<State> findAll();

	/* (non-Javadoc)
	 * @see org.test.StateService#findAll(java.lang.String)
	 */
	List<State> findAll(String stateName);

	/* (non-Javadoc)
	 * @see org.test.StateService#findAll(java.lang.String, int, int)
	 */
	List<State> findAll(String stringFilter, int start, int maxresults);

	/* (non-Javadoc)
	 * @see org.test.StateService#count()
	 */
	long count();

	/* (non-Javadoc)
	 * @see org.test.StateService#delete(org.test.State)
	 */
	void delete(State value);

	/* (non-Javadoc)
	 * @see org.test.StateService#save(org.test.State)
	 */
	void save(State entry);

}