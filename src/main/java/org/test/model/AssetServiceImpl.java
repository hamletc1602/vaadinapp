package org.test.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class AssetServiceImpl implements AssetService {

    private static AssetServiceImpl instance;
    private static final Logger LOGGER = Logger.getLogger(AssetServiceImpl.class.getName());

    private LoadData data;
    
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
    
    /**
   		
    */
    public void setData(LoadData data) {
    	this.data = data;
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

        try {
            List<Asset> assets = data.getAssets();
            if (assets != null) {
                for (Asset asset : assets) {
                    list.add(asset.clone());
                }
            } else {
               LOGGER.log(Level.WARNING, "No signals data found for asset: " + assetName);
            }
        } catch (CloneNotSupportedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        Collections.sort(list, new Comparator<Asset>() {
            @Override
            public int compare(Asset o1, Asset o2) {
                return (int) (o1.getId() - o2.getId());
            }
        });
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