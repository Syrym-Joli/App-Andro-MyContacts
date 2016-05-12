package com.example.sharpe.mycontacts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int OPTION_DIALOG = 1;
    public String[] scope = new String[]{VKScope.FRIENDS, VKScope.MESSAGES, VKScope.PHOTOS};
    private ListView lV_vk, contactsListView;
    private Button signInVk, buttonContacts;
    private ImageView imageView, imageView2;
    private FrameLayout frag;

    List<SelectUser> selectUsers;
    Cursor phones;
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        buttonContacts = (Button) findViewById(R.id.buttonContacts);
        lV_vk = (ListView) findViewById(R.id.lV_vk);

        frag = (FrameLayout) findViewById(R.id.frag);
        signInVk = (Button) findViewById(R.id.signInVk);
        signInVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(MainActivity.this, scope);
                lV_vk.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                frag.setVisibility(View.INVISIBLE);
                contactsListView.setVisibility(View.INVISIBLE);

            }
        });
        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lV_vk.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                frag.setVisibility(View.VISIBLE);
                contactsListView.setVisibility(View.VISIBLE);
            }
        });

        //-----------------------------
        selectUsers = new ArrayList<SelectUser>();
        resolver = this.getContentResolver();
        contactsListView = (ListView) findViewById(R.id.lV_contac);

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("search", "here---------------- listener " + position);
                SelectUser data = selectUsers.get(position);
                showDialog(OPTION_DIALOG);
            }
        });

        contactsListView.setFastScrollEnabled(true);
        phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
        search = (SearchView) findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                adapter.filter(newText);
                return false;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                final VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "user_id, frist_name, last_name"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKList list = (VKList) response.parsedModel;
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_expandable_list_item_1, list);
                        lV_vk.setAdapter(arrayAdapter);
                    }
                });
                Toast.makeText(getApplicationContext(), "Пользователь успешно авторизовался", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Произошла ошибка авторизации (или пользователь запретил авторизацию)", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sharpe.mycontacts/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    //--------------------------
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                while (phones.moveToNext()) {
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    SelectUser selectUser = new SelectUser();
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser.setEmail(id);
                    selectUsers.add(selectUser);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (phones.getCount() == 0) {
                Toast.makeText(MainActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
            } else {
                adapter = new SelectUserAdapter(selectUsers, MainActivity.this);
                contactsListView.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.sharpe.mycontacts/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        phones.close();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case OPTION_DIALOG:
                dialog = createOptionDialog();
                break;
            default:
                dialog = null;
        }
        return dialog;
    }

    private Dialog createOptionDialog() {
        final Dialog optionDialog;
        View optionDialogView = null;
        LayoutInflater li = LayoutInflater.from(this);
        optionDialogView = li.inflate(R.layout.option_dialog, null);

        optionDialog = new AlertDialog.Builder(this).setView(optionDialogView).create();
        ImageButton ibCall = (ImageButton) optionDialogView.findViewById(R.id.dialog_call);
        ImageButton ibView = (ImageButton) optionDialogView.findViewById(R.id.dialog_view);
        ImageButton ibSms = (ImageButton) optionDialogView.findViewById(R.id.dialog_sms);

        ibSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSMS = new Intent();
                intentSMS.setAction(Intent.ACTION_SENDTO);
                intentSMS.setData(Uri.parse("smsto:" + "99"));
                intentSMS.addCategory("android.intent.category.DEFAULT");
                startActivity(intentSMS);
                dismissDialog(OPTION_DIALOG);
            }
        });
        ibView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(Contacts.People.CONTENT_URI);
                startActivity(intent);

                dismissDialog(OPTION_DIALOG);
            }
        });
        ibCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:12345"));
                startActivity(intent);
            }
        });
        return optionDialog;
    }

    /*class ImageButtonListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.dialog_call:
                    if(contact.getPhone().equals("")){
                        Toast.makeText(MainActivity.this, "没有手机号码", Toast.LENGTH_LONG).show();
                    }else{
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + contact.getPhone()));
                        intent.addCategory("android.intent.category.DEFAULT");
                        startActivity(intent);
                    }
                    dismissDialog(OPTION_DIALOG);
                    break;
                case R.id.dialog_view:
                    // Send an intent, Active the detailActivity
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    // Send the id of the selected contact by the intent
                    intent.putExtra("id", contact.getId());
                    startActivity(intent);
                    dismissDialog(OPTION_DIALOG);
                    break;
                case R.id.dialog_sms:
                    if(contact.getPhone().equals("")){
                        Toast.makeText(MainActivity.this, "没有手机号码", Toast.LENGTH_LONG).show();
                    }else{
                        Intent intent1 = new Intent();
                        intent1.setAction(Intent.ACTION_SENDTO);
                        intent1.setData(Uri.parse("smsto:"+contact.getPhone()));
                        intent1.addCategory("android.intent.category.DEFAULT");
                        startActivity(intent1);
                    }
                    dismissDialog(OPTION_DIALOG);
                    break;
            }
        }
    }*/
}

