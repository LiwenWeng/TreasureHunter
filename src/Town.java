/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String treasure;
    private boolean searched;
    private boolean dug;
    private String printMessage;
    private boolean toughTown;
    private boolean easyMode;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, boolean easyMode) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        this.searched = false;
        this.dug = false;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        this.easyMode = easyMode;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        this.treasure = Treasure.randomTreasure();
        this.searched = false;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak() && !easyMode) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        printMessage = "You left the shop";
    }

    public void lookForTreasure() {
        if (searched) {
            printMessage = "You have already searched this town";
            return;
        }

        searched = true;
        if (!treasure.equals("Dust")) {
            boolean added = hunter.addItemToTreasureList(treasure);
            if (!added) {
                printMessage = "You have already collected " + treasure;
            } else {
                printMessage = "You found a " + this.treasure + "!";
            }
        } else {
            printMessage = "You found " + this.treasure + "!";
        }
    }

    public void digForTreasure() {
        if (dug) {
            System.out.println("You already dug for gold in this town.");
            return;
        }

        if (!hunter.hasItemInKit("shovel")) {
            System.out.println("You can't dig for gold without a shovel");
        } else {
            double rand = Math.random();
            if (rand < 0.5) {
                dug = true;
                int goldAmt = (int) (Math.random() * 21);
                hunter.changeGold(goldAmt);
                System.out.println("You dug up " + goldAmt + " gold!");
            } else {
                System.out.println("You dug but only found dirt");
            }
        }
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (easyMode) {
            if (toughTown) {
                noTroubleChance = 0.50;
            } else {
                noTroubleChance = 0.25;
            }
        } else {
            if (toughTown) {
                noTroubleChance = 0.66;
            } else {
                noTroubleChance = 0.33;
            }
        }


        if (Math.random() > noTroubleChance) {
            printMessage = Colors.color("You couldn't find any trouble", "Red");
        } else {
            printMessage = Colors.color("You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n", "Red");
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.hasItemInKit("sword")) {
                printMessage += "the brawler, seeing your sword, realizes he picked a losing fight and gives you his gold";
                printMessage += "\nYou won the brawl and receive " + Colors.color(goldDiff + " gold", "Yellow") + ".";
                hunter.changeGold(goldDiff);
            } else if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.color(goldDiff + " gold", "Yellow") + ".";
                hunter.changeGold(goldDiff);
            } else {
                printMessage += Colors.color("That'll teach you to go lookin' fer trouble in MY town! Now pay up!", "Red");
                printMessage += "\nYou lost the brawl and pay " + Colors.color(goldDiff + " gold", "Yellow") + ".";
                hunter.changeGold(-goldDiff);

            }
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < (1.0/6)) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < (2.0/6)) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < (3.0/6)) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < (4.0/6)) {
            return new Terrain("Desert", "Water");
        } else if (rnd < (5.0/6)) {
            return new Terrain("Marsh", "Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}