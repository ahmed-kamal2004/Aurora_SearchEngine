package aurora.search.dev.engine.Crawler;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.LoggerContext;
//import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;


public class Crawler {
    Set<String>excludedLinks =Collections.synchronizedSet( new HashSet<>());
    Queue<String>unCrawledLinks = new ConcurrentLinkedQueue<>();
    Set<String> crawledLinks = Collections.synchronizedSet( new HashSet<>());
    Set<String>robotChecked = Collections.synchronizedSet( new HashSet<>());
    Set<String>compactStrings = Collections.synchronizedSet(new HashSet<>());
    ArrayList<String>seedset = new ArrayList<>();
    int Crawled=0;
    final String UserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    private static  MongoDatabase database;
    public void exceptRobotLinks(String baseUrl){

        try{
            URL realUrl = new URL(baseUrl);
            String ready =realUrl.getProtocol()+"://"+realUrl.getHost()+"/"+"robots.txt";
           synchronized (this.robotChecked) {
               if (robotChecked.contains(ready)) return;
               System.out.println("Reading Excluded Links from here  : "+ready);
               robotChecked.add(ready);
               Map<String, String> map = new HashMap<>();
                map.put("url", ready);
                addToDatabase("RobotChecked", database, map);
           }

            Connection.Response response = Jsoup.connect(ready)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .execute();
            if(response.statusCode()>399){
                return;
            }
            String doc = response.parse().text();
            String[]lines = doc.split(" ");
            boolean toExclude = false;
            List<Map<String,String>>excludeList = new ArrayList<>();
            for(int i =0 ;i<lines.length;i++){
                if(lines[i].compareTo("User-agent:")==0 && lines[i+1].compareTo("*")==0){
                    toExclude = true;
                }
                else if(lines[i].compareTo("User-agent:")==0 && lines[i+1].compareTo("*")!=0) {
                    toExclude=false;
                }
                if(toExclude&&lines[i-1].compareTo("Disallow:")==0){
                    Map<String, String> map = new HashMap<>();
                    synchronized (this.excludedLinks){
                        if (lines[i].startsWith("/")){
                            excludedLinks.add(realUrl.getProtocol() + "://" + realUrl.getHost() + lines[i]);
                            map.put("url", realUrl.getProtocol() + "://" + realUrl.getHost() + lines[i]);
                        }
                        else
                            {       
                                excludedLinks.add(realUrl.getProtocol() + "://" + realUrl.getHost() + '/' + lines[i]);
                                map.put("url", realUrl.getProtocol() + "://" + realUrl.getHost() + lines[i]);
                            }
                    }
                    excludeList.add(map);
                }
            }
                addManyToDatabase("ExcludedUrls", database, excludeList);

                 return;
            }
        catch(Exception e){
            return;
        }

    }

    public String normalizeURL(String url) {
       if (url == null || url.isEmpty() || url.charAt(0) == '#' || url.length() == 1)
           return null;

       try {
           url = url.toLowerCase();
           URL baseUrl = new URL(url);
           String host = baseUrl.getHost();
           String path = baseUrl.getPath();
           String searchQuery = baseUrl.getQuery()!=null?baseUrl.getQuery():"";
           if(searchQuery!=null){
               String[] searchParams = searchQuery.split("&");
               Arrays.sort(searchParams);
               StringBuilder searchSorted = new StringBuilder();
               for (int i = 0; i < searchParams.length; i++) {
                   searchSorted.append(searchParams[i]);
                   if (i != searchParams.length - 1)
                       searchSorted.append('&');
               }
               searchQuery = searchSorted.toString();
           }
           // Add "www." to the host if it's missing
           if (!host.startsWith("www.")) {
               host = "www." + host;
           }
           StringBuilder Path  = new StringBuilder(path);
           for(int i = 0;i<Path.length()-1;){
               if(Path.charAt(i)==Path.charAt(i+1)&&Path.charAt(i)=='/'){
                   Path.delete(i,i+1);
               }
               else i++;
           }
           // Remove trailing slash from the path if present
           path = Path.toString();
           for(int i=0;i<path.length();i++){
               if(path.charAt(i)=='%'){
                   int num = Integer.parseInt(path.substring(i+1,i+3),16);
                   path=path.replace(path.substring(i,i+3),String.valueOf((char)num));
               }
           }
           // Concatenate host and path to form the normalized URL
           String normalizedUrl = baseUrl.getProtocol()+"://"+host + path+((searchQuery!="")?"?"+searchQuery:"");
           return normalizedUrl;
       } catch (MalformedURLException e) {
           return null;
       }
    }
    public  void getUrlsFromHTMLPage(String baseUrl) {
        Crawled = crawledLinks.size();
        // add here the seed set collection if wants to continue
        synchronized(this.unCrawledLinks)
        {
            unCrawledLinks.add(normalizeURL(baseUrl));
            deleteFromDatabase("Seedset", database,normalizeURL(baseUrl));
        }
        while(!unCrawledLinks.isEmpty()&&crawledLinks.size()<6000){
            String currentUrl;
            synchronized (this.unCrawledLinks) {
                currentUrl = unCrawledLinks.poll();
                deleteFromDatabase("UncrawledUrls", database, currentUrl);    
            }
            try {
                currentUrl = normalizeURL(currentUrl);
                    Connection.Response response = Jsoup.connect(currentUrl)
                    .userAgent(UserAgent)
                    .header("Accept-Language", "*")
                    .execute();
                    if (response.statusCode() > 399) {
                        continue;
                    }
                // Extract meaningful content from HTML (e.g., text from paragraphs
                Document doc = response.parse();
               // System.out.println(doc.text());
                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append(doc.title());
                ArrayList<Element> para = new ArrayList<Element>();
                para = doc.body().getElementsByTag("p");
                for (Element e:para){
                    contentBuilder.append(e.text());
                }
                // System.out.println(content);
                String compactString  = compactStringGenerator(contentBuilder.toString());
                synchronized (this.crawledLinks) {
                        if(crawledLinks.size()<6000 &&!crawledLinks.contains(currentUrl))
                        {
                            if(compactStrings.contains(compactString)==true){
                                System.out.println(currentUrl+"  has been crawled before");
                            }
                            else {
                            crawledLinks.add(currentUrl);
                            Map<String, String> map = new HashMap<>();
                            map.put("url", currentUrl);
                            addToDatabase("CrawledUrls", database,map);
                            System.out.println("Crawling "+currentUrl + " \tCrawled Sites till now : "+ crawledLinks.size());
                             }
                        }
                    }
                synchronized(this.compactStrings){
                        compactStrings.add(compactString);
                        Map<String, String> map = new HashMap<>();
                        map.put("url", currentUrl);
                        map.put("hash",compactString);
                        addToDatabase("CompactStrings", database,map);
                    }
                String contentType = response.headers().get("Content-Type");
                if (contentType == null) {
                        contentType = response.headers().get("content-type");
                    }
                if (!contentType.contains("text/html")) {
                        System.out.println("Unsupported content type: " + contentType);
                        continue;
                    }
                exceptRobotLinks(currentUrl);
                    ArrayList<Element> links = new ArrayList<Element>();
                    links = doc.getElementsByTag("a");
                    synchronized (this.unCrawledLinks){
                        ArrayList<Map<String,String>> uncrawled  = new ArrayList<>();
                        for (Element link : links) {
                            String s = normalizeURL(link.attr("href"));
                            if (s != null) {
                                Map<String,String> map = new HashMap();
                                if (!excludedLinks.contains(s) && !unCrawledLinks.contains(s) && !crawledLinks.contains(s)) {
                                    unCrawledLinks.add(s);

                                    map.put("url", s);
                                    uncrawled.add(map);
                                }
                            }
                        }
                        addManyToDatabase("UncrawledUrls", database, uncrawled);
                    }
                    
                }
                catch(Exception e){
                    continue;
                }
                
            }
        }
        public  void Starter(int id,int nthreads){
            for(int i = id*this.seedset.size()/nthreads;i<(id+1)* this.seedset.size()/nthreads;i++){
                getUrlsFromHTMLPage(seedset.get(i));
            }
        }
        
            @SuppressWarnings("finally")
            public static Set<String> getFromDatabase(Set<String> result,MongoDatabase database,String collectionName){
                MongoCollection <org.bson.Document> collection = database.getCollection(collectionName);
                MongoCursor<org.bson.Document> cursor = collection.find().iterator();
                try {
                    while (cursor.hasNext()) {
                        org.bson.Document document = cursor.next();
                        result.add(document.get("url").toString());
                    }
                } finally {
                    cursor.close();
                    return result;
                }
            }
            @SuppressWarnings({ "unchecked", "finally" })
            public static ArrayList<String> getSeedSet(MongoDatabase database){
                ArrayList<String>urls = new ArrayList<>();
                MongoCollection collection = database.getCollection("Seedset");
                MongoCursor<org.bson.Document> cursor = collection.find().iterator();
                try {
                    while (cursor.hasNext()) {
                        org.bson.Document document = cursor.next();
                        urls.add(document.get("url").toString());
                    }
                } finally {
                    cursor.close();
                    return urls;
                }
            }
        
            public static void addToDatabase(String collectionName,MongoDatabase database, Map<String,String>Document){
                    MongoCollection <org.bson.Document> collection = database.getCollection(collectionName);
                    collection.insertOne(new org.bson.Document(Document));
            }
            public static void deleteFromDatabase(String collectionName, MongoDatabase database, String Document){
                MongoCollection <org.bson.Document> collection = database.getCollection(collectionName);
                DeleteResult deleteResult = collection.deleteOne(new org.bson.Document("url",Document));
            }
            public void clearAllCollections(MongoDatabase database){
                org.bson.Document doc = new org.bson.Document();
                database.getCollection("CrawledUrls").deleteMany(doc);
                database.getCollection("ExcludedUrls").deleteMany(doc);
                database.getCollection("RobotChecked").deleteMany(doc);
                database.getCollection("Seedset").deleteMany(doc);
                database.getCollection("UncrawledUrls").deleteMany(doc);
                database.getCollection("CompactStrings").deleteMany(doc);
                this.crawledLinks.clear();
            }
            public static void addManyToDatabase(String collectionName, MongoDatabase database, List<Map<String, String>> documents) {
                MongoCollection<org.bson.Document> collection = database.getCollection(collectionName);
                collection.insertMany(documents.stream().map(org.bson.Document::new).toList());
            }
            public static Queue<String> getUnCrawledLinks(MongoDatabase database,Queue<String> queue){
                MongoCollection collection = database.getCollection("UncrawledUrls");
                MongoCursor<org.bson.Document> cursor = collection.find().iterator();
                try {
                    while (cursor.hasNext()) {
                        org.bson.Document document = cursor.next();
                        queue.add(document.get("url").toString());
                    }
                } finally {
                    cursor.close();
                    return queue;
                }
            }
            public static String compactStringGenerator(String input) {
        try {
            
            // Create MD5 Hash instance
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            // Update the message digest with the byte array of the input string
            md.update(input.getBytes());
            
            // Get the hash bytes
            byte[] hashBytes = md.digest();
            
            // Convert the byte array to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        String connectionString = "mongodb+srv://newpro125:newPro125@projectdb.m3fenzd.mongodb.net/?retryWrites=true&w=majority&appName=projectDB";
        ConnectionString  connString = new ConnectionString(connectionString);
        MongoClient mongoClient = MongoClients.create(connString);
        database = mongoClient.getDatabase("projectDB"); 
        Scanner cin2 = new Scanner(System.in);
        Scanner cin = new Scanner(new File("/media/a7med/Local Disk/University/APT Project(CurrentWorking)/Aurora_SearchEngine/SeedList.txt"));
        Crawler crawler = new Crawler();
        System.out.println("--------------------Welcome to the Crawler--------------------------------");
        crawler.crawledLinks = getFromDatabase(crawler.crawledLinks, database, "CrawledUrls");
        String next=null;
        if(crawler.crawledLinks.size()>0){
            do{
                System.out.println("You've Crawled some website already!\nDo you want to continue (Y/N):");
            next = cin2.next();
        }while(!next.equalsIgnoreCase("Y")&&!next.equalsIgnoreCase("N"));
            if(next.equalsIgnoreCase("Y")){    
                System.out.println("Please wait till Preparing The old data!");
            crawler.excludedLinks = getFromDatabase(crawler.excludedLinks, database, "ExcludedUrls");
            crawler.robotChecked = getFromDatabase(crawler.robotChecked, database, "RobotChecked");
            crawler.seedset = getSeedSet(database);
            crawler.unCrawledLinks = getUnCrawledLinks(database,crawler.unCrawledLinks);
            crawler.compactStrings = getFromDatabase(crawler.compactStrings, database, "CompactStrings");
            }
        }
        
        if(next==null||next.equalsIgnoreCase("N")){
            System.out.println("Please Wait till clearing old state is done !");
            crawler.clearAllCollections(database);
            ArrayList<Map<String,String>>maps = new ArrayList<>();
            while(cin.hasNext()){
                Map<String,String> map = new HashMap<>();
                String res = cin.next();
                crawler.seedset.add(res);
                map.put("url",res);
               maps.add(map);
            }
            addManyToDatabase("Seedset",database,maps);
        } 
        System.out.printf("Enter the number of Threads: ");
        int nThreads = cin2.nextInt();
        Thread [] threads = new Thread[nThreads];
        for(int i=0;i<nThreads;i++){
            final int id =i;
            threads[i] =new Thread(()-> crawler.Starter(id,nThreads));
            threads[i].start();
        } 
        for(Thread thread:threads){
            try {
                thread.join(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  
        }
        System.out.println("______________________________________________________");
        System.out.println(crawler.crawledLinks.size());
        System.out.println("------------------------Hello,Restricted Area-----------------------");
        System.out.println(crawler.excludedLinks.size());
    }
}
