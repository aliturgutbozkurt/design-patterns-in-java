package io.github.aliturgutbozkurt.patterns.m07.examples.mediator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc.Aircraft;
import io.github.aliturgutbozkurt.patterns.m07.examples.mediator.atc.ControlTower;
import io.github.aliturgutbozkurt.patterns.m07.support.Console;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AirTrafficTest {

    private final List<String> radio = new ArrayList<>();
    private final ControlTower tower = new ControlTower(radio::add);
    private final Aircraft tk1 = new Aircraft("TK1", tower);
    private final Aircraft lh2 = new Aircraft("LH2", tower);
    private final Aircraft ba3 = new Aircraft("BA3", tower);
    private final Aircraft af4 = new Aircraft("AF4", tower);

    @Test
    void atMostOneAircraftIsClearedForTheRunwayAtATime() {
        tk1.requestLanding();
        lh2.requestTakeoff();
        assertThat(tower.runwayOccupant()).containsSame(tk1);
        assertThat(tk1.inbox()).containsExactly("cleared to land");
        assertThat(lh2.inbox()).containsExactly("hold, you are number 1");
    }

    @Test
    void waitingAircraftAreClearedFifoWhenTheRunwayIsVacated() {
        tk1.requestLanding();
        lh2.requestLanding();
        ba3.requestTakeoff();
        tk1.vacateRunway();
        assertThat(tower.runwayOccupant()).containsSame(lh2);
        lh2.vacateRunway();
        assertThat(tower.runwayOccupant()).containsSame(ba3);
        ba3.vacateRunway();
        assertThat(tower.runwayOccupant()).isEmpty();
        assertThat(lh2.inbox()).containsExactly("hold, you are number 1", "cleared to land");
        assertThat(ba3.inbox()).containsExactly("hold, you are number 2", "cleared for takeoff");
    }

    @Test
    void emergencyJumpsTheQueue() {
        tk1.requestLanding();
        lh2.requestLanding();
        af4.declareEmergency();
        tk1.vacateRunway();
        assertThat(tower.runwayOccupant()).containsSame(af4);
        assertThat(af4.inbox())
                .containsExactly("emergency acknowledged, you are number 1", "cleared for emergency landing");
    }

    @Test
    void emergencyOnAFreeRunwayIsClearedAtOnce() {
        af4.declareEmergency();
        assertThat(af4.inbox()).containsExactly("cleared for emergency landing");
    }

    @Test
    void everyAircraftReceivesExactlyTheMessagesAddressedToIt() {
        tk1.requestLanding();
        lh2.requestLanding();
        ba3.requestTakeoff();
        tk1.vacateRunway();
        assertThat(tk1.inbox()).containsExactly("cleared to land");
        assertThat(lh2.inbox()).containsExactly("hold, you are number 1", "cleared to land");
        assertThat(ba3.inbox()).containsExactly("hold, you are number 2");
        assertThat(af4.inbox()).isEmpty();
    }

    @Test
    void onlyTheAircraftOnTheRunwayCanVacateIt() {
        tk1.requestLanding();
        assertThatIllegalStateException().isThrownBy(lh2::vacateRunway).withMessage("LH2 is not on the runway");
    }

    @Test
    void demoPrintsTheRadioLog() {
        assertThat(Console.capture(() -> AirTrafficDemo.main(new String[0]))).isEqualTo("""
                TK1 -> tower: RequestLanding
                tower -> TK1: cleared to land
                LH2 -> tower: RequestLanding
                tower -> LH2: hold, you are number 1
                BA3 -> tower: RequestTakeoff
                tower -> BA3: hold, you are number 2
                AF4 -> tower: DeclareEmergency
                tower -> AF4: emergency acknowledged, you are number 1
                TK1 -> tower: RunwayVacated
                tower -> AF4: cleared for emergency landing
                AF4 -> tower: RunwayVacated
                tower -> LH2: cleared to land
                LH2 -> tower: RunwayVacated
                tower -> BA3: cleared for takeoff
                BA3 -> tower: RunwayVacated
                """);
    }
}
