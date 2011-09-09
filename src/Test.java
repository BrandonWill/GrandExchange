import org.rsbot.gui.AccountManager;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.concurrent.Task;
import org.rsbot.script.methods.*;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.Item;
import org.rsbot.script.wrappers.NPC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@ScriptManifest(authors = { "Dwarfeh" }, keywords = { "test" }, name = "aaaa IM FIRST", description = "Trololol", version = 1.0)
public class Test extends Script {
    boolean done = false;
    int buyPrice = 19;
    int buyAmount = 1;
    int sellAmount = 1;
    int sellPrice = buyPrice+1;
    String item23 = "Steel arrow"; //"Strength potion (4)"
    
    public boolean canBuy() {
      Item Coins = Inventory.getItem(995);
      if (Inventory.contains(995)) {
        if (Coins.getStackSize() >= buyPrice*buyAmount) { 
            return true;
        }
        if (Coins.getStackSize() < buyPrice*buyAmount) { 
            return false; 
        } 
      }
      return false;
    }   
    
    @Override
    public int loop() {
        if (!Ge.bankCollectIsOpen()) {
            Ge.bankCollectClose();
        }        
        if (!Ge.isOpen()) {
            Ge.open();
        }
        if (Ge.isOpen()) {
            int t = Ge.getEmptySlot();
            if (t >0) {
                if (canBuy()) {
                    Item Coins = Inventory.getItem(995);
                    buyAmount = Coins.getStackSize()/buyPrice/Ge.getAllEmptySlots();
                    Ge.buy(item23, t, buyAmount, buyPrice);
                }
                if (!canBuy() && Inventory.contains(item23)) {
                    Item Coins = Inventory.getItem(Ge.getItemID(item23));
                    if (Inventory.contains(item23) && Ge.isOpen()) {
                        sellAmount = Coins.getStackSize()/Ge.getAllEmptySlots();
                    }
                    Ge.sell(item23, t, sellAmount, 20); 
                }
                if (!canBuy() && !Inventory.contains(item23)) {
                    for(int i = 1; i <= Ge.getTotalSlots();) {
                        Mouse.moveSlightly();
                        Task.sleep(Task.random(700, 1000)); 
                        if (Ge.isAnOfferCompleted()) {
                            if (Ge.isOpen()) {
                                Ge.close();
                            }
                            if (!Ge.bankCollectIsOpen()) {
                                Ge.bankCollectOpen();
                            }
                            if (Ge.bankCollectIsOpen()) {
                                Ge.bankCollectAll();
                            }
                        }
                        i++;
                    }
                }                
            } else {
                for(int i = 1; i <= Ge.getTotalSlots();) {
                    Mouse.moveSlightly();
                    Task.sleep(Task.random(700, 1000));
                    if (Ge.isAnOfferCompleted()) {
                        if (Ge.isOpen()) {
                            Ge.close();
                        }
                        if (!Ge.bankCollectIsOpen()) {
                            Ge.bankCollectOpen();
                        }
                        if (Ge.bankCollectIsOpen()) {
                            Ge.bankCollectAll();
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
            itemName = itemName.substring(0,1).toUpperCase() + itemName.substring(1);            
            String Sep[] = itemName.split(" ");
            String searchName = null;
            for (int i = 0; i < Sep.length;) {
                if (!Sep[i].contains("(")) {
                    if (searchName == null) {
                        searchName = Sep[i];
                    } else {
                        searchName += " "+Sep[i];
                    }
                }
                i++;
            }
            if (slotNumber == 0 || slotNumber > 5) {
                return false;
            }
            if (isOpen()) {
                GEBuyMethods t = new GEBuy(slotNumber);
                int buyClick = t.getBuyClick();
                if (!isSearching()) {
                    Interfaces.getComponent(GE_INTERFACE, buyClick).click();
                    log.severe("BUY: Not searching, clicking!");
                    Task.sleep(Task.random(700, 900));
                }
                if (isSearching() && !hasSearched(searchName)) {
                    Keyboard.sendTextInstant(searchName, true);
                    log.severe("BUY: searching, has not searched!");
                    Task.sleep(Task.random(1000, 1500));
                }
                if (isSearching() && hasSearched(searchName)) {
                    log.severe("Inside HERE!");
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
                        changePrice = price > 1;
                        int times = 0;
                        while (changeQuantity) {
                            if (times >= 3) {
                                Ge.close();
                                return false;
                            }
                            if (isOpen()) {
                                if (Interfaces.getComponent(GE_INTERFACE, 168).getText() != null && Interfaces.getComponent(GE_INTERFACE, 168).getText().contains("...")) {
                                    Interfaces.getComponent(GE_INTERFACE, 168).click();
                                    Task.sleep(Task.random(700, 900));
                                } 
                                if (Interfaces.getComponent(752, 4).getText().contains("you wish to purchase")) {
                                    Keyboard.sendTextInstant("" +quantity, true);
                                }                            
                                Task.sleep(Task.random(1000, 2000));
                                if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains("" +formatNumb(quantity))) {
                                    changeQuantity = false;
                                    Task.sleep(Task.random(700, 900));
                                }
                            } else {
                                return false;
                            }
                            times++;
                        }
                        times = 0;
                        log.severe("BUY: Quan: " +changeQuantity +" Price: " +changePrice);
                        while (changePrice && !changeQuantity) {
                            if (times >= 3) {
                                Ge.close();
                                return false;
                            }
                            if (isOpen()) {
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
                                if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().contains("" +formatNumb(price))) {
                                    changePrice = false;
                                    Task.sleep(Task.random(700, 900));
                                }
                            } else {
                                return false;
                            }
                            times++;
                        }
                        if (!changePrice && !changeQuantity) {
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
                Ge.close();
                return false;
            }
            return false;
        }
        
        public static boolean sell(String itemName, int slotNumber, int quantity, int price) {
            SLOT = slotNumber;
            if (slotNumber == 0 || slotNumber > 5) {
                return false;
            }
            if (!Inventory.contains(itemName)) {
                return false;
            }            
            if (isOpen()) {
                GEBuyMethods t = new GEBuy(slotNumber);
                int sellClick = t.getSellClick();
                boolean offerItem = false;
                boolean offeredItem = false;
                if (!isSelling()) {
                    Interfaces.getComponent(GE_INTERFACE, sellClick).click();
                    log.severe("SELL: Not searching, clicking!");
                    Task.sleep(Task.random(700, 900));
                    offerItem = true;                    
                }
                if (!isSelling() && offerItem) {
                    log.severe("SELL: searching, has not searched!");
                    Inventory.getItem(itemName).click(true);
                    Task.sleep(Task.random(300, 500));
                    offeredItem = true;
                }
                if (isSelling()) {
                    Ge.close();
                    return false;
                }
                log.severe("isSellin :" +isSelling());
                if (!isSelling() && offeredItem) {                        
                    boolean changeQuantity;
                    changeQuantity = quantity > 1;
                    boolean changePrice;
                    changePrice = price > 1;
                    int times = 0;
                    while (changeQuantity) {
                        if (times == 3) {
                            Ge.close();
                            return false;
                        }
                        if (isOpen()) {
                            if (Interfaces.getComponent(GE_INTERFACE, 168).getText() != null && Interfaces.getComponent(GE_INTERFACE, 168).getText().contains("...")) {
                                Interfaces.getComponent(GE_INTERFACE, 168).click();
                                Task.sleep(Task.random(700, 900));
                            }                       
                            if (Interfaces.getComponent(752, 4).getText().contains("amount you wish to")) {
                                Keyboard.sendTextInstant("" +quantity, true);
                            }
                            Task.sleep(Task.random(1000, 2000));
                            if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains("" +formatNumb(quantity))) {
                                changeQuantity = false;
                            }
                        } else {
                            return false;
                        }
                        times++;
                    }
                    times = 0;
                    log.severe("SELL: Quan: " +changeQuantity +" Price: " +changePrice);
                    while (changePrice && !changeQuantity) {
                        if (times == 3) {
                            Ge.close();
                            return false;
                        }
                        if (isOpen()) {
                            Task.sleep(Task.random(700, 900));
                            if (Interfaces.getComponent(GE_INTERFACE).getComponent(177) != null) {
                                Interfaces.getComponent(GE_INTERFACE).getComponent(177).click();
                                Task.sleep(Task.random(700, 900));
                            } else {
                                Mouse.move(387, 222);
                                Mouse.click(true);
                                Task.sleep(Task.random(700, 900));
                            }
                            if (Interfaces.getComponent(752, 4).getText().contains("you wish to sell")) {
                                Keyboard.sendTextInstant("" +price, true);
                            }
                            Task.sleep(Task.random(1000, 2000));
                            if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().contains("" +formatNumb(price))) {
                                changePrice = false;
                                Task.sleep(Task.random(500, 700));
                            }
                        } else {
                            return false;
                        }
                        times++;
                    }
                    if (!changeQuantity && !changePrice) {
                        Task.sleep(Task.random(700, 900));
                        if (Interfaces.getComponent(GE_INTERFACE).getComponent(185) != null) {
                            Interfaces.getComponent(GE_INTERFACE).getComponent(186).click();
                        } else {
                            Mouse.move(262, 297);
                            Mouse.click(true);
                        }        
                    }
                    Ge.close();
                    return true;                    
                }
                Ge.close();
                return false;
            }
            return false;
        }        
                
        private static boolean isSelling() {
            return Interfaces.getComponent(GE_INTERFACE, 142).isValid() && !Interfaces.getComponent(GE_INTERFACE, 142).getText().equals("Choose an item to exchange");
        }
                
        /**Sets the number format as the same as GrandExchange's
         * 
         * @param money GrandExchange's money
         * @return number to match GrandExchange's
         */
        private static String formatNumb(long money) {
            return new DecimalFormat("###,###,###,###,###,###").format(money);
	}
        
        /**Determines membership
         * 
         * @return <tt>true</tt> if members is selected for the account; otherwise <tt>false</tt>
         */
        public static boolean isMember() {
            return AccountManager.isMember(Account.getName());
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
        
        /**Checks to see if the GE slot is empty
         * 
         * @param slot gets the correct interface
         * @return <tt>true</tt> if empty; otherwise <tt>false</tt>
         */
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
        
        /**Will determine the total amount of empty slots
         * 
         * @return total amount of empty slots
         */
        public static int getAllEmptySlots() {
            if (isOpen()) {
                int total = 0;
                for (int i = 1; i <= getTotalSlots();) {
                    SLOT = i;
                    GEBuyMethods check2 = new GEBuy(i);
                    int check = check2.getInterface();
                    if (Interfaces.getComponent(GE_INTERFACE, check).getComponent(10).getText().equals("Empty")) {
                        total++;
                    }
                    if (i == getTotalSlots()) {
                        return total;
                    }
                    i++;
                }
            }        
            return 0;    
        }
        
        /**Checks for nearest empty slot
         * 
         * @return an empty spot in the GE if there is one. If not, 0
         */
        public static int getEmptySlot() {
            if (isOpen()) {
                for (int i = 1; i <= getTotalSlots();) {
                    SLOT = i;
                    GEBuyMethods check2 = new GEBuy(i);
                    int check = check2.getInterface();
                    if (Interfaces.getComponent(GE_INTERFACE, check).getComponent(10).getText().equals("Empty")) {
                        return i;
                    }                  
                    i++;
                }
                return 0;
            }
            return 0;
        }                  
        
        /**Determines if an offer is completed or not
         * 
         * @return <tt>true</tt> if an offer is completed; otherwise <tt>false</tt>
         */
        public static boolean isAnOfferCompleted() {
            GEBuyMethods check2 = new GEBuy();
            return check2.isAnOfferCompleted();
        }
        
        /**Gets the completion percent for a slot
         * 
         * @param slot gets slot Number
         * @return slot's completion percentage
         */
        public static double getCompletionPercent(int slot) {
            GEBuyMethods check2 = new GEBuy(slot);
            return check2.getCompletionPercent();
        }
        
        /**Gets the approximate bought amount for a slot
         * 
         * @param slot gets slot Number
         * @return slot's approximate bought amount
         */
        public static int getApproximateAmount(int slot) {
            GEBuyMethods check2 = new GEBuy(slot);
            return check2.getApproximateAmount();
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
         * @param itemName is defined by buy/sell item
         * @return <tt>true</tt> if they have searched; otherwise <tt>false</tt>
         */
        public static boolean hasSearched(String itemName) {
            if (!Character.isUpperCase(itemName.charAt(0))) {
                Character.toUpperCase(itemName.charAt(0));
            }
            return Interfaces.getComponent(GE_INTERFACE, 142).getText().contains(itemName) || Interfaces.getComponent(SEARCH, 4).getComponent(1) != null && Interfaces.getComponent(SEARCH, 9).getText().contains(itemName);
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
        public static int bankGetInterface(int slot) {
            Ge.BankCollectMethods collect = new Ge.BankCollect(slot);
            return collect.getBankInterface();
        }

        /**Gets the left interface for the slot
         * 
         * @param slot determines which one to take from
         * @return left interface for the slot
         */
        public static int bankGetLeftInterface(int slot) {
            Ge.BankCollectMethods collect = new Ge.BankCollect(slot);
            return collect.getBankLeftCollect();
        }

        /**Gets the right interface for the slot
         * 
         * @param slot determines which one to take from
         * @return right interface for the slot
         */
        public static int bankGetRightInterface(int slot) {
            Ge.BankCollectMethods collect = new Ge.BankCollect(slot);
            return collect.getBankRightCollect();
        }            
        
        /**Collects from slot
         * 
         * @param slot to choose from
         * @return <tt>true</tt> if collected successfully; otherwise <tt>false</tt>
         */
        public static boolean bankCollectBoth(int slot) {
            Ge.BankCollectMethods collect = new Ge.BankCollect(slot);
            return collect.bankCollectBoth();
        }
            
        /**Collects everything from the interface
         * 
         * @return <tt>true</tt> if collected all successfully; otherwise <tt>false</tt>
         */
        public static boolean bankCollectAll() {
            Ge.BankCollectMethods collect = new Ge.BankCollect();
            return collect.bankCollectAll();
        }
        
        /**Opens collection interface
         * 
         * @return <tt>true</tt> if opened successfully; otherwise <tt>false</tt>
         */
        public static boolean bankCollectOpen() {
            Ge.BankCollectMethods collect = new Ge.BankCollect();
            return collect.bankOpen();
        }
        
        /**Closes collection interface
         * 
         * @return <tt>true</tt> if closed successfully; otherwise <tt>false</tt>
         */
        public static boolean bankCollectClose() {
            Ge.BankCollectMethods collect = new Ge.BankCollect();
            return collect.bankClose();
        }        
        
        /**Checks collection interface
         * 
         * @return <tt>true</tt> if opened; otherwise <tt>false</tt>
         */
        public static boolean bankCollectIsOpen() {
            Ge.BankCollectMethods collect = new Ge.BankCollect();
            return collect.bankIsOpen();
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
        
        /**Opens the GrandExchange
         * 
         * @return <tt>true</tt> if open; otherwise <tt>false</tt>
         */
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
	 * @see Ge#lookup(int)
	 */
	public static String getItemName(final int itemID) {
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
	 * @see Ge#lookup(java.lang.String)
	 */
	public static int getItemID(final String itemName) {
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
	public static GEItem lookup(final int itemID) {
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
	public static GEItem lookup(final String itemName) {
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

	private static double parse(String str) {
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

	private static String stripFormatting(final String str) {
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
        
        private static interface BankCollectMethods {
            public int getBankInterface();
            
            public int getBankLeftCollect();
            
            public int getBankRightCollect();
            
            public boolean bankCollectBoth();
            
            public boolean bankCollectAll();
            
            public boolean bankIsOpen();
            
            public boolean bankOpen();
            
            public boolean bankClose();
        }
        
        private static class BankCollect implements BankCollectMethods {
            private int Interface = 0;
            private int leftCollect = 0;
            private int rightCollect = 0;
            private final int COLLECT_CLOSE = 14;
            
            public BankCollect(int slot) {
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

            public BankCollect() {
                
            }

            public int getBankInterface() {
                return this.Interface;
            }

            public int getBankLeftCollect() {
                return this.leftCollect;
            }

            public int getBankRightCollect() {
                return this.rightCollect;
            }

            public boolean bankCollectBoth() {
                if (bankIsOpen()) {
                    if (Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(leftCollect).getActions() != null && Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(leftCollect).getActions().length >= 1) {
                        Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(leftCollect).click();
                        Task.sleep(Task.random(300, 500));
                    }                    
                    if (Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(rightCollect).getActions() != null && Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(rightCollect).getActions().length >= 1) {
                        Interfaces.getComponent(COLLECT_INTERFACE, Interface).getComponent(rightCollect).click();
                        Task.sleep(Task.random(300, 500));
                    }
                   bankClose();
                   return true;
                }
                return false;
            }

            public boolean bankIsOpen() {
                return Interfaces.get(COLLECT_INTERFACE).isValid();
            }

            public boolean bankOpen() {
                if (!bankIsOpen()) {
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
                            for (int j = 0; j < 10 && !bankIsOpen(); j++) {
                                    Task.sleep(Task.random(100, 200));
                            }
                            // Ensures that the widget becomes valid
                            Task.sleep(Task.random(700, 900));
                            return bankIsOpen();
                    } else {
                            return false;
                    }
                }
                return true;
            }

            public boolean bankCollectAll() {
                if (bankIsOpen()) {
                    int boxToCollect;
                    boxToCollect = Ge.getTotalSlots();
                    for (int i = 1; i <= boxToCollect;) {
                        BankCollectMethods k = new BankCollect(i);
                        int inter = k.getBankInterface();
                        int left = k.getBankLeftCollect();
                        int right = k.getBankRightCollect();
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
                    bankClose();
                    return true;
                }
                return false;
            }            

            public boolean bankClose() {
                if (bankIsOpen()) {
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
            
            public boolean isAnOfferCompleted();
            
            public double getCompletionPercent();
            
            public int getApproximateAmount();
        }
        
        private static class GEBuy implements GEBuyMethods {
            private int Interface = 0;
            private int buyClick = 0;
            private int sellClick = 0;
            private int completeWidth = 124;
            private int height = 13;
            private int COMPLETION_BAR_INTERFACE = 13;
            private int STACKSIZE_INTERFACE = 17;
            
            
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
            
            public GEBuy() {
                
            }

            public int getInterface() {
                return this.Interface;
            }

            public int getBuyClick() {
                return this.buyClick;
            }

            public int getSellClick() {
                return this.sellClick;
            }

            public boolean isAnOfferCompleted() {
                if (Ge.isOpen()) {
                    int boxToCollect = Ge.getTotalSlots();
                    for (int i = 1; i <= boxToCollect;) {
                        GEBuyMethods k = new GEBuy(i);
                        int inter = k.getInterface();                
                        if (Interfaces.getComponent(GE_INTERFACE, inter).getComponent(COMPLETION_BAR_INTERFACE) != null && Interfaces.getComponent(GE_INTERFACE, inter).getComponent(COMPLETION_BAR_INTERFACE).getHeight() == height && Interfaces.getComponent(GE_INTERFACE, inter).getComponent(COMPLETION_BAR_INTERFACE).getWidth() == completeWidth) {
                            return true;
                        }
                        i++;
                    }                    
                }
                return false;                
            }

            public double getCompletionPercent() {
                if (Interfaces.getComponent(GE_INTERFACE, Interface).getComponent(COMPLETION_BAR_INTERFACE) != null) {
                    double numb = Interfaces.getComponent(GE_INTERFACE, Interface).getComponent(COMPLETION_BAR_INTERFACE).getWidth();
                    return numb/completeWidth;
                }
                return 0;
            }

            public int getApproximateAmount() {
                if (Interfaces.getComponent(GE_INTERFACE, Interface).getComponent(STACKSIZE_INTERFACE) != null) {
                    int numb = Interfaces.getComponent(GE_INTERFACE, Interface).getComponent(STACKSIZE_INTERFACE).getComponentStackSize();
                    return (int) (numb*getCompletionPercent());
                }
                return 0;
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
                    if (npc.getName().equals("Banker")) {
                        return Menu.isOpen() && Menu.clickIndex(Menu.getIndex(action));
                    }
                    return Menu.isOpen() && Menu.clickIndex(Menu.getIndex(action) + 1);
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