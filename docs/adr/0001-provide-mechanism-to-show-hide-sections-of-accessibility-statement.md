# Provide mechanism for showing/hiding/updating parts of the accessibility statement

* Status: accepted
* Date: 2022-02-14

Technical Story: [PLATUI-1552](https://jira.tools.tax.service.gov.uk/browse/PLATUI-1552)

## Context and Problem Statement

Currently, some content within the existing accessibility statement template is not suitable for VOA, C-HGV, and GVMS services.
The current implementation of the accessibility statement does not allow for hiding/showing or changing certain sections of the accessibility statement
that some teams may require.

## Decision Drivers 

* Services might have to find another way to host their accessibility statements.
* Contact information for VOA services is incorrect, users may not be able to contact them.
* Because information can be incorrect on the accessibility statement for VOA services, DCST might end up fielding queries they can't help with.
* As much as possible we want to make it impossible for a service create a statement that doesn't meet legal requirements.
* We want to avoid making use of the service more complicated for services that don't shouldn't be varying their accessibility statements.
* As service maintainers, we need to find the correct balance between flexibility and code readability, otherwise it will be increasingly difficult to maintain the service in a timely and reliable way.

## Considered Options

* Option 1: Update the necessary accessibility statement fragments to cater to VOA specific context, include flags for the fragments that need to be hidden, and update the accessibility statement view model to introduce these new properties.
* Option 2: Provide custom accessibility statement templates, allow a new parameter that can be introduced into the YAML service file e.g 'serviceTemplateType' which will allow service teams to use custom templates.

## Decision Outcome

Chosen option: Option 2

## Pros and Cons of the Options 

* Good, because custom templates will be seperated from the standard template, we will not interfere and pollute the existing HMRC template and its fragments.
* Good, because teams will need to explicitly set a property on there service YAML to use a custom template.
* Bad, because we may have to update fragments of a template in two places but this should not be a common problem.
