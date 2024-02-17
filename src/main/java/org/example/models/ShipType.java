package org.example.models;

public enum ShipType {
    TYPE_1(6, 1),
    TYPE_2(5, 2),
    TYPE_3(4, 3),
    TYPE_4(3, 4),
    TYPE_5(2, 5),
    TYPE_6(1, 6);
    private int size;
    private int count;

    ShipType(int size, int count) {
        this.size = size;
        this.count = count;
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }
}
