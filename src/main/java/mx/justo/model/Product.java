package mx.justo.model;

public class Product {
    private final String id;
    private final String name;
    private final String brand;

    public Product(String id, String name, String brand) {
        this.id = id;
        this.name = name;
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}
