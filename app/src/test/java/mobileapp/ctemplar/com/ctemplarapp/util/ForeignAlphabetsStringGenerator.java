package mobileapp.ctemplar.com.ctemplarapp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mobileapp.ctemplar.com.ctemplarapp.utils.EncodeUtils;

public class ForeignAlphabetsStringGenerator {
    public static String randomString(int length) {
        Random generator = new Random();
        int subLength = length / 5;
        String englishString = EncodeUtils.randomString(subLength);
        String arabicString = randomArabicString(subLength, generator);
        String chineseString = randomChineseString(subLength, generator);
        String japaneseString = randomJapaneseString(subLength, generator);
        String cyrillicString = randomCyrillicString(subLength, generator);
        return englishString
                + "\n" + arabicString
                + "\n" + chineseString
                + "\n" + japaneseString
                + "\n" + cyrillicString;
    }

    private static String randomArabicString(int length, Random random) {
        List<Character> arabicChars = findCharactersInUnicodeBlock(Character.UnicodeBlock.ARABIC);
        return randomStringFromCharacters(arabicChars, length, random);
    }

    private static String randomChineseString(int length, Random random) {
        List<Character> chinesChars = findCharactersInUnicodeScript(Character.UnicodeScript.HAN);
        return randomStringFromCharacters(chinesChars, length, random);
    }

    private static String randomJapaneseString(int length, Random random) {
        List<Character> japaneseChars = new ArrayList<>();
        List<Character> japaneseHiraganaChars = findCharactersInUnicodeBlock(Character.UnicodeBlock.HIRAGANA);
        List<Character> japaneseKatakanaChars = findCharactersInUnicodeBlock(Character.UnicodeBlock.KATAKANA);
        List<Character> japaneseKatakanaPhoneticChars = findCharactersInUnicodeBlock(Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS);
        japaneseChars.addAll(japaneseHiraganaChars);
        japaneseChars.addAll(japaneseKatakanaChars);
        japaneseChars.addAll(japaneseKatakanaPhoneticChars);
        return randomStringFromCharacters(japaneseChars, length, random);
    }

    private static String randomCyrillicString(int length, Random random) {
        List<Character> cyrillicChars = findCharactersInUnicodeBlock(Character.UnicodeBlock.CYRILLIC);
        return randomStringFromCharacters(cyrillicChars, length, random);
    }

    private static String randomStringFromCharacters(List<Character> characterList, int length, Random random) {
        int randomLength = random.nextInt(length);
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = characterList.get(random.nextInt(characterList.size()));
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private static List<Character> findCharactersInUnicodeScript(Character.UnicodeScript block) {
        final List<Character> chars = new ArrayList<>();
        for (int codePoint = Character.MIN_CODE_POINT; codePoint <= Character.MAX_CODE_POINT; codePoint++) {
            if (block == Character.UnicodeScript.of(codePoint)) {
                chars.add((char) codePoint);
            }
        }
        return chars;
    }

    private static List<Character> findCharactersInUnicodeBlock(Character.UnicodeBlock block) {
        final List<Character> chars = new ArrayList<>();
        for (int codePoint = Character.MIN_CODE_POINT; codePoint <= Character.MAX_CODE_POINT; codePoint++) {
            if (block == Character.UnicodeBlock.of(codePoint)) {
                chars.add((char) codePoint);
            }
        }
        return chars;
    }
}
