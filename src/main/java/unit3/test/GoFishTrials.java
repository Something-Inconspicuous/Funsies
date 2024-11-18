package unit3.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import unit3.gofish.ArbDeck;
import unit3.gofish.CPUPlayer;
import unit3.gofish.Game;
import unit3.gofish.Player;
import unit3.gofish.PlayingCard;

public class GoFishTrials {
    public static void main(String[] args) {
        int numTrials = 10_000;
        for(int i = 13; i < 26; i++)
            timeTrials(numTrials, i);
    }

    private static void timeTrials(int numTrials, int numRanks) {
        long start = System.currentTimeMillis();
        int shutouts = countShutouts(numTrials, 10, () -> new ArrayList<>(), numRanks);
        long end = System.currentTimeMillis();
        
        System.out.println("2 Players; ArrayList; " + numRanks * 4 + " cards.");
        System.out.println("\tTrials ran: " + numTrials);
        System.out.printf("\tShutout games: %d (%.5f%%)\n", shutouts, (double) shutouts / numTrials);
        System.out.println("\tTime: " + (end - start) / 1000.0 + "s");
        
        start = System.currentTimeMillis();
        shutouts = countShutouts(numTrials, 10, () -> new TreeSet<>(), numRanks);
        end = System.currentTimeMillis();

        System.out.println("2 Players; TreeSet " + numRanks * 4 + " cards.");
        System.out.println("\tTrials ran: " + numTrials);
        System.out.printf("\tShutout games: %d (%.5f%%)\n", shutouts, (double) shutouts / numTrials);
        System.out.println("\tTime: " + (end - start) / 1000.0 + "s");
    }

    private static int countShutouts(int numTrials, Supplier<? extends Collection<PlayingCard>> generator, int numRanks) {
        Player p1 = new CPUPlayer(generator.get(), generator.get());
        Player p2 = new CPUPlayer(generator.get(), generator.get());
        int shutouts = 0;
        Game game = new Game(List.of(p1, p2), new ArbDeck(numRanks));
        for(int i = 0; i < numTrials; i++) {
            game.play();

            // System.out.printf("p1: %2d books | p2: %2d books\n", p1.books().size() / 4, p2.books().size() / 4);
            if(p1.books().isEmpty() || p2.books().isEmpty()) {
                // shutout
                // System.out.println("Shutout! Game " + i);
                shutouts++;
            }
        }

        return shutouts;
    }

    private static int countShutouts(int numTrials, int numThreads, Supplier<? extends Collection<PlayingCard>> generator, int numRanks) {
        if(numThreads < 2) return countShutouts(numTrials, generator, numRanks); // single threaded
        AtomicInteger shutouts = new AtomicInteger(0);
        final int perThread = numTrials / numThreads;
        final int totalTrials = numThreads * perThread;

        Thread[] threads = new Thread[numThreads];

        for(int t = 0; t < threads.length; t++) {
            final int finalT = t;
            threads[t] = new Thread(new Runnable () {
                public void run() {
                    final Player p1 = new CPUPlayer(generator.get(), generator.get());
                    final Player p2 = new CPUPlayer(generator.get(), generator.get());
                    final Game game = new Game(List.of(p1, p2), new ArbDeck(numRanks));
                    int _shutouts = 0;
                    for(int i = 0; i < perThread; i++) {
                        game.play();
    
                        if(p1.books().isEmpty() || p2.books().isEmpty()) {
                            // shutout
                            _shutouts++;
                        }
                    }
                    shutouts.addAndGet(_shutouts);
                    // System.out.println("Thread " + finalT + " done");
                };
            }, "Fish Trial " + t);
            threads[t].start();
        }

        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return shutouts.get();
    }
}
