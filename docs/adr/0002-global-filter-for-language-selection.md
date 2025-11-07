# Provide mechanism for language selection via url query parameters

* Status: accepted
* Date: 2025-10-09

Technical Story: [PLATUI-3978](https://jira.tools.tax.service.gov.uk/browse/PLATUI-3978)

## Context and Problem Statement

The `PLAY_LANG` cookie is set which determines which language is rendered. However, users were unable to directly link to the relevant language to their audiences, unless they were able to set the cookie as well.

## Decision Drivers

* Users being unable to choose the desired language without having been on hmrc pages previously.
* We have one page in another repo where we set the language, so we had a precedent for doing this.

## Considered Options

* Provide a direct link to select the desired language
* Do nothing

## Decision Outcome

Chosen option: "Provide a direct link to select the desired language", because we should allow a mechanism to set the language cookie before the user goes onto the page.

## Pros and Cons of the Options

### Provide a direct link to select the desired language

* Good, because users don't need to have previously been on a hmrc page in order to select their desired cookies
* Good, because teams can directly link users to the relevant language for the content

### Do nothing

* Bad, because we aren't addressing the problem
