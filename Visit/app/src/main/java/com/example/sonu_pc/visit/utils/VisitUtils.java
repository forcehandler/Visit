package com.example.sonu_pc.visit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.Preference;
import com.example.sonu_pc.visit.model.preference_model.PreferencesModel;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sonupc on 14-01-2018.
 */

public class VisitUtils {

    public static final String SCREEN_ORDER_KEY_PREFIX = "ID_";
    public static PreferencesModel getPreferences(Context context){
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(context.getString(R.string.REMOTE_CONFIG_PREFERENCE), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String preference_object_json = sharedPreferences.getString(context.getString(R.string.CONFIGURATION_PREFERENCE_KEY), "");

        PreferencesModel preferencesModel = gson.fromJson(preference_object_json, PreferencesModel.class);

        return preferencesModel;
    }

    public static MasterWorkflow getDefaultMasterWorkflow(Context context){

        // Setup the screens
        // TextInput

        TextInputPreferenceModel textInputPreferenceModel = new TextInputPreferenceModel();
        textInputPreferenceModel.setPage_title("Sample Page Title");

        List<String> hints = new ArrayList<>();
        hints.add("hint1");
        hints.add("hint2");
        textInputPreferenceModel.setHints(hints);
        //textInputPreferenceModel.setType(context.getString(R.string.CLASS_TEXTINPUT));

        // Survey1
        SurveyPreferenceModel surveyPreferenceModel = new SurveyPreferenceModel();
        String survey_title = "Sample survey";
        ArrayList<String> survey_item_titles = new ArrayList<>();
        survey_item_titles.add("Sample item 1");
        survey_item_titles.add("Sample item 2");
        survey_item_titles.add("Sample item 3");

        ArrayList<String> options1 = new ArrayList<String>();
        ArrayList<String> options2 = new ArrayList<String>();
        ArrayList<String> options3 = new ArrayList<String>();

        options1.add("options11");
        options1.add("options12");
        options1.add("options13");

        options2.add("options21");
        options2.add("options22");
        options2.add("options23");

        options3.add("options31");
        options3.add("options32");
        options3.add("options33");

        ArrayList<ArrayList<String>> survey_item_options = new ArrayList<>();
        survey_item_options.add(options1);
        survey_item_options.add(options2);
        survey_item_options.add(options3);

        surveyPreferenceModel.setSurvey_title(survey_title);
        surveyPreferenceModel.setSurvey_item_name(survey_item_titles);
        surveyPreferenceModel.setSurvey_item_options(survey_item_options);
        //surveyPreferenceModel.setType(context.getString(R.string.CLASS_SURVEYINPUT));

        // Survey 2
        SurveyPreferenceModel surveyPreferenceModel1 = new SurveyPreferenceModel();
        String survey_title1 = "Sample survey";
        ArrayList<String> survey_item_titles1 = new ArrayList<>();
        survey_item_titles1.add("Sample item 1");
        survey_item_titles1.add("Sample item 2");
        survey_item_titles1.add("Sample item 3");

        ArrayList<String> options11 = new ArrayList<String>();
        ArrayList<String> options21 = new ArrayList<String>();
        ArrayList<String> options31 = new ArrayList<String>();

        options11.add("options11_!");
        options11.add("options12_1");
        options11.add("options13_!");

        options21.add("options21_1");
        options21.add("options22");
        options21.add("options23");

        options31.add("options131");
        options31.add("1");
        options31.add("options133");

        ArrayList<ArrayList<String>> survey_item_options1 = new ArrayList<>();
        survey_item_options1.add(options11);
        survey_item_options1.add(options21);
        survey_item_options1.add(options31);

        surveyPreferenceModel1.setSurvey_title(survey_title1);
        surveyPreferenceModel1.setSurvey_item_name(survey_item_titles1);
        surveyPreferenceModel1.setSurvey_item_options(survey_item_options1);
        //surveyPreferenceModel1.setType(context.getString(R.string.CLASS_SURVEYINPUT));


        // ThankYou object
        ThankYouPreference thankYouPreference = getDefaultThankYouPreference();

        // Workflow1
        PreferencesModel preferencesModel = new PreferencesModel();
        preferencesModel.setSignup_time(System.currentTimeMillis());
        preferencesModel.setStage1(true);
        preferencesModel.setStage2(true);
        preferencesModel.setStage3(false);
        preferencesModel.setStage4(false);
        preferencesModel.setStage5(false);

        preferencesModel.setInstituteEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // Setup the workflow1
        ArrayList<Preference> workflow_order = new ArrayList<>();
        workflow_order.add(textInputPreferenceModel);
        workflow_order.add(surveyPreferenceModel);
        workflow_order.add(surveyPreferenceModel1);
        workflow_order.add(thankYouPreference);

        preferencesModel.setOrder_of_screens(workflow_order);
        // Create master workflow object
        MasterWorkflow masterWorkflow = new MasterWorkflow();
        LinkedHashMap<String, PreferencesModel> workflows_map = new LinkedHashMap<String, PreferencesModel>();
        workflows_map.put("Enquiry_workflow", preferencesModel);
        masterWorkflow.setWorkflows_map(workflows_map);

        return masterWorkflow;

    }

    public static TextInputPreferenceModel getDefaultTextInputPreferenceModel(){
        // TextInput

        TextInputPreferenceModel textInputPreferenceModel = new TextInputPreferenceModel();
        textInputPreferenceModel.setPage_title("Sample Page Title");

        List<String> hints = new ArrayList<>();
        hints.add("hint1");
        hints.add("hint2");
        textInputPreferenceModel.setHints(hints);
        return textInputPreferenceModel;
    }

    public static SurveyPreferenceModel getDefaultSurveyPreferenceModel(){
        // Survey1
        SurveyPreferenceModel surveyPreferenceModel = new SurveyPreferenceModel();
        String survey_title = "Sample survey";
        ArrayList<String> survey_item_titles = new ArrayList<>();
        survey_item_titles.add("Sample item 1");
        survey_item_titles.add("Sample item 2");
        survey_item_titles.add("Sample item 3");

        ArrayList<String> options1 = new ArrayList<String>();
        ArrayList<String> options2 = new ArrayList<String>();
        ArrayList<String> options3 = new ArrayList<String>();

        options1.add("options11");
        options1.add("options12");
        options1.add("options13");

        options2.add("options21");
        options2.add("options22");
        options2.add("options23");

        options3.add("options31");
        options3.add("options32");
        options3.add("options33");

        ArrayList<ArrayList<String>> survey_item_options = new ArrayList<>();
        survey_item_options.add(options1);
        survey_item_options.add(options2);
        survey_item_options.add(options3);

        surveyPreferenceModel.setSurvey_title(survey_title);
        surveyPreferenceModel.setSurvey_item_name(survey_item_titles);
        surveyPreferenceModel.setSurvey_item_options(survey_item_options);

        return surveyPreferenceModel;
    }

    public static ThankYouPreference getDefaultThankYouPreference(){
        ThankYouPreference thankYouPreference = new ThankYouPreference();
        thankYouPreference.setThank_you_text("Default thank you");
        return thankYouPreference;
    }
}
