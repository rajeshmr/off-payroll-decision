
# offpayroll-decision-service


## Endpoint URLs
POST /decide/v1/

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
		"version": 1.0, 
		"sections": [
						{"name": "personal-service",
							"questions": [
											{"id": 1, "answer": true},
											{"id": 2, "answer": false},
											{"id": 3, "answer": null}
								]},
						{"name": "helper",
							"questions": [
											{"id": 1, "answer": false},
											{"id": 2, "answer": false},
											{"id": 3, "answer": false}
								]},
						{"name": "control",
							"questions": [
											{"id": 1, "answer": true},
											{"id": 2, "answer": true},
											{"id": 3, "answer": true}
								]},
						{"name": "financial-risk",
							"questions": [
											{"id": 1, "answer": false},
											{"id": 2, "answer": true},
											{"id": 3, "answer": false}
								]},
						{"name": "business-structure",
							"questions": [
											{"id": 1, "answer": false},
											{"id": 2, "answer": false},
											{"id": 3, "answer": true}
								]},
						{"name": "part-of-organisation",
							"questions": [
											{"id": 1, "answer": true},
											{"id": 2, "answer": true},
											{"id": 3, "answer": true},
											{"id": 4, "answer": true}
								]},
						{"name": "miscalaneous",
							"questions": [
											{"id": 1, "answer": false},
											{"id": 2, "answer": false}
								]}

		]	
	}
```
| Attribute        | Required           | Description                                                          |
| :---------------- |:------------------:| :--------------------------------------------------------------------|
| version          | true               | The version of the QuestionSet being used and therefore the endpoint |
| sections         | true               | An array of completed questions. _Note:_ does not need to contain all the questions. Once all the questions are present in this array then a Decision response will be present. Though depending on the QuestionSet a Decision can be arrived at before all questions are present, this is known as a 'hard-exit' |


## Response

* HTTP 200 OK with a JSON body containing a full or partial decision

```json
	{
		"version": 1.0,
		"result": "Ouside IR35",
		"continue": false,
		"score": [{"personal-service": "HIGH"}, {"helper": "LOW"}, {"control": "LOW"}, {"financial-risk": "HIGH"}, {"business-structure": "LOW"}, {"part-of-organisation": "HIGH"}, {"miscalaneous": "HIGH"}],
		"question-set": {"version":1.0,"sections":[{"name":"personal-service","questions":[{"id":1,"answer":true},{"id":2,"answer":false},{"id":3,"answer":null}]},{"name":"helper","questions":[{"id":1,"answer":false},{"id":2,"answer":false},{"id":3,"answer":false}]},{"name":"control","questions":[{"id":1,"answer":true},{"id":2,"answer":true},{"id":3,"answer":true}]},{"name":"financial-risk","questions":[{"id":1,"answer":false},{"id":2,"answer":true},{"id":3,"answer":false}]},{"name":"business-structure","questions":[{"id":1,"answer":false},{"id":2,"answer":false},{"id":3,"answer":true}]},{"name":"part-of-organisation","questions":[{"id":1,"answer":true},{"id":2,"answer":true},{"id":3,"answer":true},{"id":4,"answer":true}]},{"name":"miscalaneous","questions":[{"id":1,"answer":false},{"id":2,"answer":false}]}]}
	}

```

| Attribute        | Required           | Description                                                                                                    |
| :---------------- |:------------------:| :--------------------------------------------------------------------------------------------------------------|
| version          | true               | The version of the QuestionSet sent in the request and therefore applied to this Response                      |
| result           | true               | An enumeration of "Outside IR35" &#124; "Inside IR35" &#124; "Unknown"|
| continue         | true               | true if the input QuestionSet that created this Decision is incomplete or false if a QuestionSet has been completed and the Decision is therefore final|
| score            | optionaly empty    | An optional array of scores populated only when attribute "contine" is false and therefore a Decision is final |
| question-set     | false              | Present if the Decision is final                                                                               |

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
