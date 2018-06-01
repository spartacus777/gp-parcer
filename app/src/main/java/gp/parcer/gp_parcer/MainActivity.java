package gp.parcer.gp_parcer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private ProgressBar pb;

    private boolean onWorkDone = false;

    private Parser.ParcerCallback callback = new Parser.ParcerCallback() {
        @Override
        public void onProgressChanged(final int size) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvResult.setText("" + size + "/" + Constants.NEEDED_EMAILS);
                }
            });
        }

        @Override
        public void onWorkDone() {
            if (onWorkDone){
                return;
            }

            onWorkDone = true;

            for (Model model : ModelHolder.getModels().values()) {
                Log.d(Constants.TAG, model.toString());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pb.setVisibility(View.GONE);
                    tvResult.setText("Done. Results under " + Constants.TAG + " tag");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView) findViewById(R.id.tvResult);
        pb = (ProgressBar) findViewById(R.id.pb);

        startParceJob();
    }


    private void startParceJob() {
        ModelHolder.init();

        new Parser(Constants.URL1, callback).parse();
        new Parser(Constants.URL2, callback).parse();
        new Parser(Constants.URL3, callback).parse();
        new Parser(Constants.URL4, callback).parse();
        new Parser(Constants.URL5, callback).parse();
        new Parser(Constants.URL6, callback).parse();
    }
}
