<!-- Template from the capstone brief, section "SPEC.md template" (capstone/spec.en.md#specmd-template). Replace every <…> and keep the ten sections. -->
# PatternShop — <your name(s)>

> Status: draft | approved (W10) | final (W14) · Change log at the end

## 1. Objective
One paragraph: what the system does and for whom. Two or three user stories.

## 2. Scope
### 2.1 Mandatory features
| Id | Feature | Acceptance suite | My notes / interpretation |
### 2.2 Extension features
For each: description, user story, acceptance criteria (Given / When / Then), test class names.
### 2.3 Out of scope

## 3. Domain model
Mermaid class diagram of the core types (aggregates, values, sealed hierarchies).

## 4. Architecture
Packages (domain, application, adapter.in, adapter.out, config), inbound and outbound ports,
composition root. One Mermaid diagram of the hexagon.

## 5. Pattern plan
| # | Pattern (category) | Problem it solves here | Participants | Alternative considered | Test |

## 6. Concurrency design
What runs in parallel, the limit, how failures and results are collected, why it is thread-safe.

## 7. Testing strategy
Given acceptance tests, own unit tests (which test doubles), extension acceptance tests.

## 8. Boundaries and assumptions
Always / Ask first / Never. Assumptions you made where this brief is silent.

## 9. Milestones
Your plan for W10–W13 (which features and patterns per week).

## 10. Open questions and change log
| Date | Change | Why |
