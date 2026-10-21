# Assignment 5

A Java banking application demonstrating two Strategy families: transfer commissions and purchase bonuses.

## Run

* ./gradlew test
* ./gradlew run 

Supported Java 21

## Class responsibilities

| Class / Interface | Responsibility |
|---|---|
| App | Demonstrates manual selection, automatic selection, runtime strategy replacement. |
| CustomerType | Defines the DEFAULT, BROKER, ELITE customer categories. |
| CommissionStrategy | Defines the commission calculation contract. |
| PercentageCommission | Calculates 16% of the transfer amount. |
| FixedCommission | Returns a fixed commission of 100. |
| ThresholdCommission | Charges 5% on the amount exceeding 10,000. |
| CommissionContext | Delegates commission calculation to the selected strategy. |
| CommissionSelector | Selects a commission strategy based on customer type. |
| BonusStrategy | Defines the bonus calculation contract. |
| FixedBonus | Awards 15 bonuses per purchase. |
| StepBonus | Awards 15 bonuses per complete 1,000 spent. |
| LargePurchaseBonus | Awards a provider-supplied bonus for purchases exceeding 10,000. |
| BonusContext | Delegates bonus calculation to the selected strategy. |
| BonusSelector | Selects a bonus strategy based on customer type. |
| BonusProvider | Defines access to the current promotional bonus. |
| SimulatedBonusProvider | Simulates a promotional data source returning 100 bonuses. |
| CommissionStrategyTest | Verifies commission calculations, threshold boundaries, nonnegative results. |
| CommissionSelectorTest | Verifies commission selection rules. |
| BonusStrategyTest | Verifies bonus calculations using controlled external data. |
| BonusSelectorTest | Verifies bonus selection rules using controlled external data. |
| AppTest | Checks the generated greeting method. |
| BonusStrategyTest.FakeBonusProvider | Supplies a fixed test bonus of 250. |
| BonusSelectorTest.FakeBonusProvider | Supplies a fixed test bonus of 250. |

## Why Strategy, not State or Template Method

This application uses Strategy because each family of algorithms is interchangeable for one operation. Each family contains algorithms with the same contract but different implementations. For instance, commission calculation strategies calculate a transfer fee, while bonus strategies calculate a purchase reward. The strategies are implemented differently, such as using a fixed value, a proportional value, a threshold division, or a complete unit spending. Each family is exposed via a general interface which allows its context to use any of the algorithms' implementations interchangeably.

Contexts are constructed with specific strategies and are able to change them with new implementations at runtime via setStrategy(). They delegate calculation-related logic to a strategy without being coupled to a specific strategy implementation. Selector classes automatically identify which algorithm to apply depending on a customer's category. New algorithms can be added to a family without affecting the existing code since contexts use a common interface and only have an algorithm selector logic. Automatic selection rules are encapsulated within selector classes. State represents a behavior that is applied to an object depending on its state, such as switching between an active account and a blocked account. In this project, states allow customer categories to calculate respective bonuses. They encapsulate state-dependent logic but do not manage transitions between states.

Template Method specifies steps required to implement an algorithm skeleton in a superclass and allows subclasses to redefine these steps. This project uses composition over inheritance by defining a context that contains a reference to a strategy rather than extending a strategy class. As a result, it is possible to switch an entire calculation implementation at runtime without modifying a context. External promotions are encapsulated within a BonusProvider class. LargePurchaseBonus class uses an interface rather than a concrete implementation, which allows tests to mock this class and simulate a desired behavior by returning a predefined value.

## Refactoring history

- [Before refactoring](https://github.com/satiristica/software-design-patterns/commit/d763980c557efd9bd551f13eb110d305ab8c0f24): commission formulas were located inside CommissionContext with conditional type checks.
- [After refactoring](https://github.com/satiristica/software-design-patterns/commit/cb8d09411c776558f6f27fd18275503dc5186334): formulas moved into separate strategies; the context delegates calculation.

The same four commission tests are preserved unchanged in both commits.

The demo processes the same amount through all strategies, shows manual versus automatic selection, and replaces strategies at runtime.

JUnit 5 tests cover every strategy, every selector rule, threshold boundaries, fake data sources, and a parameterized nonnegative commission check.