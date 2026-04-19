package main.export;

public class ExportContext {

    private ExportStrategy strategy;

    public void setStrategy(ExportStrategy strategy) {
        this.strategy = strategy;
    }

    public byte[] export(Object data) throws Exception {
        return strategy.export(data);
    }
}