// Data class
public class Data
{
    private static Data instance;

    private Map<String, Double> map;
    private List<Integer> list;

    private TaskCallbacks callbacks;

    // Constructor
    private Data()
    {
    }

    // Get instance
    public static Data getInstance(TaskCallbacks callbacks)
    {
        if (instance == null)
            instance = new Data();

        instance.callbacks = callbacks;
        return instance;
    }

    // Set list
    public void setList(List<Integer> list)
    {
        this.list = list;
    }

    // Get list
    public List<Integer> getList()
    {
        return list;
    }

    // Set map
    public void setMap(Map<String, Double> map)
    {
        this.map = map;
    }

    // Get map
    public Map<String, Double> getMap()
    {
        return map;
    }

    // Start parse task
    protected void startParseTask(String url)
    {
        ParseTask parseTask = new ParseTask();
        parseTask.execute(url);
    }

    // ParseTask class
    protected class ParseTask
        extends AsyncTask<String, String, Map<String, Double>>
    {
        // The system calls this to perform work in a worker thread
        // and delivers it the parameters given to AsyncTask.execute()
        @Override
        protected Map doInBackground(String... urls)
        {
            // Get a parser
            Parser parser = new Parser();

            // Start the parser and report progress with the date
            if (parser.startParser(urls[0]) == true)
                publishProgress(parser.getDate());

            // Return the map
            return parser.getMap();
        }

        // On progress update
        @Override
        protected void onProgressUpdate(String... date)
        {
            if (callbacks != null)
                callbacks.onProgressUpdate(date);
        }

        // The system calls this to perform work in the UI thread and
        // delivers the result from doInBackground()
        @Override
        protected void onPostExecute(Map<String, Double> map)
        {
            if (callbacks != null)
                callbacks.onPostExecute(map);
        }
    }

    // TaskCallbacks interface
    interface TaskCallbacks
    {
        void onProgressUpdate(String... date);
        void onPostExecute(Map<String, Double> map);
    }
}