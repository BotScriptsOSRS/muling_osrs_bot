import java.util.concurrent.Callable;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import java.util.Random;

@ScriptManifest(
        name = "Muling",
        author = "BotScriptsOSRS",
        version = 1.0,
        info = "",
        logo = ""
)
public class MainScript extends Script {
    private static final Area mulingArea = (new Area(2969, 3353, 2967, 3351)).setPlane(1);
    private static final Position mulingPosition = new Position(2969, 3353, 1);
    private Random random = new Random();
    private long nextAntiAfkTime = 0;
    private int lastAction = -1;

    @Override
    public int onLoop() {
        if (!mulingArea.contains(myPlayer())) {
            this.getWalking().webWalk(mulingPosition);
        }
        if (getWorlds().getCurrentWorld() != 456) {
            waitForCondition(() -> getWorlds().hop(456));
        }
        if (!getTrade().isCurrentlyTrading()) {
            if (getTrade().getLastRequestingPlayer() != null) {
                waitForCondition(() -> getTrade().getLastRequestingPlayer().interact("Trade with"));
                waitForCondition(() -> getTrade().didOtherAcceptTrade());
            }
        }
        if (getTrade().isFirstInterfaceOpen() && getTrade().getTheirOffers().contains(995)){
            waitForCondition(() -> getTrade().acceptTrade());
        }
        if (getTrade().isSecondInterfaceOpen()){
            long coinsBeforeTrade = getInventory().getAmount(995);
            waitForCondition(() -> getTrade().acceptTrade());
            waitForCondition(() -> getInventory().getAmount(995) > coinsBeforeTrade);
        }

        if (System.currentTimeMillis() >= nextAntiAfkTime) {
            performAntiAfkActions();
            nextAntiAfkTime = System.currentTimeMillis() + getRandomInterval();
        }

        return random(2000, 3000);
    }

    private void performAntiAfkActions() {
        int action;
        do {
            action = random.nextInt(4);
        } while (action == lastAction);

        switch (action) {
            case 0:
                adjustCameraAngle();
                break;
            case 1:
                getTabs().open(Tab.QUEST);
                break;
            case 2:
                getTabs().open(Tab.INVENTORY);
                break;
            case 3:
                getTabs().open(Tab.SKILLS);
                break;
        }

        lastAction = action; // Remember the last action performed
    }

    private void adjustCameraAngle() {
        int angleChange = random.nextInt(181) - 90; // Random number between -90 and 90
        int newYawAngle = (getCamera().getYawAngle() + angleChange) % 360;
        getCamera().moveYaw(newYawAngle);
    }

    private long getRandomInterval() {
        return (long) (random.nextInt(61) + 30) * 1000; // 30 to 90 seconds in milliseconds
    }

    private void waitForCondition(final Callable<Boolean> condition) {
        (new ConditionalSleep(10000) {
            public boolean condition() {
                try {
                    return condition.call();
                } catch (Exception var2) {
                    return false;
                }
            }
        }).sleep();
    }
}