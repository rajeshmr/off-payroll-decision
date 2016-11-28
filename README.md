# off-payroll-decision



## API
| Task    | Http Method | Description |
|:--------|:------------|-------------|
|/decide/ | POST        | Returns a 'decision' on your Employment Status for Tax Purposes. [More...](./docs/api.md)|


## Running the application locally
To run the application execute

```
sbt ~run 9000 -Drule_sheets.location=[Path to Excel File]” (e.g. -Drule_sheets.location=~/hmrc/off-payroll-decision/sheets/kb-rules-01.xls)