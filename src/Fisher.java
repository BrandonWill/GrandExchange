import org.rsbot.bot.BotComposite;
import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Model;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.event.MessageEvent;
import org.rsbot.bot.event.listener.MessageListener;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.bot.input.InputManager;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.*;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.tabs.Inventory;
import org.rsbot.script.methods.ui.Camera;
import org.rsbot.script.methods.ui.Interfaces;

import org.rsbot.script.wrappers.*;
import org.rsbot.script.wrappers.Character;


import java.awt.*;
import java.awt.event.KeyEvent;

@ScriptManifest(authors = { "Dwarfeh" }, keywords = { "fish, fisher, crayfish" }, name = "Dwarfeh's Fisher", description = "Fishes Crayfish", version = 1.0)
public class Fisher extends Script implements MessageListener, PaintListener {
    int fishSpot = 6996;
    int[] noDrop = { 13431, 995 };
    int fishCaught = 0;

    @Override
    protected int loop() {
        Mouse.setPrecisionSpeed(7);
        if (Interfaces.canContinue()) {
            Interfaces.clickCont();
        }
        if (Inventory.isFull()) {
            //Inventory.dropAllExcept(13431, 995);
            for (int i = 0; i < 28; i++) {
                if (Inventory.getItemAt(i).getID() != noDrop[0] && Inventory.getItemAt(i).getID() != noDrop[1]) {
                    Mouse.setPrecisionSpeed(1);
                    Item fish = Inventory.getItemAt(i);
                    Mouse.move(fish.getComponent().getAbsLocation());
                    Mouse.click(false);
                    if (Menu.isOpen()) {
                        Menu.click("Drop");
                    }
                }
            }
        }
        while (Players.getLocal().getAnimation() == 10009 && !Interfaces.canContinue()) {
            sleep(500);
        }
        if (!Inventory.isFull() && Players.getLocal().getAnimation() != 10009) {
            NPC spot = NPCs.getNearest(fishSpot);
            if (spot != null) {
                Camera.turnTo(spot);
                if (spot.isOnScreen()) {
                    Point p = spot.getCentralPoint();
                    Mouse.hop(p);
                    Mouse.click(false);
                    if (Menu.isOpen() && Menu.contains("Cage")) {
                        Menu.click("Cage");
                    }
                    Task.sleep(1500);
                }
                if (!spot.isOnScreen()) {
                    Camera.turnTo(spot);
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
        if (text.contains("catch a")) {
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

    public static class Interfaces extends org.rsbot.script.methods.ui.Interfaces {

        public static boolean clickCont() {
            final InterfaceComponent cont = getContinueComponent();
            Mouse.move(cont.getAbsLocation());
            Mouse.click(true);
            return cont != null && cont.isValid();
        }
    }
}

