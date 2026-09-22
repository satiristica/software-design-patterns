package spacemission;

import spacemission.factory.creators.LanderMission;
import spacemission.factory.creators.Mission;
import spacemission.factory.creators.RoverMission;
import spacemission.factory.creators.SatelliteMission;

public class SpaceMission {

    public static void main(String[] args) {
        runMission(new RoverMission());
        runMission(new SatelliteMission());
        runMission(new LanderMission());
    }

    private static void runMission(Mission mission) {
        mission.launchMission();
        System.out.println();
    }
}