package org.smarthome;

import org.smarthome.integration.SecurityAuditListener;
import org.smarthome.listener.AutomationRules;
import org.smarthome.listener.DashboardListener;
import org.smarthome.listener.EnergyTracker;
import org.smarthome.listener.RoomAggregator;
import org.smarthome.model.AirFryer;
import org.smarthome.model.CoffeeMachine;
import org.smarthome.model.Home;
import org.smarthome.model.Kettle;
import org.smarthome.model.Light;
import org.smarthome.model.Room;
import org.smarthome.model.SmartTV;
import org.smarthome.observer.EventType;
import org.smarthome.source.EventSource;
import org.smarthome.visitor.FilterVisitor;
import org.smarthome.visitor.JsonVisitor;
import org.smarthome.visitor.PowerVisitor;
import org.smarthome.visitor.StatisticsVisitor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class App {
    public static void main(String[] args) throws Exception {
        int threadCount = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int eventCount = args.length > 1 ? Integer.parseInt(args[1]) : 3;
        if (threadCount < 3 || eventCount < 1) {
            throw new IllegalArgumentException("Use at least 3 threads and 1 event per producer");
        }

        Kettle kettle = new Kettle("kettle-1", 0);
        Kettle secondKettle = new Kettle("kettle-2", 0);
        AirFryer airFryer = new AirFryer("fryer-1", 0);
        CoffeeMachine coffeeMachine = new CoffeeMachine("coffee-1", 0);
        Light light = new Light("light-1", 0);
        SmartTV tv = new SmartTV("tv-1", 0);
        Room kitchen = new Room("kitchen", List.of(kettle, secondKettle, airFryer, coffeeMachine));
        Room livingRoom = new Room("living-room", List.of(light, tv));
        Home home = new Home("home-1", List.of(kitchen, livingRoom));

        EventSource kettleSource = new EventSource();
        EventSource tvSource = new EventSource();
        EventSource reports = new EventSource();
        DashboardListener dashboard = new DashboardListener(System.out);
        EnergyTracker tracker = new EnergyTracker();
        RoomAggregator aggregator = new RoomAggregator(reports);
        AutomationRules rules = new AutomationRules(2000, reports);
        FilterVisitor filter = new FilterVisitor(1000);
        PowerVisitor power = new PowerVisitor();
        StatisticsVisitor statistics = new StatisticsVisitor();
        JsonVisitor json = new JsonVisitor();
        SecurityAuditListener audit = new SecurityAuditListener(home, filter, reports);

        for (EventSource source : List.of(kettleSource, tvSource)) {
            source.subscribe(EventType.DEVICE_STATE_CHANGED, dashboard);
            source.subscribe(EventType.DEVICE_STATE_CHANGED, tracker);
            source.subscribe(EventType.DEVICE_STATE_CHANGED, aggregator);
        }
        aggregator.subscribe(EventType.ROOM_SUMMARY, dashboard);
        aggregator.subscribe(EventType.ROOM_SUMMARY, rules);
        reports.subscribe(EventType.POWER_LIMIT_EXCEEDED, dashboard);
        reports.subscribe(EventType.POWER_LIMIT_EXCEEDED, audit);
        reports.subscribe(EventType.SECURITY_AUDITED, dashboard);

        List<Runnable> producers = List.of(
                new SimulationRunner.ProducerTask("kitchen", kettle, 2200, eventCount, kettleSource),
                new SimulationRunner.ProducerTask("kitchen", secondKettle, 1500, eventCount, kettleSource),
                new SimulationRunner.ProducerTask("living-room", tv, 200, eventCount, tvSource));
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        SimulationRunner runner = new SimulationRunner(executor, producers);
        runner.run();

        home.accept(statistics);
        System.out.println("Tracked power: " + tracker.totalPowerWatts());
        System.out.println("Visitor power: " + home.accept(power));
        System.out.println("Rooms: " + statistics.roomCount() + ", devices: " + statistics.deviceCount());
        System.out.println("High-power devices: " + home.accept(filter).size());
        System.out.println(home.accept(json));
    }
}
