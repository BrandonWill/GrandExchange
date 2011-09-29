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
import org.rsbot.script.methods.ui.Lobby;
import org.rsbot.script.wrappers.*;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

@ScriptManifest(authors = { "Dwarfeh" }, keywords = { "fish, fisher, crayfish" }, name = "Dwarfeh's Fisher", description = "Fishes Crayfish", version = 1.0)
public class Fisher extends Script implements MessageListener, PaintListener {
    int fishSpot = 6996;
    int[] noDrop = { 13431, 995, 14664 };
    int fishCaught = 0;
    String fishAction;
    String cage;
    String drop;

    @Override
    protected int loop() {
        Mouse.setPrecisionSpeed(7);
        if (Game.isLoggedIn()) {
            if (Game.getWorldLanguage() == Game.Language.ENGLISH) {
                fishAction = "Cage";
            }
            if (Game.getWorldLanguage() == Game.Language.GERMAN) {
                fishAction = "Angelplatz";
                cage = "Krebsreuse";
                drop = "Fallen lassen";
            }
        }
        if (Interfaces.canContinue()) {
            Interfaces.clickContinue();
        }
        if (Inventory.isFull()) {
            log("cage: " +cage);
            //Inventory.dropAllExcept(noDrop);
            for (int i = 0; i < 28; i++) {
                if (!Inventory.getItemAt(i).getName().equals(cage)) {
                    Item fish = Inventory.getItemAt(i);
                    Mouse.hop(fish.getCentralPoint());
                    Mouse.click(false);
                    if (Menu.isOpen()) {
                        Menu.clickIndex(1);
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
                //Camera.setAngle(Camera.getTileAngle(spot.getLocation()));
                Camera.turnTo(new Tile(spot.getLocation().getX(), spot.getLocation().getY()));
                if (spot.isOnScreen()) {
                    Point p = spot.getCentralPoint();
                    Mouse.hop(p);
                    Mouse.click(false);
                    if (Menu.isOpen() && Menu.contains(fishAction)) {
                        Menu.click(fishAction);
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
        if (Game.getWorldLanguage() == Game.Language.ENGLISH) {
            if (text.contains("catch a") && !text.contains("attempt")) {
                fishCaught++;
            }
        }
        if (Game.getWorldLanguage() == Game.Language.GERMAN) {
            if (text.contains("einen")) {
                fishCaught++;
            }
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
    public static class Game extends org.rsbot.script.methods.Game {

        public static final int[] GERMAN_WORLDS = {122, 139, 140, 146, 147};
        public static final int[] FRENCH_WORLDS = {128, 150};
        public static final int[] PORTUGUESE_WORLDS = {94, 101, 125, 126, 127, 133};
        public static final int[] MEMBER_WORLDS = {2, 6, 9, 12, 15, 18, 21, 22, 23, 24, 26, 27, 28, 31, 32, 36, 39,
                40, 42, 44, 45, 46, 48, 51, 52, 53, 54, 56, 58, 59, 60, 64, 65, 66, 67, 68, 69, 70, 71, 72, 76, 77, 78,
                79, 82, 83, 84, 85, 86, 88, 89, 91, 92, 96, 97, 98, 99, 100, 103, 104, 112, 114, 115, 116, 119, 121, 124,
                127, 129, 130, 131, 132, 133, 137, 138, 140, 142, 143, 144, 145, 147, 148, 150, 151, 156, 157, 158, 159, 160, 164, 166, 172};
        public static final int[] FREE_WORLDS = {1, 3, 4, 5, 7, 8, 10, 11, 13, 14, 16, 17, 19, 20, 25, 29, 30, 33, 34,
                35, 37, 38, 41, 43, 47, 49, 50, 55, 56, 57, 61, 62, 73, 74, 75, 80, 81, 87, 90, 93, 94, 101, 102, 105, 106, 108,
                113, 118, 120, 122, 123, 125, 126, 134, 135, 136, 139, 141, 146, 149, 152, 153, 154, 155, 161, 165, 167, 169};

        public enum Language {
            ENGLISH, GERMAN, FRENCH, PORTUGUESE
        }
        /**
         * Get the current world language.
         *
         * @return The language of the current world, currently only English, German, French or Portuguese.
         */
        public static Language getWorldLanguage() {
            final Map<Language, int[]> map = new HashMap<Language, int[]>(3);
            map.put(Language.GERMAN, GERMAN_WORLDS);
            map.put(Language.FRENCH, FRENCH_WORLDS);
            map.put(Language.PORTUGUESE, PORTUGUESE_WORLDS);
            final int w = getCurrWorld();
            for (final Map.Entry<Language, int[]> entry : map.entrySet()) {
                for (int i = 0; i < entry.getValue().length; i++) {
                    if (w == entry.getValue()[i]) {
                        return entry.getKey();
                    }
                }
            }
            return Language.ENGLISH;
        }

        /**
         * Determines whether the current world is members or free to play.
         *
         * @return true if the current world is a members world, false otherwise.
         */
        public static boolean isWorldMembers() {
            final int w = getCurrWorld();
            for (final int n : MEMBER_WORLDS) {
                if (w == n) {
                    return true;
                }
            }
            return false;
        }

        public static int getCurrWorld() {
            int world = 0;
            if (Game.isLoggedIn()) {
                if (Game.getCurrentTab() != Tabs.FRIENDS) {
                    Game.openTab(Tabs.FRIENDS);
                }
                if (Interfaces.getComponent(550, 19).getText().contains("Friends List<br>RuneScape ")) {
                world = Integer.parseInt(Interfaces.getComponent(550, 19).getText().replaceAll("Friends List<br>RuneScape ", ""));
                }
                if (Interfaces.getComponent(550, 19).getText().contains("Freunde-Liste<br>RuneScape")) {
                world = Integer.parseInt(Interfaces.getComponent(550, 19).getText().replaceAll("Freunde-Liste<br>RuneScape", ""));
                }
            } else if (Game.getClientState() == Game.INDEX_LOBBY_SCREEN) {
                world = Lobby.getSelectedWorld();
            }
            return world;
        }
    }

}

