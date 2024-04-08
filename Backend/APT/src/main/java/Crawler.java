import java.net.MalformedURLException;
import java.net.*;
import java.sql.SQLOutput;
import java.util.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Crawler {
    Set<String>excludedLinks =Collections.synchronizedSet( new HashSet<>());
    Queue<String>unCrawledLinks = new ConcurrentLinkedQueue<>();
    Set<String> crawledLinks = Collections.synchronizedSet( new HashSet<>());
    Set<String>robotChecked = Collections.synchronizedSet( new HashSet<>());
    int Crawled=0;
    final String UserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    public void exceptRobotLinks(String baseUrl){

        try{
            URL realUrl = new URL(baseUrl);
            String ready =realUrl.getProtocol()+"://"+realUrl.getHost()+"/"+"robots.txt";
           synchronized (this.robotChecked) {
               if (robotChecked.contains(ready)) return;
               System.out.println(ready);
               robotChecked.add(ready);
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
            for(int i =0 ;i<lines.length;i++){
                if(lines[i].compareTo("User-agent:")==0 && lines[i+1].compareTo("*")==0){
                    toExclude = true;
                }
                else if(lines[i].compareTo("User-agent:")==0 && lines[i+1].compareTo("*")!=0) {
                    toExclude=false;
                }
                if(toExclude&&lines[i-1].compareTo("Disallow:")==0){
                    synchronized (this.excludedLinks){
                        if (lines[i].startsWith("/"))
                            excludedLinks.add(realUrl.getProtocol() + "://" + realUrl.getHost() + lines[i]);
                        else
                            excludedLinks.add(realUrl.getProtocol() + "://" + realUrl.getHost() + '/' + lines[i]);
                    }
                }
            }
                 return;
            }
        catch(Exception e){
            return;
        }

    }
    public String normalizeURL(String url) {
//        if (url == null || url.isEmpty() || url.charAt(0) == '#' || url.length() == 1)
//            return null;
//
//        try {
//            url = url.toLowerCase();
//            URL baseUrl = new URL(url);
//            String host = baseUrl.getHost();
//            String path = baseUrl.getPath();
//            String searchQuery = baseUrl.getQuery()!=null?baseUrl.getQuery():"";
//            if(searchQuery!=null){
//                String[] searchParams = searchQuery.split("&");
//                Arrays.sort(searchParams);
//                StringBuilder searchSorted = new StringBuilder();
//                for (int i = 0; i < searchParams.length; i++) {
//                    searchSorted.append(searchParams[i]);
//                    if (i != searchParams.length - 1)
//                        searchSorted.append('&');
//                }
//                searchQuery = searchSorted.toString();
//            }
//            // Add "www." to the host if it's missing
//            if (!host.startsWith("www.")) {
//                host = "www." + host;
//            }
//            StringBuilder Path  = new StringBuilder(path);
//            for(int i = 0;i<Path.length()-1;){
//                if(Path.charAt(i)==Path.charAt(i+1)&&Path.charAt(i)=='/'){
//                    Path.delete(i,i+1);
//                }
//                else i++;
//            }
//            // Remove trailing slash from the path if present
//            path = Path.toString();
//            for(int i=0;i<path.length();i++){
//                if((int)path.charAt(i)>){
//                    int num = Integer.parseInt(path.substring(i+1,i+3),16);
//                    System.out.println(num);
//                    path=path.replace(path.substring(i,i+3),String.valueOf((char)num));
//                }
//            }
//            // Concatenate host and path to form the normalized URL
//            String normalizedUrl = baseUrl.getProtocol()+"://"+host + path+((searchQuery!="")?"?"+searchQuery:"");
//            return normalizedUrl;
//        } catch (MalformedURLException e) {
//            return null;
//        }
        try{
            URI uri = new URI(url);
            return uri.normalize().toString();
        }
        catch(Exception e) {
            return null;
        }
    }
    public  void getUrlsFromHTMLPage(String baseUrl) {
        Crawled = crawledLinks.size();
        unCrawledLinks.add(normalizeURL(baseUrl));
        while(!unCrawledLinks.isEmpty()&&crawledLinks.size()<6000){
                String currentUrl;
                System.out.printf("Crawled Sites till now : %d\n", crawledLinks.size());
                synchronized (this.unCrawledLinks) {
                    currentUrl = unCrawledLinks.poll();
                }
                try {
                    URL Url = new URL(currentUrl);
                    Connection.Response response = Jsoup.connect(baseUrl)
                            .userAgent(UserAgent)
                            .header("Accept-Language", "*")
                            .execute();
                    if (response.statusCode() > 399) {
                        continue;
                    }
                    synchronized (this.crawledLinks) {
                        crawledLinks.add(currentUrl);
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
                    Document doc = response.parse();
                    ArrayList<Element> links = new ArrayList<Element>();
                    links = doc.getElementsByTag("a");
                    synchronized (this.unCrawledLinks){
                        for (Element link : links) {
                            String s = normalizeURL(link.attr("href"));
                            if (s != null) {
                                if (!excludedLinks.contains(s) && !unCrawledLinks.contains(s) && !crawledLinks.contains(s)) {
                                    unCrawledLinks.add(s);
                                }
                            }
                        }
                    }

            }
                catch(Exception e){
                    continue;
                }

        }
    }
    public  void Starter(int id,int nthreads,ArrayList<String>buffer){
        for(int i = id*buffer.size()/nthreads;i<(id+1)* buffer.size()/nthreads;i++){
            getUrlsFromHTMLPage(buffer.get(i));
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        Scanner cin2 = new Scanner(System.in);
        Scanner cin = new Scanner(new File("/media/a7med/Local Disk/University/APT Project/Backend/APT/src/main/java/SeedList.txt"));
        Crawler crawler = new Crawler();
        System.out.println("--------------------Welcome to the Crawler--------------------------------");
        System.out.printf("Enter the number of Threads: ");
        int nThreads = cin2.nextInt();
        ArrayList<String>res = new ArrayList<>();
        while(cin.hasNext()){
            res.add(cin.next());
        }
        Thread [] threads = new Thread[nThreads];
        for(int i=0;i<nThreads;i++){
            final int id =i;
            threads[i] =new Thread(()-> crawler.Starter(id,nThreads,res));
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
        for(String v: crawler.crawledLinks){
            System.out.println(v);
        }
        System.out.println("------------------------Hello,Restricted Area-----------------------");
        System.out.println(crawler.excludedLinks.size());
        for(String v:crawler.excludedLinks){
            System.out.println(v);
        }
    }
}
