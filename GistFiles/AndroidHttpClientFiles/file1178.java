package ru.happy_giraffe.mypediatrician;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ru.happy_giraffe.mypediatrician.Models.Question;
import ru.happy_giraffe.mypediatrician.Models.User;

/**
 * Created by RomanByakov on 04/02/16.
 */
public class QuestionFragment extends Fragment {

    private static final String TAG = "QuestionFragment";

    public static boolean active;
    LinearLayoutManager llm;
    RecyclerView rv;
    private ArrayList<Question> questions;
    QuestionsAdapter questionsAdapter;
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    private boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;

    ProgressBar progressBar;

    Button new_questions_button;

    Question new_question;

    Context questionFragmentContext = null;

    public QuestionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionFragmentContext = getContext();
        questions = new ArrayList<>();
        getQuestions(page);
        questionsAdapter = new QuestionsAdapter(getContext(), questions);
        active = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        new_questions_button = (Button) view.findViewById(R.id.new_questions_button);
        setRetainInstance(true);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = (RecyclerView) view.findViewById(R.id.questions_list);
        llm = new LinearLayoutManager(questionFragmentContext);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        // I load the next page of gigs using a background task,
                        // but you can call any function here.
                        page++;
                        getQuestions(page);
                        loading = true;
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        getActivity().registerReceiver(questionsBroadcastReceiver, new IntentFilter("questions"));
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
        getActivity().unregisterReceiver(questionsBroadcastReceiver);
    }

    public void getQuestions(int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("access_token", User.getInstance().getToken().getAccess_token(questionFragmentContext));
        params.add("page", String.valueOf(page));
        params.add("order", "dtimeCreate desc");
        params.add("expand", "author,category");
        client.setTimeout(30000);
        client.get(getResources().getString(R.string.host) + getResources().getString(R.string.api_version) + "/api/questions/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new Gson();
                Type questionsType = new TypeToken<ArrayList<Question>>() {
                }.getType();
                questions = gson.fromJson(response.toString(), questionsType);
                questionsAdapter.questionData.addAll(questions);
                rv.swapAdapter(questionsAdapter, false);
                questionsAdapter.notifyDataSetChanged();
                loading = false;
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                for (Header header : headers) {
                    if (!header.getName().equals("X-Has-Next") & !header.getValue().equals(true)) {
                        Toast.makeText(getContext(), "Поздравляем, Вы дочитали до конца", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), "Произошла ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
                    loading = false;
                }
                Toast.makeText(getContext(), "Произошла ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (questions.isEmpty()) {
                    rv.setAdapter(null);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void updateQuestions(int id) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        RequestParams params = new RequestParams();
        params.add("access_token", User.getInstance().getToken().getAccess_token(questionFragmentContext));
        params.add("id", String.valueOf(id));
        params.add("expand", "author,category");
        client.get(getResources().getString(R.string.host) + getResources().getString(R.string.api_version) + "/api/questions/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new Gson();
                Type questionsType = new TypeToken<ArrayList<Question>>() {
                }.getType();
                questions = gson.fromJson(response.toString(), questionsType);
                questionsAdapter.questionData.addAll(0, questions);
                rv.setAdapter(questionsAdapter);
                questionsAdapter.notifyItemRangeInserted(0, questions.size());
                rv.smoothScrollToPosition(0);
                new_questions_button.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new Gson();
                new_question = gson.fromJson(response.toString(), Question.class);
                questionsAdapter.questionData.add(0, new_question);
                rv.swapAdapter(questionsAdapter, false);
                questionsAdapter.notifyItemInserted(0);
                rv.smoothScrollToPosition(0);
                new_questions_button.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                new_questions_button.setVisibility(View.GONE);
                Toast.makeText(questionFragmentContext, "Вопрос был удалён", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BroadcastReceiver questionsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            new_questions_button.setVisibility(View.VISIBLE);
            new_questions_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateQuestions(intent.getIntExtra("id", 0));
                    Log.d(TAG, String.valueOf(intent.getExtras()));
                    Log.d(TAG, String.valueOf(intent.getIntExtra("id", 0)));
                }
            });
        }
    };
}
