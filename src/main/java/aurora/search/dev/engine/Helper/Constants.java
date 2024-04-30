package aurora.search.dev.engine.Helper;

public class Constants {

    // Integers Constants
    public static final int NUM_OF_THREADS = 16;
    public static final int BATCH_SIZE = 10;
    public static final int PARAGRAPH_LENGTH = 200;
    public static final int DESCRIPTION_LENGTH = 40;

    // Strings Constants
    public static final String CONN_STRING = "mongodb://localhost:27017";
    public static final String DATABASE_NAME = "test";
    public static final String CRAWLER_URLS_COLL_NAME= "CrawledUrls";
    public static final String WORDS_COLL_NAME = "WordsCollection";
    public static final String PARAGRAPHS_COLL_NAME = "ParagraphsCollection";
    public static final String INDEXED_URLS_COLL_NAME = "IndexedUrls";
}
