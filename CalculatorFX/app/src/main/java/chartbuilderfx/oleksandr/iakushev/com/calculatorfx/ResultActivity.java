package chartbuilderfx.oleksandr.iakushev.com.calculatorfx;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int DEFAULT_XBEGIN = -10;
    private static final int DEFAULT_XEND = 10;

    private TextView dialog_info;
    private EditText editTextXBeg, editTextXEnd;
    private double pointXBeg, pointXEnd;
    private ChartCanvas chartCanvas;
    private ChartData chartData;
    private String funcTxt;
    private Button btnBack, btnRefresh;
    AlertDialog alertDialog;
    private Handler handler;
    private int widthChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        handler = new Handler();

        funcTxt = this.getIntent().getExtras().getString(IdExtras.ID_RESULT_EXTRAS);

        dialog_info = new TextView(this);
        dialog_info.setPadding(20,7,0,0);
        editTextXBeg = (EditText) this.findViewById(R.id.editTextXBeg);
        editTextXEnd = (EditText) this.findViewById(R.id.editTextXEnd);
        chartCanvas = (ChartCanvas) this.findViewById(R.id.chartcanvas);
        chartCanvas.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Copy", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(
                        IdExtras.LABEL_CLIPBOARD,
                        dialog_info.getText().toString());
                clipboard.setPrimaryClip(clip);
                dialog.cancel();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        builder.setView(dialog_info);
        alertDialog = builder.create();

        btnBack = (Button) this.findViewById(R.id.buttonback);
        btnBack.setOnClickListener(this);
        btnRefresh = (Button) this.findViewById(R.id.buttonrefresh);
        btnRefresh.setOnClickListener(this);
        btnRefresh.setEnabled(false);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        widthChart = metrics.widthPixels;

        setDefaultPointsX();
        Thread threadChart = new Thread(new CalcDataRunnable(widthChart));
        threadChart.start();
        showToastShort(this.getString(R.string.wait_msg));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonback) {
            this.finish();
        }

        if (v.getId() == R.id.buttonrefresh) {
            getPointsX();
            if (pointXEnd<=pointXBeg) {
                setDefaultPointsX();
                showToastLong(this.getString(R.string.err_pointsx));
            }
            else {
                btnRefresh.setEnabled(false);
                Thread threadChart = new Thread(new CalcDataRunnable(widthChart));
                threadChart.start();
            }
        }

        if (v.getId() == R.id.chartcanvas) {
            if (chartData != null) {
                dialog_info.setText(Html.fromHtml(chartData.toHtmlText()));
                alertDialog.show();
            }
        }
    }

    private void setDefaultPointsX() {
        pointXBeg = DEFAULT_XBEGIN;
        pointXEnd = DEFAULT_XEND;
        editTextXBeg.setText(Double.toString(pointXBeg));
        editTextXEnd.setText(Double.toString(pointXEnd));
    }

    private void getPointsX() {
        try {
            pointXBeg = Double.parseDouble(editTextXBeg.getText().toString());
            pointXEnd = Double.parseDouble(editTextXEnd.getText().toString());
        }
        catch (NumberFormatException e) {
            setDefaultPointsX();
            showToastLong(e.getMessage());
        }
    }

    private void showToastLong(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void showToastShort(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private class CalcDataRunnable implements Runnable {
        private int nPoints;

        public CalcDataRunnable(int nPoints) {
            this.nPoints = nPoints;
        }

        @Override
        public void run() {
            try {
                ProviderData providerData = new ProviderData();
                providerData.setFunction(funcTxt);
                providerData.setPointXBegin(pointXBeg);
                providerData.setPointXEnd(pointXEnd);
                providerData.setnPoints(nPoints);
                providerData.checkFunction();

                chartData = providerData.getChartData();
                ArrayList<ChartPointXY> pointsXY = providerData.getArrayPointXY();
                handler.post(new ShowChartRunnable(pointsXY));

            } catch (Exception e) {
                handler.post(new ShowErrorRunnable(e.getMessage()));
                Log.e(IdExtras.NAMETAG_LOGGER, e.toString());
            }
        }

    }

    private class ShowChartRunnable implements Runnable {
        private ArrayList<ChartPointXY> pointsXY;

        public ShowChartRunnable(ArrayList<ChartPointXY> pointsXY) {
            this.pointsXY = pointsXY;
        }

        @Override
        public void run() {
            chartCanvas.setPointsXY(pointsXY);
            btnRefresh.setEnabled(true);
            showToastShort(getString(R.string.press_msg));
        }

    }

    private class ShowErrorRunnable implements Runnable {
        private String txtError;

        public ShowErrorRunnable(String txtError) {
            this.txtError = txtError;
        }

        @Override
        public void run() {
            showToastLong(txtError);
            btnRefresh.setEnabled(true);
        }

    }
}
