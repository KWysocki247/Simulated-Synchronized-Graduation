//Programmer: Krzysztof Wysocki

public class Coordinator implements Runnable{

    public static long time = System.currentTimeMillis();
    public static Object rowOneWait = new Object();
    public static Object rowTwoWait = new Object();
    public static Object rowThreeWait = new Object();
    public static Object allowSeating = new Object();
    public static boolean allowSeats = true;
    public static boolean allStudentsSat = true;
    public static boolean rowOneFlag = true;
    public static boolean rowTwoFlag = true;
    public static boolean rowThreeFlag = true;


    private String name;
    private int count = 0;
    public Coordinator(){
        this.name = "Coordinator";
    }

    public String getName(){
        return this.name;
    }

    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }

    public void run() { //start of the Coordinator thread
        msg("Thank you for coming to our Mini-Simulated Graduation!");
        msg("We will now be allowing students to enter the auditorium and begin seating");
        msg("Is seated next to the chairman");
        synchronized (Coordinator.allowSeating){ //notify all students and parents that they can begin seating
            Coordinator.allowSeating.notifyAll();
        }
        synchronized (Student.chairAndCoord){ //wait for all students to have taken a seat
            while(Coordinator.allStudentsSat){
                try{
                    Coordinator.allowSeats = false;
                    synchronized (Coordinator.allowSeating){
                        Coordinator.allowSeating.notifyAll();
                    }
                    Student.chairAndCoord.wait();
                    break;
                }
                catch(InterruptedException e){
                    continue;
                }
            }
        }
        Coordinator.allStudentsSat = false; //notifies chairman that all students have taken a seat
        synchronized (Student.chairAndCoord){
            Student.chairAndCoord.notifyAll();
        }
        msg("Got notified that all students took a seat.");
        synchronized (Chairman.speech){ //waits for the Chairmans speech to have finished
            while(Chairman.speechBool){
                try{
                    Chairman.speech.wait();
                    break;
                }
                catch(InterruptedException e){
                    continue;
                }
            }
        }
        msg("Will the first row students please stand up");
        synchronized (Coordinator.rowOneWait){ //Notify all students in row one to stand
            Coordinator.rowOneWait.notifyAll();
            Coordinator.rowOneFlag = false;
        }
        Coordinator.rowOneFlag = true;
        synchronized (Chairman.rowOneStudents){ //wait for row one to have finished and get a notification from the chairman
            while(true) {
                try {
                    Chairman.rowOneStudents.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        msg("Will the second row students please stand up");
        synchronized (Coordinator.rowTwoWait){ //notify all students in row two to stand
            Coordinator.rowTwoWait.notifyAll();
            Coordinator.rowTwoFlag = false;
        }
        Coordinator.rowTwoFlag = true;
        synchronized (Chairman.rowTwoStudents){ //Wait for all students in row two to have gotten their diplomas
            while(true) {
                try {
                    Chairman.rowTwoStudents.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        msg("Will the third row students please stand up");
        synchronized (Coordinator.rowThreeWait){ //notify all students in row three to stand
            Coordinator.rowThreeWait.notifyAll();
            Coordinator.rowThreeFlag = false;
        }
        Coordinator.rowThreeFlag = true;
        synchronized (Chairman.rowThreeStudents){ //Wait for all students in row two to have gotten their diplomas
            while(true) {
                try {
                    Chairman.rowThreeStudents.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        synchronized (Chairman.eatTime){ //Wait for the chairman to let everyone know it is time to eat
            try{
                Chairman.eatTime.wait();
            } catch (InterruptedException e) {

            }
        }
        synchronized(Chairman.done) { //waits for everyone to have left the event
            try {
                Chairman.done.wait();
            } catch (InterruptedException e) {

            }
        }
        msg("Has left the building because all students are gone");
    }
}
