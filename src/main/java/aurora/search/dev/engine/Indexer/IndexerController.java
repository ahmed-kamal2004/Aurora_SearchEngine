package aurora.search.dev.engine.Indexer;

import aurora.search.dev.engine.Helper.Constants;
import aurora.search.dev.engine.DataBase.MongoDB;

import org.bson.Document;
import java.util.Iterator;


public class IndexerController {
    public static MongoDB DBController;
    public static void main(String[] args) throws InterruptedException {
        DBController = new MongoDB(Constants.DATABASE_NAME);
        startIndexer(DBController);
    }

    public static void startIndexer(MongoDB DB) throws InterruptedException {
        class RunIndexer implements Runnable {
            int cnt = DB.getIndexedUrlsCount();
            int iteration = cnt / Constants.BATCH_SIZE - 1;
            public RunIndexer() throws InterruptedException {
                System.out.printf("indexed pages: %d\n", cnt);

                Thread thread = new Thread(this);
                String Idx = Integer.toString(0, 10);
                thread.setName(Idx);

                thread.start();
                thread.join();
            }
            public void run() {
                try {
                    Iterator<Document> CrawledPagesCollection;
                    int localIteration = -1;
                    synchronized (this) {
                        iteration++;
                        localIteration = iteration;
                        System.out.printf("Thread %s updating iteration first block, iteration = %d\n", Thread.currentThread().getName(), localIteration);
                    }
                    CrawledPagesCollection = DB.getCrawlerCollection(Constants.BATCH_SIZE, localIteration).iterator();
                    Document crawledPageDoc;
                    String url;
                    Integer _id;
                    while (CrawledPagesCollection.hasNext()) {
                        crawledPageDoc = CrawledPagesCollection.next();
                        url = crawledPageDoc.getString("URL");
                        System.out.println("Current URL\t" + url);
                        synchronized (this) {
                            cnt++;
                            _id = cnt;
                        }
                        System.out.printf("index page: %d url:%s \n", _id, url);
                        Indexer Indexer = new Indexer(DB);
                        Indexer.startIndexer(url, _id);
                        if (!CrawledPagesCollection.hasNext()) {
                            synchronized (this) {
                                iteration++;
                                localIteration = iteration;
                                System.out.printf("Thread %s updating iteration second block, iteration = %d, _id = %d\n", Thread.currentThread().getName(), localIteration, _id);
                            }
                            CrawledPagesCollection = DB.getCrawlerCollection(Constants.BATCH_SIZE, localIteration).iterator();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        RunIndexer runIndexer = new RunIndexer();
    }
}