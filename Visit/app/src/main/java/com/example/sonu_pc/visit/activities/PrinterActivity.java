package com.example.sonu_pc.visit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.epos2.Log;
import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.ShowMsg;
import com.example.sonu_pc.visit.model.CouponModel;


public class PrinterActivity extends Activity implements ReceiveListener {
    private static final String TAG = PrinterActivity.class.getSimpleName();

    private Context mContext = null;
    private EditText mEditTarget = null;
    private Spinner mSpnSeries = null;
    private Spinner mSpnLang = null;
    private Printer  mPrinter = null;

    private String mStringTarget = null;


    private CouponModel mCouponModel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_printing);

        mContext = this;

        // To retrieve object in second Activity
        mCouponModel =  getIntent().getParcelableExtra(getString(R.string.intent_key_coupon));

        android.util.Log.d(TAG, "onCreate()");
        //android.util.Log.d(TAG, getIntent().getStringExtra(getString(R.string.intent_key_coupon)));

        //Bitmap bitmap = getIntent().getParcelableExtra(getString(R.string.intent_key_coupon));
       // byte[] byteArray = getIntent().getByteArrayExtra(getString(R.string.intent_key_coupon));
        //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        /*mCouponModel = new CouponModel();
        mCouponModel.setVisitee_name("visitee");
        mCouponModel.setVisitee_position("ceo");
        mCouponModel.setVisitor_name("visitor");
        mCouponModel.setCompany_name("company");
        mCouponModel.setVisitor_face_photo( BitmapFactory.decodeResource(getResources(), R.drawable.apple));*/



        try {
            Log.setLogSettings(mContext, Log.PERIOD_TEMPORARY, Log.OUTPUT_STORAGE, null, 0, 1, Log.LOGLEVEL_LOW);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "setLogSettings", mContext);
        }


        Intent intent;
        intent = new Intent(this, DiscoveryActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        android.util.Log.d(TAG, "onDiscoveryActivityResult()");
        if (data != null && resultCode == RESULT_OK) {
            String target = data.getStringExtra(getString(R.string.title_target));
            if (target != null) {

                mStringTarget = target;

                if(mCouponModel != null) {
                    android.util.Log.d(TAG, "coupon received");
                    runPrintCouponSequence(mCouponModel);
                }
            }
        }
    }



    private boolean runPrintCouponSequence(CouponModel couponModel) {
        if (!initializeObject()) {
            return false;
        }

        if (!createCouponData(couponModel)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }

    private String getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(currentTime);
    }

    private String getTime(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(currentTime);
    }

    private boolean createCouponData(CouponModel couponModel) {
        String method = "";
        Bitmap borderBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.roundborder2_1);

        final int pageAreaHeight = 400;
        final int pageAreaWidth = 600;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addPageBegin";
            mPrinter.addPageBegin();

            method = "addPageArea";
            mPrinter.addPageArea(0, 0, pageAreaWidth, pageAreaHeight);

            method = "addPageDirection";
            mPrinter.addPageDirection(Printer.DIRECTION_LEFT_TO_RIGHT);


            //border
            method = "addPagePosition";
            mPrinter.addPagePosition(0, borderBitmap.getHeight());

            method = "addImage";
            mPrinter.addImage(borderBitmap, 0, 0, borderBitmap.getWidth(), borderBitmap.getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, 3, Printer.PARAM_DEFAULT);
            android.util.Log.d("border details:", "h = " + borderBitmap.getHeight() + " w = " + borderBitmap.getWidth());


            // company name
            //TODO: Improve Company logo placement
            method = "addPagePosition";
            mPrinter.addPagePosition(pageAreaWidth/2 - 40, 50);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.TRUE, Printer.TRUE, Printer.PARAM_DEFAULT);

            method = "addTextSmooth";
            mPrinter.addTextSmooth(Printer.TRUE);

            method = "addText";
            mPrinter.addText(couponModel.getCompany_name());


            //photo
            method = "addPagePosition";
            mPrinter.addPagePosition(15, couponModel.getVisitor_face_photo().getHeight()+18);

            method = "addImage";
            mPrinter.addImage(couponModel.getVisitor_face_photo(), 0, 0, couponModel.getVisitor_face_photo().getWidth(), couponModel.getVisitor_face_photo().getHeight(), Printer.PARAM_DEFAULT, Printer.MODE_MONO_HIGH_DENSITY, Printer.PARAM_DEFAULT, 3, Printer.COMPRESS_NONE);
            android.util.Log.d("photo details:", "h = " + couponModel.getVisitor_face_photo().getHeight() + " w = " + couponModel.getVisitor_face_photo().getWidth());


            // add date of entry
            method = "addPagePosition";
            mPrinter.addPagePosition(couponModel.getVisitor_face_photo().getWidth() + 50, 50+50+50); // gap and text for company,visitor, and date

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.FALSE, Printer.PARAM_DEFAULT);

            method = "addText";
            mPrinter.addText(getDate());


            // add time of entry
            method = "addPagePosition";
            mPrinter.addPagePosition(couponModel.getVisitor_face_photo().getWidth() + 50, 50+50+50+50); // gap and text for company,visitor, and date

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.FALSE, Printer.PARAM_DEFAULT);

            method = "addText";
            mPrinter.addText(getTime());


            // visitor name
            method = "addPagePosition";
            mPrinter.addPagePosition(couponModel.getVisitor_face_photo().getWidth() + 50, 50+50);     //50 for company name and 50 for gap + visitor name height

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.TRUE, Printer.PARAM_DEFAULT);

            method = "addTextSmooth";
            mPrinter.addTextSmooth(Printer.TRUE);

            method = "addText";
            mPrinter.addText(couponModel.getVisitor_name());


            // to visit
            method = "addPagePosition";
            mPrinter.addPagePosition(15, 290);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.FALSE, Printer.PARAM_DEFAULT);

            method = "addTextSmooth";
            mPrinter.addTextSmooth(Printer.TRUE);

            method = "addText";
            mPrinter.addText("To visit:\n");


            // visitee name
            method = "addPagePosition";
            mPrinter.addPagePosition(15, 320);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.TRUE, Printer.PARAM_DEFAULT);

            method = "addTextSmooth";
            mPrinter.addTextSmooth(Printer.TRUE);

            method = "addText";
            mPrinter.addText(couponModel.getVisitee_name());

            // visitee position name
            method = "addPagePosition";
            mPrinter.addPagePosition(15, 350);

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);

            method = "addTextStyle";
            mPrinter.addTextStyle(Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.FALSE, Printer.PARAM_DEFAULT);

            method = "addTextSmooth";
            mPrinter.addTextSmooth(Printer.TRUE);

            method = "addText";
            mPrinter.addText(couponModel.getVisitee_position());


            // qr code
            method = "addPagePosition";
            mPrinter.addPagePosition(pageAreaWidth-200, pageAreaHeight-200);

            method = "addSymbol";
            mPrinter.addSymbol(couponModel.getVisitor_name() + couponModel.getVisitee_name(), Printer.SYMBOL_QRCODE_MODEL_2, Printer.PARAM_DEFAULT,  6, 6, 250);


            //end
            method = "addPageEnd";
            mPrinter.addPageEnd();

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_M30,
                    Printer.MODEL_ANK, mContext);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;


    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            android.util.Log.d(TAG, mStringTarget);
            //android.util.Log.d(TAG, mEditTarget.getText().toString());
            mPrinter.connect(mStringTarget, Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {

                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {

        }

        return true;
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private void dispPrinterWarnings(PrinterStatusInfo status) {
         //EditText edtWarnings = (EditText)findViewById(R.id.edtWarnings);
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

    }


    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                dispPrinterWarnings(status);


                //Move back to the first activity
                Intent intent = new Intent(PrinterActivity.this, MasterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }
}
