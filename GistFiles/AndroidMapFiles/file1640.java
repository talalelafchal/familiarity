public enum DTMFUtils {

    INSTANCE;

    private static final ODLLogger LOG = ODLLogger.getODLLogger(ResourceSyncConstants.LOGGER);
    private static final Map<Character, Character> KEYPAD_MAP = new HashMap<>();
    private static final String REGEX_ILLEGAL_CHARS = "[^\\p{IsAlphabetic}^\\p{IsDigit}]";
    private static final String EMPTY = "";

    static {
        KEYPAD_MAP.put('a', '2');
        KEYPAD_MAP.put('b', '2');
        KEYPAD_MAP.put('c', '2');
        KEYPAD_MAP.put('A', '2');
        KEYPAD_MAP.put('B', '2');
        KEYPAD_MAP.put('C', '2');
        KEYPAD_MAP.put('d', '3');
        KEYPAD_MAP.put('e', '3');
        KEYPAD_MAP.put('f', '3');
        KEYPAD_MAP.put('D', '3');
        KEYPAD_MAP.put('E', '3');
        KEYPAD_MAP.put('F', '3');
        KEYPAD_MAP.put('g', '4');
        KEYPAD_MAP.put('h', '4');
        KEYPAD_MAP.put('i', '4');
        KEYPAD_MAP.put('G', '4');
        KEYPAD_MAP.put('H', '4');
        KEYPAD_MAP.put('I', '4');
        KEYPAD_MAP.put('j', '5');
        KEYPAD_MAP.put('k', '5');
        KEYPAD_MAP.put('l', '5');
        KEYPAD_MAP.put('J', '5');
        KEYPAD_MAP.put('K', '5');
        KEYPAD_MAP.put('L', '5');
        KEYPAD_MAP.put('m', '6');
        KEYPAD_MAP.put('n', '6');
        KEYPAD_MAP.put('o', '6');
        KEYPAD_MAP.put('M', '6');
        KEYPAD_MAP.put('N', '6');
        KEYPAD_MAP.put('O', '6');
        KEYPAD_MAP.put('p', '7');
        KEYPAD_MAP.put('q', '7');
        KEYPAD_MAP.put('r', '7');
        KEYPAD_MAP.put('s', '7');
        KEYPAD_MAP.put('P', '7');
        KEYPAD_MAP.put('Q', '7');
        KEYPAD_MAP.put('R', '7');
        KEYPAD_MAP.put('S', '7');
        KEYPAD_MAP.put('t', '8');
        KEYPAD_MAP.put('u', '8');
        KEYPAD_MAP.put('v', '8');
        KEYPAD_MAP.put('T', '8');
        KEYPAD_MAP.put('U', '8');
        KEYPAD_MAP.put('V', '8');
        KEYPAD_MAP.put('w', '9');
        KEYPAD_MAP.put('x', '9');
        KEYPAD_MAP.put('y', '9');
        KEYPAD_MAP.put('z', '9');
        KEYPAD_MAP.put('W', '9');
        KEYPAD_MAP.put('X', '9');
        KEYPAD_MAP.put('Y', '9');
        KEYPAD_MAP.put('Z', '9');
    }

    /**
     * Converts the provided letters to digits (understood by phones)
     *
     * @param candidate _
     * @return _
     */
    public String convertKeypadLettersToDigits(String candidate) {
        if (StringUtils.isEmpty(candidate)) {
            return candidate;
        }
        LOG.fine(String.format("Converting {%s} to digits. Stripping all characters that is not of type {%s}", candidate, REGEX_ILLEGAL_CHARS));
        candidate = candidate.replaceAll(REGEX_ILLEGAL_CHARS, EMPTY);
        if (StringUtils.isEmpty(candidate)) {
            LOG.info(String.format("A string were provided, however after transforming it using {%s}, there were no characters left. Skipping", REGEX_ILLEGAL_CHARS));
            return candidate;
        }
        int len = candidate.length();
        char[] out = candidate.toCharArray();
        for (int i = 0; i < len; i++) {
            char c = out[i];
            Character keyCandidate = KEYPAD_MAP.get(c);
            if (keyCandidate == null) {
                continue;
            }
            out[i] = keyCandidate;
        }
        return new String(out);
    }

}