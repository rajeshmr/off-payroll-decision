
# off-payroll-decision-service


## Endpoint URLs
POST /decide/

## Service Definitions

Requests use the HTTP `POST` method

### Decision Service

#### Functionality

* Evaluates a "QuestionSet"
* Provides a Decision based on a versioned set of "Decision Tables" _Note:_ This service supports multiple versions of Decision Tables, this version is 'addressed' in the version of the URL and the version number in the request and response JSON. 


## Request

* Body contains QuestionSet JSON

```json
	{
	  "version": "1.0",
	  "correlation-id": "48fd5b38-9047-4d9b-983e-27fb2c5360dd",
	    "personal-service":{
	        "contractrual-right-for-substitute": true,
	        "contractrual-obligation-for-substitute": false
	      
	    },
	    "control":{
	        "can-work-be-instructed": true,
	        "can-worker-be-moved": true
	      
	    },
	    "financial-risk": {
	      "motor-vehical-in-order-to-work": false,
	      "engager-contribution-to-cost": false
	    }
	  
	}
```
| Attribute        | Required           | Description                                                          |
| :---------------- |:------------------:| :--------------------------------------------------------------------|
| version          | true               | The version of the QuestionSet being used and therefore the endpoint |
| correlation-id   | true               | Unique number to identify this QuestionSet and correlate it to its  decision |
| personal-service | false              | Part of the Question Set  ...|
| cotrol           | false              | Part of the Question Set  ...|
| financial-risk   | false              | Part of the Question Set  ...|
| busines-structure| false              | Part of the Question Set  ...|
| part-of-organisation| false              | Part of the Question Set  ...|
| miscellaneous| false              | Part of the Question Set  ...|

 _Note:_ does not need to contain all the sections. Once all the sections are present in this array then a Decision response will be present. Though depending on the QuestionSet a Decision can be arrived at before all sections are present, this is known as a 'hard-exit'


## Response

* HTTP 200 OK with a JSON body containing a full or partial decision

```json
	{
		"version": "1.0",
		"correlation-id": "48fd5b38-9047-4d9b-983e-27fb2c5360dd",
		"result": "Unknown",
		"carry-on-with-questions": true,
		"score": [{"personal-service": "HIGH"}, {"control": "LOW"}, {"financial-risk": "HIGH"}]
	}
```

| Attribute        | Required           | Description                                                                                                    |
| :---------------- |:------------------:| :--------------------------------------------------------------------------------------------------------------|
| version          | true               | The version of the QuestionSet sent in the request and therefore applied to this Response                      |
| correlation-id   | true               | Unique number to identify this QuestionSet and correlate it to its  decision |
| result           | true               | An enumeration of "Outside IR35" &#124; "Inside IR35" &#124; "Unknown"|
| carry-on-with-questions         | true               | true if the input QuestionSet that created this Decision is incomplete <br /> or false if a QuestionSet has been completed and the Decision is therefore final|
| score            | optionaly empty    | An optional array of scores populated only when attribute "carry-on-with-questions" is false and therefore a Decision is final |


* HTTP 400 Bad Request for invalid/error scenarios

```json
	{
		"code": 4001,
		"message": "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
	}
```

| Attribute         | Required           | Description                                                                                                 |
| :-----------------|:------------------:| :-----------------------------------------------------------------------------------------------------------|
| code              | true               | Extension to the HTTP Status Code. Use this code to reference internationalised text for errors if required |
| message           | true               | English readable message as to the reason for the error.                                                    |

## Runing the application locally
To run the application execute

```
sbt ~run 9000 -Drule_sheets.location=[Path to Excel File]” (e.g. /Users/miloszmuszynski/hmrc/decision-service/sheets/kb-rules-01.xls)
```


## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
