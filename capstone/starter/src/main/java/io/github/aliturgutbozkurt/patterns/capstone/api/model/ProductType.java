package io.github.aliturgutbozkurt.patterns.capstone.api.model;

/**
 * GIVEN — do not modify. Physical products have stock and are shipped; digital products have unlimited stock (shown
 * as 0) and need neither an address nor the warehouse.
 *
 * @see "capstone brief, Business rules — Catalogue"
 */
public enum ProductType {
    PHYSICAL, DIGITAL
}
