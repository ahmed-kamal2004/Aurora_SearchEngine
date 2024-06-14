# Aurora
### Intro
<p align="center">
  <img src="https://readme-typing-svg.demolab.com/?lines=Aurora+:+hello+world+Aurora+is+here&font=Dancing_Script%70Code&color=%247E3DCE&size=20&center=true&width=500&height=100&duration=4000&pause=1&theme=dark" alt="Aurora">
</p>

## New Search Engine Coming to the World
  #### Crawler
    The web crawler is responsible for collecting documents from the web. Key features include:
- **Avoiding Re-visits:** Ensuring the crawler does not visit the same page more than once.
- **URL Normalization:** Checking if different URLs refer to the same page.
- **Document Type Handling:** Limiting crawling to specific document types (HTML for this project).
- **State Maintenance:** Allowing the crawler to resume from where it left off after interruptions.
- **Robots.txt Compliance:** Respecting rules set by web administrators to exclude certain pages.
- **Multithreading:** Supporting user-defined number of threads with proper synchronization.
- **Seed Management:** Careful selection and management of seed URLs.
- **Crawl Limit:** Capable of crawling up to 6000 pages.
- **Visit Order:** Utilizing appropriate data structures to determine the order of page visits.
  
  #### Indexer

  The indexer processes the downloaded documents to facilitate fast and efficient querying. Features include:
- **Persistence:** Maintaining the index in secondary storage (file structure or database).
- **Fast Retrieval:** Optimized for quick response to queries for specific words or sets of words.
- **Incremental Updates:** Capability to update the index with new documents without rebuilding from scratch.
- **Design Consideration:** Ensuring compatibility with the ranker and search modules.

  ### Query Processor
  This module handles user search queries with the following features:
- **Preprocessing:** Preparing search queries for efficient processing.
- **Stemming:** Matching words with the same root (e.g., "travel" matches "traveler", "traveling").
- **Phrase Searching:** Supporting phrase searches with quotation marks, ensuring precise order matching.

### Ranker 🚀
The ranker sorts search results based on relevance and popularity:
- **Relevance:** Calculated using methods like tf-idf, considering word occurrence in titles, headers, and body text.
- **Popularity:** Measured independently of the query, using algorithms like PageRank.
#### PageRank Algorithm
  - Details :
    ```
    PR(i) = (1 - d) + d * Σ(PR(j) / Outlinks(j))  where j points to i
    ```
    Where d is 0.15 "Approx".

    This equation another view is

    First I initialize M
    ```
    M = (1-d) A + dB
    ```
    
    where :
    > d is dumping factor, <br/>
    > S is the number of URLs on the web,<br/>
    > B is Matrix of S x S filled with 1/S float number,<br/>
    > A is a transition matrix of size S x S that indicates the relations between every URL and other URLs outgoing from it.<br/>

    Final Equation :bulb:
    ```
    X = M.T * X
    ```
    The number of Iterations is determined by the degree of precision required.
    Precision criteria: ⚡
    ```
    | norm(X after multiplication operation) - norm(X before multiplication operation) |   should be < Precision Factor
    ```
### Web interface
The web interface provides user interaction with the search engine:
- **Query Handling:** Receives and processes user queries.
- **Result Display:** Shows search results with snippets highlighting query words.
- **Pagination:** Handles large result sets by dividing them into pages.

**Examples**

![SearchEngine](./assets/Main%20Page.png)

![image](./assets/Results%20Page.png)

![image](./assets/Voice%20Recognition.png)

## Built With
  - Java
  - SpringBoot
  - ReactJS


## API "Spring Boot" 📖
 #### POST:
 - "http://localhost:8090/ranker/rank" -- for applying PageRank algorithm
    ##### Response
   > True or False "if there is an error" <br/>
 #### GET:
 - "http://localhost:8090/ranker/search"
    ##### Request Body
   > { "query": "al-Khwarizmi" } <br />
    ##### Response
   > Ranked URLs with appropriate information. <br/>

## Database "MongoDB"
![image](https://github.com/ahmed-kamal2004/Aurora_SearchEngine/assets/98265644/5592cb3e-9f0b-47a2-9c3b-56fab912969c)







## Contributors
<a href="https://github.com/ahmed-kamal2004/Aurora_SearchEngine/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=ahmed-kamal2004/Aurora_SearchEngine" />
</a>

## LICENSE

[MIT](/LICENSE) © ahmed-kamal2004
<img align="right" src="https://visitor-badge.laobi.icu/badge?page_id=ahmed-kamal2004.Aurora_SearchEngine"/>
