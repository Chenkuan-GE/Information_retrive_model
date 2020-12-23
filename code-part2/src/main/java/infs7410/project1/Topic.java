package infs7410.project1;

public class Topic {

    private String topic;
    private String query;

    public Topic(String topic, String query) {
        this.topic = topic;
        this.query = query;
    }

    public String getTopic() {
        return topic;
    }

    public String getQuery() {
        return query;
    }
}
