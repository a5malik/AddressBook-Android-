package deitel.com.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by akash on 8/22/2015.
 */
public class DetailsFragment extends Fragment {

    public interface DetailsFragmentListener
    {
        public void onContactDeleted();

        public void onEditContact(Bundle arguments);
    }

    private DetailsFragmentListener listener;

    private long rowID = -1; // selected contact's rowID
    private TextView nameTextView; // displays contact's name
    private TextView phoneTextView; // displays contact's phone
    private TextView emailTextView; // displays contact's email
    private TextView streetTextView; // displays contact's street
    private TextView cityTextView; // displays contact's city
    private TextView stateTextView; // displays contact's state
    private TextView zipTextView; // displays contact's zip

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (DetailsFragmentListener) activity;
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

        if(savedInstanceState != null)
            rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
        else {
            Bundle arguments = getArguments();
            rowID = arguments.getLong(MainActivity.ROW_ID);
        }
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        streetTextView = (TextView) view.findViewById(R.id.streetTextView);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        stateTextView = (TextView) view.findViewById(R.id.stateTextView);
        zipTextView = (TextView) view.findViewById(R.id.zipTextView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadContactTask().execute(rowID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(MainActivity.ROW_ID, rowID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_edit:
                Bundle arguments = new Bundle();
                arguments.putLong(MainActivity.ROW_ID, rowID);
                arguments.putCharSequence("name", nameTextView.getText());
                arguments.putCharSequence("phone", phoneTextView.getText());
                arguments.putCharSequence("email", emailTextView.getText());
                arguments.putCharSequence("street", streetTextView.getText());
                arguments.putCharSequence("city", cityTextView.getText());
                arguments.putCharSequence("state", stateTextView.getText());
                arguments.putCharSequence("zip", zipTextView.getText());
                listener.onEditContact(arguments);
                return true;
            case R.id.action_delete:
                deleteContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadContactTask extends AsyncTask<Long, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getActivity());

        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();
            return databaseConnector.getOneContact(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst();
            int nameIndex = result.getColumnIndex("name");
            int phoneIndex = result.getColumnIndex("phone");
            int emailIndex = result.getColumnIndex("email");
            int streetIndex = result.getColumnIndex("street");
            int cityIndex = result.getColumnIndex("city");
            int stateIndex = result.getColumnIndex("state");
            int zipIndex = result.getColumnIndex("zip");

            nameTextView.setText(result.getString(nameIndex));
            phoneTextView.setText(result.getString(phoneIndex));
            emailTextView.setText(result.getString(emailIndex));
            streetTextView.setText(result.getString(streetIndex));
            cityTextView.setText(result.getString(cityIndex));
            stateTextView.setText(result.getString(stateIndex));
            zipTextView.setText(result.getString(zipIndex));

            result.close();
            databaseConnector.close();
        }
    }

    public void deleteContact()
    {
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    DialogFragment confirmDelete = new DialogFragment(){
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.confirm_message);
            builder.setPositiveButton(getResources().getString(R.string.button_delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final DatabaseConnector databaseConnector =
                            new DatabaseConnector(getActivity());
                    AsyncTask<Long, Object, Object> deleteTask =
                            new AsyncTask<Long, Object, Object>() {
                                @Override
                                protected Object doInBackground(Long... params) {
                                    databaseConnector.deleteContact(params[0]);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    listener.onContactDeleted();
                                }
                            };
                    deleteTask.execute(new Long[] {rowID});
                }
            });
            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create();
        }
    };
}
