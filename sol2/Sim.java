/**
 * The driver of the simulation
 */

public class Sim {
    /**
     * Create all components and start all of the threads.
     */
    public static void main(String[] args) {

        // create one long belt and one short belt
        Belt longBelt = new Belt(5);
        Belt shortBelt = new Belt(2);

        // producer put a bicycle onto the long belt
        Producer producer = new Producer(longBelt);

        // Create a longConsumer that consumes from the long belt
        // and a shortConsumer that consumes from the short belt
        Consumer longConsumer = new Consumer(longBelt);
        Consumer shortConsumer = new Consumer(shortBelt);

        // longMover moves the long belt, and shortMover moves the
        // short belt
        BeltMover longMover = new BeltMover(longBelt);
        BeltMover shortMover = new BeltMover(shortBelt);

        // sensor checks the bicycle on the segment 3 of the long belt
        Sensor sensor = new Sensor(longBelt);

        // robot move bicycle from long belt to inspector, and move
        // bicycle from inspector to short belt
        Robot robot = new Robot(longBelt, shortBelt);


        producer.start();
        longConsumer.start();
        shortConsumer.start();
        longMover.start();
        shortMover.start();
        robot.start();
        sensor.start();



        while (longConsumer.isAlive() &&
                producer.isAlive() &&
                longMover.isAlive() &&
                shortConsumer.isAlive() &&
                shortMover.isAlive() &&
                sensor.isAlive() &&
                robot.isAlive())
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                BicycleHandlingThread.terminate(e);
            }

        // interrupt other threads
        longConsumer.interrupt();
        shortConsumer.interrupt();
        producer.interrupt();
        longMover.interrupt();
        shortMover.interrupt();
        robot.interrupt();
        sensor.interrupt();



        System.out.println("Sim terminating");
        System.out.println(BicycleHandlingThread.getTerminateException());
        System.exit(0);
    }
}
