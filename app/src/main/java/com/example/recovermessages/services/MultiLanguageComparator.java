package com.example.recovermessages.services;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MultiLanguageComparator {


    Set<Locale> localeList = new HashSet<Locale>();

    public MultiLanguageComparator() {
//        Log.e("Locale", "is " + Locale.getDefault());
        localeList.add(Locale.getDefault());
        localeList.add(Locale.ENGLISH);
    }



    public boolean contain(String s1, String s2) {
        for (Locale locale : localeList) {
            String tmp1 = s1.toLowerCase(locale);
            String tmp2 = s2.toLowerCase(locale);
            if (tmp1.contains(tmp2)) return true;
        }
        return false;
    }

}
