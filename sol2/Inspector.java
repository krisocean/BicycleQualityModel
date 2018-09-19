/**
 * A inspector checks the bicycle in a specific way
 */
public class Inspector{
    protected Belt belt;

    public Inspector(Belt belt){

        this.belt = belt;
    }

    /**
     * inspector checks bicycle
     * @param inspectedBicycle
     */
    public synchronized void inspect(){


        // if the inspected bicycle is not defective,
        // remove the tag from it.
        if(!belt.inspectedBicycle.isDefective()){
            belt.inspectedBicycle.setNotTagged();
        }
        belt.inspectedBicycle.setInspected();

        // notify any waiting threads that the belt has changed
        notifyAll();
    }
}
