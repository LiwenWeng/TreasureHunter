public class Treasure {
    private static String[] treasures = {
            "Crown",
            "Trophy",
            "Gem",
            "Dust"
    };

    public static String randomTreasure() {
        return treasures[(int) (Math.random() * treasures.length)];
    }
}