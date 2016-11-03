
# decision-service


## Endpoint URLs
POST /decision-service

## Service Definitions

Requests use the HTTP `POST` method

### Decision Service (external facing API - `/decision-service`)

#### Functionality

	*Evaluates given data by feeding them into the rule engine
	*Returns decisions passed back by the engine


## Request
	*Body JSON with data feed for the rule engine


## Response
	*HTTP 200 OK with a JSON body containing a decision
	*HTTP 400 Bad Request for invalid/error scenarios


## Runing the application locally
To run the application execute

```
sbt ~run 9000 -Drule_sheets.location=[Path to Excel File]” (e.g. /Users/miloszmuszynski/hmrc/decision-service/sheets/kb-rules-01.xls)
```


## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
