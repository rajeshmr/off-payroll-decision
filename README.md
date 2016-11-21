
# off-payroll-decision-service



## API
| Task    | Http Method | Description |
|:--------|:------------|-------------|
|/decide/ | POST        | Returns a 'decision' on your Employment Status for Tax Purposes. [More...](./docs/api.md)|


## Runing the application locally
To run the application execute

```
sbt ~run 9000 -Drule_sheets.location=[Path to Excel File]” (e.g. -Drule_sheets.location=~/hmrc/off-payroll-decision-service/sheets/kb-rules-01.xls)
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
