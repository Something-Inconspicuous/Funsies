package unit6;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

public class Zombie {
    private static Random rng = new Random();

    private int health;

    private int numDaysWaiting;

    private int numDaysTreated;
    
    public Zombie() {
        super();
        this.health = 100;
    }
    
    public boolean day() {
        // checked-in
        if(health < 30) {
            ++numDaysWaiting;
            return false;
        } else {
            int random = rng.nextInt(0, 100);
            boolean doesInfect = random < health / 2;
            int damageTaken = rng.nextInt(10, 40);
            health -= damageTaken;
            
            return doesInfect;
        }
    }

    public int getNumDaysWaiting() {
        return numDaysWaiting;
    }

    public int getNumDaysTreated() {
        return numDaysTreated;
    }
    
    public int getHealth() {
        return health;
    }

    public void heal() {
        this.health = 100;
        numDaysTreated = 0;
        numDaysWaiting = 0;
    }

    public void treat() {
        ++numDaysTreated;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int daysToTreat() {
        // 3 days for 1 - 9 health
        // 2 days for 10 - 19 health
        // 1 day for 20 - 29 health
        return 3 - (health / 10);
    }

    public static void main(String[] args) {
        final int numTrials = 1000;
        // pay no mind to the spaghetti behind the method
        simulateTrials(numTrials, true);
        // simulateTrials(numTrials, false);
        // simulateTrials(numTrials, false);
        // simulateTrials(numTrials, false);
        // simulateTrials(numTrials, false);
    }

    private static void simulateTrials(final int numTrials, boolean doLog) {
        if(doLog)
            simulateTrialsDoLog(numTrials);
        else 
            simulateTrialsNoLog(numTrials);
    }

    private static void simulateTrialsNoLog(final int numTrials) {
        int[] data = new int[numTrials];
        int sum = 0;
        for(int i = 0; i < numTrials; i++) {
            System.out.println("=======================================================");
            System.out.println("Trial " + i);
            int[] n = getNumDoctors(7);
            System.out.format("\tDoctors: %d\n\tMax Wait: %d\n\tZombies: %d\n", n[0], n[1], n[2]);
            sum += n[0];
            data[i] = n[0];
            maxDoctorsNeeded = Math.max(maxDoctorsNeeded, n[0]);
        }
        double mean = (double) sum / numTrials;

        double variance = 0;
        for(int i = 0; i < numTrials; i++) {
            double diff = data[i] - mean;
            variance += diff * diff;
        }
        variance /= numTrials - 1;

        System.out.println("=======================================================");
        System.out.println("Average doctors needed: " + mean);
        System.out.println("\tStandard Deviation: " + Math.sqrt(variance));
        System.out.println("Maximum doctors needed: " + maxDoctorsNeeded);
    }

    private static int[] getNumDoctors(int numDaysTolerence) {
        int numDoctors = 1;
        int numDaysWaiting = 0;
        int numZombies = 0;
        while (true) {
            // System.out.format("Testing %d doctors...\n", numDoctors);
            int[] n = simulateMonth(numDoctors, numDaysTolerence);
            numDaysWaiting = n[0];
            if(numDaysWaiting > numDaysTolerence) {
                // System.out.format("Failed: %d days waited by longest.\n", numDaysWaiting);
                numZombies = n[1];
            } else {
                // System.out.format("%d doctors work with wait-time=%d\n", numDoctors, numDaysWaiting);
                break;
            }
            ++numDoctors;
        }
        return new int[]{numDoctors, numDaysWaiting, numZombies};
    }

    public static int[] simulateMonth(int numDoctors, int maxDaysWaitingAllowed) {
        return simulate(30, numDoctors, maxDaysWaitingAllowed);
    }

    public static int[] simulate(int days, int numDoctors, int maxDaysWaitingAllowed) {
        Comparator<Zombie> comp = Comparator.comparingInt(Zombie::getHealth);
        PriorityQueue<Zombie> zombies = new PriorityQueue<>(comp);
        zombies.offer(new Zombie());
        
        int numNewZombies = 0;

        int maxDaysWaiting = 0;

        for(int day = 0; day < days; day++) {
            numNewZombies = 0;
            // System.out.format("Day %d with %d zombies\n", day, zombies.size());
            Iterator<Zombie> it = zombies.iterator();
            while(it.hasNext()) {
                Zombie zombie = it.next();
                if(zombie.day()) {
                    ++numNewZombies;
                }

                if(zombie.isDead()) {
                    it.remove();
                } else if(zombie.getHealth() < 30 && numDoctors >= 0) {
                    // holy something this is some spaghetti 
                    boolean canBeTreated = true;
                    if(zombie.getNumDaysTreated() == 0){
                        if(numDoctors > 0){
                            // System.out.println("Treating for " + zombie.daysToTreat() + " days");
                            --numDoctors;
                        } else {
                            canBeTreated = false;
                        }
                    }

                    if (canBeTreated) {
                        zombie.treat();
                        if(zombie.getNumDaysTreated() >= zombie.daysToTreat()){
                            // System.out.println("Healed");
                            if(zombie.getNumDaysWaiting() > maxDaysWaiting) {
                                maxDaysWaiting = zombie.getNumDaysWaiting();

                                // optimisation, stop simulating if we fail early
                                if(maxDaysWaiting > maxDaysWaitingAllowed) {
                                    return new int[]{ maxDaysWaiting, zombies.size() };
                                }
                            }
                            ++numDoctors;
                            zombie.heal();
                        }
                    }
                }
            }

            // System.out.println("Adding " + numNewZombies + " new zombies.");
            while (numNewZombies --> 0) {
                zombies.offer(new Zombie());
            }
        }
        return new int[]{ maxDaysWaiting, zombies.size() };
    }
}
