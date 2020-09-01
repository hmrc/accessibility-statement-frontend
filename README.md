# accessibility-statement-frontend

Accessibility Statement Frontend is a service for providing for accessibility statements across HMRC.

## To add your service's accessibility statement

Create a YAML file of the form:

```yaml
serviceName: Discounted Icecreams       # The service name that will appear in the title of the accessibility statement.
                                        # Do not include the word service at the end, as this will be added by the templates
serviceHeaderName: Icecreams            # The service name as it should appear in the grey Gov.UK header bar
serviceDescription: |                   # A description of the service
  Use this paragraph to describe your service. It can go over multiple lines
  like this.
serviceDomain: www.tax.service.gov.uk   # The domain name under which this service exists (exclude the https:// and the path)
serviceUrl: /icecreams                  # The relative URL to the service (omitting www.tax.service.gov.uk)
contactFrontendServiceId: icecreams     # The service id passed to contact-frontend and hmrc-deskpro
complianceStatus: partial               # full|partial
accessibilityProblems:                  # If there are no issues do not include this section
  - a description of the first problem
  - another description of a problem
  - a description of the last problem
milestones:                             # If there are no issues do not include this section
  - description: The X page does not have Y and also does not display Z. This doesn't meet WCAG success
      criterion X.Y.Z (Criterion Description).
    date: 2020-10-31                    # The date that this issue will be fixed by in ISO format YYYY-MM-DD
  - description: |
      The service has errors in X and incorrectly nested Z which means assistive technologies
      cannot use the service reliably. This does not meet WCAG success criterion
      X.Y.Z (Criterion Description).
    date: 2020-09-30                    # The date that this issue will be fixed by
serviceLastTestedDate: 2019-09-15       # In ISO format YYYY-MM-DD
statementVisibility: public             # If set to public, the statement will be visible in production
statementCreatedDate: 2019-09-30        # In ISO format YYYY-MM-DD
statementLastUpdatedDate: 2019-09-30    # In ISO format YYYY-MM-DD
```

You can also use the following files as examples to copy:
- [/conf/services/example-fully-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-fully-compliant.yml)
- [/conf/services/example-partially-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-partially-compliant.yml)

Save the YAML file to the `conf/services` directory.

The filename must be of the form <my-service>.yml. The name of the file will become the URL of the accessibility statement 
e.g. `conf/services/discounted-icecreams.yml` will create an accessibility statement at 
`https://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams`.

Also note, the filename can contain only lower case letters, dashes or numbers. The filename extension must be `.yml`

Before opening a PR, check the service renders successfully at http://localhost:12346/accessibility-statement/discounted-icecreams
and run all the tests locally as described below.

## To run locally

To run the application:
```
sbt run
```

Navigate to the desired accessibility statement e.g. http://localhost:12346/accessibility-statement/disguised-remuneration
where disguised-remuneration is the serviceKey defined in the accessibility statement YAML file.

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

## Performance testing

The repository https://github.com/hmrc/accessibility-statement-performance-tests contains 
Gatling performance tests designed to be run against a production-like environment.

While the service expects to attract a low volume of traffic, the fact that the
accessibility statements load into memory at start-up means the service may require
more memory than a regular service. For this reason, performance testing has
been carried out with the service ingesting well above the maximum number of statements
expected to exist on the platform for the foreseeable future.

The shell script `./generate_test_data.sh` creates fake accessibility statement
YAML files in testOnlyConf/testOnlyServices for the purposes of load testing the application.

To run the application using this test data run `./run_with_test_data.sh`

## Running ZAP scan locally

To run the ZAP scan, you will need a copy of the ZAP proxy running locally on port 11000: https://www.zaproxy.org/, with the 
following options configured:

* under HUD, uncheck 'Enable when using the ZAP Desktop' (stops ZAP converting requests to HTTPS)
* under API, check 'Disable the API key'

```
./run_zap_tests.sh
```

More information on HMRC's ZAP scanning automation library can be found at https://github.com/hmrc/zap-automation

## Adding to your service
When adding to your service, an additional parameter should be added to your query string, 
to help end users report any accessibility that they find. this is:
```
referrerUrl (the full, absolute, URI encoded page URL in your service from which the user clicked on the Accessibility link)
```
This will be passed through on the call to `contact-frontend`, for example:
```
http://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams?referrerUrl=https%3A%2F%2Fwww.tax.service.gov.uk%2Fyour-service
```
will bind the following URL in your statement page
```
http://www.tax.service.gov.uk/contact/accessibility-unauthenticated?service=icecreams&referrerUrl=https%3A%2F%2Fwww.tax.service.gov.uk%2Fyour-service
```
This `referrerUrl` parameter is important in helping HMRC customer service agents find out exactly where the 
end user discovered the accessibility issue.

## Service Manager config for local development

When developing locally you can run

```
sm --start A11Y_STATEMENT_ALL
```:wq

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
 