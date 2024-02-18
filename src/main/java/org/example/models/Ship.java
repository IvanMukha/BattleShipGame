package org.example.models;

import org.example.utils.Converter;

public class Ship {
    private String startX;
    private String startY;
    private Orientation orientation;
    private int size;
    public Ship(String startX, String startY, Orientation orientation, int size) {
        this.startX = startX;
        this.startY = startY;
        this.orientation = orientation;
        this.size = size;
    }

    public String getStartX() {
        return startX;
    }

    public void setStartX(String startX) {
        this.startX = startX;
    }

    public String getStartY() {
        return startY;
    }

    public void setStartY(String startY) {
        this.startY = startY;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }



    public boolean isShipDestroyed(Board board) {
        int startXNumber = Converter.convertLetterToNumber(startX);
        int startYNumber = Integer.parseInt(startY);

        if (orientation == Orientation.HORIZONTAL) {
            for (int i = startXNumber - 1; i < startXNumber + size - 1; i++) {
                if (board.getCell(i, startYNumber - 1) == 'O') {
                    return false; // Найдена непораженная клетка корабля
                }
            }
        } else {
            for (int j = startYNumber - 1; j < startYNumber + size - 1; j++) {
                if (board.getCell(startXNumber - 1, j) == 'O') {

                    return false; // Найдена непораженная клетка корабля
                }
            }

        }

        System.out.println("Все клетки корабля были уничтожены ");
        return true; // Все клетки корабля поражены
    }
    public boolean containsPoint(int x, int y) {
        int startXNumber = Converter.convertLetterToNumber(startX);
        int startYNumber = Integer.parseInt(startY);

        if (orientation == Orientation.HORIZONTAL) {
            if (startYNumber != y) {
                return false; // Если y-координата не совпадает с начальной y-координатой корабля, точка не принадлежит кораблю
            }
            return (x >= startXNumber && x < startXNumber + size); // Проверяем, лежит ли x-координата в пределах корабля
        } else {
            if (startXNumber != x) {
                return false; // Если x-координата не совпадает с начальной x-координатой корабля, точка не принадлежит кораблю
            }
            return (y >= startYNumber && y < startYNumber + size); // Проверяем, лежит ли y-координата в пределах корабля
        }
    }




    // Дополнительные методы и функции класса Ship
}
