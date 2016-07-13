package org.test;

import java.util.List;

public interface SignalService {

	enum GroupBy {
		DAY,
		MONTH,
		YEAR
	};
	
	/**
	 * @return all available Signal objects.
	 */
	List<Signal> findAll();

	/**
	 * Finds all Signal's that match given filter.
	 *
	 * @param assetName Return all signals for this asset. Required.
	 *
	 * @return list of Signal objects
	 */
	List<Signal> findAll(String assetName, GroupBy groupBy);

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
	List<Signal> findAll(String stringFilter, GroupBy groupBy, int start, int maxresults);

	/**
	 * @return the amount of all Signals in the system
	 */
	long count();

	/**
	 * Deletes a Signal from a system
	 *
	 * @param value
	 *            the Signal to be deleted
	 */
	void delete(Signal value);

	/**
	 * Persists or updates Signal in the system. Also assigns an identifier
	 * for new Signal instances.
	 *
	 * @param entry
	 */
	void save(Signal entry);

}