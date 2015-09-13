package deitel.com.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by akash on 8/22/2015.
 */
public class AddEditFragment extends Fragment {
    public interface AddEditFragmentListener
    {
        public void onAddEditCompleted(long rowID);
    }

    private AddEditFragmentListener listener;

    private long rowID; // database row ID of the contact
    private Bundle contactInfoBundle; // arguments for editing a contact

    // EditTexts for contact information
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText streetEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText zipEditText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (AddEditFragmentListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        streetEditText = (EditText) view.findViewById(R.id.streetEditText);
        cityEditText = (EditText) view.findViewById(R.id.cityEditText);
        stateEditText = (EditText) view.findViewById(R.id.stateEditText);
        zipEditText = (EditText) view.findViewById(R.id.zipEditText);

        contactInfoBundle = getArguments();

        if(contactInfoBundle != null)
        {
            rowID = contactInfoBundle.getLong(MainActivity.ROW_ID);
            nameEditText.setText(contactInfoBundle.getString("name"));
            phoneEditText.setText(contactInfoBundle.getString("phone"));
            emailEditText.setText(contactInfoBundle.getString("email"));
            streetEditText.setText(contactInfoBundle.getString("street"));
            cityEditText.setText(contactInfoBundle.getString("city"));
            stateEditText.setText(contactInfoBundle.getString("state"));
            zipEditText.setText(contactInfoBundle.getString("zip"));
        }

        Button saveContactButton =
                (Button) view.findViewById(R.id.saveContactButton);
        saveContactButton.setOnClickListener(saveContactButtonClicked);
        return view;
    }

    View.OnClickListener saveContactButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(nameEditText.getText().toString().trim().length() != 0){
                AsyncTask<Object, Object, Object> saveContactTask =
                        new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                saveContact();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                InputMethodManager imm =
                                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                                listener.onAddEditCompleted(rowID);
                            }
                        };
                saveContactTask.execute((Object[]) null);
            }
            else
            {
                DialogFragment errorSaving = new DialogFragment(){
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                        builder.setMessage(getResources().getString(R.string.error_message));
                        builder.setPositiveButton(R.string.ok, null);
                        return builder.create();
                    }
                };
                errorSaving.show(getFragmentManager(), "error saving contact");
            }

        }
    };

    public void saveContact()
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());
        if(contactInfoBundle == null)
        {
            rowID = databaseConnector.insertContact(
                    nameEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    streetEditText.getText().toString(),
                    cityEditText.getText().toString(),
                    stateEditText.getText().toString(),
                    zipEditText.getText().toString());
        }
        else
        {
            databaseConnector.updateContact(rowID,
                    nameEditText.getText().toString(),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    streetEditText.getText().toString(),
                    cityEditText.getText().toString(),
                    stateEditText.getText().toString(),
                    zipEditText.getText().toString());
        }
    }
}
