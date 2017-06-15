package vn.tpf.andping;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: hung.vo
 * Date: 2/1/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class TabPing extends Fragment {

    private static final int TASK_DONE = 1;
    private static final int TASK_CANCELLED = 0;
    private static final int ERROR = -1;

    private Button btnPing, btnStop;
    private EditText edtIp;
    private TextView txRs, txNPackets;
    private ProgressBar prStart;
    private SeekBar skTimes;
    //private PingTask pt;
    private ScrollView svScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        RelativeLayout thelayout  = (RelativeLayout)inflater.inflate(R.layout.ping_layout,container,false);

        skTimes =(SeekBar)thelayout.findViewById(R.id.seekTimes);
        txNPackets = (TextView)thelayout.findViewById(R.id.txPackets);
        btnPing = (Button)thelayout.findViewById(R.id.btPing);


        //Implement On Seek Bar Change
        skTimes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if (progress == 0){
                    txNPackets.setText("Unlimit");
                }
                else{
                    txNPackets.setText(progress + " Packets");
                }

            }
        });

        //Handle event button Ping click
        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return thelayout;
    }
}
