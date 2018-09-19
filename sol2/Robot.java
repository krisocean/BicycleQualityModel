/**
 * A robot moves the bicycle from belt to inspector, and move
 * it back.
 */
public class Robot extends BicycleHandlingThread {

    protected Belt longBelt;
    protected Belt shortBelt;
    protected Inspector inspector;

    public Robot(Belt longBelt, Belt shortBelt){
        super();
        this.longBelt = longBelt;
        this.shortBelt = shortBelt;
        inspector = new Inspector(longBelt);
    }

    /**
     * Thread robot will move the tagged bicycle from long belt,
     * and put it in the inspector, then move it to the short
     * belt
     */

    public void run(){

        while (!isInterrupted()) {
            try {

                // spend ROBOT_MOVE_TIME milliseconds moving the bicycle
                // from long belt to inspector
                Thread.sleep(Params.ROBOT_MOVE_TIME);
                longBelt.moveBicycleFromBelt();

                // spend INSPECT_TIME milliseconds inspecting a bicycle
                Thread.sleep(Params.INSPECT_TIME);
                inspector.inspect();

                // print out inspector event for the bicycle
                System.out.println("                  "+
                        longBelt.inspectedBicycle+ " inspection finished");

                shortBelt.inspectedBicycle = longBelt.inspectedBicycle;
                longBelt.inspectedBicycle = null;

                // spend ROBOT_MOVE_TIME milliseconds moving the bicycle
                // from inspector to short belt
                Thread.sleep(Params.ROBOT_MOVE_TIME);
                shortBelt.moveBicycleTOshrotbelt();
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Robot terminated");
    }
}
