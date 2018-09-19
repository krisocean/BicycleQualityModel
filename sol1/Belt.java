import java.awt.*;

/**
 * The bicycle quality control belt
 */
public class Belt {

    // the position of sensor in the belt
    public final static int SENSOR_POSITION = 2;

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength = 5;

    // to help format output trace
    final private static String indentation = "                  ";

    // target bicycle, which is the tagged bicycle found by the sensor
    protected Bicycle targetBicycle;

    // the bicycle inspected by inspector
    protected Bicycle inspectedBicycle;


    /**
     * Create a new, empty belt, initialised as empty
     */
    public Belt() {
        segment = new Bicycle[beltLength];
        for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }
        targetBicycle = null;
        inspectedBicycle = null;
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
        System.out.println(bicycle + " arrived");

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

        // make a note of the event in output trace
        System.out.print(indentation + indentation+"     ");
        finalCheck(bicycle);

        // notify any waiting threads that the belt has changed
        notifyAll();
        return bicycle;
    }

    /**
     * Move the belt along one segment
     * 
     * @throws OverloadException
     *             if there is a bicycle at position beltLength.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void move() 
            throws InterruptedException, OverloadException {
        // if there is something at the end of the belt, 
    	// or the belt is empty, do not move the belt
        while (isEmpty() || segment[segment.length-1] != null ||
                (segment[SENSOR_POSITION]!= null &&
                        segment[SENSOR_POSITION].isTagged()&&
                        !segment[SENSOR_POSITION].isInspected())) {
            wait();
        }

        // double check that a bicycle cannot fall of the end
        if (segment[segment.length-1] != null) {
            String message = "Bicycle fell off end of " + " belt";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = segment.length-1; i > 0; i--) {
            if (this.segment[i-1] != null) {
                System.out.println(
                		indentation +
                		this.segment[i-1] +
                        " [ s" + (i) + " -> s" + (i+1) +" ]");
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

    /**
     * ToString method for a segment
     * @return printing String
     */
    public String toString() {
        return java.util.Arrays.toString(segment);
    }

    /**
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
     * Move the tagged bicycle from belt to inspector
     * @throws InterruptedException
     */
    public synchronized void moveBicycleFromBelt() throws InterruptedException{

        // make sure there is tagged bicycle in segment 3, and if there is
        // already a bicycle in the inspector, we need to wait, because inspector
        // can only inspect one bicycle at one time.
        while(targetBicycle == null || inspectedBicycle !=null || targetBicycle.isInspected()){
            wait();
        }

        inspectedBicycle = targetBicycle;

        // print out this event
        System.out.println(indentation+ "Move bicycle " + this.inspectedBicycle+
                " " + "to inspector");
        targetBicycle = null;
        segment[SENSOR_POSITION] = null;

        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * After inspecting the bicycle, move it back to the belt
     * @throws InterruptedException
     */
    public synchronized void moveBicycleFromInspector() throws
            InterruptedException{
        // Because we need to put the inspected bicycle back to segment 3, if
        // there is a bicycle already in that segment, we should wait. And if
        // there is no bicycle in the inspector or the bicycle has not finish
        // the inspecting process, we need to wait too.
        while (segment[SENSOR_POSITION]!= null || inspectedBicycle == null||
                !inspectedBicycle.isInspected()){
            wait();
        }

        // move the bicycle from inspector back to belt
        segment[SENSOR_POSITION] = inspectedBicycle;

        // print out this event
        System.out.println(indentation+ "Move bicycle "+this.inspectedBicycle+
                " back to belt");
        inspectedBicycle = null;

        // notify any waiting threads that the belt has changed
        notifyAll();
    }




}
