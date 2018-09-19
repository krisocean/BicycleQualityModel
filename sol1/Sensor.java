/**
 * A sensor used to check bicycle in segment 3
 */

public class Sensor{

    protected Belt belt;

    public final static int SENSOR_POSITION = 2;

    public Sensor(Belt belt){
        this.belt = belt;
    }

    /**
     * Sensor check the tagged bicycle
     * @throws InterruptedException
     */
    public synchronized void sensorCheck() throws InterruptedException {

        // if there is bicycle in segment 3 and is tagged, then set this
        // bicycle to be targetBicycle, move it to inspector later.
        if(belt.segment[SENSOR_POSITION]!= null &&
                belt.segment[SENSOR_POSITION].isTagged()== true){
            belt.targetBicycle = belt.segment[SENSOR_POSITION];
        }

        // notify any waiting threads that the belt has changed
        notifyAll();
    }
}

