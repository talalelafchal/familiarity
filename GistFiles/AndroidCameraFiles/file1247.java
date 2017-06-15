package com.getbewarned.bewarned.util;

import com.getbewarned.bewarned.Constants;

import java.util.*;

/**
 * Created by lr on 01.12.2015.
 */
public class Rythms extends HashMap<Integer, List<Morse>> implements Map<Integer, List<Morse>> {
    private Rythms() {
    }

    public static Map<Integer, List<Morse>> getRythms() {
        List dogRythm = Arrays.asList(new Morse[]{Morse.Dot, Morse.Dash});
        List humanRythm = Arrays.asList(new Morse[]{Morse.Dash, Morse.Dash});
        List sirenRythm = Arrays.asList(new Morse[]{Morse.Dot, Morse.Dot, Morse.Dash});
        List hornRythm = Arrays.asList(new Morse[]{Morse.Dash, Morse.Dot, Morse.Dot});
        List quesRythm = Arrays.asList(new Morse[]{Morse.Dot, Morse.Dot, Morse.Dot, Morse.Dot});


        Map<Integer, List<Morse>> modifiableMap = new HashMap<>();
        modifiableMap.put(Constants.Messages.DOG_EVENT, dogRythm);
        modifiableMap.put(Constants.Messages.HUMAN_EVENT, humanRythm);
        modifiableMap.put(Constants.Messages.SIREN_EVENT, sirenRythm);
        modifiableMap.put(Constants.Messages.HORN_EVENT, hornRythm);
        modifiableMap.put(Constants.Messages.QUES_EVENT, quesRythm);

        return Collections.unmodifiableMap(modifiableMap);
    }
}