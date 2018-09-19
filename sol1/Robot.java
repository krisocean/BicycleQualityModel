/**
 * A robot moves the bicycle from belt to inspector, and move
 * it back.
 */
public class Robot extends BicycleHandlingThread {

    protected Belt belt;
    protected Inspector inspector;

    public Robot(Belt belt){
        super();
        this.belt = belt;
        inspector = new Inspector(belt);
    }

    /**
     * Thread robot will control the inspector
     */

    public void run(){

        while (!isInterrupted()){
            try{
                // spend ROBOT_MOVE_TIME milliseconds moving the bicycle
                // from belt to inspector
                Thread.sleep(Params.ROBOT_MOVE_TIME);
                belt.moveBicycleFromBelt();

                // spend INSPECT_TIME milliseconds inspecting a bicycle
                Thread.sleep(Params.INSPECT_TIME);
                inspector.inspect();

                // print out inspector event for the bicycle
                System.out.println("                  "+
                        belt.inspectedBicycle+ " inspection finished");

                // spend ROBOT_MOVE_TIME milliseconds moving the bicycle
                // from inspector to belt
                Thread.sleep(Params.ROBOT_MOVE_TIME);
                belt.moveBicycleFromInspector();
            }catch (InterruptedException e){
                this.interrupt();
            }
        }
        System.out.println("Robot terminated");
    }
}
