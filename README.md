# accessibility-statement-frontend

Accessibility Statement Frontend is a service for providing for accessibility statements across HMRC.

## To add your service's accessibility statement

Create a YAML file of the form:

```yaml
serviceKey: discounted-icecreams        # A unique name for the statement used for routing e.g. https://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams
serviceName: Discounted Icecreams       # The service name that will appear in the title of the accessibility statement.
                                        # Do not include the word service at the end, as this will be added by the templates
serviceHeaderName: Icecreams            # The service name as it should appear in the grey Gov.UK header bar
serviceDescription: |                   # A description of the service
  Use this paragraph to describe your service. It can go over multiple lines
  like this.
serviceDomain: www.tax.service.gov.uk   # The domain name underwhich this service exists (exclude the https:// and the path)
serviceUrl: /icecreams                  # The relative URL to the service (omitting www.tax.service.gov.uk)
contactFrontendServiceId: icecreams     # The service id passed to contact-frontend and hmrc-deskpro
complianceStatus: partial               # full|partial
accessibilityProblems:                  # If there are no issues enter []
  - a description of the first problem
  - another description of a problem
  - a description of the last problem
milestones:                             # If there are no issues enter []
  - description: The X page does not have Y and also does not display Z. This doesn't meet WCAG success
      criterion X.Y.Z (Criterion Description).
    date: 2020-10-31                    # The date that this issue will be fixed by in ISO format YYYY-MM-DD
  - description: |
      The service has errors in X and incorrectly nested Z which means assistive technologies
      cannot use the service reliably. This does not meet WCAG success criterion
      X.Y.Z (Criterion Description).
    date: 2020-09-30                    # The date that this issue will be fixed by
serviceSendsOutboundMessages: false     # Set to true if the service sends or asks for documents from service users
serviceLastTestedDate: 2019-09-15       # In ISO format YYYY-MM-DD
statementVisibility: public             # If set to public, the statement will be visible in production
statementCreatedDate: 2019-09-30        # In ISO format YYYY-MM-DD
statementLastUpdatedDate: 2019-09-30    # In ISO format YYYY-MM-DD
```

Save the YAML file to e.g. `conf/services/discounted-icecreams.yml` The name of the file without the yml extension 
has to match the value of the serviceKey.

We support a JSON-compatible subset of YAML. Multi-line strings are allowed but no markup is allowed within
 textual descriptions.

Open up `conf/services.yml` and add the key to your service at the end:

```yaml
services:
  - challenge-a-childcare-decision
  - coronavirus-job-retention-scheme
  - direct-debit
  - online-payments
  - disguised-remuneration
  - pay-what-you-owe-in-instalments
  - discounted-icecreams              # It's crucial this key matches the serviceKey in your service's statement YAML file
``` 

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
 
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
 