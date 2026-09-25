# Assignment 02 — Payment Fees with a Sealed Hierarchy

> Module: m00-setup-and-modern-java · Difficulty: ★★☆ · Estimated time: 1.5 h

## Goal

A shop accepts three kinds of payment, modelled as a **sealed** interface with three records. Compute the processing
fee for any payment with **one `switch`** that uses record patterns and guards — and no `default` branch, so the
compiler tells you if a new payment kind is ever added.

## What you are given

- `exercises/ex02/Payment.java` — `sealed interface Payment permits CardPayment, BankTransfer, WalletPayment` — **do not modify**
- `exercises/ex02/CardPayment.java`, `BankTransfer.java`, `WalletPayment.java` — the records — **do not modify**
- `exercises/ex02/FeeCalculator.java` — the interface to implement — **do not modify**
- `exercises/ex02/PaymentFees.java` — your code goes here (`TODO(ex02)` marker)

## Tasks

Implement `PaymentFees.feeFor(Payment)` with these rules (amounts in euros):

| Payment | Fee |
|---|---|
| Card, domestic | 2.9 % of the amount + 0.30 |
| Card, international | 3.9 % of the amount + 0.30 (1 % surcharge) |
| Bank transfer | 0.50 flat; **free** when the amount is 1000 or more |
| Wallet | 1.5 % of the amount, **at most** 5.00 |

Return a `BigDecimal` with scale 2, rounded with `RoundingMode.HALF_EVEN` ("banker's rounding"). A `null` payment
throws `NullPointerException`.

## Acceptance criteria

- [ ] `domesticCardFee` — 100.00 → 3.20
- [ ] `internationalCardFee` — 100.00 → 4.20
- [ ] `bankTransferFlatFee` — 999.99 → 0.50
- [ ] `bankTransferFreeFromThousand` — 1000.00 → 0.00
- [ ] `walletFeeBelowCap` — 200.00 → 3.00
- [ ] `walletFeeCapped` — 1000.00 → 5.00
- [ ] `feesUseBankersRounding` — card 5.00 → 0.445 → 0.44
- [ ] `rejectsNull`

## Run the tests

```bash
./mvnw -pl modules/m00-setup-and-modern-java test -Pexercises -Dtest='Ex02*'
```

All tests green = done. Compare with `solutions/ex02/` only **after** you have tried.

## Hints

<details><summary>Hint 1 — shape of the switch</summary>

`case CardPayment(BigDecimal amount, boolean international) when international -> …` comes **before** the general
`CardPayment` case. Use `_` for components you do not need.

</details>

<details><summary>Hint 2 — the cap</summary>

`BigDecimal.min(other)` returns the smaller of two values.

</details>

## Stretch goals (optional, not graded)

- Add a fourth record `CryptoPayment` to `Payment` in a scratch copy and watch which code stops compiling. Why is this
  a feature?
- Rewrite the calculator as an abstract method `fee()` on each record. Compare the two designs: which one makes adding
  a new *payment kind* easy, and which one makes adding a new *operation* (e.g. `refundFee`) easy?
