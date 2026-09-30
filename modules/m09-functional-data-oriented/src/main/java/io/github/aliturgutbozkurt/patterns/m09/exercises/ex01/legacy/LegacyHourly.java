package io.github.aliturgutbozkurt.patterns.m09.exercises.ex01.legacy;

/** GIVEN — do not modify. An hourly worker and the hours worked this month. */
public class LegacyHourly extends LegacyEmployee {

    private final long hourlyRateCents;
    private final int hoursThisMonth;

    public LegacyHourly(String id, String name, long hourlyRateCents, int hoursThisMonth) {
        super(id, name);
        this.hourlyRateCents = hourlyRateCents;
        this.hoursThisMonth = hoursThisMonth;
    }

    public long getHourlyRateCents() {
        return hourlyRateCents;
    }

    public int getHoursThisMonth() {
        return hoursThisMonth;
    }

    @Override
    public <R> R accept(EmployeeVisitor<R> visitor) {
        return visitor.visitHourly(this);
    }
}
