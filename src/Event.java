import java.time.LocalDateTime;

public class Event implements Comparable<Event> {

    private LocalDateTime startDateAndTime, endDateAndTime;
    private String title;
    private int id;

    /**
     * Constructor for creating event Objects for insertion into database
     * @param aTitle title for the event.
     * @param aStartDateAndTime start date and time for the event.
     * @param anEndDateAndTime end date and time for the event.
     */
    public Event(String aTitle, LocalDateTime aStartDateAndTime, LocalDateTime anEndDateAndTime) {
        this.title=aTitle;
        this.startDateAndTime = aStartDateAndTime;
        this.endDateAndTime = anEndDateAndTime;
    }


    /**
     * Constructor for creating Event objects after retrieving from database.
     * @param id primary key assigned on insertion to database.
     * @param aTitle title for the event.
     * @param aStartDateAndTime start date and time for the event.
     * @param anEndDateAndTime end date and time for the event.
     */
    public Event(int id, String aTitle, LocalDateTime aStartDateAndTime, LocalDateTime anEndDateAndTime) {
        this.id = id;
        this.title=aTitle;
        this.startDateAndTime = aStartDateAndTime;
        this.endDateAndTime = anEndDateAndTime;
    }

    protected String getTitle(){
        return this.title;
    }

    protected LocalDateTime getStartDateAndTime() {
        return this.startDateAndTime;
    }

    protected LocalDateTime getEndDateAndTime() {
        return this.endDateAndTime;
    }

    @Override
    public int compareTo(Event otherEvent) {

        if (this.startDateAndTime.compareTo(otherEvent.getStartDateAndTime()) == 0) {
            return this.title.compareToIgnoreCase(otherEvent.getTitle());
        }
        else {
            return this.startDateAndTime.compareTo(otherEvent.getStartDateAndTime());
        }
    }

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if(!(object instanceof Event)) {
            return false;

        }

        Event other = (Event) object;

        return this.title.equals(other.getTitle()) && this.startDateAndTime.equals(other.getStartDateAndTime()) && this.getEndDateAndTime().equals(other.getEndDateAndTime());
    }

    @Override
    public int hashCode() {
        final int HASH_MULTIPLIER = 31;
        int result = 17;
        result = HASH_MULTIPLIER * result + (title.hashCode());
        result = HASH_MULTIPLIER * result + (startDateAndTime.hashCode());
        result = HASH_MULTIPLIER * result + (endDateAndTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Title: " + title + "Starts: " + startDateAndTime + "Ends: " + endDateAndTime;
    }
}
