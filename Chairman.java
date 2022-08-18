//Programmer: Krzysztof Wysocki

public class Chairman implements Runnable {

    public static long time = System.currentTimeMillis();
    public static Object speech = new Object();
    public static Object eatTime = new Object();
    public static Object rowOneStudents = new Object();
    public static Object rowTwoStudents = new Object();
    public static Object rowThreeStudents = new Object();
    public static int graduatedStudents = 0;
    public static boolean speechBool = true;
    public static Object waitStudent = new Object();
    public static boolean foodTime = true;
    public static Object done = new Object();



    private String name;

    public Chairman() {
        this.name = "Chairman";
    }

    public String getName() {
        return this.name;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    public void run() { //start of the coordinator thread
        msg("Is seated next to the coordinator");
        synchronized (Student.chairAndCoord) { //wait until all students have taken a seat
            while (Coordinator.allStudentsSat) {
                try {
                    Student.chairAndCoord.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        Coordinator.allStudentsSat = false;
        msg("Got notified that all students took a seat.");
        msg("Stands up and makes a speech to the graduating class");
        try { //Makes a speech at a random interval //sleeps to simulate the chairman giving a speech
            Thread.sleep((long) (Math.random() * (30000 - 20000)) + 20000);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        msg("And that concludes my speech. We are now ready to hand out diplomas");
        synchronized (Chairman.speech) { //notify all students and coordinator that the speech has ended
            Chairman.speech.notifyAll();
        }
        synchronized (Coordinator.rowOneWait) { //wait until rowOne has been called to stand
            while (Coordinator.rowOneFlag) {
                try {
                    Coordinator.rowOneWait.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        while(!Student.rowOne.isEmpty()){ //while there are students still in row one, call them up one by one in order
            if(graduatedStudents == 4){
                break;
            }
            msg(Student.rowOne.get(graduatedStudents).getName() + " has been called forward to the stage");
            msg(Student.rowOne.get(graduatedStudents).getName() + " has been congratulated and pictures were taken");
            graduatedStudents++;
        }
        graduatedStudents = 0;
        synchronized (Student.gotDiploma){ //notify all students in rowOne that they have received their diplomas
            try {
                Student.gotDiploma.notifyAll();
            } catch(Exception e){

            }
        }
        synchronized (Chairman.rowOneStudents){ //notify the cooridnator that all students in Row One have received their diplomas
            try {
                Chairman.rowOneStudents.notify();
            }catch (Exception e){

            }
        }
        synchronized (Chairman.waitStudent){ //wait for all students in row two to stand
            try {
                Chairman.waitStudent.wait();
            }catch (InterruptedException e){

            }
        }
        while(!Student.rowTwo.isEmpty()){ //while there are students still in row two, call them up one by one in order
            if(graduatedStudents == 4){
                break;
            }
            msg(Student.rowTwo.get(graduatedStudents).getName() + " has been called forward to the stage");
            msg(Student.rowTwo.get(graduatedStudents).getName() + " has been congratulated and pictures were taken");
            graduatedStudents++;
        }
        graduatedStudents = 0;
        synchronized (Student.gotDiploma){ //notify all students in row Two that they have received their diplomas
            try {
                Student.gotDiploma.notifyAll();
            } catch(Exception e){

            }
        }
        synchronized (Chairman.rowTwoStudents){ //notify the coordinator that all students in row two have received their diplomas
            try {
                Chairman.rowTwoStudents.notify();
            }catch (Exception e){

            }
        }
        synchronized (Chairman.waitStudent){ //wait for all students in row three to stand
            try {
                Chairman.waitStudent.wait();
            }catch (InterruptedException e){

            }
        }
        while(!Student.rowThree.isEmpty()){ //while there are students still in row three, call them up one by one in order
            if(graduatedStudents == 2){
                break;
            }
            msg(Student.rowThree.get(graduatedStudents).getName() + " has been called forward to the stage");
            msg(Student.rowThree.get(graduatedStudents).getName() + " has been congratulated and pictures were taken");
            graduatedStudents++;
        }
        graduatedStudents = 0;
        synchronized (Student.gotDiploma){  //notify all students in row three that they have received their diplomas
            try {
                Student.gotDiploma.notifyAll();
            } catch(Exception e){

            }
        }
        synchronized (Chairman.rowThreeStudents){ //notify the coordinator that all students in row three have received their diplomas
            try {
                Chairman.rowThreeStudents.notify();
            }catch (Exception e){

            }
        }
        synchronized (Chairman.waitStudent){ //wait for all students in row Three to have left the stage
            try {
                Chairman.waitStudent.wait();
            }catch (InterruptedException e){

            }
        }
        while(Chairman.foodTime) { //Wait for all students to be waiting in the hallway now
            synchronized (Chairman.speech) {
                try{
                    Chairman.speech.wait();
                }catch (InterruptedException e){

                }
            }
        }
        synchronized (Chairman.eatTime){ //Notify all students, coordinator, and parents that it is time to eat and socialize
            try {
                msg("Now it is time to eat and socialize!");
                Chairman.eatTime.notifyAll();
            }catch (Exception e){

            }
        }
        synchronized(Chairman.done) { //wait for everyone to have left the auditorium
            try {
                Chairman.done.wait();
            } catch (InterruptedException e) {

            }
        }
        msg("Has left the building because all students are gone");
    }
}




