package andrej.jelic.attendance;

/**
 * Created by Korisnik on 25.6.2015..
 */
public class Student {

    int _id;
    String _student;
    String _attend_time;
    String _leave_time;

    public Student() {
    }

    public Student(String _student, String _leave_time) {

        this._student = _student;
        this._leave_time = _leave_time;
    }


    public Student(String _student, String _attend_time, String _leave_time) {

        this._student = _student;
        this._attend_time = _attend_time;
        this._leave_time = _leave_time;
    }

    public Student (String _student) {
        this._student = _student;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int _id) {
        this._id = _id;
    }

    public String getStudent() {
        return this._student;
    }

    public void setStudent(String _student) {
        this._student = _student;
    }

    public String getAttendTime() {
        return this._attend_time;
    }

    public void setAttendTime(String _attend_time) {
        this._attend_time = _attend_time;
    }

    public String getLeaveTime () {
        return this._leave_time;
    }

    public void setLeaveTime(String _leave_time) {
        this._leave_time = _leave_time;
    }

}
