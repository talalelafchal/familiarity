package sagarpreet97.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FlipCard_activity extends AppCompatActivity implements CardFrontFragment.frontListener , CardBackFragment.backtListener {

    String title , desc ;
    boolean mShowingBack=false ;
    CardBackFragment gf=new CardBackFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final CardFrontFragment bf = new CardFrontFragment();
        bf.setFragmentListener(this);
        setContentView(R.layout.content_flip_card_activity);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, bf)
                    .commit();
        }

        Button button=(Button)findViewById(R.id.button) ;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });




        gf.setBackFragmentListener(this);

        Button add=(Button)findViewById(R.id.addfaq) ;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mtitle=(EditText)bf.v.findViewById(R.id.editText3) ;
                EditText mdesc=(EditText)gf.v.findViewById(R.id.editText4) ;
                title=mtitle.getText().toString() ;
                desc=mdesc.getText().toString() ;
                if(title!="" && desc!="")
                {

                    Intent f=new Intent() ;
                    f.putExtra("title" , title) ;
                    f.putExtra("desc" , desc) ;
                    setResult(3 , f);
                    finish();
                }
                else
                {
                    Toast.makeText(FlipCard_activity.this , "Add Title as well as Descrition " , Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cncel=(Button)findViewById(R.id.cancel) ;
        cncel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }




    @Override
    public void getTitle(String mtitle) {
            title=mtitle ;
    }

//
//
//    public static class CardFrontFragment extends Fragment {
//
//        public CardFrontFragment() {
//
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View v= inflater.inflate(R.layout.fragment_card_front, container, false);
////          EditText title=(EditText)v.findViewById(R.id.editText3) ;
// //           mt=title.getText().toString() ;
//
//            return v ;
//        }
//    }

//    /**
//     * A fragment representing the back of the card.
//     */
//    public static class  CardBackFragment extends Fragment {
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            return inflater.inflate(R.layout.fragment_card_back, container, false);
//        }
//    }


    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            mShowingBack=false ;
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.

        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in ,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                .replace(R.id.container, gf)

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();
    }

    @Override
    public void getDesc(String mtitle) {
        desc=mtitle ;
    }
}
