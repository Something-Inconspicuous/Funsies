package unit6;

import java.util.Random;

// Don't ask what game...
public class Player {
    private static final Random rng = new Random();

    // These are just randomly generated
    private static final String[] firstNames = {
        "Shawn", "Kiley", "Ellis", "Damian", "John", 
        "Jefferson", "Isaac", "Nicky", "Brielle", 
        "Flora", "Trace", "Liam", "Ellie", "Jayla", 
        "Lillian", "Graham", "Abigail", "Bella", 
        "Sasha", "Ashton", "Althea", "Kelsey", 
        "Rocco", "Knox", "Jarrett", "Jesse", "Jamey", 
        "Bronwen", "Bennet", "Lilah", "Ramsey", 
        "Leontine", "Ianthe", "Pascal", "Rowan", 
        "Storm", "Christian", "Thaddeus", "Josephine", 
        "Rafael", "Kaye", "Dallas", "Corvo", "John"
    };
    private static final String[] lastNames = {
        "Hartley", "Hopkins", "Greer", "Clayton", "Peck", 
        "Martin", "Washington", "Justice", "Cochrane", 
        "Greer", "Lynn", "Fitzgerald", "Mitchell", 
        "Mitchell", "Lamb", "Day", "Turner", "Blair", 
        "Davidson", "Lancaster", "Dillard", "Manning", 
        "Higgins", "Voss", "Jenkins", "Johnson", "Kearney", 
        "Ward", "Richardson", "McNeil", "Jones", "Robson", 
        "Vogel", "Chambers", "Gray", "Wilkes", "Dunbar", 
        "Wallace", "Johnson", "Doran", "Noble", "Bates", 
        "Norwood", "Ellsworth", "Sanchez", "O'Brien", 
        "Attano", "Baldwin"
    };

    private String firstName;
    private String lastName;

    private double winRate;

    public static String randomFirstName() {
        return randomChoice(rng, firstNames);
    }

    public static String randomLastName() {
        return randomChoice(rng, lastNames);
    }
    
    public static String randomName() {
        return randomFirstName() + " " + randomLastName();
    }

    public static double randomWinRate() {
        // maybe gaussian?
        return rng.nextDouble(0.0, 1.0);
    }

    private static <T> T randomChoice(Random rng, T[] array) {
        int n = array.length;
        return array[rng.nextInt(n)];
    }

    public Player(String firstName, String lastName, double winRate) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.winRate = winRate;
    }
    
    public Player(String firstName, String lastName) {
        this(firstName, lastName, randomWinRate());
    }

    public Player(String name, double winRate) {
        int spaceIndex = name.indexOf(" ");
        this.firstName = name.substring(0, spaceIndex);
        this.lastName = name.substring(spaceIndex + 1);
        this.winRate = winRate;
    }
    public Player(String name) {
        this(name, randomWinRate());
    }

    public Player(double winRate) {
        this(randomFirstName(), randomLastName(), winRate);
    }

    public Player() {
        this(randomFirstName(), randomLastName(), randomWinRate());
    }

    public static Player random() {
        return new Player();
    }

    public static Random getRng() {
        return rng;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        long temp;
        temp = Double.doubleToLongBits(winRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (Double.doubleToLongBits(winRate) != Double.doubleToLongBits(other.winRate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "%s, %s: %3.2f%%".formatted(lastName, firstName, winRate * 100.0);
    }
}
