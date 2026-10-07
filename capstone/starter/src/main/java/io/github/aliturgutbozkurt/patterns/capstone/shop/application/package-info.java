/**
 * The application layer: services that implement the GIVEN use-case interfaces ({@code api.catalogue}, {@code api.cart},
 * …) by orchestrating the domain, plus the outbound ports they need (interfaces such as a repository, a payment port, a
 * notifier). Never depends on adapters, on {@code config} or on the external systems' APIs (ArchitectureRules rule 2).
 */
package io.github.aliturgutbozkurt.patterns.capstone.shop.application;
