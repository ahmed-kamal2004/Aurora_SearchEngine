package aurora.search.dev.engine.Repositories;

import aurora.search.dev.engine.Models.InvertedFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvertedFileRepository extends MongoRepository<InvertedFile,String> {
}
