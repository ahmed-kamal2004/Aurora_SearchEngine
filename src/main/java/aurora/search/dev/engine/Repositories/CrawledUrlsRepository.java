package aurora.search.dev.engine.Repositories;

import aurora.search.dev.engine.Models.CrawledUrls;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawledUrlsRepository extends MongoRepository<CrawledUrls,String> {
    public List<CrawledUrls> findAllByOrderByRankDesc();
    public CrawledUrls findByURL(String url);

}
