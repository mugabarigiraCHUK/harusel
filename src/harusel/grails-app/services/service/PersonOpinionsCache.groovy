package service

import java.lang.ref.SoftReference
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Cache of Person opinions model.
 * @since 13-Jul-2010 17:20:17
 * @version
 */
class PersonOpinionsCache {
    private static final Log logger = LogFactory.getLog(PersonOpinionsCache.class);
    private final Map<Long, SoftReference<List>> personOpinions = Collections.synchronizedMap(new HashMap<Long, SoftReference<List>>(1000))

    private static PersonOpinionsCache instance = new PersonOpinionsCache();

    private PersonOpinionsCache() {}

    public static PersonOpinionsCache getInstance() {
        instance
    }

    /**
     * Puts new object to cache.
     * @param personId Person id
     * @param opinionsModel Opinions model to put.
     * @return Old opinions model or <code>null</code>
     */
    def put(Long personId, List opinionsModel) {
        logger.debug("Adding to cache model for person with id = ${personId}")
        personOpinions.put(personId, new SoftReference(opinionsModel))
    }

    /**
     * Removes opinions model from cache.
     * @param personId Person id.
     * @return Removed opinions model or <code>null</code>
     */
    def remove(Long personId) {
        logger.debug("Removing cache for person with id = ${personId}")
        personOpinions.remove(personId)
    }

    /**
     * Retrieves opinions model from cache.
     * @param personId Id of person.
     * @return Cached opinions model or <code>null</code>
     */
    def get(Long personId) {
        personOpinions.get(personId)?.get();
    }
}
