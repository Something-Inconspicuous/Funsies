package unit1;

public class PetStylist {
    static final int MINUTES_PER_RUN = 60 * 8 * 5 * 52;

    public static class Pet {
        public enum Species {
            DOG(5, 60),
            CAT(15, 90),
            HORSE(30, 120),
            RODENT(10, 30),
            ;

            int minCareTime;
            int maxCareTime;

            Species(int minCareTime, int maxCareTime) {
                this.minCareTime = minCareTime;
                this.maxCareTime = maxCareTime;
            }

            int getRandomCareTime() {
                return (int)(Math.random() * maxCareTime) + minCareTime;
            }
        }

        private int timeLeft;
        private Species species;

        public Pet(Species species) {
            this.species = species;
            timeLeft = species.getRandomCareTime();
        }

        public void minute() {
            timeLeft--;
        }

        @Override
        public String toString() {
            return "Pet{" +
                    "timeLeft=" + timeLeft +
                    ", species=" + species +
                    '}';
        }
    }

    public record Data(int numPetsMissed, int totalPets) {
        public int petsCared(){
            return totalPets - numPetsMissed;
        }

        public double percentMissed() {
            return (double) numPetsMissed / totalPets;
        }

        public double percentCared() {
            return (double) petsCared() / totalPets;
        }
    }

    private LinkedQueue<Pet> line = new LinkedQueue<>();
    private LinkedQueue<Pet> tomorrow = new LinkedQueue<>();

    private int totalPets = 0;

    private Pet front;

    public Data simulate() {
        for(int day = 0; day < 5 * 52; day++) {
            for(int minsLeft = 60 * 8; minsLeft > 0; minsLeft--) {
                simulateMinute(minsLeft);
            }
            // end of the day
            line.clear();
            LinkedQueue<Pet> temp = line;
            line = tomorrow;
            tomorrow = temp;
        }

        return new Data(line.size(), totalPets);
    }

    private void simulateMinute(int minsLeft) {
        if(doesPetArrive()) {
            Pet pet = randomPet();

            if(pet.timeLeft > minsLeft)
                tomorrow.offer(pet);
            else
                line.offer(pet);
        }

        if(line.isEmpty()) return;

        if(front == null)
            front = line.poll();

        if(front.timeLeft <= 0) {
            totalPets++;
            if (line.isEmpty()) {
                front = null;
                return;
            }
            front = line.poll();
        }

        front.timeLeft--;

    }

    private Pet randomPet() {
        Pet.Species[] speciesArray = Pet.Species.values();
        int randomIndex = (int)(Math.random() * speciesArray.length);
        Pet.Species randomSpecies = speciesArray[randomIndex];

        return new Pet(randomSpecies);
    }

    private static final double CHANCE_PER_MINUTE = 34.5 / 480.0;
    public boolean doesPetArrive() {
        return Math.random() < CHANCE_PER_MINUTE;
    }
}
