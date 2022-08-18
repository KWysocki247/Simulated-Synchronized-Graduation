//Programmer: Krzysztof Wysocki

import java.util.Vector;

public class Parent implements Runnable {

    public static long time = System.currentTimeMillis();
    public Vector<Student> myChild = new Vector<>();
    private String name;
    private int seatRow;
    private int seatCol;
    private static int count = main.parentRows;
    private static int rowCount = (main.parentRows + 1);
    private static int colCount = 0;
    private int id;

    public Parent(String name, int id, Student child) {
        name = name + "-Parent" + id;
        setName(name);
        setId(id);
        myChild.add(child);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
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

    public void setId(int id) {
        this.id = id;
    }

    public synchronized void takeParentSeat(Parent parent) { //synchronously allows parents to take seats in the auditorium if there are seats available
        if (Parent.count > main.numRows) {
            msg("Has taken a seat in the hallway as all seats are currently taken.");
            return;
        } else if (Parent.colCount == main.rowCapacity) { //this counts column position
            Parent.colCount = 0;
            Parent.rowCount++;
            count++;
        } else {
            this.setSeatRow(Parent.rowCount);
            this.setSeatCol(Parent.colCount);
            main.seats[Parent.rowCount][Parent.colCount] = this.getName();
            msg("Has taken seat in row " + this.getSeatRow() + " and column " + (this.getSeatCol() + 1));
            Parent.colCount++;
        }
    }

    public void run() {
        try { //sleep for a random time once they are started so parents do not run at the same time
            Thread.sleep((long) (Math.random() * 200));
        } catch (Exception exception) {
            System.out.println(exception);
        }
        takeParentSeat(this); //parents take seats
        synchronized (Chairman.eatTime) { //parents wait to be notified that all students received diplomas and they can go eat now
            while (true) {
                try {
                    Chairman.eatTime.wait();
                    break;
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
        msg("Is enjoying eating food now");
        if (!myChild.isEmpty()) { //if the parent has a child - block on their specific child
            synchronized(myChild.get(0).parentCall){
                try {
                    myChild.get(0).parentCall.wait();
                    Student.parentCount++;
                }catch(InterruptedException e){

                }
            }
        }
        msg("Is heading home now with their child");
        if(Student.studentCount == main.numStudent && Student.parentCount == Student.totalParents){ //if the parent is the last one to leave the auditorium - notify the chairman and coordinator that everyone left
            synchronized (Chairman.done){
                try{
                    Chairman.done.notifyAll();
                }catch(Exception e){

                }
            }
        }
    }
}

