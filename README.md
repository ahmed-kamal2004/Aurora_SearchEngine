# Aurora
## New Search Engine Coming to the World
<p align="center">
  <img src="https://readme-typing-svg.demolab.com/?lines=Aurora+:+hello+world&font=Dancing_Script%70Code&color=%247E3DCE&size=20&center=true&width=500&height=100&duration=4000&pause=1&theme=dark" alt="Aurora">
</p>
<p align="center">
  <img src="https://github.com/ahmed-kamal2004/Aurora/blob/main/images.jpg?raw=true" height=200 width=500>
  <img src="https://static.toiimg.com/thumb/msid-96286166,width-748,height-499,resizemode=4,imgsize-129000/.jpg?raw=true" height=200 width=500>

</p>

## API "Spring Boot"
 #### POST:
 - "http://localhost:8090/ranker/calc" -- for calculating TD and IDF
    ##### Response
   > True or False "if there is an error" <br\>
 - "http://localhost:8090/ranker/rank" -- for applying PageRank algorithm
    ##### Response
   > True or False "if there is an error" <br\>
 #### GET:
 - "http://localhost:8090/ranker/search"
    ##### Request Body
   > { "query": "al-Khwarizmi" } <br \>
    ##### Response
   > Ranked Urls with appropriate information. <br\>
 #### PUT:
 -
 #### DELETE:
 -
 
## Ranker
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

## Database "MongoDB"
#### Inverted File
- Details :
#### Urls
- Details :


## How to Run 🚀?
  #### Ranker & Search engine
  #### Crawler
  #### Indexer
