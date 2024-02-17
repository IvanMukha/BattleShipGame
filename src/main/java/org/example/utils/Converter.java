package org.example.utils;

public  class Converter {
    public static  String convertNumberToLetter(int number) {
        if (number < 1 || number > 16) {
        throw new IllegalArgumentException("Некорректное число для преобразования в букву.");
    }
        char letter = (char) ('A' + number - 1);
        return String.valueOf(letter);
    }
    public static int convertLetterToNumber(String letter) {
        letter = letter.toUpperCase();
        char[] letters = letter.toCharArray();
        int result = 0;

        for (int i = 0; i < letters.length; i++) {
            result = result * 26 + (letters[i] - 'A' + 1);
        }

        return result;
    }
}
