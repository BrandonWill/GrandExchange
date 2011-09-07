
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.concurrent.Task;
import org.rsbot.script.methods.Account;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Keyboard;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.Mouse;
import org.rsbot.script.methods.NPCs;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.Item;
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
        if (!Ge.collectIsOpen()) {
            Ge.collectClose();
        }
        Ge.open();
        if (Ge.isOpen()) {            
            int t = Ge.getEmptySlot();
            if (t >0) {
                Ge.buy("Steel arrow", t, 5, 19);
//                if (!Ge.sell("Steel arrow", t, 0, 20)) {
//                    Ge.close();   
//                }
            } else {
                for(int i = 1; i <= Ge.getTotalSlots();) {
                    log("isOfferCompleted for " +i +" :" +Ge.isOfferCompleted(i));
                    Mouse.moveSlightly();
                    Task.sleep(Task.random(700, 1000));                    
                    if (Ge.isOfferCompleted(i)) {
                        if (Ge.isOpen()) {
                            Ge.close();
                        }
                        if (!Ge.collectIsOpen()) {
                            Ge.collectOpen();
                        }
                        if (Ge.collectIsOpen()) {
                            Ge.collectAllCollection();
                        }
                    }
                    i++;
                }
            }
        }
        return 200;
    }
    
    public static class Ge {
        public static final int[] CLERKS = new int[]{2241, 2240, 2593, 1419};
        public static final int[] BANKERS = new int[]{3293, 3416, 2718, 3418};
        public static final int GE_INTERFACE = 105;
        public static final int GE_CLOSE = 14;
        public static final int SEARCH = 389;
        public static final int COLLECT_INTERFACE = 109;
        public static int SLOT = 0;
     
        private static final Pattern PATTERN = Pattern.compile("(?i)<td><img src=\".+obj_sprite\\.gif\\?id=(\\d+)\" alt=\"(.+)\"");        

	private static final String HOST = "http://services.runescape.com";
	private static final String GET = "/m=itemdb_rs/viewitem.ws?obj=";
        
        public static boolean buy(String itemName, int slotNumber, int quantity, int price) {
            SLOT = slotNumber;
            if (slotNumber == 0 || slotNumber > 5) {
                return false;
            }
            if (isOpen()) {
                GEBuyMethods t = new GEBuy(slotNumber);
                int buyClick = t.getBuyClick();
                if (!isSearching()) {
                    Interfaces.getComponent(GE_INTERFACE, buyClick).click();
                    log.severe("Not searching, clicking!");
                    Task.sleep(Task.random(700, 900));
                }
                if (isSearching() && !hasSearched(itemName)) {
                    Keyboard.sendTextInstant(itemName, true);
                    log.severe("searching, has not searched!");
                    Task.sleep(Task.random(1000, 1500));
                }
                if (isSearching() && hasSearched(itemName)) {
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
                        boolean changePrice;
                        if (price > 1) {
                            changePrice = true;
                        } else {
                            changePrice = false;
                        }
                        if (changeQuantity) {
                            if (Interfaces.getComponent(GE_INTERFACE, 167).getActions() != null && Interfaces.getComponent(GE_INTERFACE, 167).getActions().length >= 1) {
                                Interfaces.getComponent(GE_INTERFACE, 167).click();
                                Task.sleep(Task.random(700, 900));
                            } else {
                                Mouse.move(237, 222);
                                Mouse.click(true);
                                Task.sleep(Task.random(700, 900));
                            }
                            if (Interfaces.getComponent(752, 4).getText().contains("you wish to purchase")) {
                                Keyboard.sendTextInstant("" +quantity, true);
                            }                            
                            Task.sleep(Task.random(1000, 2000));
                            if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains("" +quantity)) {
                                changeQuantity = false;
                            }
                        }
                        if (!changeQuantity && changePrice) {
                            Task.sleep(Task.random(700, 900));
                            if (Interfaces.getComponent(GE_INTERFACE).getComponent(177) != null) {
                                Interfaces.getComponent(GE_INTERFACE).getComponent(177).click();
                                Task.sleep(Task.random(700, 900));
                            } else {
                                Mouse.move(387, 222);
                                Mouse.click(true);
                                Task.sleep(Task.random(700, 900));
                            }
                            if (Interfaces.getComponent(752, 4).getText().contains("you wish to buy")) {
                                Keyboard.sendTextInstant("" +price, true);
                            }
                            Task.sleep(Task.random(1000, 2000));
                            if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().contains("" +price)) {
                                changePrice = false;
                            }                              
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

        public static boolean sell(String itemName, int slotNumber, int quantity, int price) {
            SLOT = slotNumber;
            if (slotNumber == 0 || slotNumber > 5) {
                return false;
            }
            if (isOpen()) {
                GEBuyMethods t = new GEBuy(slotNumber);
                int sellClick = t.getSellClick();
                boolean offerItem = false;
                boolean offeredItem = false;
                if (!isSelling()) {
                    Interfaces.getComponent(GE_INTERFACE, sellClick).click();
                    log.severe("Not searching, clicking!");
                    Task.sleep(Task.random(700, 900));
                    offerItem = true;                    
                }
                if (!isSelling() && offerItem) {
                    if (Inventory.contains(itemName)) {
                        Inventory.getItem(itemName).click(true);
                    } else {
                        return false;
                    }
                    Task.sleep(Task.random(300, 500));
                    offeredItem = true;
                }
                if (!isSelling() && offeredItem) {                        
                    boolean changeQuantity;
                    if (quantity > 1) {
                        changeQuantity = true;
                    } else {
                        changeQuantity = false;
                    }
                    boolean changePrice;
                    if (price > 1) {
                        changePrice = true;
                    } else {
                        changePrice = false;
                    }
                    if (changeQuantity) {
                        if (Interfaces.getComponent(GE_INTERFACE, 168).getActions() != null && Interfaces.getComponent(GE_INTERFACE, 168).getActions().length >= 1) {
                            Interfaces.getComponent(GE_INTERFACE, 168).click();
                            Task.sleep(Task.random(700, 900));
                        } else {
                            Mouse.move(237, 222);
                            Mouse.click(true);
                            Task.sleep(Task.random(700, 900));
                        }                        
                        if (Interfaces.getComponent(752, 4).getText().contains("you wish to purchase")) {
                            Keyboard.sendTextInstant("" +quantity, true);
                        }   
                        if (Interfaces.getComponent(GE_INTERFACE).getComponent(148).getText() != null && Integer.parseInt(Interfaces.getComponent(GE_INTERFACE, 148).getText()) == quantity) {
                            changeQuantity = false;
                        }
                    }
                    if (!changeQuantity && changePrice) {
                        Task.sleep(Task.random(700, 900));
                        if (Interfaces.getComponent(GE_INTERFACE).getComponent(177) != null) {
                            Interfaces.getComponent(GE_INTERFACE).getComponent(177).click();
                            Task.sleep(Task.random(700, 900));
                        } else {
                            Mouse.move(387, 222);
                            Mouse.click(true);
                            Task.sleep(Task.random(700, 900));
                        }
                        if (Interfaces.getComponent(752, 4).getText().contains("you wish to buy")) {
                            Keyboard.sendTextInstant("" +price, true);
                        }
                        Task.sleep(Task.random(700, 900));
                        if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().equals("" +price +" gp")) {
                            changePrice = false;
                        }                            
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
            return false;
        }        
                
        public static boolean isSelling() {
            return Interfaces.getComponent(GE_INTERFACE, 142).isValid() && !Interfaces.getComponent(GE_INTERFACE, 142).getText().equals("Choose an item to exchange");
        }
                
                
        /**Determines membership
         * 
         * @return <tt>true</tt> if members is selected for the account; otherwise <tt>false</tt>
         */
        public static boolean isMember() {
            if (AccountManager.isMember(Account.getName())) {
                return true;
            }
            return false;
        }
        
        /**Gets the total slots there are for the person
         * 
         * @return number of slots if account is member
         */
        public static int getTotalSlots() {
            if (isMember()) {
                return 6;
            }
            return 2;
        }
        
        public static boolean isSlotEmpty(int slot) {
            GEBuyMethods check2 = new GEBuy(slot);
            int check = check2.getInterface();
            if (isOpen()) {
                if (Interfaces.getComponent(GE_INTERFACE, check).getComponent(10).getText().equals("Empty")) {
                    return true;
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
                    GEBuyMethods check2 = new GEBuy(i);
                    int check = check2.getInterface();
                    if (Interfaces.getComponent(GE_INTERFACE, check).getComponent(10).getText().equals("Empty")) {
                        return i;
                    }
                    if (!isMember() && i == 2) {
                        return 0;
                    }                    
                    i++;
                }              
            }
            return 0;
        }                  
        
        public static boolean isOfferCompleted(int slot) {
            GEBuyMethods check2 = new GEBuy(slot);
            return check2.isOfferCompleted(slot);
        }
        /**Determines if there is an item by the name
         * 
         * @return <tt>true</tt> if an item was found; otherwise <tt>false</tt>
         */
        public static boolean findItem() {
            return Interfaces.getComponent(SEARCH, 4).getComponent(0) != null && Interfaces.getComponent(SEARCH, 4).getComponent(0).getText() != null && !Interfaces.getComponent(SEARCH, 4).getComponent(0).getText().equals("No matching items found.");
        }
        
        /**Determines if the person has searched or not
         * 
         * @return <tt>true</tt> if they have searched; otherwise <tt>false</tt>
         */
        public static boolean hasSearched(String itemName) {
            if (!Character.isUpperCase(itemName.charAt(0))) {
                Character.toUpperCase(itemName.charAt(0));
            }
            if (Interfaces.getComponent(GE_INTERFACE, 142).getText().contains(itemName)) {
                return true;
            }            
            return Interfaces.getComponent(SEARCH, 4).getComponent(1) != null && Interfaces.getComponent(SEARCH, 9).getText().contains(itemName);
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
            
            

        /**Gets the general interface for the slot
         * 
         * @param slot determines which one to take from
         * @return interface for the slot
         */
        public static int collectGetInterface(int slot) {
            Ge.GECollectMethods collect = new Ge.GECollect(slot);
            return collect.getInterface();
        }

        /**Gets the left interface for the slot
         * 
         * @param slot determines which one to take from
         * @return left interface for the slot
         */
        public static int collectGetLeftInterface(int slot) {
            Ge.GECollectMethods collect = new Ge.GECollect(slot);
            return collect.getLeftCollect();
        }

        /**Gets the right interface for the slot
         * 
         * @param slot determines which one to take from
         * @return right interface for the slot
         */
        public static int collectGetRightInterface(int slot) {
            Ge.GECollectMethods collect = new Ge.GECollect(slot);
            return collect.getRightCollect();
        }            
        
        /**Collects from slot
         * 
         * @return <tt>true</tt> if collected successfully; otherwise <tt>false</tt>
         */
        public static boolean collectBothCollection(int slot) {
            Ge.GECollectMethods collect = new Ge.GECollect(slot);
            return collect.collectBoth();
        }
            
        /**Collects everything from the interface
         * 
         * @return <tt>true</tt> if collected all successfully; otherwise <tt>false</tt>
         */
        public static boolean collectAllCollection() {
            Ge.GECollectMethods collect = new Ge.GECollect();
            return collect.collectAll();
        }
        
        /**Opens collection interface
         * 
         * @return <tt>true</tt> if opened successfully; otherwise <tt>false</tt>
         */
        public static boolean collectOpen() {
            Ge.GECollectMethods collect = new Ge.GECollect();
            return collect.open();
        }
        
        /**Closes collection interface
         * 
         * @return <tt>true</tt> if closed successfully; otherwise <tt>false</tt>
         */
        public static boolean collectClose() {
            Ge.GECollectMethods collect = new Ge.GECollect();
            return collect.close();
        }        
        
        /**Checks collection interface
         * 
         * @return <tt>true</tt> if opened; otherwise <tt>false</tt>
         */
        public static boolean collectIsOpen() {
            Ge.GECollectMethods collect = new Ge.GECollect();
            return collect.isOpen();
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
        
	/**
	 * Gets the name of the given item ID. Should not be used.
	 *
	 * @param itemID The item ID to look for.
	 * @return The name of the given item ID or an empty String if unavailable.
	 * @see GrandExchange#lookup(int)
	 */
	public String getItemName(final int itemID) {
		final GEItem geItem = lookup(itemID);
		if (geItem != null) {
			return geItem.getName();
		}
		return "";
	}

	/**
	 * Gets the ID of the given item name. Should not be used.
	 *
	 * @param itemName The name of the item to look for.
	 * @return The ID of the given item name or -1 if unavailable.
	 * @see GrandExchange#lookup(java.lang.String)
	 */
	public int getItemID(final String itemName) {
		final GEItem geItem = lookup(itemName);
		if (geItem != null) {
			return geItem.getID();
		}
		return -1;
	}
        
	/**
	 * Collects data for a given item ID from the Grand Exchange website.
	 *
	 * @param itemID The item ID.
	 * @return An instance of GrandExchange.GEItem; <code>null</code> if unable
	 *         to fetch data.
	 */
	public GEItem lookup(final int itemID) {
		try {
                    
			final URL url = new URL(Ge.HOST + Ge.GET + itemID);
			final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String input;
			boolean exists = false;
			int i = 0;
			final double[] values = new double[4];
			String name = "", examine = "";
			while ((input = br.readLine()) != null) {
				if (input.contains("<div class=\"brown_box main_ge_page") && !exists) {
					if (!input.contains("vertically_spaced")) {
						return null;
					}
					exists = true;
					br.readLine();
					br.readLine();
					name = br.readLine();
				} else if (input.contains("<img id=\"item_image\" src=\"")) {
					examine = br.readLine();
				} else if (input.matches("(?i).+ (price|days):</b> .+")) {
					values[i] = parse(input);
					i++;
				} else if (input.matches("<div id=\"legend\">")) {
					break;
				}
			}
			return new GEItem(name, examine, itemID, values);
		} catch (final IOException ignore) {
		}
		return null;
	}

	/**
	 * Collects data for a given item name from the Grand Exchange website.
	 *
	 * @param itemName The name of the item.
	 * @return An instance of GrandExchange.GEItem; <code>null</code> if unable
	 *         to fetch data.
	 */
	public GEItem lookup(final String itemName) {
		try {
			final URL url = new URL(Ge.HOST + "/m=itemdb_rs/results.ws?query=" + itemName + "&price=all&members=");
			final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String input;
			while ((input = br.readLine()) != null) {
				if (input.contains("<div id=\"search_results_text\">")) {
					input = br.readLine();
					if (input.contains("Your search for")) {
						return null;
					}
				} else if (input.startsWith("<td><img src=")) {
					final Matcher matcher = Ge.PATTERN.matcher(input);
					if (matcher.find()) {
						if (matcher.group(2).contains(itemName)) {
							return lookup(Integer.parseInt(matcher.group(1)));
						}
					}
				}
			}
		} catch (final IOException ignored) {
		}
		return null;
	}

	private double parse(String str) {
		if (str != null && !str.isEmpty()) {
			str = stripFormatting(str);
			str = str.substring(str.indexOf(58) + 2, str.length());
			str = str.replace(",", "");
			str = str.trim();
			if (!str.endsWith("%")) {
				if (!str.endsWith("k") && !str.endsWith("m") && !str.endsWith("b")) {
					return Double.parseDouble(str);
				}
				return Double.parseDouble(str.substring(0, str.length() - 1)) * (str.endsWith("b") ? 1000000000 : str.endsWith("m") ? 1000000 : 1000);
			}
			final int k = str.startsWith("+") ? 1 : -1;
			str = str.substring(1);
			return Double.parseDouble(str.substring(0, str.length() - 1)) * k;
		}
		return -1D;
	}

	private String stripFormatting(final String str) {
		if (str != null && !str.isEmpty()) {
			return str.replaceAll("(^[^<]+>|<[^>]+>|<[^>]+$)", "");
		}
		return "";
	}

	/**
	 * Provides access to GEItem Information.
	 */
	public static class GEItem {               
		private final String name;
		private final String examine;

		private final int id;

		private final int guidePrice;

		private final double change30;
		private final double change90;
		private final double change180;

		GEItem(final String name, final String examine, final int id, final double[] values) {
			this.name = name;
			this.examine = examine;
			this.id = id;
			guidePrice = (int) values[0];
			change30 = values[1];
			change90 = values[2];
			change180 = values[3];
		}

		/**
		 * Gets the change in price for the last 30 days of this item.
		 *
		 * @return The change in price for the last 30 days of this item.
		 */
		public double getChange30Days() {
			return change30;
		}

		/**
		 * Gets the change in price for the last 90 days of this item.
		 *
		 * @return The change in price for the last 90 days of this item.
		 */
		public double getChange90Days() {
			return change90;
		}

		/**
		 * Gets the change in price for the last 180 days of this item.
		 *
		 * @return The change in price for the last 180 days of this item.
		 */
		public double getChange180Days() {
			return change180;
		}

		/**
		 * Gets the ID of this item.
		 *
		 * @return The ID of this item.
		 */
		public int getID() {
			return id;
		}

		/**
		 * Gets the market price of this item.
		 *
		 * @return The market price of this item.
		 */
		public int getGuidePrice() {
			return guidePrice;
		}

		/**
		 * Gets the name of this item.
		 *
		 * @return The name of this item.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the description of this item.
		 *
		 * @return The description of this item.
		 */
		public String getDescription() {
			return examine;
		}
	}
        
        private static interface GECollectMethods {
            public int getInterface();
            
            public int getLeftCollect();
            
            public int getRightCollect();
            
            public boolean collectBoth();
            
            public boolean collectAll();
            
            public boolean isOpen();
            
            public boolean open();
            
            public boolean close();
        }
        
        private static class GECollect implements GECollectMethods {
            private int Interface = 0;
            private int leftCollect = 0;
            private int rightCollect = 0;
            private final int COLLECT_CLOSE = 14;
            
            public GECollect(int slot) {
                SLOT = slot;
                switch (slot) {            
                    case 1:
                       Interface = 19;
                       leftCollect = 0;
                       rightCollect = 2;
                       break;

                    case 2:
                        Interface = 23;
                        leftCollect = 0;
                        rightCollect = 2;
                        break;

                    case 3:
                        Interface = 27;
                        leftCollect = 0;
                        rightCollect = 2;
                        break;

                    case 4:
                        Interface = 32;
                        leftCollect = 0;
                        rightCollect = 2;
                        break;

                    case 5:
                        Interface = 37;
                        leftCollect = 0;
                        rightCollect = 2;
                        break;

                    case 6:
                        Interface = 42;
                        leftCollect = 0;
                        rightCollect = 2;
                        break;
                }                
            }

            public GECollect() {
                
            }
            @Override
            public int getInterface() {
                return this.Interface;
            }

            @Override
            public int getLeftCollect() {
                return this.leftCollect;
            }

            @Override
            public int getRightCollect() {
                return this.rightCollect;
            }

            @Override
            public boolean collectBoth() {
                if (isOpen()) {
                    if (Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(leftCollect).getActions() != null && Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(leftCollect).getActions().length >= 1) {
                        Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(leftCollect).click();
                        Task.sleep(Task.random(300, 500));
                    }                    
                    if (Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(rightCollect).getActions() != null && Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(rightCollect).getActions().length >= 1) {
                        Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(rightCollect).click();
                        Task.sleep(Task.random(300, 500));
                    }
                   close();
                   return true;
                }
                return false;
            }


            @Override
            public boolean isOpen() {
                return Interfaces.get(COLLECT_INTERFACE).isValid();
            }

            @Override
            public boolean open() {
                if (!isOpen()) {
                    Interactable i = new Npc("Collect ", BANKERS);
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

            @Override
            public boolean collectAll() {
                if (isOpen()) {
                    int boxToCollect;
                    boxToCollect = Ge.getTotalSlots();
                    for (int i = 1; i <= boxToCollect;) {
                        GECollectMethods k = new GECollect(i);
                        int inter = k.getInterface();
                        int left = k.getLeftCollect();
                        int right = k.getRightCollect();
                        if (Interfaces.getComponent(COLLECT_INTERFACE, inter).getComponent(left).getActions() != null && Interfaces.getComponent(COLLECT_INTERFACE, inter).getComponent(left).getActions().length >= 1) {
                            Interfaces.getComponent(COLLECT_INTERFACE, inter).getComponent(left).click();
                            Task.sleep(Task.random(300, 500));
                        }  
                        if (Interfaces.getComponent(COLLECT_INTERFACE, inter).getComponent(right).getActions() != null && Interfaces.getComponent(COLLECT_INTERFACE, inter).getComponent(right).getActions().length >= 1) {
                            Interfaces.getComponent(COLLECT_INTERFACE, inter).getComponent(right).click();
                            Task.sleep(Task.random(300, 500));
                        }                       
                        i++;
                    }
                    close();
                    return true;
                }
                return false;
            }            
            
            @Override
            public boolean close() {
                if (isOpen()) {
                    Interfaces.getComponent(COLLECT_INTERFACE, COLLECT_CLOSE).click();
                    Task.sleep(Task.random(700, 900));
                }
                return true;
            }
            
        }
        
        private static interface GEBuyMethods {
            public int getInterface();
            
            public int getBuyClick();
            
            public int getSellClick();
            
            public boolean isOfferCompleted(int slotNumber);
        }
        
        private static class GEBuy implements GEBuyMethods {
            private int Interface = 0;
            private int buyClick = 0;
            private int sellClick = 0;
            
            public GEBuy(int slot) {
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

            @Override
            public boolean isOfferCompleted(int slot) {
                if (Ge.isOpen()) {
                    if (Interfaces.getComponent(GE_INTERFACE, getInterface()).getComponent(13) != null && Interfaces.getComponent(GE_INTERFACE, getInterface()).getComponent(13).getHeight() != 13 && Interfaces.getComponent(GE_INTERFACE, getInterface()).getComponent(13).getWidth() != 124) {
                        return false;
                    }
                    if (Interfaces.getComponent(GE_INTERFACE, getInterface()).getComponent(13) != null && Interfaces.getComponent(GE_INTERFACE, getInterface()).getComponent(13).getHeight() == 13 && Interfaces.getComponent(GE_INTERFACE, getInterface()).getComponent(13).getWidth() == 124) {
                        return true;
                    }                    
                }
                return false;                
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

            @Override
		public int getDistance() {
                    return Calculations.distanceTo(npc);
		}

            @Override
		public boolean interact() {
                    Mouse.click(npc.getPoint(), false);
                    if (npc.getName().equals("Banker")) {
                        return Menu.isOpen() && Menu.clickIndex(Menu.getIndex(action));  
                    }
                    return Menu.isOpen() && Menu.clickIndex(Menu.getIndex(action) + 1);
                    //return npc.interact(action);
		}

            @Override
		public boolean isOnScreen() {
			return npc.isOnScreen();
		}

            @Override
		public boolean isValid() {
			return npc != null;
		}
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
}