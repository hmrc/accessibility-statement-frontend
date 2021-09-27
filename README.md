# Accessibility Statement Frontend

Accessibility Statement Frontend is a service for providing for accessibility statements across HMRC.

## Prerequisites

Adding an accessibility statement involves creating a structured text file known as
a [YAML](https://en.wikipedia.org/wiki/YAML) (YAML Ain't Markup Language) file and 
adding to this repository.

Creating such a file does not require any coding skills and can be done in a simple text editor, online 
 YAML editor or even directly within Github. However, if you are a developer you may 
prefer to use your preferred IDE and Git to upload the YAML file to the repository.

## To add your service's accessibility statement

First create a YAML file similar to the one listed below. This example includes all possible fields. Your statement
may not need all of these fields depending on whether your service is fully, partially or non-compliant. Note 
that the text following the # characters are comments and will not appear in the fully rendered accessibility statement.

```yaml
serviceName: Discounted Icecreams       # The service name that will appear in the title of the accessibility statement.
                                        # Do not include the word service at the end, as this will be added by the templates
serviceDescription: |                   # A description of the service
  Use this paragraph to describe your service. It can go over multiple lines
  like this.
serviceDomain: www.tax.service.gov.uk   # The domain name under which this service exists (exclude the https:// and the path)
serviceUrl: /icecreams                  # The relative URL to the service (omitting www.tax.service.gov.uk)
contactFrontendServiceId: icecreams     # A unique identifier for your service (see notes below)
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
serviceLastTestedDate: 2019-09-15       # In ISO format YYYY-MM-DD. If your statement's compliance status is noncompliant, you can omit this line
statementVisibility: public             # If set to public, the statement will be visible in production. One of: public|draft|archived.
statementCreatedDate: 2019-09-30        # In ISO format YYYY-MM-DD
statementLastUpdatedDate: 2019-09-30    # In ISO format YYYY-MM-DD
automatedTestingOnly: true              # Only add this value if your service has only had automated testing. Otherwise, do not include
automatedTestingDetails: |             # Only add this value if your service has only had automated testing
  If your service has only had automated testing, add a text description of testing tools used, e.g.
  It was tested using the automated tool(s) AATT by PayPal and Accessibility Checklist by Elsevier.
```

If your statement is specifically for a mobile application, add the line `mobilePlatform: android` for an android application or
`mobilePlatform: ios` for an iOS application. Do not add this for web-based services.

You can also use the following files as examples to copy:
- [/conf/services/example-fully-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-fully-compliant.yml)
- [/conf/services/example-partially-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-partially-compliant.yml)
- [/conf/services/example-non-compliant.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-non-compliant.yml)
- [/conf/services/example-automated-testing-only.yml](https://github.com/hmrc/accessibility-statement-frontend/blob/master/conf/services/example-automated-testing-only.yml)

Save the YAML file to the `conf/services` directory.

The filename must be of the form &lt;my-service&gt;.yml. The name of the file will become the URL of the accessibility statement 
e.g. `conf/services/discounted-icecreams.yml` will create an accessibility statement at 
`https://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams`.

The filename can contain only lower case letters, dashes or numbers, and the filename extension must be `.yml`

## Opening a PR to get your statement merged into the repository
Before opening a pull request, check the service renders successfully at http://localhost:12346/accessibility-statement/discounted-icecreams
and run the integration tests with `sbt it:test` as described below.

If your team would like repository write access to create branches and submit PRs without forking the repository, contact us via Slack at [#team-plat-ui](https://hmrcdigital.slack.com/messages/team-plat-ui/). Having write access will mean your team will receive alerts regarding production deployments.

If you do not have write access to the repository, you are welcome to fork the repository using the Github user interface and submit PRs via your forked copy. In this case you will need to contact us to request PRs are approved and merged.

## Release an accessibility statement to Production
Production releases for ```accessibility-statement-frontend``` are managed by PlatUI. If you would like the team to release your accessibility statement to Production, please contact us via Slack at [#team-plat-ui](https://hmrcdigital.slack.com/messages/team-plat-ui/).

## Adding to your service

Adding the accessibility statement to your service requires basic knowledge of the Play framework, the MDTP platform, and
 the ability and permission to build & deploy your service.
 
### Users of play-ui (version 8.12.0 or above) or play-frontend-hmrc (version 0.19.0 or above)

If you are using [hmrc/play-ui](https://github.com/hmrc/play-ui#accessibility-statements)
 or [hmrc/play-frontend-hmrc](https://github.com/hmrc/play-frontend-hmrc#accessibility-statements), you can add the 
 `accessibility-statement.service-path` key to your `conf/application.conf` file. This key is 
 the path to your accessibility statement under https://www.tax.service.gov.uk/accessibility-statement.
                                                                       
For example, if your accessibility statement is https://www.tax.service.gov.uk/accessibility-statement/discounted-icecreams, 
this property must be set to `/discounted-icecreams` as follows:

```
accessibility-statement.service-path = "/discounted-icecreams"
```

Once this is set, the play-ui [FooterLinks](https://github.com/hmrc/play-ui/blob/master/src/main/twirl/uk/gov/hmrc/play/views/layouts/FooterLinks.scala.html)
  component will auto-generate the correct link to your accessibility statement, including
the full referrerUrl parameter as described below. Likewise, the new
 [hmrcStandardFooter](https://github.com/hmrc/play-frontend-hmrc/blob/master/src/main/play-26/twirl/uk/gov/hmrc/hmrcfrontend/views/helpers/hmrcStandardFooter.scala.html)
 component will deliver the full govukFooter including the standardised links.
 
Also available is the [hmrcFooterItems](https://github.com/hmrc/play-frontend-hmrc/blob/master/src/main/scala/uk/gov/hmrc/hmrcfrontend/views/config/HmrcFooterItems.scala) helper
for occasions where it is not convenient to use hmrcFooter.

### Users of older versions of play-ui or Java-based services
 
If you are not able to upgrade to the minimum supported versions of play-ui or play-frontend-hmrc listed above, you 
will need to manually add a footer link to the accessibility statement entitled 'Accessibility statement' after the 
Cookies link in the gov.uk footer.
 
The link should have an additional querystring parameter added to help end users report any accessibility that they 
find. This is:

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

## A note on contactFrontendServiceId

contactFrontendServiceId helps identify your service when members of the general public report accessibility problems.

If your service is already integrating with contact-frontend's *Get help with this page* form, you will find it added 
as a querystring parameter `service` in URLs to contact-frontend.

If you look in your service's frontend source code, you should see URLs constructed similar to

`s"$contactHost/contact/problem_reports_ajax?service=foo"`

or

`s"$contactHost/contact/problem_reports_nonjs?service=foo"`

The contactFrontendServiceId here would be `foo`.

If your service is *not* already integrating with contact-frontend, we advise choosing an
 identifier that is specific to your service and unlikely to be used by any other service, avoiding any special characters
 or whitespace.

## Creating accessibility statements in the Welsh language

To create a version of your accessibility statement in the Welsh language, create a copy of the English language
version and save with the suffix `.cy.yml`. For example, if your English language statement is in
`/conf/services/discounted-icecreams.yml`, the Welsh version should be saved as
`/conf/services/discounted-icecreams.cy.yml`.

Translate the following fields into Welsh only: serviceName, serviceDescription, accessibilityProblems
and milestone description. All other fields must be left untouched.

Open a PR to get the file merged into the repository. Once merged and deployed, the language toggle will 
automatically appear on the statement and will translate all content, including the legal text into Welsh.

## To run locally

To run the application:
```
sbt run
```

Navigate to the desired accessibility statement e.g. http://localhost:12346/accessibility-statement/disguised-remuneration
where disguised-remuneration is the filename of the accessibility statement YAML file with the language and yaml suffix
removed.

## Running unit tests

```
sbt a11yTest
```

The above tests include accessibility checks via the
[sbt-accessibility-linter](https://www.github.com/hmrc/sbt-accessibility-linter)
plugin. This plugin requires Node.js v12 or above to be installed locally.

If you are a member of a service team, contributing a new accessibility statement, it's not necessary
to run these tests before opening a PR.

## Running the integration tests

```
sbt it:test
```

The integration tests are responsible for enforcing a number of business rules for accessibility statements via
[test/it/ServicesISpec.scala](). For this reason, it's recommended that service teams run these tests before
opening a PR.

## Generating a report

It is possible to generate a tab-separated-value (TSV) file containing information on all the accessibility statements
that exist in the repository. This TSV file can then easily be imported into your favourite spreadsheet application
for further analysis.

To generate the report, clone the repository locally and run

```
sbt generateReport
```

If all goes well, the report `target/report.tsv` will be created.

Optionally, the filename of the report can be customised. For example,

```
sbt "generateReport alternativeName.tsv"
```

will generate the report in `target/alternativeName.tsv`

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
more memory than a regular service. 

## Running ZAP scan locally

To run the ZAP scan, use the Docker helper supplied by `dast-config-manager` (https://github.com/hmrc/dast-config-manager#running-zap-locally)

Follow the following steps:
1. Clone the repo at: https://github.com/hmrc/dast-config-manager
2. Enable port forwarding: `export ZAP_FORWARD_ENABLE="true"`
3. Configure port forwarding: `export ZAP_FORWARD_PORTS=12346`
4. In the `dast-config-manager` directory, start the ZAP docker container: `make local-zap-running`
5. In the `accessibility-statement-frontend` directory, run the acceptance tests with ZAP proxying: `sbt -Dbrowser=chrome -Dzap.proxy=true acceptance:test`
6. In the `dast-config-manager` directory, stop the ZAP docker container: `make local-zap-stop`

Information about the local ZAP test output can be found at https://github.com/hmrc/dast-config-manager#running-zap-locally.

## Service Manager config for local development

When developing locally you can run

```
sm --start A11Y_STATEMENT_ALL
```

This is useful if you have already merged your accessibility statement into the repository and now wish to test
the linking to the accessibility statement from your own service.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
 
