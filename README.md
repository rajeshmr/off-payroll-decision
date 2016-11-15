
# off-payroll-decision-service


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
  "version": 1,
  "sections": [
    {
      "name": "personal-service",
      "facts": {
        "personal-service.short-name-tba-1": true,
        "personal-service.short-name-tba-2": true
      }
    },
    {
      "name": "helper",
      "facts": {
        "helper.short-name-tba-1": true,
        "helper.short-name-tba-2": false
      }
    },
    {
      "name": "control",
      "facts": {
        "control.short-name-tba-1": true,
        "control.short-name-tba-2": true
      }
    },
    {
      "name": "financial-risk",
      "facts": {
        "financial-risk.short-name-tba-1": true,
        "financial-risk.short-name-tba-2": true
      }
    },
    {
      "name": "business-structure",
      "facts": {
        "business-structure.short-name-tba-1": true,
        "business-structure.short-name-tba-2": true
      }
    },
    {
      "name": "part-of-organisation",
      "facts": {
        "part-of-organisation.short-name-tba-1": true,
        "part-of-organisation.short-name-tba-2": true,
        "part-of-organisation.short-name-tba-3": true
      }
    },
    {
      "name": "miscalaneous",
      "facts": {
        "miscalaneous.short-name-tba-1": true,
        "miscalaneous.short-name-tba-2": true
      }
    }
  ]
}
```
| Attribute        | Required           | Description                                                          |
| :---------------- |:------------------:| :--------------------------------------------------------------------|
| version          | true               | The version of the QuestionSet being used and therefore the endpoint |
| sections         | true               | An array of completed QuestionSet Sections. _Note:_ does not need to contain all the sections. Once all the sections are present in this array then a Decision response will be present. Though depending on the QuestionSet a Decision can be arrived at before all sections are present, this is known as a 'hard-exit' |


## Response

* HTTP 200 OK with a JSON body containing a full or partial decision

```json
	{
		"version": 1.0,
		"result": "Outside IR35",
		"continue": false,
		"score": [{"personal-service": "HIGH"}, {"helper": "LOW"}, {"control": "LOW"}, {"financial-risk": "HIGH"}, {"business-structure": "LOW"}, {"part-of-organisation": "HIGH"}, {"miscalaneous": "HIGH"}],
		"question-set": {"version":1,"sections":[{"name":"personal-service","facts":{"personal-service.short-name-tba-1":true,"personal-service.short-name-tba-2":true}},{"name":"helper","facts":{"short-name-tba-1":true,"short-name-tba-2":false}},{"name":"control","facts":{"helper.short-name-tba-1":true,"helper.short-name-tba-2":true}},{"name":"financial-risk","facts":{"financial-risk.short-name-tba-1":true,"financial-risk.short-name-tba-2":true}},{"name":"business-structure","facts":{"business-structure.short-name-tba-1":true,"business-structure.short-name-tba-2":true}},{"name":"part-of-organisation","facts":{"part-of-organisation.short-name-tba-1":true,"part-of-organisation.short-name-tba-2":true,"part-of-organisation.short-name-tba-3":true}},{"name":"miscalaneous","facts":{"miscalaneous.short-name-tba-1":true,"miscalaneous.short-name-tba-2":true}}]}
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
