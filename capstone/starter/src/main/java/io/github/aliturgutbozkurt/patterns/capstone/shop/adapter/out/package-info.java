/**
 * Outbound adapters, one subpackage each (e.g. {@code adapter.out.memory}, {@code adapter.out.payment},
 * {@code adapter.out.warehouse}, {@code adapter.out.notification}): they implement the application's outbound ports and
 * are the only code (with {@code config}) allowed to touch {@code api.external}.
 */
package io.github.aliturgutbozkurt.patterns.capstone.shop.adapter.out;
