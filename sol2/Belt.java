import java.awt.*;

/**
 * The bicycle quality control belt
 */
public class Belt {

    // the position of sensor in the belt
    public final static int SENSOR_POSITION = 2;

    // the length of long belt
    public final static int LONG_BELT_LENGTH = 5;

    // the length of short belt
    public final static int SHORT_BELT_LENGTH = 2;

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength;

    // to help format output trace
    final private static String indentation = "                  ";

    // to help format output trace
    final private static String shortIdentation = "         ";

    // target bicycle, which is the tagged bicycle found by the sensor
    protected Bicycle targetBicycle;

    protected Bicycle inspectedBicycle;


    /**
     * Create a new, empty belt, initialised as empty
     */
    public Belt(int beltLength) {
        segment = new Bicycle[beltLength];
        for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }
        targetBicycle = null;
        inspectedBicycle = null;
        this.beltLength = beltLength;
    }

    /**
     * Put a bicycle on the belt.
     *
     * @param bicycle
     *            the bicycle to put onto the belt.
     * @param index
     *            the place to put the bicycle
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void put(Bicycle bicycle, int index)
            throws InterruptedException {

        // while there is another bicycle in the way, block this thread
        while (segment[index] != null) {
            wait();
        }

        // insert the element at the specified location
        segment[index] = bicycle;

        // make a note of the event in output trace
        if(length() == LONG_BELT_LENGTH ){
            System.out.println(bicycle + " arrived");
        }else if(length() == SHORT_BELT_LENGTH){
            System.out.println("                                           " +
                    "                 "
                    +indentation+bicycle+ " arrived");
        }


        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * Take a bicycle off the end of the belt
     *
     * @return the removed bicycle
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Bicycle getEndBelt() throws InterruptedException {

        Bicycle bicycle;

        // while there is no bicycle at the end of the belt, block this thread
        while (segment[segment.length-1] == null) {
            wait();
        }

        // get the next item
        bicycle = segment[segment.length-1];
        segment[segment.length-1] = null;

        // make a note of the event in output trace, different belts have
        // different output format
        if(beltLength == LONG_BELT_LENGTH){
            System.out.print(indentation + indentation+"     ");
            finalCheck(bicycle);
        }else if(beltLength == SHORT_BELT_LENGTH){
            System.out.print("                                             " +
                    "               ");
            System.out.print(indentation + indentation+"     ");
            finalCheck(bicycle);
        }

        // notify any waiting threads that the belt has changed
        notifyAll();
        return bicycle;
    }

    /**
     * Move the belt along one segment, there are two different
     * move rule for two kinds of belts
     *
     * @throws OverloadException
     *             if there is a bicycle at position beltLength.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void move()
            throws InterruptedException, OverloadException {

        if(beltLength == LONG_BELT_LENGTH){
            // if there is something at the end of the belt, or the belt
            // is empty, do not move the belt. And if there is a bicycle in
            // sensor position, and is tagged but not inspected yet, do not
            // move the belt too, because our system wants every tagged
            // bicycle to be inspected.
            while (isEmpty() || segment[segment.length-1] != null ||
                    (segment[SENSOR_POSITION]!= null &&
                            segment[SENSOR_POSITION].isTagged()&&
                            !segment[SENSOR_POSITION].isInspected())) {
                wait();
            }
        }else if(beltLength == SHORT_BELT_LENGTH){
            // if there is something at the end of the belt,
            // or the belt is empty, do not move the belt
            while (isEmpty() || segment[segment.length-1] != null ) {
                wait();
            }
        }


        // double check that a bicycle cannot fall of the end
        if (segment[segment.length-1] != null) {
            String message = "Bicycle fell off end of " + " belt";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = segment.length-1; i > 0; i--) {
            if (this.segment[i-1] != null) {
                if(beltLength == LONG_BELT_LENGTH){
                    System.out.println(
                            indentation +
                                    this.segment[i-1] +
                                    " [ s" + (i) + " -> s" + (i+1) +" ]");
                }else if(beltLength == SHORT_BELT_LENGTH){
                    System.out.println(
                            indentation + "                           " +
                                    "                                 "
                                    +this.segment[i-1] +
                                    " [ s" + (i) + " -> s" + (i+1) +" ]");
                }

            }
            segment[i] = segment[i-1];
        }
        segment[0] = null;
        //System.out.println(indentation + this);

        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * @return the maximum size of this belt
     */
    public int length() {
        return beltLength;
    }

    /**
     * Peek at what is at a specified segment
     *
     * @param index
     *            the index at which to peek
     * @return the bicycle in the segment (or null if the segment is empty)
     */
    public Bicycle peek(int index) {
        Bicycle result = null;
        if (index >= 0 && index < beltLength) {
            result = segment[index];
        }
        return result;
    }

    /**
     * Check whether the belt is currently empty
     * @return true if the belt is currently empty, otherwise false
     */
    private boolean isEmpty() {
        for (int i = 0; i < segment.length; i++) {
            if (segment[i] != null) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return java.util.Arrays.toString(segment);
    }

    /*
     * @return the final position on the belt
     */
    public int getEndPos() {
        return beltLength-1;
    }

    /**
     * Check the bicycle need to be recycled or departed
     * @param bicycle the bicycle need to be checked
     */

    public void finalCheck(Bicycle bicycle){
        if(bicycle.isDefective()){
            System.out.println(bicycle + " recycled");
        }else{
            System.out.println(bicycle + " departed");
        }
    }

    /**
     * Sensor check if a bicycle need to be sent to inspector
     * @throws InterruptedException
     */
    public synchronized void sensorCheck() throws InterruptedException {

        // if there is no bicycle on the sensor position, or the bicycle in the
        // sensor position has been checked, sensor will wait.
        while (segment[SENSOR_POSITION]== null||
                segment[SENSOR_POSITION].isInspected()){
            wait();
        }

        // if the bicycle is tagged, then set it as targetBicycle, and send it
        // to inspector later
        if(segment[SENSOR_POSITION].isTagged()== true){
            targetBicycle = segment[SENSOR_POSITION];
        }

        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    public synchronized void moveBicycleFromBelt() throws InterruptedException{
        // make sure there is tagged bicycle in segment 3, and if there is
        // already a bicycle in the inspector, we need to wait, because inspector
        // can only inspect one bicycle at one time.
        while(targetBicycle == null || inspectedBicycle !=null ){
            wait();
        }

        inspectedBicycle = targetBicycle;

        // print out this event
        System.out.println(indentation+ "Move bicycle " + this.inspectedBicycle
                + " to inspector");
        targetBicycle = null;
        segment[SENSOR_POSITION] = null;

        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * After inspecting the bicycle, move it to the short belt.
     * @throws InterruptedException
     */
    public synchronized void moveBicycleTOshrotbelt()
            throws InterruptedException{

        // there is no bicycle in the inspector or the bicycle has not finish
        // the inspecting process, we need to wait too.
        while (inspectedBicycle == null|| !inspectedBicycle.isInspected()){
            wait();
        }

        // print out this event
        System.out.println(indentation +"                                 " +
                "         "+ "Move bicycle "+this.inspectedBicycle+
                " to short belt");

        // move the bicycle to the short belt
        put(inspectedBicycle, 0);
        inspectedBicycle = null;

        // notify any waiting threads that the belt has changed
        notifyAll();
    }




}
