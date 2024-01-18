package chartbuilderfx.oleksandr.iakushev.com.calculatorfx;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int[] BUTTONS = {
            R.id.buttonBuildChart,
            R.id.buttond0,
            R.id.buttond1,
            R.id.buttond2,
            R.id.buttond3,
            R.id.buttond4,
            R.id.buttond5,
            R.id.buttond6,
            R.id.buttond7,
            R.id.buttond8,
            R.id.buttond9,
            R.id.buttondpoint,
            R.id.buttonbreakbeg,
            R.id.buttonbreakend,
            R.id.buttonopower,
            R.id.buttonomulty,
            R.id.buttonodiv,
            R.id.buttonoplus,
            R.id.buttonominus,
            R.id.buttondel,
            R.id.buttonclr,
            R.id.buttonvx,
            R.id.buttonvpi,
            R.id.buttonve,
            R.id.buttonfsin,
            R.id.buttonfcos,
            R.id.buttonftan,
            R.id.buttonfasin,
            R.id.buttonfacos,
            R.id.buttonfatan,
            R.id.buttonfsqrt,
            R.id.buttonfln,
            R.id.buttonfexp,
            R.id.buttonendleft,
            R.id.buttonleft,
            R.id.buttonendright,
            R.id.buttonright};

    private EditText editTextFunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSizeForSmallButtons();

        editTextFunc = (EditText) this.findViewById(R.id.editFunc);
        editTextFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        for (int btnID : BUTTONS) {
            this.findViewById(btnID).setOnClickListener(this);
        }

    }


    private void setSizeForSmallButtons() {
        DisplayMetrics size = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(size);
        int maxWidth = size.widthPixels;
        int maxHeight = size.heightPixels;
        int padding = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
        int btnWidth = (maxWidth - 2 * padding) / 6;
        int btnHeight = (int) (maxHeight*0.7/7.0);

        for (int btnID : BUTTONS) {
            Button btnItem = (Button) this.findViewById(btnID);
            btnItem.setWidth(btnWidth);
            btnItem.setHeight(btnHeight);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            this.finish();
        } else if (id == R.id.action_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("About");
            alertDialog.setMessage(getString(R.string.created_by));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else if (id == R.id.action_copy_text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(
                    IdExtras.LABEL_CLIPBOARD,
                    editTextFunc.getText().toString());
            clipboard.setPrimaryClip(clip);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBuildChart:
                showResultActivity();
                break;

            case R.id.buttond0:
                appendStringToEditFunc("0");
                break;
            case R.id.buttond1:
                appendStringToEditFunc("1");
                break;
            case R.id.buttond2:
                appendStringToEditFunc("2");
                break;
            case R.id.buttond3:
                appendStringToEditFunc("3");
                break;
            case R.id.buttond4:
                appendStringToEditFunc("4");
                break;
            case R.id.buttond5:
                appendStringToEditFunc("5");
                break;
            case R.id.buttond6:
                appendStringToEditFunc("6");
                break;
            case R.id.buttond7:
                appendStringToEditFunc("7");
                break;
            case R.id.buttond8:
                appendStringToEditFunc("8");
                break;
            case R.id.buttond9:
                appendStringToEditFunc("9");
                break;
            case R.id.buttondpoint:
                appendStringToEditFunc(".");
                break;
            case R.id.buttonbreakbeg:
                appendStringToEditFunc("(");
                break;
            case R.id.buttonbreakend:
                appendStringToEditFunc(")");
                break;

            case R.id.buttonopower:
                appendStringToEditFunc("^");
                break;
            case R.id.buttonomulty:
                appendStringToEditFunc("*");
                break;
            case R.id.buttonodiv:
                appendStringToEditFunc("/");
                break;
            case R.id.buttonoplus:
                appendStringToEditFunc("+");
                break;
            case R.id.buttonominus:
                appendStringToEditFunc("-");
                break;

            case R.id.buttondel:
                delLastCharEdiFunc();
                break;
            case R.id.buttonclr:
                clearEditFunc();
                break;

            case R.id.buttonvpi:
                appendStringToEditFunc("pi");
                break;
            case R.id.buttonvx:
                appendStringToEditFunc("x");
                break;
            case R.id.buttonve:
                appendStringToEditFunc("e");
                break;

            case R.id.buttonfsin:
                appendFStringToEditFunc("sin()");
                break;
            case R.id.buttonfcos:
                appendFStringToEditFunc("cos()");
                break;
            case R.id.buttonftan:
                appendFStringToEditFunc("tan()");
                break;
            case R.id.buttonfasin:
                appendFStringToEditFunc("asin()");
                break;
            case R.id.buttonfacos:
                appendFStringToEditFunc("acos()");
                break;
            case R.id.buttonfatan:
                appendFStringToEditFunc("atan()");
                break;
            case R.id.buttonfsqrt:
                appendFStringToEditFunc("sqrt()");
                break;
            case R.id.buttonfln:
                appendFStringToEditFunc("ln()");
                break;
            case R.id.buttonfexp:
                appendFStringToEditFunc("exp()");
                break;
            case R.id.buttonendleft:
                editTextFunc.setSelection(0);
                break;
            case R.id.buttonendright:
                editTextFunc.setSelection(editTextFunc.getText().length());
                break;
            case R.id.buttonleft:
                int selectionLeft = editTextFunc.getSelectionStart();
                if (selectionLeft > 0)
                    editTextFunc.setSelection(selectionLeft - 1);
                break;
            case R.id.buttonright:
                int selectionRight = editTextFunc.getSelectionStart();
                if (selectionRight < editTextFunc.getText().length())
                    editTextFunc.setSelection(selectionRight + 1);
                break;

        }
    }

    private void appendStringToEditFunc(String txt) {
        int indexCur = editTextFunc.getSelectionStart();
        editTextFunc.getEditableText().insert(indexCur, txt);
    }

    private void appendFStringToEditFunc(String txt) {
        int indexCur = editTextFunc.getSelectionStart();
        editTextFunc.getEditableText().insert(indexCur, txt);
        indexCur += txt.length() - 1;
        editTextFunc.setSelection(indexCur);
    }

    private void delLastCharEdiFunc() {
        int indexCur = editTextFunc.getSelectionStart();

        if (indexCur > 0) {
            editTextFunc.getEditableText().delete(indexCur - 1, indexCur);
        }
    }

    private void clearEditFunc() {
        editTextFunc.setText("");
    }

    private void showResultActivity() {
        String funcTxt = editTextFunc.getText().toString();

        if (!ProviderData.isFunction(funcTxt)) {
            try {
                editTextFunc.setText(ProviderData.getExpressionValue(funcTxt));
                editTextFunc.setSelection(editTextFunc.getText().length());
            } catch (Exception e) {
                showToastLong(e.getMessage());
                Log.e(IdExtras.NAMETAG_LOGGER, e.toString());
            }
            return;
        }

        if (funcTxt != null && !funcTxt.equals("") && funcTxt.length() > 1) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra(IdExtras.ID_RESULT_EXTRAS, funcTxt);
            this.startActivity(intent);
        } else {
            showToastLong(this.getString(R.string.err_emptyfunc));
        }
    }

    private void showToastLong(String txt) {
        Toast toast = Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

}
