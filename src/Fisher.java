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
                    if (Menu.isOpen()) {
                        Menu.click("Cage");
                    }
                    Task.sleep(1500);
                }
                if (!spot.isOnScreen()) {
                    Camera.turnTo(spot);
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

    public static class Camera {

        public static void turnTo(final Character n) {
            final int angle = getTileAngle(n.getLocation());
            setAngle(angle);
            setPitch(0);
        }

        public static int getTileAngle(final Tile t) {
            int ang = Calculations.angleToTile(t) - 90;
            if (ang < 0) {
                ang = 360 - ang;
            }
            return ang % 360;
        }

        public static void setAngle(final int degrees) {
            final InputManager inputManager = Context.get().composite.inputManager;
            if (getAngleTo(degrees) > 5) {
                inputManager.pressKey((char) KeyEvent.VK_LEFT);
                while (getAngleTo(degrees) > 5) {
                    Task.sleep(10);
                }
                inputManager.releaseKey((char) KeyEvent.VK_LEFT);
            } else if (getAngleTo(degrees) < -5) {
                inputManager.pressKey((char) KeyEvent.VK_RIGHT);
                while (getAngleTo(degrees) < -5) {
                    Task.sleep(10);
                }
                inputManager.releaseKey((char) KeyEvent.VK_RIGHT);
            }
        }

        public static int getAngleTo(final int degrees) {
            int ca = getAngle();
            if (ca < degrees) {
                ca += 360;
            }
            int da = ca - degrees;
            if (da > 180) {
                da -= 360;
            }
            return da;
        }

        public static int getAngle() {
            // the client uses fixed point radians 0 - 2^14
            // degrees = yaw * 360 / 2^14 = yaw / 45.5111...
            return (int) (Context.get().client.getCameraYaw() / 45.51);
        }

        public static boolean setPitch(final boolean up) {
            if (up) {
                return setPitch(100);
            } else {
                return setPitch(0);
            }
        }

        /**
         * Set the camera to a certain percentage of the maximum pitch. Don't rely
         * on the return value too much - it should return whether the camera was
         * successfully set, but it isn't very accurate near the very extremes of
         * the height.
         * <p/>
         * <p/>
         * This also depends on the maximum camera angle in a region, as it changes
         * depending on situation and surroundings. So in some areas, 68% might be
         * the maximum altitude. This method will do the best it can to switch the
         * camera altitude to what you want, but if it hits the maximum or stops
         * moving for any reason, it will return.
         * <p/>
         * <p/>
         * <p/>
         * Mess around a little to find the altitude percentage you like. In later
         * versions, there will be easier-to-work-with methods regarding altitude.
         *
         * @param percent The percentage of the maximum pitch to set the camera to.
         * @return true if the camera was successfully moved; otherwise false.
         */
        public static boolean setPitch(final int percent) {
            final InputManager inputManager = Context.get().composite.inputManager;
            int curAlt = getPitch();
            int lastAlt = 0;
            if (curAlt == percent) {
                return true;
            } else if (curAlt < percent) {
                inputManager.pressKey((char) KeyEvent.VK_UP);
                long start = System.currentTimeMillis();
                while (curAlt < percent && System.currentTimeMillis() - start < Task.random(50, 100)) {
                    if (lastAlt != curAlt) {
                        start = System.currentTimeMillis();
                    }
                    lastAlt = curAlt;
                    Task.sleep(Task.random(5, 10));
                    curAlt = getPitch();
                }
                inputManager.releaseKey((char) KeyEvent.VK_UP);
                return true;
            } else {
                inputManager.pressKey((char) KeyEvent.VK_DOWN);
                long start = System.currentTimeMillis();
                while (curAlt > percent && System.currentTimeMillis() - start < Task.random(50, 100)) {
                    if (lastAlt != curAlt) {
                        start = System.currentTimeMillis();
                    }
                    lastAlt = curAlt;
                    Task.sleep(Task.random(5, 10));
                    curAlt = getPitch();
                }
                inputManager.releaseKey((char) KeyEvent.VK_DOWN);
                return true;
            }
        }

        public static int getPitch() {
            return (int) ((Context.get().client.getCameraPitch() - 1024) / 20.48);
        }
    }
    public static class Inventory extends org.rsbot.script.methods.tabs.Inventory {

        /**
         * Checks whether or not your inventory contains the provided item name.
         *
         * @param name The item(s) you wish to evaluate.
         * @return <tt>true</tt> if your inventory contains an item with the name
         *         provided; otherwise <tt>false</tt>.
         */
        public static boolean contains(final String name) {
            return getItem(name) != null;
        }

        /**
         * Gets the first item in the inventory containing any of the provided names.
         *
         *
         * @param names The names of the item to find.
         * @return The first <tt>RSItem</tt> for the given name(s); otherwise null.
         */
        public static Item getItem(final String... names) {
            for (final Item item : getItems()) {
                String name = item.getName();
                if (name != null) {
                    name = name.toLowerCase();
                    for (final String n : names) {
                        if (n != null && name.contains(n.toLowerCase())) {
                            return item;
                        }
                    }
                }
            }
            return null;
        }
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

