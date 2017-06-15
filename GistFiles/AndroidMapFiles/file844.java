interface ICommand {
    String getKey();
    void onChange(Map<String, Object> props);
}

class NotifyTextView extends TextView implements ICommand {
    
    ...
        
    @Override
    public String getKey() {
        return "topic";
    }

    @Override
    public void onChange(Map<String, Object> props) {
        setText((String) props.get("topic"))
    }
}

class Presenter {
    Map<String, List<ICommand>> keyViewMap;
    public onTopicChange(Map<String, Object> props) {
        ...
        for command : keyViewMap.get("topic") {
            command.onChange(props);
        }
    }
}