![image](https://github.com/ahmed-kamal2004/Aurora_SearchEngine/assets/98265644/02f14563-9759-4868-a87c-6a72545838e4)[![License](https://img.shields.io/github/license/get-icon/geticon)](https://github.com/get-icon/geticon/blob/master/LICENSE "License")


# Aurora
## New Search Engine Coming to the World
## Built With
  - Java
  - SpringBoot
  - ReactJS
## Intro
<p align="center">
  <img src="https://readme-typing-svg.demolab.com/?lines=Aurora+:+hello+world&font=Dancing_Script%70Code&color=%247E3DCE&size=20&center=true&width=500&height=100&duration=4000&pause=1&theme=dark" alt="Aurora">
</p>
<p align="center">
  <img src="https://static.toiimg.com/thumb/msid-96286166,width-748,height-499,resizemode=4,imgsize-129000/.jpg?raw=true" height=200 width=500>
</p>

## API "Spring Boot"
 #### POST:
 - "http://localhost:8090/ranker/rank" -- for applying PageRank algorithm
    ##### Response
   > True or False "if there is an error" <br/>
 #### GET:
 - "http://localhost:8090/ranker/search"
    ##### Request Body
   > { "query": "al-Khwarizmi" } <br />
    ##### Response
   > Ranked Urls with appropriate information. <br/>
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
![image](https://github.com/ahmed-kamal2004/Aurora_SearchEngine/assets/98265644/5592cb3e-9f0b-47a2-9c3b-56fab912969c)


## Demo
![SearchEngine](https://github.com/ahmed-kamal2004/Aurora_SearchEngine/assets/98265644/65ed9d1a-cfad-4bba-b0d0-793458a97a85)
![image](https://github.com/ahmed-kamal2004/Aurora_SearchEngine/assets/98265644/a9b517fb-287f-4090-81d8-aced034ab9b4)
![image](https://github.com/ahmed-kamal2004/Aurora_SearchEngine/assets/98265644/969ee8d9-06a3-4298-8587-9ecddc881d01)

## How to Run 🚀?
  #### Ranker & Search engine
  #### Crawler
  #### Indexer
## CTRL + C
<img align="right" src="https://visitor-badge.laobi.icu/badge?page_id=ahmed-kamal2004.Aurora_SearchEngine"/>
