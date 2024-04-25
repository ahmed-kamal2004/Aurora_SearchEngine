package aurora.search.dev.engine.Repositories;

import aurora.search.dev.engine.Models.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends MongoRepository<Url,String> {
    public List<Url> findAllByOrderByRankDesc();
}
