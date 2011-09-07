import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import org.rsbot.bot.event.events.MessageEvent;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.concurrent.Task;
import org.rsbot.script.internal.event.MessageListener;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Keyboard;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.Mouse;
import org.rsbot.script.methods.NPCs;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.NPC;


@ScriptManifest(authors = { "Dwarfeh" }, keywords = { "test" }, name = "aaaa IM FIRST", description = "Trololol", version = 1.0)
public class Test extends Script implements MessageListener {
    boolean done = false;

    @Override
    public int loop() {
//        if (!done) {
//        for(int i = 0; Interfaces.getComponent(389, 4).getComponent(i) != null;) {
//            log("HOW BIG" +i +":" +Interfaces.getComponent(389, 4).getComponent(i).getText() +" IS NOT NULL");
//            i++;
//            if (Interfaces.getComponent(389, 4).getComponent(i +1) == null) {
//                done = true;
//            }
//        }        
//        }
//        if (done) {
//          sleep(100);
//        }
        Ge.open();
        if (Ge.isOpen()) {
            Ge.buy("Steel arrow", 1, 5, 20);
            sleep(400);
        }
        return 200;
    }
    
    public static class Ge {
        public static final int[] CLERKS = new int[]{2241, 2240, 2593, 1419};
        public static final int INTERFACE_GE = 105;
        public static final int GE_CLOSE = 14;
        public static final int SEARCH = 389;
        public static int SLOT = 0;
        
        public static boolean buy(String itemName, int slotNumber, int quantity, int price) {
            SLOT = slotNumber;
            if (slotNumber == 0) {
                return false;
            }
            if (isOpen()) {              
                int Interface = GEStuff.Stuff.Interface;
                int buyClick = GEStuff.Stuff.buyClick;
                int sellClick = GEStuff.Stuff.sellClick;
                log.severe("buyclick:" +buyClick);
                if (!isSearching()) {
                    Interfaces.getComponent(INTERFACE_GE, buyClick).click();
                    log.severe("Not searching, clicking!");
                    Task.sleep(Task.random(700, 900));
                }
                if (isSearching() && !hasSearched()) {
                    Keyboard.sendTextInstant(itemName, true);
                    log.severe("searching, has not searched!");
                    Task.sleep(Task.random(700, 900));
                }
                if (isSearching() && hasSearched()) {
                    boolean foundItem = false;
                    if (findItem() && !foundItem) {
                        boolean done = false;
                        int index = 0;
                        if (!done) {                            
                            for(int i = 0; Interfaces.getComponent(389, 4).getComponent(i) != null;) {
                                if (Interfaces.getComponent(389, 4).getComponent(i).getText().equals(itemName)) {
                                    index = i;
                                }
                                i++;
                                if (Interfaces.getComponent(389, 4).getComponent(i +1) == null) {
                                    done = true;
                                }
                            }        
                        }
                        if (done && index == 0) {
                            return false;
                        }
                        if (done && index > 0) {
                            if (!Interfaces.getComponent(389, 4).getComponent(index).isVisible() && Interfaces.getComponent(389, 8).getComponent(5).isVisible()) {
                                Interfaces.getComponent(389, 8).getComponent(5).click();
                                Task.sleep(Task.random(200, 500));
                            }                              
                            if (Interfaces.getComponent(389, 4).getComponent(index).isVisible()) {
                                Interfaces.getComponent(389, 4).getComponent(index).click();
                                Task.sleep(Task.random(700, 900));
                                foundItem = true;
                            }                          
                        }                        
                    }
                    if (foundItem) {                        
                        boolean changeQuantity = true;
                        boolean changePrice = true;                        
                        if (changeQuantity) {
                            if (Interfaces.getComponent(INTERFACE_GE).getComponent(167) != null && Interfaces.getComponent(INTERFACE_GE).getComponent(167).containsAction("Edit Quantity")) {
                                Interfaces.getComponent(INTERFACE_GE).getComponent(167).click();
                            } else {
                                Mouse.move(237, 222);
                                Mouse.click(true);
                            }
                            Task.sleep(Task.random(700, 900));
                            Keyboard.sendTextInstant("" +quantity, true);
                            changeQuantity = false;
//                            if (Interfaces.getComponent(INTERFACE_GE).getComponent(148) != null && Integer.parseInt(Interfaces.getComponent(INTERFACE_GE).getComponent(148).getText()) == quantity) {
//                                log.severe("changeQuantity is false");
//                                changeQuantity = false;
//                            }
                        }
                        if (!changeQuantity && changePrice) {
                            Task.sleep(Task.random(700, 900));
                            if (Interfaces.getComponent(INTERFACE_GE).getComponent(177) != null) {
                                Interfaces.getComponent(INTERFACE_GE).getComponent(177).click();
                            } else {
                                Mouse.move(387, 222);
                                Mouse.click(true);
                            }
                            Task.sleep(Task.random(700, 900));
                            Keyboard.sendTextInstant("" +price, true); 
                            changePrice = false;
//                            if (Integer.parseInt(Interfaces.getComponent(INTERFACE_GE).getComponent(153).getText()) == quantity) {
//                                changePrice = false;
//                            }                            
                        }
                        if (!changeQuantity && !changePrice) {
                            Task.sleep(Task.random(700, 900));
                            if (Interfaces.getComponent(INTERFACE_GE).getComponent(185) != null) {
                                Interfaces.getComponent(INTERFACE_GE).getComponent(186).click();
                            } else {
                                Mouse.move(262, 297);
                                Mouse.click(true);
                            }
                            return true;        
                        }
                    }                    
                }
            }
            return false;
        }
        
        public static int getEmptySlot() {
            
            return 0;
        }
        
        public static boolean findItem() {
            return !Interfaces.getComponent(SEARCH, 4).getComponent(0).getText().equals("No matching items found.");
        }
        
        public static boolean hasSearched() {
            //log.log(Level.SEVERE, "text: {0}", Interfaces.getComponent(SEARCH, 4).getComponent(1).getText().length());
            if (Interfaces.getComponent(SEARCH, 4).getComponent(1) != null) {
                log.log(Level.SEVERE, "hasSearched check: {0}", Interfaces.getComponent(SEARCH, 4).getComponent(1).getText());
            }
            return Interfaces.getComponent(SEARCH, 4).getComponent(1) != null;
        }
        
        public static boolean isSearching() {
            return Interfaces.getComponent(INTERFACE_GE, 134).isValid();
        }
                
        /**Checks whether the GE is open
         * 
         * @return <tt>true</tt> if the GE interface is valid; otherwise <tt>false</tt>
         */
        public static boolean isOpen() {
            return getInterface().isValid(); 
        }
                
        /**Gets the bank's interface.
         * 
         * @return <tt>true</tt> if interface is valid
         */
        public static Interface getInterface() {
            return Interfaces.get(INTERFACE_GE);
        }
    
	/**
	 * Closes the GE.
	 *
	 * @return <tt>true</tt> if the GE is no longer open; otherwise <tt>false</tt>
	 */        
        public static boolean close() {
            if (isOpen()) {
                Interfaces.getComponent(INTERFACE_GE, GE_CLOSE).click();
                Task.sleep(Task.random(500, 600));
                return !isOpen();                
            }
            return !isOpen();
        }
        
        public static boolean open() {
            if (!isOpen()) {
                Interactable i = new Npc("Exchange ", CLERKS);
                if (!i.isValid()) {
                    return false;
                }
                if (i.interact()) {
                        if (i.getDistance() > 1) {
                                long time = System.currentTimeMillis();
                                int max = Task.random(2000, 4000);
                                while ((System.currentTimeMillis() - time) < max) {
                                        if (Players.getLocal().isMoving()) {
                                                do {
                                                        Task.sleep(Task.random(5, 15));
                                                } while (Players.getLocal().isMoving() || !i.isOnScreen());
                                                break;
                                        }
                                        Task.sleep(Task.random(5, 15));
                                }
                        }
                        for (int j = 0; j < 10 && !isOpen(); j++) {
                                Task.sleep(Task.random(100, 200));
                        }
                        // Ensures that the widget becomes valid
                        Task.sleep(Task.random(700, 900));
                        return isOpen();
                } else {
                        return false;
                }
            }
            return true;
        }       
        
        public static int getPrice(String name) {
            try {
                name = name.replace(' ', '+');
                if (!Character.isUpperCase(name.charAt(0))) {
                    Character.toUpperCase(name.charAt(0));
                }
                URL url = new URL("http://rscript.org/lookup.php?type=ge&search=" + name);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;            
                int lineIndex = 0;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains(name.replace('+', '_')) && lineIndex >= 8) {
                        String words[] = inputLine.toString().split(" ");  
                        return Integer.parseInt(words[4]);                    
                    }
                }
            } catch (Exception e) {
            }
        return 0;
        }
        
        public static int getPrice(int id) {
            try {
                URL url = new URL("http://rscript.org/lookup.php?type=ge&search=" + id);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;            
                int lineIndex = 0;
                while ((inputLine = in.readLine()) != null) {
                    if (lineIndex == 8 && inputLine.contains("ITEM:")) {
                        String words[] = inputLine.toString().split(" ");  
                        return Integer.parseInt(words[4]);                
                    }
                }
            } catch (Exception e) {
            }
            return 0;
        }

    private enum GEStuff {
        Stuff(SLOT);
        int Interface;
        int buyClick;
        int sellClick;        

        GEStuff(int slot) {
            switch (slot) {            
                case 1:
                   Interface = 19;
                   buyClick = 30;
                   sellClick = 29;
                   break;

                case 2:
                    Interface = 35;
                    buyClick = 46;
                    sellClick = 45;
                    break;

                case 3:
                    Interface = 51;
                    buyClick = 62;
                    sellClick = 61;
                    break;

                case 4:
                    Interface = 70;
                    buyClick = 81;
                    sellClick = 80;
                    break;

                case 5:
                    Interface = 89;
                    buyClick = 100;
                    sellClick = 99;
                    break;

                case 6:
                    Interface = 108;
                    buyClick = 119;
                    sellClick = 118;
                    break;
            }            
        }
    }
        
	private static interface Interactable {
		public int getDistance();

		boolean interact();

		boolean isOnScreen();

		boolean isValid();
	}
        
	private static class Npc implements Interactable {
		private final String action;

		private final NPC npc;

		public Npc(String action, int... ids) {
                    this.action = action;
                    npc = NPCs.getNearest(ids);
		}

		public int getDistance() {
                    return Calculations.distanceTo(npc);
		}

		public boolean interact() {
                    Mouse.click(npc.getPoint(), false);
                    return Menu.isOpen() && Menu.clickIndex(Menu.getIndex(action) +1);
                    //return npc.interact(action);
		}

		public boolean isOnScreen() {
			return npc.isOnScreen();
		}

		public boolean isValid() {
			return npc != null;
		}
	}         
    }  
}

