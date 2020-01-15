# mybaas
My BackEnd as a Service Experimental Project (Microservices Arch)

All microservices will run on a platform and supports 
- [x] Service discovery via Apache Zookeper
- [x] Cache support via Apache Infinispan
- [ ] IAM via KeyCloak
- [x] MongoDB as document storage
- [ ] API gateway 

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


 