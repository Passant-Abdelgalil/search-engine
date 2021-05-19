# crawler-based-search-engine
Crawler based search engine


Requirements:
- [ ] Crawler must not visit same url more than once.
- [ ] only crawl documents of specific types (HTML)
- [ ] maintain its state, if interrupted, be started again to crawl the documents on the list without revisiting documents that have been previously downloaded.
- [ ] check for Robot.txt
- [ ] a multithreaded crawler implementation, the user can control the number of threads before starting the crawler
- [ ] When Crawler finishes one iteration by reaching stopping criteria, it restarts again, Frequency of crawling is an important part of a web crawler. Some sites will be visited more often than others. You have to set some criteria to the sites. In another words, during recrawl, you don’t have to repeat all the sites again.