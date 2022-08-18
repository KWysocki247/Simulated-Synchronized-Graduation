//Programmer: Krzysztof Wysocki

public class main {

    public static int numStudent = 10; //default values for number of students
    public static int rowCapacity = 4; //default values of row capacity
    public static int numRows = 10; //default values of number of rows
    public static int parentRows = (int)Math.ceil(numRows/3); //calculates where parents can begin sitting
    public static String[][] seats = new String[numRows][rowCapacity]; //2D array to store all names of all students and parents
    public static int seatColumnCounter = 0; //Counts which column the next student will sit at
    public static int seatRowCounter = 0; //counts the current row we are up to
    public static Chairman chairman = new Chairman(); //creates a public chairman object
    public static Coordinator coordinator = new Coordinator(); //creats a public coordinator object

    public static void main(String args[]){
        Thread chairManThread = new Thread(chairman); //Cast the chairman as a thread so we can use .start()
        Thread coordinatorThread = new Thread(coordinator); //Cast the coordinator as a thread so we can use .start()
        Thread[] students = new Thread[numStudent]; //creates an array of threads to start all student threads
        for(int i = 0; i < numStudent; i++){
            students[i] = new Thread(new Student(i+1));
            students[i].start(); //starts all students
        }
        chairManThread.start(); //starts the chairman
        coordinatorThread.start(); //starts the coordinator
    }
}
