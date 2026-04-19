package main.event;

public class ResourceEvent {
    private String type;
    private String entity;
    private String name;
    private Object payload;

    public ResourceEvent(String type, String entity, String name, Object payload) {
        this.type = type;
        this.entity = entity;
        this.name = name;
        this.payload = payload;
    }

    public String getType() { return type; }
    public String getEntity() { return entity; }
    public String getName() { return name; }
    public Object getPayload() { return payload; }
}