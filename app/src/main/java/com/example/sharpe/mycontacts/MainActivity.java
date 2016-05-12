package com.example.sharpe.mycontacts;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

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

    public String[] scope = new String[]{VKScope.FRIENDS, VKScope.MESSAGES, VKScope.PHOTOS};
    private ListView lV_vk, contactsListView;
    private Button signInVk, buttonContacts;
    private ImageView imageView,imageView2;
    private FrameLayout frag;

    List<SelectUser> selectUsers;
    Cursor phones;
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        buttonContacts = (Button) findViewById(R.id.buttonContacts);
        lV_vk = (ListView) findViewById(R.id.lV_vk);

        frag = (FrameLayout) findViewById(R.id.frag);
        signInVk = (Button)findViewById(R.id.signInVk);


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
        contactsListView =(ListView) findViewById(R.id.lV_contac);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("search", "here---------------- listener"+ position);
                SelectUser data = selectUsers.get(position);
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
        }else {
            adapter = new SelectUserAdapter(selectUsers, MainActivity.this);
            contactsListView.setAdapter(adapter);
        }
    }
}
    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
}

