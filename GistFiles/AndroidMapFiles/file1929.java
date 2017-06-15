public class MarkupFormatter implements Html.TagHandler {

    public enum Tag {
        LI(ListItemSpan.class) {
            @Override
            public Object createSpan(Context context, Map<String, String> attributes) {
                return new ListItemSpan();
            }
        };

        private final Class<?> mType;

        Tag(Class<?> type) {
            mType = type;
        }

        public Class<?> getType() {
            return mType;
        }

        public abstract Object createSpan(Context context, Map<String, String> attributes);

        public static Tag fromString(String text) {
            if (text != null) {
                for (Tag b : Tag.values()) {
                    if (text.equalsIgnoreCase(b.name())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    private final Context mContext;

    public MarkupFormatter(Context context) {
        mContext = context;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        Tag tagSpan = Tag.fromString(tag);
        if (tagSpan != null) {
            int len = output.length();
            if (opening) {
                Object span = tagSpan.createSpan(mContext, processAttributes(xmlReader));
                if (span instanceof TextDecoration) {
                    ((TextDecoration) span).insertBefore(output);
                }
                output.setSpan(span, len, len, Spannable.SPAN_MARK_MARK);
            } else {
                Object span = getLastSpan(output, tagSpan.getType());
                int where = output.getSpanStart(span);
                output.removeSpan(span);
                if (span instanceof TextDecoration) {
                    ((TextDecoration) span).appendAfter(output);
                    len = output.length();
                }
                if (where != len) {
                    output.setSpan(span, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }


    private Map<String, String> processAttributes(XMLReader xmlReader) {
        final Map<String, String> attributes = new HashMap<>();
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[])dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = lengthField.getInt(atts);

            /**
             * MSH: Look for supported attributes and add to hash map.
             * This is as tight as things can get :)
             * The data index is "just" where the keys and values are stored.
             **/
            for(int i = 0; i < len; i++) {
                attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
            }
        } catch (Exception ignored) { }
        return attributes;
    }

    private Object getLastSpan(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }

    public void clearSpan(Tag tag, Editable editable) {
        Object[] spans = editable.getSpans(0, editable.length(), tag.getType());
        for (Object span : spans) {
            editable.removeSpan(span);
        }
    }

    public Spanned format(int res, Object... args) {
        return format(mContext.getString(res), args);
    }

    public Spanned format(String template, Object... args) {
        Object[] safeArgs = new Object[args.length];
        for (int i = 0; i != args.length; i++) {
            if (args[i] instanceof String) {
                safeArgs[i] = TextUtils.htmlEncode(String.valueOf(args[i]));
            } else {
                safeArgs[i] = args[i];
            }
        }
        return Html.fromHtml(String.format(template, safeArgs), null, this);
    }

    public interface TextDecoration {
        void insertBefore(Editable editable);
        void appendAfter(Editable editable);
    }

    public static class ListItemSpan implements TextDecoration {

        @Override
        public void insertBefore(Editable editable) {
            editable.append("・ ");
        }

        @Override
        public void appendAfter(Editable editable) {
            editable.append("\n");
        }
    }
}