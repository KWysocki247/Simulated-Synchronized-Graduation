//Programmer: Krzysztof Wysocki

import java.util.Random;
import java.util.Vector;

public class Student implements Runnable {

    public static long time = System.currentTimeMillis();
    public static Object chairAndCoord = new Object();
    public Object parentCall = new Object();
    public static Vector<Student> standingStudents = new Vector<>();
    public static Vector<Student> rowOne = new Vector<>();
    public static Vector<Student> rowTwo = new Vector<>();
    public static Vector<Student> rowThree = new Vector<>();
    public static Object gotDiploma = new Object();
    public static int studentCount = 0;
    public Vector<Thread> parentVectors = new Vector<>();
    public static int parentCount = 0;
    public static int totalParents = 0;

    private String name;
    private int numParents;
    private int seatRow;
    private int seatCol;
    private static int count = 0;



    public Student(int id) {
        setName("Student-" + id);
        Random rand = new Random();
        setNumParents(rand.nextInt(3));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setNumParents(int num) {
        this.numParents = num;
    }

    public int getNumParents() {
        return this.numParents;
    }

    public void setSeatRow(int row) {
        this.seatRow = row;
    }

    public void setSeatCol(int col) {
        this.seatCol = col;
    }

    public int getSeatRow() {
        return this.seatRow;
    }

    public int getSeatCol() {
        return this.seatCol;
    }

    public synchronized void incrementCount(){ //synchronously increments the number of students
        msg("Is heading home now");
        studentCount++;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    public synchronized void takeStudentSeat(Student student) { //synchronizes students taking seats so that no two students get the same seat
        if (main.seatColumnCounter == main.rowCapacity) { //this counts column position
            main.seatColumnCounter = 0;
            main.seatRowCounter++;
        }
        this.setSeatRow(main.seatRowCounter+1);
        this.setSeatCol(main.seatColumnCounter+1);
        main.seats[main.seatRowCounter][main.seatColumnCounter] = this.getName();
        addToVector(this);
        msg("Has taken seat in row " +  this.getSeatRow() + " and column " + this.getSeatCol());
        main.seatColumnCounter++;
        count++;
        if(count == main.numStudent){
            synchronized (Student.chairAndCoord){
                Student.chairAndCoord.notifyAll();
            }
        }
    }

    public synchronized void addToVector(Student student){ //adds students to their respective row vectors to keep track of which student is in which row
        standingStudents.add(this);
        if(this.getSeatRow() == 1)
            rowOne.add(this);
        else if(this.getSeatRow() == 2)
            rowTwo.add(this);
        else
            rowThree.add(this);
    }

    public void run() { //beginning of the Student threads
        synchronized (Coordinator.allowSeating) { //wait for the Coordinator to let students inside the building
            while (Coordinator.allowSeats) {
                try {
                    Coordinator.allowSeating.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        try { //Students arrive at random intervals to simulate arriving at random times
            Thread.sleep((long) (Math.random() * 30000));
        } catch (Exception exception) {
            System.out.println(exception);
        }

        Thread[] parents = new Thread[getNumParents()]; //creates a thread array for the parents and then starts the parent threads
        if (getNumParents() > 0) {
            for (int i = 0; i < getNumParents(); i++) {
                parents[i] = new Thread(new Parent(this.name, i + 1, this));
                parents[i].start();
                parentVectors.add(parents[i]);
                totalParents++;
            }
        }
        if (getNumParents() == 0) {
            msg("Has arrived at the graduation alone as no parents were able to join them today :(.");
        } else if (getNumParents() == 1) {
            msg("Has arrived at the graduation along with " + getNumParents() + " Parental Figure.");
        } else {
            msg("Has arrived at the graduation along with " + getNumParents() + " Parental Figures.");
        }

        msg("Is going to take a seat now");
        takeStudentSeat(this); //called for students to take seats now
        if (this.getSeatRow() == 1) { //if the student is in row one, wait for row one to be called to stand
            synchronized (Coordinator.rowOneWait) {
                while (true) {
                    try {
                        Coordinator.rowOneWait.wait();
                        msg("Has stood up waiting to be called");
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }
        }
        if (this.getSeatRow() == 2) { //if the student is in row two, wait for row two to be called to stand
            synchronized (Coordinator.rowTwoWait) {
                while (true) {
                    try {
                        Coordinator.rowTwoWait.wait();
                        msg("Has stood up waiting to be called");
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }

        }
        if (this.getSeatRow() == 3) { //if the student is in row three, wait for row threww to be called to stand
            synchronized (Coordinator.rowThreeWait) {
                while (true) {
                    try {
                        Coordinator.rowThreeWait.wait();
                        msg("Has stood up waiting to be called");
                        break;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }

        }
        synchronized (Student.gotDiploma){ //waits for the Chairman to notify that they have received their diplomas
            try{
                Student.gotDiploma.wait();
            } catch(InterruptedException e){

            }
        }
        msg("Has left the stage.");
        synchronized (Chairman.eatTime) { //wait for the Chairman to go eat and socialize after the graduation
            while (true) {
                try {
                    studentCount++;
                    msg("I am waiting in the hallway now");
                    synchronized (Chairman.waitStudent) {
                        Chairman.waitStudent.notify();
                    }
                    if(studentCount == main.numStudent){ //if they are the last student - notify the chairman that all students have been given diplomas and waiting to go eat and socialize
                        synchronized(Chairman.speech){
                            try{
                                studentCount = 0;
                                Chairman.foodTime = false;
                                Chairman.speech.notify();
                            }catch (Exception e){

                            }
                        }
                    }
                    Chairman.eatTime.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        msg("Is enjoying eating food now");
        try { //sleep to simulate the students socializing and talking to each other before they leave
            Thread.sleep((long) (Math.random() * (15000 - 10000)) + 10000);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        incrementCount(); //increments the count of students leaving
        if(!parentVectors.isEmpty()) { //if the student has parents - notify the parents they are leaving
            synchronized (parentCall) {
                try{
                    parentCall.notifyAll();
                } catch(Exception e){

                }
            }
        }
        if(studentCount == main.numStudent && parentCount == totalParents){ //if all parents and students have left, notify both the chairman and coordinator to leave
            synchronized (Chairman.done){
                try{
                    Chairman.done.notifyAll();
                }catch(Exception e){

                }
            }
        }
    }
}
