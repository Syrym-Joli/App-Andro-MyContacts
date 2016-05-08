package com.example.sharpe.mycontacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity {

/*
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
*/

    public String[] scope = new String[]{VKScope.FRIENDS, VKScope.MESSAGES, VKScope.PHOTOS};
    private ListView lV_vk,LV_contac;
    private Button signInVk, buttonContacts;
    private ImageView imageView,imageView2;
    private FrameLayout frag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        buttonContacts = (Button) findViewById(R.id.buttonContacts);
        lV_vk = (ListView) findViewById(R.id.lV_vk);
        LV_contac =(ListView) findViewById(R.id.LV_contac);
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
                LV_contac.setVisibility(View.INVISIBLE);
            }
        });
        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lV_vk.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                frag.setVisibility(View.VISIBLE);
                LV_contac.setVisibility(View.VISIBLE);
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
}
