
# off-payroll-decision-service


## Endpoint URLs
POST /decide/

## Service Definitions

Requests use the HTTP `POST` method

### Decision Service

#### Functionality

* Evaluates a __QuestionSet__
* Provides a Decision based on a versioned set of "Decision Tables" _Note:_ This service supports multiple versions of Decision Tables, this version is 'addressed' in the version number in the request and response JSON. 


## Request

* Body contains __QuestionSet__ JSON
- A [JSON Schema](off-payroll-question-set-schema.json) defines this __QuestionSet__.
- [Example](off-payroll-question-set-sample.json) JSON with all fields populated.


| Attribute        | Required           | Description                                                          |
| :---------------- |:------------------:| :--------------------------------------------------------------------|
| version          | true               | The version of the QuestionSet being used and therefore the endpoint |
| correlationID   | true               | A value unique to the consumer, to identify this QuestionSet and correlate it to its decision, we have used a UUID however any String of length min 1 max 36 is acceptable |
| personalService | true               | 1st section  of the Question Set |
| control           | false              | 2nd section  of the Question Set |
| financialRisk   | false              | 3rd section  of the Question Set |
| businesStructure| false              | 4th section  of the Question Set |
| partOfOrganisation| false              | 5th section  of the Question Set |
| miscellaneous| false              | 6th section  of the Question Set |

 _Note:_ The __QuestionSet__ does not need to contain all the sections apsection  from the first mandatory 'personalService' section. The QuestionSet __Sections__ from two onwards are optional but they must be supplied in order. For example section three should not be supplied without two and so on. Within each section there are a number of __Scenarios__, for a complete list refer to the [JSON Schema](off-payroll-question-set-schema.json) Once all the sections are present in this QuestionSet then a __Decision__ response will be present. Though depending on the QuestionSet a __Decision__ may be arrived at before all sections are present, this is known as a 'hard-exit'


## Response

* HTTP 200 OK
* Body contains __Decision__ JSON
- A [JSON Schema](off-payroll-decision-schema.json) defines this __Decision__.
- [Example](off-payroll-decision-sample.json) JSON with all fields populated.


| Attribute            | Required           | Description                                                                                                    |
| :------------------- |:------------------:| :--------------------------------------------------------------------------------------------------------------|
| version              | true               | The version of the QuestionSet sent in the request and therefore applied to this Response                      |
| correlationID        | true               | Unique number to identify this QuestionSet and correlate it to its  decision |
| result               | true               | An enumeration of "Outside IR35" &#124; "Inside IR35" &#124; "Unknown"|
| carryOnWithQuestions | true               | true if the input QuestionSet that created this Decision is incomplete <br /> or false if a QuestionSet has been completed and the Decision is therefore final|
| score                | true               | A map of scores fully populated only when attribute "carryOnWithQuestions" is false and therefore a Decision is final |


* HTTP 400 Bad Request for invalid/error scenarios

```json
	{
		"code": 4001,
		"message": "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
	}
```

| Attribute         | Required           | Description                                                                                                 |
| :-----------------|:------------------:| :-----------------------------------------------------------------------------------------------------------|
| code              | true               | Extension to the HTTP Status Code. Use this code to reference internationalised text for errors. Refer to this [document](errors.md) for a full list or error codes. |
| message           | true               | English readable message as to the reason for the error.                                                    |

## Runing the application locally
To run the application execute

```
sbt ~run 9000 -Drule_sheets.location=[Path to Excel File]” (e.g. /Users/miloszmuszynski/hmrc/decision-service/sheets/kb-rules-01.xls)
```


## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
