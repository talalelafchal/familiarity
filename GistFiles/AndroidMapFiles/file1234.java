//Breadth first view tree -> json transformation

    private static final String CHILDREN = "children";

    @SuppressWarnings("unchecked")
    private Object extract(ViewGroup container) throws JSONException {

        final Map<String, Object> result = new HashMap<>(); //create root node
        List<Object> currentNodeList = new LinkedList<>();

        result.put(CHILDREN, currentNodeList);
        final LinkedList<Object> todo = new LinkedList<>();
        todo.add(container); //append first view

        while (!todo.isEmpty()) {
            final Object current = todo.removeFirst(); //deque next instruction

            if (current instanceof List) { //switch root
                currentNodeList = (List<Object>) current;
            } else if (current instanceof ViewGroup) { //enqueue root switch instruction then enqueue children
                final Map<String, Object> viewProps = extractViewDimensions((View) current);
                //noinspection MismatchedQueryAndUpdateOfCollection
                final List<Object> childHolder = new LinkedList<>();
                viewProps.put(CHILDREN, childHolder);

                //noinspection unchecked
                currentNodeList.add(viewProps);

                //enqueue root switch instruction
                todo.addLast(childHolder);

                final int childCount = ((ViewGroup) current).getChildCount();
                for (int i = 0; i < childCount; i++) {
                    todo.addLast(((ViewGroup) current).getChildAt(i));
                }

            } else if (current instanceof View) { //just process view, nothing special to do here
                final Map<String, Object> viewProps = extractViewDimensions((View) current);
                //noinspection unchecked
                currentNodeList.add(viewProps);
            }
        }

        final List<Object> top = ((List<Object>) result.get(CHILDREN));
        return top.get(0);
    }

    private Map<String, Object> extractViewDimensions(View target) throws JSONException {
        final HashMap<String, Object> map = new HashMap<>();

        for (ViewProperty property : ViewProperty.values()) {
            map.put(property.name(), property.getPropertyValue(target));
        }

        return map;
    }