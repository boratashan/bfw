# bfw
BFW Experimental Project (Microservices Arch)

All microservices will run on a platform and supports 
- [x] Service discovery via Apache Zookeper
- [x] Cache support via Apache Infinispan
- [ ] IAM via KeyCloak
- [x] MongoDB as document storage
- [ ] API gateway 
- [ ] Dependency Injection Support

##b-commons
- [X] Provides base verticles supports common services (Service registration, discovery, configuration)
- [ ] Configuration standarts (microservice, deployment, logs etc...)
- [ ] Distributed configuration and configuration update hook via Apache Zookeeper
- [X] Log4j2 text based log management
- [ ] Providing basic DI through Google Guice
- [ ] Provides standart logging with exception trace numbers.

##b-currencycrawler
This service 
- [x] crawls one website on the internet
- [x] stores captured currency rates into MongoDB instance
- [x] announce captured currency rates to other subscribers via event bus
- [x] announce captured rates via ActiveMQ
- [x] Has got APIs as REST API and EventBus Service - IN-PROGRESS
- [x] getLatestCurrencyRates which returns the last rate stored in mongodb
- [x] getCurrencyRates which returns all rates with pagination and criteria(Datetime, number of items in a page, sorting etc...) 


##b-currencylistener
This service 
- [x] listens crawler and get updated by currencyrate announcement


##b-contentcollector
Responsible for downloading specific web pages from the internet and save them into storage.
This service is  aimed to demonstrate executing blocking code, using distributed locking to share tasks
on the resources, saving content into distributed storage and informing other services regarding download complete 
events.
- [] Building basic running microservice project. (Using commons library)
- [] Reading bootstrapping configuration from commandline, if commandline is not specified then read from resources.
- [] Obtaining system logger (Configuring logger via commandline or resource and inject via DI)
- [] Reading configuration from distributed configuration storage (Apache zookeeper)
- [] Reload configuration from distributed configuration storage when it is updated
- [] Prevent microservices to download the same resource at the same by distributed locking. (Creating next available task ordering by zookeeper)
- [] Download content and write into the storage (MongoDB as storage)  
- [] Obtaining operational logger (Logger which writes logs into MongoDB)
- [] Implementing rest interface for serving downloaded contents. (Single Responsibilty Principle for µServices Arch.)
  
  
 