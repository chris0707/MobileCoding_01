package albarillo.barebarz.mobilecoding_01;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPost extends AppCompatActivity {

    private int userId;
    private String username;
    private boolean sort = false;
    ListView lvPosts;
    Button btnPostPage, btnSort;

    HashMap<String, String> hash_map = new HashMap();
    List<HashMap<String, String>> listItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);


        TextView tvUsername = findViewById(R.id.tvName);
        lvPosts = findViewById(R.id.lvPosts);
        btnPostPage = findViewById(R.id.btnPostPage);
        btnSort = findViewById(R.id.btnSort);

        //Retrieve value from Main
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt("userId");
            username = extras.getString("username");
            tvUsername.setText("Username: " + username);
        }
        GetRequest("https://jsonplaceholder.typicode.com/posts?userId=" + userId);

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.list_item,
                        new String[]{"First", "Second"},
                        new int[]{R.id.text1, R.id.text2});

                //lvPosts.setAdapter(adapter);
                if (sort) {
                    Collections.sort(listItems, new Comparator<HashMap<String, String>>() {
                        public int compare(HashMap<String, String> one, HashMap<String, String> two) {
                            return one.get("First").compareTo(two.get("First"));
                        }
                    });
                    sort = false;
                }else{
                    Collections.sort(listItems, new Comparator<HashMap<String, String>>(){
                        public int compare(HashMap<String, String> one, HashMap<String, String> two) {
                            String s1 = one.get("First"); String s2 = two.get("First");
                            if(s1 == s2)
                                return 0;
                            if(s1 != s2)
                                return -1;

                            return 1;
                        }
                    });
                    sort = true;
                }
                lvPosts.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                Toast.makeText(getBaseContext(), "Sorted", Toast.LENGTH_SHORT).show();
            }
        });

        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final String postId = listItems.get(i).get("id");
                final String title = listItems.get(i).get("First");
                final String body = listItems.get(i).get("Second");


                //show dialog and select if edit or delete
                AlertDialog.Builder builder = new AlertDialog.Builder(UserPost.this);
                builder.setTitle("Modify post");

                builder.setNeutralButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteRequest("https://jsonplaceholder.typicode.com/posts/" + postId);
                            }
                        });
                builder.setNegativeButton("Edit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //PutRequest();
                                Intent intent = new Intent(UserPost.this, UserPost_Add.class);
                                intent.putExtra("postId", postId);
                                intent.putExtra("title", title);
                                intent.putExtra("body", body);

                                startActivity(intent);
                            }
                        });
                builder.setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert1 = builder.create();
                alert1.show();
            }
        });

        btnPostPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPost.this, UserPost_Add.class);
                intent.putExtra("userId", userId);
                intent.putExtra("username", username);

                startActivity(intent);
            }
        });

    }

    public void PopulateUsers()
    {
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[]{"First", "Second"},
                new int[]{R.id.text1, R.id.text2});
        for (Map.Entry<String, String> kvp : hash_map.entrySet())
        {
            //assign a hashmap to the list
            HashMap<String, String> res = new HashMap<>();
            String[] temp1 = kvp.getKey().split("\\|");
            res.put("First", kvp.getKey().split("\\|")[0]);
            res.put("Second", kvp.getValue());
            res.put("id", kvp.getKey().split("\\|")[1]);
            listItems.add(res);
        }

        lvPosts.setAdapter(adapter);
    }

    public void GetRequest(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        //final TextView textView = (TextView) findViewById(R.id.textView);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray jsonA = new JSONArray(response);

                            for(int i = 0; i < jsonA.length(); i++)
                            {
                                JSONObject jsnObj = jsonA.getJSONObject(i);
                                String body = jsnObj.getString("body").toString().replace("\\", "\\\\");
                                hash_map.put(jsnObj.get("title").toString() + "|" + jsnObj.get("id").toString(),body);
                            }

                            if (hash_map.size() > 0)
                                PopulateUsers();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), "Error: " + e.getMessage().substring(0,500), Toast.LENGTH_SHORT).show();
                        }

                        //textView.setText("Response is: "+ response.substring(0,500));

                        Toast.makeText(getBaseContext(), "Users retrieved", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }


    public void DeleteRequest(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        //final TextView textView = (TextView) findViewById(R.id.textView);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                            //JSONArray jsonA = new JSONArray(response);
                        if (!response.isEmpty())
                            Toast.makeText(getApplicationContext(), "Deleted: " + response, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Posted - empty response", Toast.LENGTH_SHORT).show();


                        //textView.setText("Response is: "+ response.substring(0,500));

                        //Toast.makeText(getBaseContext(), "Users retrieved", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }
}
