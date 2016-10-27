package com.matiullahkarimi.onlineshopping;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Karimi on 10/22/2016.
 */
public class OrderDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View modifyView = inflater.inflate(R.layout.custom_dialog, null);

        // initializing views
        final NumberPicker numberPicker = (NumberPicker) modifyView.findViewById(R.id.amount);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        numberPicker.setWrapSelectorWheel(true);

        final EditText numberEdit = (EditText) modifyView.findViewById(R.id.contact_number);
        final EditText addressEdit = (EditText) modifyView.findViewById(R.id.address);


            final Spinner colorSpinner = (Spinner) modifyView.findViewById(R.id.spinner_color);
            final Spinner sizeSpinner = (Spinner) modifyView.findViewById(R.id.spinner_size);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.colors, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            colorSpinner.setAdapter(colorAdapter);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.sizes, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            sizeSpinner.setAdapter(sizeAdapter);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(modifyView)
                // Add action buttons
                .setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        String amount = Integer.toString(numberPicker.getValue());
                        String color = colorSpinner.getSelectedItem().toString();
                        String size = sizeSpinner.getSelectedItem().toString();
                        String address = addressEdit.getText().toString();
                        String contactNumber = numberEdit.getText().toString();
                        String pId = getArguments().getString("pId");

                        Dialog dialogContext  = (Dialog) dialog;
                        final Context context = dialogContext.getContext();

                        ProductClient client = new ProductClient();
                        client.order(pId, amount, color, size, address, contactNumber, new Helper().getToken(getActivity()),
                                new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);

                                try {
                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                                    Log.d("sucess", response.getString("message"));


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                new Helper().toast(getActivity(), "Something went wrong!!!");
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
