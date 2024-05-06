package aurora.search.dev.engine.Repositories;

import aurora.search.dev.engine.Models.IndexedUrls;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexedUrlsRepository extends MongoRepository<IndexedUrls,String> {
    public List<IndexedUrls> findAllByOrderByRankDesc();
    public IndexedUrls findByUrl(String url);
}
