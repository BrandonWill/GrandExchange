import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.event.MessageEvent;
import org.rsbot.bot.event.listener.MessageListener;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.*;
import org.rsbot.script.methods.tabs.Inventory;
import org.rsbot.script.methods.ui.Camera;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.*;

import java.awt.*;

@ScriptManifest(authors = { "Dwarfeh" }, keywords = { "fish, fisher, crayfish" }, name = "Dwarfeh's Fisher", description = "Fishes Crayfish", version = 1.0)
public class Fisher extends Script implements MessageListener, PaintListener {
    int fishSpot = 6996;
    int[] noDrop = { 13431, 995, 14664 };
    int fishCaught = 0;

    @Override
    protected int loop() {
        Mouse.setPrecisionSpeed(7);
        if (Interfaces.canContinue()) {
            Interfaces.clickContinue();
        }
        if (Inventory.isFull()) {
            Inventory.dropAllExcept(noDrop);
        }
        while (Players.getLocal().getAnimation() == 10009 && !Interfaces.canContinue()) {
            sleep(500);
        }
        if (!Inventory.isFull() && Players.getLocal().getAnimation() != 10009) {
            NPC spot = NPCs.getNearest(fishSpot);
            if (spot != null) {
                //Camera.setAngle(Camera.getTileAngle(spot.getLocation()));
                Camera.turnTo(new Tile(spot.getLocation().getX(), spot.getLocation().getY()));
                if (spot.isOnScreen()) {
                    Point p = spot.getCentralPoint();
                    Mouse.hop(p);
                    Mouse.click(false);
                    if (Menu.isOpen() && Menu.contains("Cage")) {
                        Menu.click("Cage");
                    }
                    Task.sleep(2000);
                }
                if (!spot.isOnScreen()) {
                    Camera.setAngle(Camera.getTileAngle(spot.getLocation()));
                    Camera.setPitch(0);
                    Path p = Walking.getPath(spot.getLocation());
                    p.traverse();
                }
            }
            if (spot == null) {
                sleep(500);
            }
        }
        return 300;
    }

    public void messageReceived(MessageEvent e) {
        String text = e.getMessage().toLowerCase();
        if (text.contains("catch a") && !text.contains("attempt")) {
            fishCaught++;
        }
    }

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.gray);
        g.draw3DRect(14, 74, 100, 30, true);
        g.setColor(new Color(0, 0, 0, 70));
        g.fill3DRect(14, 74, 100, 30, true);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", 0, 9));
        g.drawString("Fish caught: " +fishCaught, 18, 85);
    }
}

