package org.ananie.parishManagementSystem.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VowelRemoval {
   static String[] vowelArray = {"a", "e", "i", "o", "u"};
   static List<String> list = Arrays.asList(vowelArray);
    public static void main(String[] args) {
      String word = "ARS Gratia Gratis";
      String wordoutput = removeVowels3(word);
      System.out.println(wordoutput);
    }

    public static String removeVowels(String word) {


        StringBuilder sb = new StringBuilder();

      for(char c: word.toCharArray()){

          String cs = String.valueOf(c).toLowerCase();
          if(!list.contains(cs)){
              sb.append(c);
          }
      }
    return sb.toString();
    }
    public static String removeVowels2(String word) {
    return word.replaceAll("[aeiouAEIOU]", "");
    }
    public static String removeVowels3(String word) {
        return word.chars().
                filter(c -> !list.contains(String.valueOf((char) c).toLowerCase()))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
