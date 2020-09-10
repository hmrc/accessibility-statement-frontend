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
complianceStatus: partial               # full|partial|noncompliant
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
serviceLastTestedDate: 2019-09-15       # In ISO format YYYY-MM-DD. If your statemenent's compliance status is noncompliant, you can omit this line
statementVisibility: public             # If set to public, the statement will be visible in production
statementCreatedDate: 2019-09-30        # In ISO format YYYY-MM-DD
statementLastUpdatedDate: 2019-09-30    # In ISO format YYYY-MM-DD
automatedTestingOnly: true              # Only add this value if your service has only had automated testing. Otherwise, do not include
automatedTestingDetails: |             # Only add this value if your service has only had automated testing
  If your service has only had automated testing, add a text description of testing tools used, e.g.
  It was tested using the automated tool(s) AATT by PayPal and Accessibility Checklist by Elsevier.
```

You can also use the following files as examples to copy:
- [/conf/services/example-fully-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-fully-compliant.yml)
- [/conf/services/example-partially-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-partially-compliant.yml)
- [/conf/services/example-non-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-non-compliant.yml)
- [/conf/services/example-automated-testing-only.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-automated-testing-only.yml)

Save the YAML file to the `conf/services` directory.

The filename must be of the form <my-service>.yml. The name of the file will become the URL of the accessibility statement 
e.g. `conf/services/discounted-icecreams.yml` will create an accessibility statement at 
`https://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams`.

Also note, the filename can contain only lower case letters, dashes or numbers. The filename extension must be `.yml`

Before opening a PR, check the service renders successfully at http://localhost:12346/accessibility-statement/discounted-icecreams
and run all the tests locally as described below.

## Adding to your service

### Users of play-ui (version 8.12.0 or above) or play-frontend-hmrc (version 0.19.0 or above)

If you are using [hmrc/play-ui](https://github.com/hmrc/play-ui#accessibility-statements)
 or [hmrc/play-frontend-hmrc](https://github.com/hmrc/play-frontend-hmrc#accessibility-statements), you can add the 
 `accessibility-statement.service-path` key to your application.conf. This key is 
 the path to your accessibility statement under https://www.tax.service.gov.uk/accessibility-statement.
                                                                       
For example, if your accessibility statement is https://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams, 
this property must be set to `/discounted-icecreams` as follows:

```
accessibility-statement.service-path = "/discounted-icecreams"
```

Once this is set, the play-ui [FooterLinks](https://github.com/hmrc/play-ui/blob/master/src/main/twirl/uk/gov/hmrc/play/views/layouts/FooterLinks.scala.html)
  component will auto-generate the correct link to your accessibility statement, including
the full referrerUrl parameter as described below. Likewise, the new
 [hmrcFooter](https://github.com/hmrc/play-frontend-hmrc/blob/master/src/main/play-26/twirl/uk/gov/hmrc/hmrcfrontend/views/components/hmrcFooter.scala.html)
 component will deliver the full govukFooter including the standardised links.
 
Also available is the [hmrcFooterItems](https://github.com/hmrc/play-frontend-hmrc/blob/master/src/main/scala/uk/gov/hmrc/hmrcfrontend/views/config/HmrcFooterItems.scala) helper
for occasions where it is not convenient to use hmrcFooter.

### Users of older versions of play-ui or Java-based services
 
When adding to your service, an additional parameter should be added to your query string, 
to help end users report any accessibility that they find. This is:

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

The referrerUrl parameter should be dynamic, not hard-coded, and based on the request the user made to the page
they were visiting when clicking on the 'Accessibility statement' link. It can be constructed from the
`platform.frontend.host` configuration key (available only when running on the MDTP platform) and the `request.path` 
as shown in the following code: https://github.com/hmrc/play-ui/blob/master/src/main/play-26/uk/gov/hmrc/play/config/AccessibilityStatementConfig.scala#L43

This `referrerUrl` parameter is important in helping HMRC customer service agents find out exactly where the 
end user discovered the accessibility issue.

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

## Service Manager config for local development

When developing locally you can run

```
sm --start A11Y_STATEMENT_ALL
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
 
