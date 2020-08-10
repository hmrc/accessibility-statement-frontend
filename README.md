# accessibility-statement-frontend

Accessibility Statement Frontend is a service for providing for accessibility statements across HMRC.

## To run locally

To run the application:
```
sbt run
```

## Running unit and integration tests together

```
sbt test it:test
```

## Running UI acceptance tests

To run the UI acceptance tests locally, you will need a copy of Chrome
and the Chrome browser driver installed at /usr/local/bin/chromedriver
```
sbt -Dbrowser=chrome acceptance:test
```

The Chrome driver is available at https://chromedriver.chromium.org/

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
 