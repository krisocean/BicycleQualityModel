/**
 * A sensor used to check bicycle in segment 3
 */


public class Sensor extends BicycleHandlingThread {

    protected Belt belt;

    public Sensor(Belt belt){
        super();
        this.belt = belt;
    }

    /**
     * Thread sensor will check all bicycle in the segment 3.
     */

    public void run(){
        while (!isInterrupted()){
            try{

                // Spend SENSOR_TIME milliseconds moving for
                // sensor to check a bicycle.
                Thread.sleep(Params.SENSOR_TIME);
                belt.sensorCheck();
            }catch (InterruptedException e){
                this.interrupt();
            }
        }
        System.out.println("Sensor terminated");
    }

}


