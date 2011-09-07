
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.concurrent.Task;
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
public class Test extends Script {
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
            int t = Ge.getEmptySlot();
            log("t:" +t);
            Ge.buy("Steel arrow", t, 5, 20, false);            
        }
        return 200;
    }
    
    public static class Ge {
        public static final int[] CLERKS = new int[]{2241, 2240, 2593, 1419};
        public static final int GE_INTERFACE = 105;
        public static final int GE_CLOSE = 14;
        public static final int SEARCH = 389;
        public static int SLOT = 0;
        public static boolean Membs;
        
        public static boolean buy(String itemName, int slotNumber, int quantity, int price, boolean members) {
            SLOT = slotNumber;
            Membs = members;
            if (slotNumber == 0 || slotNumber > 5) {
                return false;
            }
            if (isOpen()) {
                GEMethods t = new GEStuff2(slotNumber);
                log.severe("SlotNumber: " +SLOT);
                int Interface = t.getInterface();
                int buyClick = t.getBuyClick();
                int sellClick = t.getSellClick();
                log.severe("buyclick:" +buyClick);
                if (!isSearching()) {
                    Interfaces.getComponent(GE_INTERFACE, buyClick).click();
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
                            if (Interfaces.getComponent(GE_INTERFACE).getComponent(167) != null && Interfaces.getComponent(GE_INTERFACE).getComponent(167).containsAction("Edit Quantity")) {
                                Interfaces.getComponent(GE_INTERFACE).getComponent(167).click();
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
                            if (Interfaces.getComponent(GE_INTERFACE).getComponent(177) != null) {
                                Interfaces.getComponent(GE_INTERFACE).getComponent(177).click();
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
                            if (Interfaces.getComponent(GE_INTERFACE).getComponent(185) != null) {
                                Interfaces.getComponent(GE_INTERFACE).getComponent(186).click();
                            } else {
                                Mouse.move(262, 297);
                                Mouse.click(true);
                            }
                            Ge.close();
                            return true;        
                        }
                    }                    
                }
            }
            return false;
        }
        
        /**Checks for nearest empty slot
         * 
         * @return an empty spot in the GE if there is one. If not, 0
         */
        public static int getEmptySlot() {
            if (isOpen()) {
                for (int i = 1; i != 5;) {
                    SLOT = i;
                    GEMethods check2 = new GEStuff2(i);
                    int check = check2.getInterface();
                    if (Interfaces.getComponent(GE_INTERFACE, check).getComponent(10).getText().equals("Empty")) {
                        return i;
                    }
                    if (!Membs && i == 2) {
                        return 0;
                    }                    
                    i++;
                }              
            }
            return 0;
        }       
        /**Determines if there is an item by the name
         * 
         * @return <tt>true</tt> if an item was found; otherwise <tt>false</tt>
         */
        public static boolean findItem() {
            return !Interfaces.getComponent(SEARCH, 4).getComponent(0).getText().equals("No matching items found.");
        }
        
        /**Determines if the person has searched or not
         * 
         * @return <tt>true</tt> if they have searched; otherwise <tt>false</tt>
         */
        public static boolean hasSearched() {
            //log.log(Level.SEVERE, "text: {0}", Interfaces.getComponent(SEARCH, 4).getComponent(1).getText().length());
            if (Interfaces.getComponent(SEARCH, 4).getComponent(1) != null) {
                log.log(Level.SEVERE, "hasSearched check: {0}", Interfaces.getComponent(SEARCH, 4).getComponent(1).getText());
            }
            return Interfaces.getComponent(SEARCH, 4).getComponent(1) != null;
        }
        
        /**Determines if the player is searching
         * 
         * @return <tt>true</tt> if interface is valid; otherwise <tt>false</tt>
         */
        public static boolean isSearching() {
            return Interfaces.getComponent(GE_INTERFACE, 134).isVisible();
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
            return Interfaces.get(GE_INTERFACE);
        }
    
	/**
	 * Closes the GE.
	 *
	 * @return <tt>true</tt> if the GE is no longer open; otherwise <tt>false</tt>
	 */        
        public static boolean close() {
            if (isOpen()) {
                Interfaces.getComponent(GE_INTERFACE, GE_CLOSE).click();
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
        
        private static interface GEMethods {
            public int getInterface();
            
            public int getBuyClick();
            
            public int getSellClick();
        }
        
        private static class GEStuff2 implements GEMethods {
            private int Interface = 0;
            private int buyClick = 0;
            private int sellClick = 0;
            
            public GEStuff2(int slot) {
                switch (slot) {            
                    case 1:
                       this.Interface = 19;
                       this.buyClick = 30;
                       this.sellClick = 29;
                       break;

                    case 2:
                        this.Interface = 35;
                        this.buyClick = 46;
                        this.sellClick = 45;
                        break;

                    case 3:
                        this.Interface = 51;
                        this.buyClick = 62;
                        this.sellClick = 61;
                        break;

                    case 4:
                        this.Interface = 70;
                        this.buyClick = 81;
                        this.sellClick = 80;
                        break;

                    case 5:
                        this.Interface = 89;
                        this.buyClick = 100;
                        this.sellClick = 99;
                        break;

                    case 6:
                        this.Interface = 108;
                        this.buyClick = 119;
                        this.sellClick = 118;
                        break;
                }
            }
            
            @Override
            public int getInterface() {
                return this.Interface;
            }

            @Override
            public int getBuyClick() {
                return this.buyClick;
            }

            @Override
            public int getSellClick() {
                return this.sellClick;
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

