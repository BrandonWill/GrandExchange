import org.rsbot.bot.concurrent.Task;
import org.rsbot.ui.AccountManager;
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
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrandExchange {
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

    /**
     * Buys items from the Grand Exchange if it's open
     *
     * @param itemName   item to buy
     * @param slotNumber slot number to buy from (1-5)
     * @param quantity   amount to buy
     * @param price      Price to buy from. 0 will leave it as default price
     * @return <tt>true</tt> if bought successfully; otherwise <tt>false</tt>
     */
    public static boolean buy(String itemName, int slotNumber, int quantity, int price) {
        SLOT = slotNumber;
        itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1).toLowerCase();
        String Sep[] = itemName.split(" ");
        String searchName = null;
        for (int i = 0; i < Sep.length; ) {
            if (!Sep[i].contains("(")) {
                if (searchName == null) {
                    searchName = Sep[i];
                } else {
                    searchName += " " + Sep[i];
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
                //has to wait for search bar to come up
                Task.sleep(Task.random(900, 1000));
            }
            if (isSearching() && !hasSearched(searchName)) {
                Keyboard.sendTextInstant(searchName, true);
                //has to wait for list before it closes
                Task.sleep(Task.random(1000, 1500));
            }
            if (isSearching() && hasSearched(searchName)) {
                boolean foundItem = false;
                if (findItem() && !foundItem) {
                    boolean done = false;
                    int index = 0;
                    if (!done) {
                        for (int i = 0; Interfaces.getComponent(389, 4).getComponent(i) != null; ) {
                            if (Interfaces.getComponent(389, 4).getComponent(i).getText().equals(itemName)) {
                                index = i;
                            }
                            i++;
                            if (Interfaces.getComponent(389, 4).getComponent(i + 1) == null) {
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
                        }
                        if (Interfaces.getComponent(389, 4).getComponent(index).isVisible()) {
                            Interfaces.getComponent(389, 4).getComponent(index).click();
                            foundItem = true;
                        }
                    }
                }
                if (foundItem) {
                    boolean changeQuantity = quantity > 1;
                    boolean changePrice = price > 1;
                    int times = 0;
                    while (changeQuantity) {
                        if (times >= 3 || !isOpen()) {
                            close();
                            return false;
                        }
                        if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains(formatNumb(quantity))) {
                            changeQuantity = false;
                            break;
                        }
                        if (Interfaces.getComponent(GE_INTERFACE, 168).getText() != null && Interfaces.getComponent(GE_INTERFACE, 168).getText().contains("...")) {
                            Interfaces.getComponent(GE_INTERFACE, 168).click();
                            //has to wait for interface to show up
                            Task.sleep(Task.random(900, 1000));
                        }
                        if (Interfaces.getComponent(752, 4).getText().contains("you wish to purchase")) {
                            Keyboard.sendTextInstant(Integer.toString(quantity), true);
                        }
                        //prevents spam clicking
                        Task.sleep(Task.random(1000, 1500));
                        if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains(formatNumb(quantity))) {
                            changeQuantity = false;
                        }
                        times++;
                    }
                    times = 0;
                    while (changePrice && !changeQuantity) {
                        if (times >= 3 || !isOpen()) {
                            close();
                            return false;
                        }
                        if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().contains(formatNumb(price))) {
                            changePrice = false;
                            break;
                        }
                        if (Interfaces.getComponent(GE_INTERFACE, 168).getText() != null && Interfaces.getComponent(GE_INTERFACE, 168).getText().contains("...")) {
                            Interfaces.getComponent(GE_INTERFACE, 168).click();
                            //has to wait for interface to show up
                            Task.sleep(Task.random(700, 900));
                        }
                        if (Interfaces.getComponent(752, 4).getText().contains("you wish to buy")) {
                            Keyboard.sendTextInstant(Integer.toString(price), true);
                        }
                        //prevents spam clicking
                        Task.sleep(Task.random(1000, 1500));
                        if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().contains(formatNumb(price))) {
                            changePrice = false;
                        }
                        times++;
                    }
                    if (!changePrice && !changeQuantity) {
                        if (Interfaces.getComponent(GE_INTERFACE, 187).getText() != null) {
                            Interfaces.getComponent(GE_INTERFACE, 187).click();
                        }
                        close();
                        return true;
                    }
                }
            }
            close();
            return false;
        }
        return false;
    }

    /**
     * Sells items from the Grand Exchange if it's open
     *
     * @param itemName   item to sell
     * @param slotNumber slot number to sell from (1-5)
     * @param quantity   amount to sell
     * @param price      Price to sell from. 0 will leave it as default price
     * @return <tt>true</tt> if sold successfully; otherwise <tt>false</tt>
     */
    public static boolean sell(String itemName, int slotNumber, int quantity, int price) {
        SLOT = slotNumber;
        if (slotNumber == 0 || slotNumber > 5 || !Inventory.contains(itemName)) {
            return false;
        }
        if (isOpen()) {
            GEBuyMethods t = new GEBuy(slotNumber);
            int sellClick = t.getSellClick();
            boolean offerItem = false;
            boolean offeredItem = false;
            if (!isSelling()) {
                Interfaces.getComponent(GE_INTERFACE, sellClick).click();
                //waits for interface to be valid
                Task.sleep(Task.random(900, 1000));
                offerItem = true;
            }
            if (!isSelling() && offerItem) {
                Inventory.getItem(itemName).click(true);
                //waits for interface to be valid
                Task.sleep(Task.random(500, 700));
                offeredItem = true;
            }
            if (isSelling()) {
                close();
                return false;
            }
            if (!isSelling() && offeredItem) {
                boolean changeQuantity = quantity > 1;
                boolean changePrice = price > 1;
                int times = 0;
                while (changeQuantity) {
                    if (times == 3 || !isOpen()) {
                        close();
                        return false;
                    }
                    if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains(formatNumb(quantity))) {
                        changeQuantity = false;
                        break;
                    }
                    if (Interfaces.getComponent(GE_INTERFACE, 168).getText() != null && Interfaces.getComponent(GE_INTERFACE, 168).getText().contains("...")) {
                        Interfaces.getComponent(GE_INTERFACE, 168).click();
                        //has to wait for interface to show up
                        Task.sleep(Task.random(700, 900));
                    }
                    if (Interfaces.getComponent(752, 4).getText().contains("amount you wish to")) {
                        Keyboard.sendTextInstant(Integer.toString(quantity), true);
                    }
                    //prevents spam clicking
                    Task.sleep(Task.random(1000, 1500));
                    if (Interfaces.getComponent(GE_INTERFACE, 148).getText() != null && Interfaces.getComponent(GE_INTERFACE, 148).getText().contains(formatNumb(quantity))) {
                        changeQuantity = false;
                    }
                    times++;
                }
                times = 0;
                while (changePrice && !changeQuantity) {
                    if (times == 3 || !isOpen()) {
                        close();
                        return false;
                    }
                    if (Interfaces.getComponent(GE_INTERFACE, 177).getText() != null && Interfaces.getComponent(GE_INTERFACE, 168).getText().contains("...")) {
                        Interfaces.getComponent(GE_INTERFACE, 177).click();
                        //has to wait for interface to show up
                        Task.sleep(Task.random(700, 900));
                    }
                    if (Interfaces.getComponent(752, 4).getText().contains("you wish to sell")) {
                        Keyboard.sendTextInstant(Integer.toString(price), true);
                    }
                    //prevents spam clicking
                    Task.sleep(Task.random(1000, 1500));
                    if (Interfaces.getComponent(GE_INTERFACE, 153).getText() != null && Interfaces.getComponent(GE_INTERFACE, 153).getText().contains(formatNumb(price))) {
                        changePrice = false;
                    }
                    times++;
                }
                if (!changeQuantity && !changePrice) {
                    if (Interfaces.getComponent(GE_INTERFACE, 187).getText() != null) {
                        Interfaces.getComponent(GE_INTERFACE, 187).click();
                    }
                }
                close();
                return true;
            }
            close();
            return false;
        }
        return false;
    }

    /**
     * @return <tt>true</tt> if selling; otherwise <tt>false</tt>
     */
    private static boolean isSelling() {
        return Interfaces.getComponent(GE_INTERFACE, 142).isValid() && !Interfaces.getComponent(GE_INTERFACE, 142).getText().equals("Choose an item to exchange");
    }

    /**
     * Sets the number format as the same as GrandExchange's
     *
     * @param money GrandExchange's money
     * @return number to match GrandExchange's
     */
    private static String formatNumb(long money) {
        return new DecimalFormat("###,###,###,###,###,###").format(money);
    }

    /**
     * Determines membership
     *
     * @return <tt>true</tt> if members is selected for the account; otherwise <tt>false</tt>
     */
    public static boolean isMember() {
        return AccountManager.isMember(Account.getName());
    }

    /**
     * Gets the total slots there are for the person
     *
     * @return number of slots if account is member
     */
    public static int getTotalSlots() {
        return isMember() ? 6 : 2;
    }

    /**
     * Checks to see if the GE slot is empty
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

    /**
     * Will determine the total amount of empty slots
     *
     * @return total amount of empty slots
     */
    public static int getAllEmptySlots() {
        if (isOpen()) {
            int total = 0;
            for (int i = 1; i <= getTotalSlots(); ) {
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

    /**
     * Checks for nearest empty slot
     *
     * @return an empty spot in the GE if there is one. If not, 0
     */
    public static int getEmptySlot() {
        if (isOpen()) {
            for (int i = 1; i <= getTotalSlots(); ) {
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

    /**
     * Determines if an offer is completed or not
     *
     * @return <tt>true</tt> if an offer is completed; otherwise <tt>false</tt>
     */
    public static boolean isAnOfferCompleted() {
        GEBuyMethods check2 = new GEBuy();
        return check2.isAnOfferCompleted();
    }

    /**
     * Gets the completion percent for a slot
     *
     * @param slot gets slot Number
     * @return slot's completion percentage
     */
    public static double getCompletionPercent(int slot) {
        GEBuyMethods check2 = new GEBuy(slot);
        return check2.getCompletionPercent();
    }

    /**
     * Gets the approximate bought amount for a slot
     *
     * @param slot gets slot Number
     * @return slot's approximate bought amount
     */
    public static int getApproximateAmount(int slot) {
        GEBuyMethods check2 = new GEBuy(slot);
        return check2.getApproximateAmount();
    }

        /**
         * Loads up a hash table of all Runescape IDs
         *
         * @return true if the hash table is loaded/ is loading
         */
        public static boolean tableLoad() {
            itemTableMethods table = new itemTable();
            return table.loadTable();
        }

        /**
         * Gets the id from the name from the hash table
         *
         * @param name to lookup to get the ID
         * @return correct ID for the name; 0 if the ID can't be found
         */
        public static int tableGetID(String name) {
            itemTableMethods table = new itemTable();
            return table.getID(name);
        }

        /**
         * Gets the itemname from table rather than looking it up
         *
         * @param id to look up
         * @return Name for the id; null if it can't be found
         */
        public static String tableGetName(int id) {
            itemTableMethods table = new itemTable();
            return table.getName(id);
        }

    /**
     * Determines if there is an item by the name
     *
     * @return <tt>true</tt> if an item was found; otherwise <tt>false</tt>
     */
    public static boolean findItem() {
        return Interfaces.getComponent(SEARCH, 4).getComponent(0) != null && Interfaces.getComponent(SEARCH, 4).getComponent(0).getText() != null && !Interfaces.getComponent(SEARCH, 4).getComponent(0).getText().equals("No matching items found.");
    }

    /**
     * Determines if the person has searched or not
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

    /**
     * Determines if the player is searching
     *
     * @return <tt>true</tt> if interface is valid; otherwise <tt>false</tt>
     */
    public static boolean isSearching() {
        return Interfaces.getComponent(GE_INTERFACE, 134).isVisible();
    }

    /**
     * Checks whether the GE is open
     *
     * @return <tt>true</tt> if the GE interface is valid; otherwise <tt>false</tt>
     */
    public static boolean isOpen() {
        return getInterface().isValid();
    }

    /**
     * Gets the bank's interface.
     *
     * @return <tt>true</tt> if interface is valid
     */
    public static Interface getInterface() {
        return Interfaces.get(GE_INTERFACE);
    }

    /**
     * Gets the general interface for the slot
     *
     * @param slot determines which one to take from
     * @return interface for the slot
     */
    public static int bankGetInterface(int slot) {
        BankCollectMethods collect = new BankCollect(slot);
        return collect.getBankInterface();
    }

    /**
     * Gets the left interface for the slot
     *
     * @param slot determines which one to take from
     * @return left interface for the slot
     */
    public static int bankGetLeftInterface(int slot) {
        BankCollectMethods collect = new BankCollect(slot);
        return collect.getBankLeftCollect();
    }

    /**
     * Gets the right interface for the slot
     *
     * @param slot determines which one to take from
     * @return right interface for the slot
     */
    public static int bankGetRightInterface(int slot) {
        BankCollectMethods collect = new BankCollect(slot);
        return collect.getBankRightCollect();
    }

    /**
     * Collects from slot
     *
     * @param slot to choose from
     * @return <tt>true</tt> if collected successfully; otherwise <tt>false</tt>
     */
    public static boolean bankCollectBoth(int slot) {
        BankCollectMethods collect = new BankCollect(slot);
        return collect.bankCollectBoth();
    }

    /**
     * Collects everything from the interface
     *
     * @return <tt>true</tt> if collected all successfully; otherwise <tt>false</tt>
     */
    public static boolean bankCollectAll() {
        BankCollectMethods collect = new BankCollect();
        return collect.bankCollectAll();
    }

    /**
     * Opens collection interface
     *
     * @return <tt>true</tt> if opened successfully; otherwise <tt>false</tt>
     */
    public static boolean bankCollectOpen() {
        BankCollectMethods collect = new BankCollect();
        return collect.bankOpen();
    }

    /**
     * Closes collection interface
     *
     * @return <tt>true</tt> if closed successfully; otherwise <tt>false</tt>
     */
    public static boolean bankCollectClose() {
        BankCollectMethods collect = new BankCollect();
        return collect.bankClose();
    }

    /**
     * Checks collection interface
     *
     * @return <tt>true</tt> if opened; otherwise <tt>false</tt>
     */
    public static boolean bankCollectIsOpen() {
        BankCollectMethods collect = new BankCollect();
        return collect.bankIsOpen();
    }

    /**
     * Closes the GE
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


    /**
     * Opens the GrandExchange
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
     * @see GrandExchange#lookup(int)
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
     * @see GrandExchange#lookup(java.lang.String)
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

            final URL url = new URL(GrandExchange.HOST + GrandExchange.GET + itemID);
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
            final URL url = new URL(GrandExchange.HOST + "/m=itemdb_rs/results.ws?query=" + itemName + "&price=all&members=");
            final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            while ((input = br.readLine()) != null) {
                if (input.contains("<div id=\"search_results_text\">")) {
                    input = br.readLine();
                    if (input.contains("Your search for")) {
                        return null;
                    }
                } else if (input.startsWith("<td><img src=")) {
                    final Matcher matcher = GrandExchange.PATTERN.matcher(input);
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

    /**Searches Yahoo to grab the ID
     *
     * @param itemName item to search
     * @return possible id for the item
     */
    public static int searchYahooForID(final String itemName) {
        try {
            final URL url = new URL("http://search.yahoo.com/search;_ylt=?p=" + itemName.replace(" ", "+") + "+runescape+grand+exchange");
            final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String input;
            while ((input = br.readLine()) != null) {
                if(input.contains("viewitem.ws")) {
                    final int startIndex = input.indexOf("viewitem.ws") + 16;
                    final int endIndex = input.contains("&scale=") ? input.indexOf("&scale=", startIndex) : input.indexOf('"', startIndex);
                    return Integer.parseInt(input.substring(startIndex, endIndex));
                }
            }
        } catch (final IOException ignored) {
        }
        return 0;
    }

    private static interface itemTableMethods {
        public boolean loadTable();

        public int getID(String item);

        public String getName(int id);
    }

    public static class itemTable extends Thread implements itemTableMethods {
        private static final String TABLE_URL = "https://raw.github.com/Dwarfeh/GrandExchange/206093061353f57f5e192d45c64a15bf5b73fc2e/src/IDS";
        private static final Hashtable<String, Integer > itemStringList = new Hashtable<String, Integer>();
        private static final Hashtable<Integer, String > itemIDList = new Hashtable<Integer, String>();

        public boolean loadTable() {
            if (itemStringList.isEmpty() || itemIDList.isEmpty()) {
                itemLoader.start();
                return true;
            }
            return false;
        }

        public int getID(String item) {
            item = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
            try {
                return itemStringList.get(item);
            } catch (Exception e) {
                return 0;
            }
        }

        public String getName(int id) {
            try {
                return itemIDList.get(id);
            } catch (Exception e) {
                return null;
            }
        }

        final Thread itemLoader = new Thread(new Runnable() {


            public void run() {
                try {
                    URL url = new URL(TABLE_URL);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line, lines = "";
                    while((line = in.readLine()) != null) {
                        lines += line + "\n";
                        String word = line.toString();
                        String ItemNumber[] = line.split(":");
                        if (line.length() > 1 && !itemStringList.containsKey(ItemNumber[0])) {
                            itemStringList.put(ItemNumber[0], Integer.parseInt(ItemNumber[1]));
                            itemIDList.put(Integer.parseInt(ItemNumber[1]), ItemNumber[0]);
                        }
                        interrupt();
                    }
                    itemLoader.interrupt();
                } catch(IOException e) {
                    itemLoader.interrupt();
                }
                itemLoader.interrupt();
            }
        });
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
                boxToCollect = GrandExchange.getTotalSlots();
                for (int i = 1; i <= boxToCollect; ) {
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
                return true;
            }
            return !bankIsOpen();
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
        private final int completeWidth = 124;
        private final int height = 13;
        private final int COMPLETION_BAR_INTERFACE = 13;
        private final int STACKSIZE_INTERFACE = 17;


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
            if (GrandExchange.isOpen()) {
                int boxToCollect = GrandExchange.getTotalSlots();
                for (int i = 1; i <= boxToCollect; ) {
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
                return numb / completeWidth;
            }
            return 0;
        }

        public int getApproximateAmount() {
            if (Interfaces.getComponent(GE_INTERFACE, Interface).getComponent(STACKSIZE_INTERFACE) != null) {
                int numb = Interfaces.getComponent(GE_INTERFACE, Interface).getComponent(STACKSIZE_INTERFACE).getComponentStackSize();
                return (int) (numb * getCompletionPercent());
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
            Mouse.click(npc.getCentralPoint(), false);
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
