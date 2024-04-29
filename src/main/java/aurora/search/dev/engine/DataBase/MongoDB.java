package aurora.search.dev.engine.DataBase;

import aurora.search.dev.engine.Helper.Constants;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.DBObject;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

public class MongoDB {
    MongoCollection<Document> crawledUrlsCollection;
    MongoCollection<Document> wordsCollection;

    MongoCollection<Document> paragraphsCollection;
    MongoCollection<Document> indexedUrlsCollection;

    public MongoDB(String DB_Name) {
        ConnectionString connectionString = new ConnectionString(Constants.CONN_STRING);
        MongoClient client = MongoClients.create(connectionString);
        MongoDatabase DBController = client.getDatabase(DB_Name);
        crawledUrlsCollection = DBController.getCollection(Constants.CRAWLER_URLS_COLL_NAME);
        wordsCollection = DBController.getCollection(Constants.WORDS_COLL_NAME);
        paragraphsCollection = DBController.getCollection(Constants.PARAGRAPHS_COLL_NAME);
        indexedUrlsCollection = DBController.getCollection(Constants.INDEXED_URLS_COLL_NAME);
        System.out.println("SUCESSS");
    }

    public boolean isUrlIndexed(String url) {
        return indexedUrlsCollection.find(new Document("url", url)).iterator().hasNext();
    }

    public boolean isParagraphIndexed(String paragraph, Integer paragraphId) {
        synchronized (this) {
            DBObject and_part1 = new BasicDBObject("_id", new BasicDBObject("$eq", paragraphId));
            DBObject and_part2 = new BasicDBObject("paragraph", new BasicDBObject("$eq", paragraph));
            BasicDBList and = new BasicDBList();
            and.add(and_part1);
            and.add(and_part2);
            DBObject query = new BasicDBObject("$and", and);

            return paragraphsCollection.find((Bson) query).iterator().hasNext();
        }
    }

    public Iterable<Document> getCrawlerCollection(int batchSize, int iteration) {
        List<Document> results = new ArrayList<>();
        FindIterable<Document> iterable = crawledUrlsCollection.find().skip(iteration * batchSize).limit(batchSize);
        iterable.into(results);
        return results;
    }

    public void addIndexedWord(String newWord, List<Document> newWordPages) {
        if (newWordPages == null) {
            return;
        }
        Document filter = new Document("word", newWord);
        FindIterable<Document> fi = wordsCollection.find(filter);
        Iterator<Document> it = fi.iterator();
        Boolean newWordExists = it.hasNext();
        int newWordPagesCnt = newWordPages.size();
        if (newWordExists) {
            List<Document> prevWordPages = it.next().get("pages", List.class);
            int prevWordPagesCnt = prevWordPages.size();
            prevWordPages.addAll(newWordPages);
            HashSet<Document> uniquePages = new HashSet<>();
            uniquePages.addAll(prevWordPages);
            wordsCollection.findOneAndUpdate(filter, new Document("$set", new Document("word", newWord)
                    .append("IDF", Math.log(crawledUrlsCollection.countDocuments() / (double) newWordPagesCnt + prevWordPagesCnt))
                    .append("pages", uniquePages)));
        } else {
            Document doc = new Document("word", newWord)
                    .append("IDF", Math.log(crawledUrlsCollection.countDocuments() / (double) newWordPagesCnt))
                    .append("pages", newWordPages);
            wordsCollection.insertOne(doc);
        }
    }
    public void addIndexedUrl(Integer _id, String url, String title) {
        boolean pageExists = isUrlIndexed(url);
        if (!pageExists) {
            Document doc = new Document("_id", _id)
                    .append("url", url)
                    .append("title", title);
            indexedUrlsCollection.insertOne(doc);
        }
    }
    public void addIndexedParagraph(String paragraph, Integer paragraphId) {
        synchronized (this) {
            boolean paragraphExists = isParagraphIndexed(paragraph, paragraphId);
            if (!paragraphExists) {
                Document paragraphDoc = new Document();
                paragraphDoc.append("_id", paragraphId)
                        .append("paragraph", paragraph);

                paragraphsCollection.insertOne(paragraphDoc);
            }
        }
    }
    public int getIndexedParagraphsCount() {
        synchronized (this) {
            return (int) paragraphsCollection.countDocuments();
        }
    }
    public int getIndexedUrlsCount() {
        return (int) indexedUrlsCollection.countDocuments();
    }

}