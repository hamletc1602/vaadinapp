package org.test.model;

import java.util.List;

public interface AssetService {

	/**
	 * @return all available Asset objects.
	 */
	List<Asset> findAll();

	/**
	 * Finds all Asset's that match given filter.
	 *
	 * @param assetName Return all signals for this asset. Required.
	 *
	 * @return list of Asset objects
	 */
	List<Asset> findAll(String assetName);

	/**
	 * Finds all Asset's that match given filter and limits the resultset.
	 *
	 * @param stringFilter
	 *            filter that returned objects should match or null/empty string
	 *            if all objects should be returned.
	 * @param start
	 *            the index of first result
	 * @param maxresults
	 *            maximum result count
	 * @return list a Asset objects
	 */
	List<Asset> findAll(String stringFilter, int start, int maxresults);

	/**
	 * @return the amount of all Assets in the system
	 */
	long count();

	/**
	 * Deletes a Asset from a system
	 *
	 * @param value
	 *            the Asset to be deleted
	 */
	void delete(Asset value);

	/**
	 * Persists or updates Asset in the system. Also assigns an identifier
	 * for new Asset instances.
	 *
	 * @param entry
	 */
	void save(Asset entry);

}