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
    public static final String CRAWLER_URLS_COLL_NAME = "CrawledUrls";
    public static final String WORDS_COLL_NAME = "WordsCollection";
    public static final String PARAGRAPHS_COLL_NAME = "ParagraphsCollection";
    public static final String INDEXED_URLS_COLL_NAME = "IndexedUrls";
    // Constants for CrawlerController
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    public static final String SEED_SET_COL = "SeedSet";
    public static final String UNCRAWLED_URLS_COL = "UncrawledUrls";
    public static final String ROBOT_CHECKED_COL = "RobotChecked";
    public static final String COMPACT_STRING_COL = "CompactStrings";
    public static final String EXCLUDED_URLS_COL = "ExcludedUrls";
    public static final String KEY = "URL";
    public static final String DATA_BASE = "test";
    public static final String USERNAME = "newpro125";
    public static final String PASSWORD = "newPro125";
    public static final String DATABASE_CLUSTER_URL = "projectdb.m3fenzd.mongodb.net/";
    public static final String SEED_LIST_PATH = "SeedList.txt";

}
